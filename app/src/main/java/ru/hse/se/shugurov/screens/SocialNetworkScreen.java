package ru.hse.se.shugurov.screens;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 01.11.13.
 */
public class SocialNetworkScreen extends BaseScreen
{
    public static final Creator<SocialNetworkScreen> CREATOR = new Creator<SocialNetworkScreen>()
    {
        @Override
        public SocialNetworkScreen createFromParcel(Parcel source)
        {
            return new SocialNetworkScreen(source);
        }

        @Override
        public SocialNetworkScreen[] newArray(int size)
        {
            return new SocialNetworkScreen[size];
        }
    };
    private String objectId = "";

    protected SocialNetworkScreen(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject);
        objectId = jsonObject.getString("objectId");
    }

    private SocialNetworkScreen(Parcel source)
    {
        super(source);
        objectId = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeString(objectId);
    }

    public String getObjectId()
    {
        return objectId;
    }
}
