package ru.hse.se.shugurov.social_networks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import ru.hse.se.shugurov.Requester;

/**
 * Created by Иван on 04.05.2014.
 */
public class FacebookRequester extends AbstractRequester
{
    public static final String REDIRECTION_URL = "https://www.facebook.com/connect/login_success.html";
    public static final String AUTH = "https://www.facebook.com/dialog/oauth?" +
            "client_id=465339086933483" +
            "&redirect_uri=" + REDIRECTION_URL + "&response_type=token";

    private final static String GET_PAGE = "https://graph.facebook.com/%s/feed?access_token=%s";
    private final static String GET_PHOTO = "http://graph.facebook.com/%s/?fields=picture&type=large";

    public FacebookRequester(AccessToken accessToken)
    {
        super(accessToken);
    }

    private static void getGroupPictureUrl(String groupId, Requester.RequestResultCallback callback)
    {
        String request = String.format(GET_PHOTO, groupId);
        Requester requester = new Requester(callback);
        requester.execute(request);
    }

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
    public void getTopics(String groupId, Requester.RequestResultCallback callback)
    {
        String request = String.format(GET_PAGE, groupId, getAccessToken().getAccessToken());
        Requester requester = new Requester(callback);
        requester.execute(request);
    }

    @Override
    public SocialNetworkTopic[] getTopics(String topicsJson)
    {
        SocialNetworkTopic[] topics;
        SimpleDateFormat dateFormat = new SimpleDateFormat("y-MM-dd'T'HH:mm:ss'+0000'");
        try
        {
            JSONObject pageObject = new JSONObject(topicsJson);
            JSONArray dataObject = pageObject.getJSONArray("data");
            topics = new SocialNetworkTopic[dataObject.length()];
            for (int i = 0; i < dataObject.length(); i++)
            {
                JSONObject topicObject = dataObject.getJSONObject(i);
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
                topics[i] = new SocialNetworkTopic(topicId, author, message, 0, dateFormat.parse(date));
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

}
