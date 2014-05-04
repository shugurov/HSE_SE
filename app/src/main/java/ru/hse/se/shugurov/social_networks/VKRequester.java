package ru.hse.se.shugurov.social_networks;

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

import ru.hse.se.shugurov.Requester;

/**
 * Created by Иван on 11.02.14.
 */
public class VKRequester//TODO fix throwing exceptions here, naming conventions
{
    public static final String REDIRECTION_URL = "https://oauth.vk.com/blank.html";
    public static final String OAUTH = "https://oauth.vk.com/authorize?" +
            "client_id=3965004&" +
            "redirect_uri=" + REDIRECTION_URL + "&" +
            "display=mobile&" +
            "response_type=token" +
            "&scope=wall,groups";
    private static final String ACCESS_TOKEN_TAG = "access_token";
    private static final String GROUP_ID_TAG = "group_id";
    private static final String BOARD_GET_COMMENTS = "board.getComments";
    private static final String VK_TOPIC_ID_TAG = "topic_id";
    private static final String REQUEST_BEGINNING = "https://api.vk.com/method/";
    private static final String BOARD_GET_TOPICS = "board.getTopics";
    private static final String WALL_GET_POSTS = "https://api.vk.com/method/wall.get?owner_id=-%s&extended=1";
    private static final String GET_PROFILE_INFORMATION = "https://api.vk.com/method/users.get?user_ids=%s&fields=photo_100";
    private static final String WALL_GET_COMMENTS_FOR_POST = "https://api.vk.com/method/wall.getComments?owner_id=-%s&post_id=%s&extended=1&count=100";
    private static final String ADD_COMMENT_TO_WALL_POST = "https://api.vk.com/method/wall.addComment?owner_id=-%s&post_id=%s&text=%s&access_token=%s";
    private static final String ADD_COMMENT_TO_TOPIC = "https://api.vk.com/method/board.addComment?group_id=%s&topic_id=%s&text=%s&access_token=%s";
    private static final String ADD_TOPIC = "https://api.vk.com/method/board.addTopic?group_id=%s&title=%s&text=%s&access_token=%s";
    private AccessToken accessToken;

    public VKRequester(AccessToken accessToken)
    {
        this.accessToken = accessToken;
    }

    public static VKAbstractItem[] getWallComments(String commentsJson)
    {
        VKAbstractItem[] comments = null;
        try
        {
            JSONObject jsonObject = new JSONObject(commentsJson);
            JSONArray responseArray = jsonObject.getJSONArray("response");
            comments = new VKAbstractItem[responseArray.length() - 1];
            for (int i = 1; i < responseArray.length(); i++)
            {
                JSONObject commentObject = responseArray.getJSONObject(i);
                int userId = commentObject.getInt("uid");
                long date = commentObject.getLong("date") * 1000;
                String text = commentObject.getString("text");
                VKProfile profile = new VKProfile(userId);
                comments[i - 1] = new VKAbstractItem(profile, text, new Date(date));
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return comments;
    }

    public static void getProfileInformation(VKAbstractItem[] comments, Requester.RequestResultCallback callback)//TODO искать только неизвестных пользователей
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

    public static void fillProfileInformation(VKAbstractItem[] comments, String profilesJson)
    {
        Map<Integer, List<VKProfile>> profilesMap = new HashMap<Integer, List<VKProfile>>();
        for (VKAbstractItem comment : comments)
        {
            List<VKProfile> profilesForCurrentId = profilesMap.get(comment.getAuthor().getId());
            if (profilesForCurrentId == null)
            {
                profilesForCurrentId = new ArrayList<VKProfile>();
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
                int userId = profileObject.getInt("uid");
                String name = profileObject.getString("first_name") + " " + profileObject.getString("last_name");
                String photo = profileObject.getString("photo_100");//TODO наверное, спрятать в константу
                List<VKProfile> userProfiles = profilesMap.get(userId);
                for (VKProfile userProfile : userProfiles)
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

    private static void parseProfiles(Map<Integer, VKProfile> profilesMap, JSONArray profiles)
    {
        for (int i = 0; i < profiles.length(); i++)
        {
            JSONObject currentProfile;
            try
            {
                currentProfile = profiles.getJSONObject(i);
                int userID = currentProfile.getInt("uid");
                String photo = currentProfile.getString("photo_medium_rec");
                String firstName = currentProfile.getString("first_name");
                String lastName = currentProfile.getString("last_name");
                VKProfile currentUser = new VKProfile(userID, firstName + " " + lastName, photo);
                profilesMap.put(userID, currentUser);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void parseGroups(Map<Integer, VKProfile> profilesMap, JSONArray profiles)//TODO тупо копирую код(
    {
        for (int i = 0; i < profiles.length(); i++)
        {
            JSONObject currentGroup;
            try
            {
                currentGroup = profiles.getJSONObject(i);
                int groupID = -currentGroup.getInt("gid");
                String photo = currentGroup.getString("photo_medium");
                String groupName = currentGroup.getString("name");
                VKProfile vkGroup = new VKProfile(groupID, groupName, photo);
                profilesMap.put(groupID, vkGroup);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void getTopics(String groupID, Requester.RequestResultCallback callback)//темы в обсуждении группы
    {
        String request = REQUEST_BEGINNING + BOARD_GET_TOPICS + "?" + GROUP_ID_TAG + "=" + groupID +
                "&" + ACCESS_TOKEN_TAG + "=" + accessToken + "&extended=1&preview=1";
        Requester requester = new Requester(callback);
        requester.execute(request);
    }

    //TODO строка с комментами не внизу страницы(
    public VKTopic[] getTopics(String topicsJson)
    {
        if (topicsJson == null)
        {
            return null;
        }
        VKTopic[] vkBoardTopics;
        Map<Integer, VKProfile> profilesMap = new HashMap<Integer, VKProfile>();
        try
        {
            JSONObject jsonObject = new JSONObject(topicsJson);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            JSONArray profiles = responseObject.getJSONArray("profiles");
            parseProfiles(profilesMap, profiles);
            JSONArray itemsJSONArray = responseObject.getJSONArray("topics");
            vkBoardTopics = new VKTopic[itemsJSONArray.length() - 1];
            for (int i = 1; i < itemsJSONArray.length(); i++)
            {
                JSONObject currentTopic = itemsJSONArray.getJSONObject(i);
                int topicID = currentTopic.getInt("tid");
                int authorID = currentTopic.getInt("created_by");
                String text = currentTopic.getString("first_comment");
                int comments = currentTopic.getInt("comments");
                long date = currentTopic.getLong("updated");
                VKProfile user = profilesMap.get(authorID);
                vkBoardTopics[i - 1] = new VKTopic(topicID, user, text, comments, new Date(date * 1000));
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
        return vkBoardTopics;
    }

    public void getComments(String groupID, int topicID, Requester.RequestResultCallback callback)
    {
        String request = REQUEST_BEGINNING + BOARD_GET_COMMENTS + "?" + GROUP_ID_TAG + "=" + groupID +
                "&" + VK_TOPIC_ID_TAG + "=" + Integer.toString(topicID) + "&" + ACCESS_TOKEN_TAG + "=" + accessToken + "&extended=1";
        Requester requester = new Requester(callback);
        requester.execute(request);
    }

    public VKAbstractItem[] getComments(String commentsJson)
    {
        if (commentsJson == null)
        {
            return null;
        }
        VKAbstractItem[] comments;
        Map<Integer, VKProfile> profilesMap = new HashMap<Integer, VKProfile>();// key - uid, value - user
        try
        {
            JSONObject jsonObject = new JSONObject(commentsJson);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            JSONArray profiles = responseObject.getJSONArray("profiles");
            parseProfiles(profilesMap, profiles);
            JSONArray jsonComments = responseObject.getJSONArray("comments");
            comments = new VKAbstractItem[jsonComments.length() - 1];
            for (int i = 1; i < jsonComments.length(); i++)
            {
                JSONObject currentComment = jsonComments.getJSONObject(i);
                long date = currentComment.getLong("date") * 1000;
                String text = currentComment.getString("text");
                int authorID = currentComment.getInt("from_id");
                VKProfile profile = profilesMap.get(authorID);
                comments[i - 1] = new VKAbstractItem(profile, text, new Date(date));
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
        return comments;
    }

    public void getWallPosts(String groupId, Requester.RequestResultCallback callback)
    {
        Requester requester = new Requester(callback);
        String url = String.format(WALL_GET_POSTS, groupId);
        requester.execute(url);
    }

    public VKTopic[] getWallPosts(String wallPostJson)
    {
        VKTopic[] posts = null;
        Map<Integer, VKProfile> profilesMap = new HashMap<Integer, VKProfile>();// key - uid, value - user TODO зачем, если всегда не используется?
        try
        {
            JSONObject jsonObject = new JSONObject(wallPostJson);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            JSONArray profiles = responseObject.getJSONArray("profiles");
            parseProfiles(profilesMap, profiles);
            JSONArray groups = responseObject.getJSONArray("groups");
            parseGroups(profilesMap, groups);
            JSONArray wall = responseObject.getJSONArray("wall");
            posts = new VKTopic[wall.length() - 1];
            for (int i = 1; i < wall.length(); i++)
            {
                JSONObject currentPost = wall.getJSONObject(i);
                int id = currentPost.getInt("id");
                long date = currentPost.getLong("date") * 1000;
                String text = currentPost.getString("text");
                int authorID = currentPost.getInt("from_id");
                VKProfile profile = profilesMap.get(authorID);
                JSONObject commentsObject = currentPost.getJSONObject("comments");
                int commentsQuantity = commentsObject.getInt("count");
                String attachedPicture = null; //TODO почему только одна?(
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
                    posts[i - 1] = new VKTopic(id, profile, text, commentsQuantity, new Date(date));
                } else
                {
                    posts[i - 1] = new VKTopic(id, profile, text, commentsQuantity, new Date(date), attachedPicture);
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return posts;
    }

    public void getWallComments(String groupId, int postId, Requester.RequestResultCallback callback)
    {
        String url = String.format(WALL_GET_COMMENTS_FOR_POST, groupId, postId);
        Requester requester = new Requester(callback);
        requester.execute(url);
    }

    public void addCommentToWallPost(String groupId, int postId, String text, Requester.RequestResultCallback callback)
    {
        try
        {
            String url = String.format(ADD_COMMENT_TO_WALL_POST, groupId, postId, URLEncoder.encode(text, "utf8"), accessToken.getAccessToken());
            Requester requester = new Requester(callback);
            requester.execute(url);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            callback.pushResult(null);
        }
    }

    public void addCommentToTopic(String groupId, int topicId, String text, Requester.RequestResultCallback callback)
    {
        try
        {
            String url = String.format(ADD_COMMENT_TO_TOPIC, groupId, topicId, URLEncoder.encode(text, "utf8"), accessToken.getAccessToken());
            Requester requester = new Requester(callback);
            requester.execute(url);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            callback.pushResult(null);
        }
    }

    public void addTopic(String groupId, String title, String text, Requester.RequestResultCallback callback)
    {
        try
        {
            String url = String.format(ADD_TOPIC, groupId, URLEncoder.encode(title, "utf8"), URLEncoder.encode(text, "utf8"), accessToken);
            Requester requester = new Requester(callback);
            requester.execute(url);
        } catch (UnsupportedEncodingException e)
        {
            callback.pushResult(null);
        }
    }
}
