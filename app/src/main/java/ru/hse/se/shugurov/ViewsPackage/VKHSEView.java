package ru.hse.se.shugurov.ViewsPackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 01.11.13.
 */
public class VKHSEView extends HSEView
{
    private String objectID = "";

    protected VKHSEView(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject);
        objectID = jsonObject.getString("objectId");
    }

    public String getObjectID()
    {
        return objectID;
    }
}
