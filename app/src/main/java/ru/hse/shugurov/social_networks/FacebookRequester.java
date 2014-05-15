package ru.hse.shugurov.social_networks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.hse.shugurov.utils.Requester;

/**
 * Handles facebook requests.
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class FacebookRequester extends AbstractRequester
{
    /**
     * url with an access token begins with this string
     */
    public static final String REDIRECTION_URL = "https://www.facebook.com/connect/login_success.html";

    /**
     * Has to be called in order to get an access token
     */
    public static final String AUTH = "https://www.facebook.com/dialog/oauth?" +
            "client_id=465339086933483" +
            "&redirect_uri=" + REDIRECTION_URL + "&response_type=token&scope=publish_actions";


    private final static String GET_PAGE = "https://graph.facebook.com/%s/feed?access_token=%s";
    private final static String GET_PHOTO = "http://graph.facebook.com/%s/?fields=picture&type=large";
    private final static String GET_COMMENTS = "https://graph.facebook.com/%s/comments?access_token=%s";
    private final static String GET_POST = "https://graph.facebook.com/%s?access_token=%s";
    private final static String ADD_COMMENT = "https://graph.facebook.com/%s/comments?method=post&message=%s&access_token=%s";

    /**
     * Creates a new instance with a specified token
     *
     * @param accessToken
     */
    public FacebookRequester(AccessToken accessToken)
    {
        super(accessToken);
    }

    /*requests a picture for the group given*/
    private static void getGroupPictureUrl(String groupId, Requester.RequestResultCallback callback)
    {
        String request = String.format(GET_PHOTO, groupId);
        Requester requester = new Requester(callback);
        requester.execute(request);
    }

    /*extracts a photo url from a json and sets it it every topic*/
    private static void fillPhotos(String json, SocialNetworkTopic[] topics)
    {
        String url = "";
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject pictureObject = jsonObject.getJSONObject("picture");
            JSONObject dataObject = pictureObject.getJSONObject("data");
            url = dataObject.getString("url");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        for (SocialNetworkTopic topic : topics)
        {
            topic.getAuthor().setPhoto(url);
        }
    }

    @Override
    public void getTopics(final String groupId, final RequestResultListener<SocialNetworkTopic> listener)
    {
        String request = String.format(GET_PAGE, groupId, getAccessToken().getAccessToken());
        Requester.RequestResultCallback requestResultCallback = new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String topicJson)
            {
                handleTopicsResponse(topicJson, listener, groupId);
            }
        };
        Requester requester = new Requester(requestResultCallback);
        requester.execute(request);
    }

    /*checks if a response with topics is not null and does not contain error messages*/
    private void handleTopicsResponse(String topicJson, final RequestResultListener<SocialNetworkTopic> listener, String groupId)
    {
        if (topicJson == null || (topicJson != null && topicJson.contains("error")))
        {
            listener.resultObtained(null);
        } else
        {
            createTopics(topicJson, listener, groupId);
        }
    }

    /*creates topic objects and pushes them back*/
    private void createTopics(String topicJson, final RequestResultListener<SocialNetworkTopic> listener, String groupId)
    {
        final SocialNetworkTopic[] topics = getTopics(topicJson);
        getGroupPictureUrl(groupId, new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String result)
            {
                if (result == null || (result != null && result.contains("error")))
                {
                    listener.resultObtained(null);
                } else
                {
                    fillPhotos(result, topics);
                    listener.resultObtained(topics);
                }
            }
        });
    }

    /*parses json and creates topic objects*/
    private SocialNetworkTopic[] getTopics(String topicsJson)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("y-MM-dd'T'HH:mm:ss'+0000'");
        try
        {
            JSONObject pageObject = new JSONObject(topicsJson);
            JSONArray dataObject = pageObject.getJSONArray("data");
            SocialNetworkTopic[] topics = new SocialNetworkTopic[dataObject.length()];
            for (int i = 0; i < dataObject.length(); i++)
            {
                JSONObject topicObject = dataObject.getJSONObject(i);
                topics[i] = parseTopic(dateFormat, topicObject);
            }
            return topics;
        } catch (JSONException e)
        {
            return null;
        } catch (ParseException e)
        {
            return null;
        }
    }

    /*parses json, creates a topic object and returns it*/
    private SocialNetworkTopic parseTopic(SimpleDateFormat dateFormat, JSONObject topicObject) throws JSONException, ParseException
    {
        String message;
        if (topicObject.has("message"))
        {
            message = topicObject.getString("message");
        } else if (topicObject.has("story"))
        {
            message = topicObject.getString("story");
        } else
        {
            message = "";
        }
        String topicId = topicObject.getString("id");
        JSONObject fromObject = topicObject.getJSONObject("from");
        String userId = fromObject.getString("id");
        String userName = fromObject.getString("name");
        String date = topicObject.getString("created_time");
        SocialNetworkProfile author = new SocialNetworkProfile(userId, userName);
        return new SocialNetworkTopic(null, topicId, author, message, 0, dateFormat.parse(date));
    }

    @Override
    public void getComments(final String groupID, final String topicID, final RequestResultListener<SocialNetworkEntry> listener)
    {
        getPost(topicID, new RequestResultListener<SocialNetworkTopic>()
        {
            @Override
            public void resultObtained(SocialNetworkTopic[] result)
            {
                if (result == null)
                {
                    listener.resultObtained(null);
                } else
                {
                    requestComments(result[0], topicID, listener);
                }
            }
        });
    }

    /*creates a callback and makes q requests for comments*/
    private void requestComments(final SocialNetworkTopic post, String topicID, final RequestResultListener<SocialNetworkEntry> listener)
    {
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
                    handleCommentsJson(post, commentsJson, listener);
                }
            }
        };
        String request = String.format(GET_COMMENTS, topicID, getAccessToken());
        Requester requester = new Requester(callback);
        requester.execute(request);
    }

    /*creates array of topics and fills it*/
    private void handleCommentsJson(SocialNetworkTopic post, String commentsJson, final RequestResultListener<SocialNetworkEntry> listener)
    {
        try
        {
            JSONObject commentObject = new JSONObject(commentsJson);
            JSONArray commentsArray = commentObject.getJSONArray("data");
            final SocialNetworkEntry[] comments = new SocialNetworkEntry[commentsArray.length() + 1];
            comments[0] = post;
            SimpleDateFormat dateFormat = new SimpleDateFormat("y-MM-dd'T'HH:mm:ss'+0000'");
            final Map<String, List<SocialNetworkProfile>> idToProfiles = new HashMap<String, List<SocialNetworkProfile>>();
            for (int i = 0; i < commentsArray.length(); i++)
            {
                comments[i + 1] = parseTopic(dateFormat, commentsArray.getJSONObject(i));
                addProfileToMap(comments[i + 1], idToProfiles);
            }
            addProfileToMap(comments[0], idToProfiles);
            String[] photoUrlRequests = new String[idToProfiles.size()];
            int requestIndex = 0;
            for (String userId : idToProfiles.keySet())
            {
                photoUrlRequests[requestIndex] = String.format(GET_PHOTO, userId);
                requestIndex++;
            }
            Requester.MultipleRequestResultCallback callback = new Requester.MultipleRequestResultCallback()
            {
                @Override
                public void pushResult(String[] results)
                {
                    handlePhotoJson(results, listener, idToProfiles, comments);
                }
            };
            Requester requester = new Requester(callback);
            requester.execute(photoUrlRequests);
        } catch (Exception e)
        {
            listener.resultObtained(null);
        }

    }

    /*adds an author of the comment given to a map of authors*/
    private void addProfileToMap(SocialNetworkEntry comment, Map<String, List<SocialNetworkProfile>> idToProfiles)
    {
        SocialNetworkProfile profile = comment.getAuthor();
        List<SocialNetworkProfile> profilesWithCurrentId = idToProfiles.get(profile.getId());
        if (profilesWithCurrentId == null)
        {
            profilesWithCurrentId = new ArrayList<SocialNetworkProfile>();
            idToProfiles.put(profile.getId(), profilesWithCurrentId);
        }
        profilesWithCurrentId.add(profile);
    }

    /*requests posts*/
    private void getPost(String postId, final RequestResultListener<SocialNetworkTopic> listener)
    {
        String request = String.format(GET_POST, postId, getAccessToken());
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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("y-MM-dd'T'HH:mm:ss'+0000'");
                    try
                    {
                        JSONObject topicObject = new JSONObject(result);
                        SocialNetworkTopic topic = parseTopic(dateFormat, topicObject);
                        listener.resultObtained(new SocialNetworkTopic[]{topic});
                    } catch (Exception e)
                    {
                        listener.resultObtained(null);
                    }
                }
            }
        };
        Requester requester = new Requester(callback);
        requester.execute(request);
    }

    /*parses json for a photo*/
    private void handlePhotoJson(String[] results, RequestResultListener<SocialNetworkEntry> listener, Map<String, List<SocialNetworkProfile>> idToProfiles, SocialNetworkEntry[] comments)
    {
        if (results == null)
        {
            listener.resultObtained(null);
        } else
        {
            for (String response : results)
            {
                if (response.contains("error"))
                {
                    listener.resultObtained(null);
                } else
                {
                    try
                    {
                        JSONObject responseObject = new JSONObject(response);
                        JSONObject pictureObject = responseObject.getJSONObject("picture");
                        JSONObject dataObject = pictureObject.getJSONObject("data");
                        String url = dataObject.getString("url");
                        String id = responseObject.getString("id");
                        List<SocialNetworkProfile> profilesWithCurrentId = idToProfiles.get(id);
                        if (profilesWithCurrentId != null)
                        {
                            for (SocialNetworkProfile profile : profilesWithCurrentId)
                            {
                                profile.setPhoto(url);
                            }
                        }
                        listener.resultObtained(comments);
                    } catch (JSONException e)
                    {
                        listener.resultObtained(null);
                    }
                }
            }
        }
    }

    @Override
    public void addCommentToTopic(String groupId, String topicId, String text, Requester.RequestResultCallback callback)
    {
        try
        {
            String request = String.format(ADD_COMMENT, topicId, URLEncoder.encode(text, "utf8"), getAccessToken());
            Requester requester = new Requester(callback);
            requester.execute(request);
        } catch (UnsupportedEncodingException e)
        {
            callback.pushResult(null);
        }

    }

    @Override
    public boolean showCommentsQuantity()
    {
        return false;
    }
}
