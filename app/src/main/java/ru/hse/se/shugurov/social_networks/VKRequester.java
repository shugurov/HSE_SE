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
    private static String REQUEST_BEGINNING = "https://api.vk.com/method/";
    private static String BOARD_GET_TOPICS = "board.getTopics";
    Requester.RequestResultCallback callback;
    private String accessToken;

    public VKRequester(String accessToken, Requester.RequestResultCallback callback)
    {
        this.accessToken = accessToken;
        this.callback = callback;
    }

    public void getTopics(String groupID)//темы в обсуждении группы
    {
        String request = REQUEST_BEGINNING + BOARD_GET_TOPICS + "?" + GROUP_ID_TAG + "=" + groupID +
                "&" + ACCESS_TOKEN_TAG + "=" + accessToken + "&extended=1&preview=1";
        Requester requester = new Requester(callback);
        requester.execute(request);
    }

    public VKTopic[] getTopicsAdapter(String data)
    {
        VKTopic[] vkBoardTopics = null;
        Map<Integer, VKUser> users = new HashMap<Integer, VKUser>();// key - uid, value - user
        try
        {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            JSONArray profilesJSONArray = responseObject.getJSONArray("profiles");
            for (int i = 0; i < profilesJSONArray.length(); i++)
            {
                JSONObject currentProfile = profilesJSONArray.getJSONObject(i);
                int userID = currentProfile.getInt("uid");
                String photo = currentProfile.getString("photo");
                String firstName = currentProfile.getString("first_name");
                String lastName = currentProfile.getString("last_name");
                VKUser currentUser = new VKUser(userID, firstName, lastName, photo);
                users.put(userID, currentUser);
            }
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
                VKUser user = users.get(authorID);
                vkBoardTopics[i - 1] = new VKTopic(topicID, user, text, comments, new Date(date));
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return vkBoardTopics;
    }

}
