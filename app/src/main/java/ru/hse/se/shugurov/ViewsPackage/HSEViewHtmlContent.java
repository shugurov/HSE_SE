package ru.hse.se.shugurov.ViewsPackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 25.10.13.
 */
public class HSEViewHtmlContent extends HSEView implements HasFile
{
    private String html;

    HSEViewHtmlContent(JSONObject jsonObject, String index)
    {
        super(jsonObject, index);
        try
        {
            html = jsonObject.getString("html");
        } catch (JSONException e)
        {
            e.printStackTrace();
            html = "";
        }
    }

    @Override
    public FileDescription getFileDescription()
    {
        return new FileDescription(getKey(), SERVER_LINK + html);
    }
}
