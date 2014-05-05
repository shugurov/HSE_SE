package ru.hse.se.shugurov.social_networks;


import java.util.Date;

/**
 * Created by Иван on 13.02.14.
 */
public class SocialNetworkTopic extends SocialNetworkEntry
{
    private String id;
    private int comments;//TODO remove?
    private String attachedPicture;

    public SocialNetworkTopic(String topicId, SocialNetworkProfile author, String text, int comments, Date date)
    {
        super(author, text, date);
        this.id = topicId;
        this.comments = comments;
    }

    public SocialNetworkTopic(String id, SocialNetworkProfile author, String text, int comments, Date date, String attachedPicture)
    {
        this(id, author, text, comments, date);
        this.attachedPicture = attachedPicture;
    }

    public int getComments()
    {
        return comments;
    }

    public String getId()
    {
        return id;
    }

    public String getAttachedPicture()
    {
        return attachedPicture;
    }

}
