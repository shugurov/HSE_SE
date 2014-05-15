package ru.hse.shugurov.social_networks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.hse.shugurov.utils.Requester;

/**
 * Handles vk requests.
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class VKRequester extends AbstractRequester
{
    /**
     * url with an access token begins with this string
     */
    public static final String REDIRECTION_URL = "https://oauth.vk.com/blank.html";

    /**
     * Has to be called in order to get an access token
     */
    public static final String OAUTH = "https://oauth.vk.com/authorize?" +
            "client_id=3965004&redirect_uri=" + REDIRECTION_URL +
            "&display=mobile&response_type=token&scope=wall,groups,offline";


    private static final String GET_COMMENTS = "https://api.vk.com/method/board.getComments?group_id=%s&topic_id=%s&access_token=%s&extended=1&count=100";
    private static final String BOARD_GET_TOPICS = "https://api.vk.com/method/board.getTopics?group_id=%s&access_token=%s&extended=1&preview=1&order=1";
    private static final String WALL_GET_POSTS = "https://api.vk.com/method/wall.get?owner_id=-%s&extended=1&count=100";
    private static final String GET_PROFILE_INFORMATION = "https://api.vk.com/method/users.get?user_ids=%s&fields=photo_100";
    private static final String WALL_GET_COMMENTS_FOR_POST = "https://api.vk.com/method/wall.getComments?owner_id=-%s&post_id=%s&extended=1&count=100";
    private static final String ADD_COMMENT_TO_WALL_POST = "https://api.vk.com/method/wall.addComment?owner_id=-%s&post_id=%s&text=%s&access_token=%s";
    private static final String ADD_COMMENT_TO_TOPIC = "https://api.vk.com/method/board.addComment?group_id=%s&topic_id=%s&text=%s&access_token=%s";
    private static final String ADD_TOPIC = "https://api.vk.com/method/board.addTopic?group_id=%s&title=%s&text=%s&access_token=%s";

    /**
     * Creates a new instance with a specified token
     *
     * @param accessToken
     */
    public VKRequester(AccessToken accessToken)
    {
        super(accessToken);
    }

    /*sets author fields*/
    private void parseProfiles(Map<String, SocialNetworkProfile> profilesMap, JSONArray profiles)
    {
        for (int i = 0; i < profiles.length(); i++)
        {
            JSONObject currentProfile;
            try
            {
                currentProfile = profiles.getJSONObject(i);
                String userID = currentProfile.getString("uid");
                String photo = currentProfile.getString("photo_medium_rec");
                String firstName = currentProfile.getString("first_name");
                String lastName = currentProfile.getString("last_name");
                SocialNetworkProfile currentUser = new SocialNetworkProfile(userID, firstName + " " + lastName, photo);
                profilesMap.put(userID, currentUser);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    /*parses groups*/
    private void parseGroups(Map<String, SocialNetworkProfile> profilesMap, JSONArray profiles)
    {
        for (int i = 0; i < profiles.length(); i++)
        {
            JSONObject currentGroup;
            try
            {
                currentGroup = profiles.getJSONObject(i);
                String groupID = "-" + currentGroup.getString("gid");
                String photo = currentGroup.getString("photo_medium");
                String groupName = currentGroup.getString("name");
                SocialNetworkProfile vkGroup = new SocialNetworkProfile(groupID, groupName, photo);
                profilesMap.put(groupID, vkGroup);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    /*pares a string which contains json representation of a wall*/
    private SocialNetworkEntry[] getWallComments(String commentsJson)
    {
        SocialNetworkEntry[] comments = null;
        try
        {
            JSONObject jsonObject = new JSONObject(commentsJson);
            JSONArray responseArray = jsonObject.getJSONArray("response");
            comments = new SocialNetworkEntry[responseArray.length() - 1];
            for (int i = 1; i < responseArray.length(); i++)
            {
                JSONObject commentObject = responseArray.getJSONObject(i);
                String userId = commentObject.getString("uid");

                long date = commentObject.getLong("date") * 1000;
                String text = commentObject.getString("text");
                SocialNetworkProfile profile = new SocialNetworkProfile(userId);
                comments[i - 1] = new SocialNetworkEntry(profile, text, new Date(date));
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return comments;
    }

    /*Requests authors for comments*/
    private void getProfileInformation(SocialNetworkEntry[] comments, Requester.RequestResultCallback callback)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < comments.length; i++)
        {
            if (i != 0)
            {
                builder.append(',');
            }
            builder.append(comments[i].getAuthor().getId());
        }
        String url = String.format(GET_PROFILE_INFORMATION, builder.toString());
        Requester requester = new Requester(callback);
        requester.execute(url);
    }

    /*sets authors to topics*/
    private void fillProfileInformation(SocialNetworkEntry[] comments, String profilesJson)
    {
        Map<String, List<SocialNetworkProfile>> profilesMap = new HashMap<String, List<SocialNetworkProfile>>();
        for (SocialNetworkEntry comment : comments)
        {
            List<SocialNetworkProfile> profilesForCurrentId = profilesMap.get(comment.getAuthor().getId());
            if (profilesForCurrentId == null)
            {
                profilesForCurrentId = new ArrayList<SocialNetworkProfile>();
                profilesMap.put(comment.getAuthor().getId(), profilesForCurrentId);
            }
            profilesForCurrentId.add(comment.getAuthor());
        }
        try
        {
            JSONObject receivedObject = new JSONObject(profilesJson);
            JSONArray profilesArray = receivedObject.getJSONArray("response");
            for (int i = 0; i < profilesArray.length(); i++)
            {
                JSONObject profileObject = profilesArray.getJSONObject(i);
                String userId = profileObject.getString("uid");
                String name = profileObject.getString("first_name") + " " + profileObject.getString("last_name");
                String photo = profileObject.getString("photo_100");
                List<SocialNetworkProfile> userProfiles = profilesMap.get(userId);
                for (SocialNetworkProfile userProfile : userProfiles)
                {
                    userProfile.setFullName(name);
                    userProfile.setPhoto(photo);
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void getTopics(String groupId, final RequestResultListener<SocialNetworkTopic> listener)//темы в обсуждении группы
    {
        String request = String.format(BOARD_GET_TOPICS, groupId, getAccessToken());
        Requester.RequestResultCallback callback = new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String topicJson)
            {
                if (topicJson == null || topicJson != null && topicJson.contains("error"))
                {
                    listener.resultObtained(null);
                } else
                {
                    SocialNetworkTopic[] topics = getTopics(topicJson);
                    listener.resultObtained(topics);
                }
            }
        };
        Requester requester = new Requester(callback);
        requester.execute(request);
    }


    /*parses a string representation of topics*/
    private SocialNetworkTopic[] getTopics(String topicsJson)
    {
        SocialNetworkTopic[] vkBoardTopics;
        Map<String, SocialNetworkProfile> profilesMap = new HashMap<String, SocialNetworkProfile>();
        try
        {
            JSONObject jsonObject = new JSONObject(topicsJson);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            JSONArray profiles = responseObject.getJSONArray("profiles");
            parseProfiles(profilesMap, profiles);
            JSONArray itemsJSONArray = responseObject.getJSONArray("topics");
            vkBoardTopics = new SocialNetworkTopic[itemsJSONArray.length() - 1];
            for (int i = 1; i < itemsJSONArray.length(); i++)
            {
                JSONObject currentTopic = itemsJSONArray.getJSONObject(i);
                String title = currentTopic.getString("title");
                String topicID = currentTopic.getString("tid");
                String authorID = currentTopic.getString("created_by");
                String text = currentTopic.getString("first_comment");
                int comments = currentTopic.getInt("comments");
                long date = currentTopic.getLong("updated");
                SocialNetworkProfile user = profilesMap.get(authorID);
                vkBoardTopics[i - 1] = new SocialNetworkTopic(title, topicID, user, text, comments, new Date(date * 1000));
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
        return vkBoardTopics;
    }

    @Override
    public void getComments(String groupID, String topicID, final RequestResultListener<SocialNetworkEntry> listener)
    {
        String request = String.format(GET_COMMENTS, groupID, topicID, getAccessToken());
        Requester.RequestResultCallback callback = new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String commentsJson)
            {
                if (commentsJson == null || (commentsJson != null && commentsJson.contains("error")))
                {
                    listener.resultObtained(null);
                } else
                {
                    SocialNetworkEntry[] comments = getComments(commentsJson);
                    listener.resultObtained(comments);
                }
            }
        };
        Requester requester = new Requester(callback);
        requester.execute(request);
    }

    /*parses a string representation of comments*/
    private SocialNetworkEntry[] getComments(String commentsJson)
    {
        SocialNetworkEntry[] comments;
        Map<String, SocialNetworkProfile> profilesMap = new HashMap<String, SocialNetworkProfile>();// key - uid, value - user
        try
        {
            JSONObject jsonObject = new JSONObject(commentsJson);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            JSONArray profiles = responseObject.getJSONArray("profiles");
            parseProfiles(profilesMap, profiles);
            JSONArray jsonComments = responseObject.getJSONArray("comments");
            comments = new SocialNetworkEntry[jsonComments.length() - 1];
            for (int i = 1; i < jsonComments.length(); i++)
            {
                JSONObject currentComment = jsonComments.getJSONObject(i);
                long date = currentComment.getLong("date") * 1000;
                String text = currentComment.getString("text");
                String authorID = currentComment.getString("from_id");
                SocialNetworkProfile profile = profilesMap.get(authorID);
                comments[i - 1] = new SocialNetworkEntry(profile, text, new Date(date));
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
        return comments;
    }

    @Override
    public void getWallPosts(String groupId, final RequestResultListener<SocialNetworkTopic> listener)
    {
        Requester.RequestResultCallback callback = new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String postsJson)
            {
                if (postsJson == null || (postsJson != null && postsJson.contains("error")))
                {
                    listener.resultObtained(null);
                } else
                {
                    listener.resultObtained(getWallPosts(postsJson));
                }
            }
        };
        Requester requester = new Requester(callback);
        String url = String.format(WALL_GET_POSTS, groupId);
        requester.execute(url);
    }

    /*parses a string representation of wall posts*/
    private SocialNetworkTopic[] getWallPosts(String wallPostJson)
    {
        SocialNetworkTopic[] posts = null;
        Map<String, SocialNetworkProfile> profilesMap = new HashMap<String, SocialNetworkProfile>();
        try
        {
            JSONObject jsonObject = new JSONObject(wallPostJson);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            JSONArray profiles = responseObject.getJSONArray("profiles");
            parseProfiles(profilesMap, profiles);
            JSONArray groups = responseObject.getJSONArray("groups");
            parseGroups(profilesMap, groups);
            JSONArray wall = responseObject.getJSONArray("wall");
            posts = new SocialNetworkTopic[wall.length() - 1];
            for (int i = 1; i < wall.length(); i++)
            {
                JSONObject currentPost = wall.getJSONObject(i);
                String id = currentPost.getString("id");
                long date = currentPost.getLong("date") * 1000;
                String text = currentPost.getString("text");
                String authorID = currentPost.getString("from_id");
                SocialNetworkProfile profile = profilesMap.get(authorID);
                JSONObject commentsObject = currentPost.getJSONObject("comments");
                int commentsQuantity = commentsObject.getInt("count");
                String attachedPicture = null;
                if (currentPost.has("attachment"))
                {

                    JSONObject attachedItem = currentPost.getJSONObject("attachment");
                    if (attachedItem.getString("type").equals("photo"))
                    {
                        JSONObject photo = attachedItem.getJSONObject("photo");
                        attachedPicture = photo.getString("src_big");
                    }
                }
                if (attachedPicture == null)
                {
                    posts[i - 1] = new SocialNetworkTopic(null, id, profile, text, commentsQuantity, new Date(date));
                } else
                {
                    posts[i - 1] = new SocialNetworkTopic(null, id, profile, text, commentsQuantity, new Date(date), attachedPicture);
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public void getWallComments(String groupId, String postId, final RequestResultListener<SocialNetworkEntry> listener)
    {
        String url = String.format(WALL_GET_COMMENTS_FOR_POST, groupId, postId);
        Requester.RequestResultCallback callback = new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String result)
            {
                if (result == null || (result != null && result.contains("error")))
                {
                    listener.resultObtained(null);
                } else
                {
                    SocialNetworkEntry[] comments = getWallComments(result);
                    if (comments == null)
                    {
                        listener.resultObtained(comments);
                    } else
                    {
                        requestAuthorsOfComments(comments, listener);
                    }
                }
            }
        };
        Requester requester = new Requester(callback);
        requester.execute(url);
    }

    /*makes a request*/
    private void requestAuthorsOfComments(final SocialNetworkEntry[] comments, final RequestResultListener<SocialNetworkEntry> listener)
    {
        getProfileInformation(comments, new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String profilesJson)
            {
                if (profilesJson == null)
                {
                    listener.resultObtained(null);
                } else
                {
                    fillProfileInformation(comments, profilesJson);
                    listener.resultObtained(comments);
                }
            }
        });
    }

    @Override
    public void addCommentToWallPost(String groupId, String postId, String text, Requester.RequestResultCallback callback)
    {
        try
        {
            String url = String.format(ADD_COMMENT_TO_WALL_POST, groupId, postId, URLEncoder.encode(text, "utf8"), getAccessToken().getAccessToken());
            Requester requester = new Requester(callback);
            requester.execute(url);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            callback.pushResult(null);
        }
    }

    @Override
    public boolean canAddPosts()
    {
        return true;
    }

    @Override
    public void addCommentToTopic(String groupId, String topicId, String text, Requester.RequestResultCallback callback)
    {
        try
        {
            String url = String.format(ADD_COMMENT_TO_TOPIC, groupId, topicId, URLEncoder.encode(text, "utf8"), getAccessToken().getAccessToken());
            Requester requester = new Requester(callback);
            requester.execute(url);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            callback.pushResult(null);
        }
    }

    @Override
    public void addTopic(String groupId, String title, String text, Requester.RequestResultCallback callback)
    {
        try
        {
            String url = String.format(ADD_TOPIC, groupId, URLEncoder.encode(title, "utf8"), URLEncoder.encode(text, "utf8"), getAccessToken());
            Requester requester = new Requester(callback);
            requester.execute(url);
        } catch (UnsupportedEncodingException e)
        {
            callback.pushResult(null);
        }
    }

}
