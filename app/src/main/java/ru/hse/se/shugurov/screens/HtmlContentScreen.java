package ru.hse.se.shugurov.screens;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a screen with html content. Overrides receiving url and then returns FileDescription
 * <p/>
 * Created Ivan Shugurov
 */
public class HtmlContentScreen extends BaseScreen implements HasFile
{

    /**
     * @param jsonObject json object which stores information about a screen
     * @param serverURL
     * @throws JSONException
     */
    HtmlContentScreen(JSONObject jsonObject, String serverURL) throws JSONException
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
