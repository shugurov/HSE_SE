package ru.hse.shugurov.screens;


import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.hse.shugurov.utills.FileManager;


/**
 * Describes a screen with a list of RSS elements
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class RSSWrapperScreen extends BaseScreen implements HasFile
{
    /**
     * Used for recreating objects after their serialization
     */
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

    /**
     * Used for recreating objects after their serialization. All subclasses <strong>have to call it first</strong>
     */
    protected RSSWrapperScreen(Parcel input)
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
        return new FileDescription(getKey(), url, true);
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
