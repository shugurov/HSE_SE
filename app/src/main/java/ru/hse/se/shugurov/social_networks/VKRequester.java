package ru.hse.se.shugurov.social_networks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.hse.se.shugurov.Requester;

/**
 * Created by Иван on 11.02.14.
 */
public class VKRequester
{
    private static final String ACCESS_TOKEN_TAG = "access_token";
    private static final String GROUP_ID_TAG = "group_id";
    private static final String BOARD_GET_COMMENTS = "board.getComments";
    private static final String VK_TOPIC_ID_TAG = "topic_id";
    private static String REQUEST_BEGINNING = "https://api.vk.com/method/";
    private static String BOARD_GET_TOPICS = "board.getTopics";
    private String accessToken;

    public VKRequester(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public void getTopics(String groupID, Requester.RequestResultCallback callback)//темы в обсуждении группы
    {
        String request = REQUEST_BEGINNING + BOARD_GET_TOPICS + "?" + GROUP_ID_TAG + "=" + groupID +
                "&" + ACCESS_TOKEN_TAG + "=" + accessToken + "&extended=1&preview=1";
        Requester requester = new Requester(callback);
        requester.execute(request);
    }

    public VKTopic[] getTopicsAdapter(String data)
    {
        if (data == null)
        {
            return null;
        }
        VKTopic[] vkBoardTopics;
        Map<Integer, VKProfile> profilesMap = new HashMap<Integer, VKProfile>();// key - uid, value - user
        try
        {
            JSONObject jsonObject = new JSONObject(data);
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
                long date = currentTopic.getLong("created");
                VKProfile user = profilesMap.get(authorID);
                vkBoardTopics[i - 1] = new VKTopic(topicID, user, text, comments, new Date(date));
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

    public VKAbstractItem[] getComments(String data)
    {
        if (data == null)
        {
            return null;
        }
        VKAbstractItem[] comments;
        Map<Integer, VKProfile> profilesMap = new HashMap<Integer, VKProfile>();// key - uid, value - user
        try
        {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            JSONArray profiles = responseObject.getJSONArray("profiles");
            parseProfiles(profilesMap, profiles);
            JSONArray jsonComments = responseObject.getJSONArray("comments");
            comments = new VKAbstractItem[jsonComments.length() - 1];
            for (int i = 1; i < jsonComments.length(); i++)
            {
                JSONObject currentComment = jsonComments.getJSONObject(i);
                long date = currentComment.getLong("date");
                String text = currentComment.getString("text");
                int authorID = currentComment.getInt("from_id");
                VKProfile profile = profilesMap.get(authorID);
                comments[i - 1] = new VKAbstractItem(profile, text, new Date(date));
            }
            parseProfiles(profilesMap, profiles);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
        return comments;
    }

    private void parseProfiles(Map<Integer, VKProfile> profilesMap, JSONArray profiles)
    {
        for (int i = 0; i < profiles.length(); i++)
        {
            JSONObject currentProfile;
            try
            {
                currentProfile = profiles.getJSONObject(i);
                int userID = currentProfile.getInt("uid");
                String photo = currentProfile.getString("photo");
                String firstName = currentProfile.getString("first_name");
                String lastName = currentProfile.getString("last_name");
                VKProfile currentUser = new VKProfile(userID, firstName, lastName, photo);
                profilesMap.put(userID, currentUser);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

}
