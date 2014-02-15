package ru.hse.se.shugurov.social_networks;

/**
 * Created by Иван on 14.02.14.
 */
public class VKUser
{
    private final int userID;
    private final String firstName;
    private final String lastName;
    private final String photo;

    public VKUser(int userID, String firstName, String lastName, String photo)
    {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
    }

    public String getLastName()
    {
        return lastName;
    }

    public int getUserID()
    {
        return userID;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getPhoto()
    {
        return photo;
    }

}
