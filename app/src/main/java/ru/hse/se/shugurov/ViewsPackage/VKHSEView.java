package ru.hse.se.shugurov.ViewsPackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 01.11.13.
 */
public class VKHSEView extends HSEView
{
    public String getObjectID()
    {
        return objectID;
    }

    private String objectID = "";

    protected VKHSEView(JSONObject jsonObject, String index)
    {
        super(jsonObject, index);
        try
        {
            objectID = jsonObject.getString("objectId");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
