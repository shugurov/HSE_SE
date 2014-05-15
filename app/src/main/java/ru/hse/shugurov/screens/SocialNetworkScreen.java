package ru.hse.shugurov.screens;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Describes common properties of social network screens
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class SocialNetworkScreen extends BaseScreen
{
    /**
     * Used for recreating objects after their serialization
     */
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

    /**
     * Parses JSON and creates a new instance
     *
     * @param jsonObject JSON response which contains essential information for creation
     * @throws JSONException if supplied JSON response is incorrect
     */
    protected SocialNetworkScreen(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject);
        objectId = jsonObject.getString("objectId");
    }

    /**
     * Used for recreating objects after their serialization. All subclasses <strong>have to call it first</strong>
     */
    protected SocialNetworkScreen(Parcel source)
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

    /**
     * Usually returns id of a post or a group
     *
     * @return id
     */
    public String getObjectId()
    {
        return objectId;
    }
}
