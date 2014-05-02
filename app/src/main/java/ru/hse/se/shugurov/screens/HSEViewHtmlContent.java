package ru.hse.se.shugurov.screens;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 25.10.13.
 */
public class HSEViewHtmlContent extends HSEView implements HasFile
{

    HSEViewHtmlContent(JSONObject jsonObject, String serverURL) throws JSONException
    {
        super(jsonObject, serverURL);
        url = serverURL + jsonObject.getString("html");
    }

    @Override
    public FileDescription getFileDescription()
    {
        return new FileDescription(getKey(), getUrl());
    }

}
