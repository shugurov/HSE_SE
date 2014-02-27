package ru.hse.se.shugurov.social_networks;

import java.util.Date;

/**
 * Created by Иван on 13.02.14.
 */
public class VKTopic extends VKAbstractItem
{
    private final int topicID;
    private final int comments;

    public VKTopic(int topicID, VKProfile author, String text, int comments, Date date)
    {
        super(author, text, date);
        this.topicID = topicID;
        this.comments = comments;
    }

    public int getComments()
    {
        return comments;
    }

    public int getTopicID()
    {
        return topicID;
    }

}
