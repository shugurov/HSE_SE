package ru.hse.shugurov.screens;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a screen with html content. Overrides receiving url and then returns FileDescription
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class HtmlContentScreen extends BaseScreen implements HasFile
{
    /**
     * Used for recreating objects after their serialization
     */
    public static final Creator<HtmlContentScreen> CREATOR = new Creator<HtmlContentScreen>()
    {
        @Override
        public HtmlContentScreen createFromParcel(Parcel source)
        {
            return new HtmlContentScreen(source);
        }

        @Override
        public HtmlContentScreen[] newArray(int size)
        {
            return new HtmlContentScreen[size];
        }
    };

    /**
     * @param jsonObject json object which stores information about a screen
     * @param serverURL
     * @throws JSONException
     */
    protected HtmlContentScreen(JSONObject jsonObject, String serverURL) throws JSONException
    {
        super(jsonObject, serverURL);
        url = serverURL + jsonObject.getString("html");
    }

    protected HtmlContentScreen(Parcel parcel)
    {
        super(parcel);
    }

    @Override
    public FileDescription getFileDescription()
    {
        return new FileDescription(getKey(), getUrl());
    }
}
