package ru.hse.se.shugurov.social_networks;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a user of a social network. Every profile consists of an univ identifier, a link to a photo and a full name
 * <p/>
 * Created by Ivan Shugurov
 */
public class SocialNetworkProfile implements Parcelable
{
    /**
     * Used for recreating objects after their serialization
     */
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

    /**
     * Creates a profile with only id specified
     *
     * @param userId unique identifier of a user
     */
    public SocialNetworkProfile(String userId)
    {
        this.userId = userId;
    }

    /**
     * Creates a profile with all fields specified
     *
     * @param userId   unique identifier of a user
     * @param fullName a name of a user
     * @param photo    a link to a photo
     */
    public SocialNetworkProfile(String userId, String fullName, String photo)
    {
        this(userId, fullName);
        this.photo = photo;
    }

    /**
     * Creates an instance with unknown photo url
     *
     * @param userId   unique identifier of a user
     * @param fullName a name of a user
     */
    public SocialNetworkProfile(String userId, String fullName)
    {
        this.userId = userId;
        this.fullName = fullName;
    }

    /**
     * Used for recreating objects after their serialization. All subclasses <strong>have to call it first</strong>
     */
    protected SocialNetworkProfile(Parcel source)
    {
        userId = source.readString();
        photo = source.readString();
        fullName = source.readString();
    }

    /**
     * @return unique identifier of a user
     */
    public String getId()
    {
        return userId;
    }

    /**
     * @return a link to a photo of a user
     */
    public String getPhoto()
    {
        return photo;
    }

    /**
     * Allows to set or change a photo after object creation
     *
     * @param photo a link to a photo of a user
     */
    public void setPhoto(String photo)
    {
        this.photo = photo;
    }

    /**
     * @return a name of a user
     */
    public String getFullName()
    {
        return fullName;
    }

    /**
     * Allows to set or change a full name after object creation
     *
     * @param fullName a name of a user
     */
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
