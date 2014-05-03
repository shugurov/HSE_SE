package ru.hse.se.shugurov.social_networks;


import java.util.Date;

/**
 * Created by Иван on 13.02.14.
 */
public class VKTopic extends VKAbstractItem
{
    private int id;
    private int comments;
    private String attachedPicture;

    public VKTopic(int id, VKProfile author, String text, int comments, Date date)
    {
        super(author, text, date);
        this.id = id;
        this.comments = comments;
    }

    public VKTopic(int id, VKProfile author, String text, int comments, Date date, String attachedPicture)
    {
        this(id, author, text, comments, date);
        this.attachedPicture = attachedPicture;
    }

    public int getComments()
    {
        return comments;
    }

    public int getId()
    {
        return id;
    }

    public String getAttachedPicture()
    {
        return attachedPicture;
    }

}
