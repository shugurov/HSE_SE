package ru.hse.se.shugurov.ViewsPackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 25.10.13.
 */
public class HSEViewHtmlContent extends HSEView implements HasFile
{
    private String html;

    HSEViewHtmlContent(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject);
        html = jsonObject.getString("html");
        url = "";
    }

    @Override
    public FileDescription getFileDescription()
    {
        return new FileDescription(getKey(), SERVER_LINK + html);
    }
}
