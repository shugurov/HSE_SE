package ru.hse.se.shugurov.screens;


import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.hse.se.shugurov.utills.FileManager;


/**
 * Created by Иван on 28.10.13.
 */
public class RSSWrapperScreen extends BaseScreen implements HasFile
{
    public static final Creator<RSSWrapperScreen> CREATOR = new Creator<RSSWrapperScreen>()
    {
        @Override
        public RSSWrapperScreen createFromParcel(Parcel source)
        {
            return new RSSWrapperScreen(source);
        }

        @Override
        public RSSWrapperScreen[] newArray(int size)
        {
            return new RSSWrapperScreen[size];
        }
    };

    private RSSWrapperScreen(Parcel input)
    {
        super(input);
    }

    RSSWrapperScreen(JSONObject jsonObject, String serverURL) throws JSONException
    {
        super(jsonObject, serverURL);
        url = serverURL + "/api/structure/rss/" + getKey();
    }

    @Override
    public FileDescription getFileDescription()
    {
        return new FileDescription(getKey(), url);
    }


    public RSSScreen[] getRSS() throws JSONException
    {
        FileManager fileManager = FileManager.instance();
        String content;
        try
        {
            content = fileManager.getFileContent(getKey());
            JSONObject jsonObject;
            jsonObject = new JSONObject(content);
            JSONArray jsonArray = jsonObject.getJSONArray("entries");
            RSSScreen[] childViews = new RSSScreen[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++)
            {
                childViews[i] = new RSSScreen(jsonArray.getJSONObject(i));
            }
            return childViews;
        } catch (IOException e)
        {
            return null;
        }

    }
}
