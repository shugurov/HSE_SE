package ru.hse.se.shugurov.social_networks;

/**
 * Created by Иван on 13.02.14.
 */
public class VKTopic
{
    private final String authorPhoto;
    private final String authorName;
    private final String text;
    private final String comments;
    private final String date;

    public VKTopic(String authorPhoto, String authorName, String text, String comments, String date)
    {
        this.authorPhoto = authorPhoto;
        this.authorName = authorName;
        this.text = text;
        this.comments = comments;
        this.date = date;
    }

    public String getDate()
    {
        return date;
    }

    public String getAuthorPhoto()
    {
        return authorPhoto;
    }

    public String getAuthorName()
    {
        return authorName;
    }

    public String getText()
    {
        return text;
    }

    public String getComments()
    {
        return comments;
    }

}
