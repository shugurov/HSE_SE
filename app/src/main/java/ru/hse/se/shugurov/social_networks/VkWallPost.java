package ru.hse.se.shugurov.social_networks;

import java.util.Date;

/**
 * Created by Иван on 02.05.2014.
 */
public class VkWallPost extends VKAbstractItem
{
    private String photoURL;

    public VkWallPost(VKProfile author, String text, Date date)
    {
        super(author, text, date);
    }

    public VkWallPost(VKProfile author, String text, Date date, String photoURL)
    {
        super(author, text, date);
        this.photoURL = photoURL;
    }

    public String getPhotoURL()
    {
        return photoURL;
    }
}
