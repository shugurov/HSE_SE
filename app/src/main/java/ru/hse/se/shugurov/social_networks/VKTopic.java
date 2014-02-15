package ru.hse.se.shugurov.social_networks;

import java.util.Date;

/**
 * Created by Иван on 13.02.14.
 */
public class VKTopic
{
    private final int topicID;
    private final VKUser author;
    private final String text;
    private final int comments;
    private final Date date;

    public VKTopic(int topicID, VKUser author, String text, int comments, Date date)
    {
        this.topicID = topicID;
        this.author = author;
        this.text = text;
        this.comments = comments;
        this.date = date;
    }

    public Date getDate()
    {
        return date;
    }

    public String getAuthorPhoto()
    {
        return author.getPhoto();
    }

    public String getAuthorName()
    {
        return author.getFirstName() + " " + author.getLastName();
    }

    public VKUser getAuthor()
    {
        return author;
    }

    public String getText()
    {
        return text;
    }

    public int getComments()
    {
        return comments;
    }

}
