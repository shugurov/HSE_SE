package ru.hse.se.shugurov.social_networks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 11.02.14.
 */
public class VKRequester
{
    private static final String ACCESS_TOKEN_TAG = "access_token";
    private static final String GROUP_ID_TAG = "group_id";
    private static String REQUEST_BEGINNING = "https://api.vk.com/method/";
    private static String BOARD_GET_TOPICS = "board.getTopics";

    private String accessToken;

    public VKRequester(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public void getTopics(String groupID, Callback<VKTopic> callback)//темы в обсуждении группы
    {
        String request = REQUEST_BEGINNING + BOARD_GET_TOPICS + "?" + GROUP_ID_TAG + "=" + groupID +
                "&" + ACCESS_TOKEN_TAG + "=" + accessToken + "&extended=1";
    }

    private VKTopic[] parseTopics(String data)
    {
        VKTopic[] vkBoardTopics = null;
        try
        {
            JSONObject responseObject = new JSONObject(data);
            JSONArray array = responseObject.getJSONArray("response");
            String count = array.getString(0);
            JSONArray items = responseObject.getJSONArray("items");
            JSONArray profiles = responseObject.getJSONArray("profiles");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return vkBoardTopics;
    }


    public interface Callback<T>
    {
        public T pushData();
    }
}
