package ru.hse.se.shugurov.social_networks;

/**
 * Created by Иван on 14.02.14.
 */
public class VKProfile
{
    private final int userID;
    private String photo;
    private String fullName;

    public VKProfile(int userID)
    {
        this.userID = userID;
    }

    public VKProfile(int userID, String fullName, String photo)
    {
        this.userID = userID;
        this.photo = photo;
        this.fullName = fullName;
    }

    public int getId()
    {
        return userID;
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
