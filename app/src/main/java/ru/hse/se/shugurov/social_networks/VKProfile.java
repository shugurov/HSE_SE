package ru.hse.se.shugurov.social_networks;

/**
 * Created by Иван on 14.02.14.
 */
public class VKProfile
{
    private final int userID;
    private final String photo;
    private final String fullName;

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

    public String getFullName()
    {
        return fullName;
    }

}
