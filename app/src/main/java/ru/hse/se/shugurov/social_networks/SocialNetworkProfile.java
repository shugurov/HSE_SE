package ru.hse.se.shugurov.social_networks;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Иван on 14.02.14.
 */
public class SocialNetworkProfile implements Parcelable
{
    public static final Creator<SocialNetworkProfile> CREATOR = new Creator<SocialNetworkProfile>()
    {
        @Override
        public SocialNetworkProfile createFromParcel(Parcel source)
        {
            return new SocialNetworkProfile(source);
        }

        @Override
        public SocialNetworkProfile[] newArray(int size)
        {
            return new SocialNetworkProfile[size];
        }
    };
    private String userId;
    private String photo;
    private String fullName;

    public SocialNetworkProfile(String userId)
    {
        this.userId = userId;
    }

    public SocialNetworkProfile(String userId, String fullName, String photo)
    {
        this(userId, fullName);
        this.photo = photo;
    }

    public SocialNetworkProfile(String userId, String fullName)
    {
        this.userId = userId;
        this.fullName = fullName;
    }

    private SocialNetworkProfile(Parcel source)
    {
        userId = source.readString();
        photo = source.readString();
        fullName = source.readString();
    }

    public String getId()
    {
        return userId;
    }

    public String getPhoto()
    {
        return photo;
    }

    public void setPhoto(String photo)
    {
        this.photo = photo;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(userId);
        dest.writeString(photo);
        dest.writeString(fullName);
    }
}
