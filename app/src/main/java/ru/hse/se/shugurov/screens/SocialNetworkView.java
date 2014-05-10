package ru.hse.se.shugurov.screens;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 01.11.13.
 */
public class SocialNetworkView extends BaseScreen
{
    private String objectID = "";

    protected SocialNetworkView(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject);
        objectID = jsonObject.getString("objectId");
    }

    public String getObjectID()
    {
        return objectID;
    }
}
