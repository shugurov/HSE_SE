package ru.hse.se.shugurov.social_networks;

/**
 * Created by Иван on 14.02.14.
 */
public class SocialNetworkProfile
{
    private final String userId;
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

}
