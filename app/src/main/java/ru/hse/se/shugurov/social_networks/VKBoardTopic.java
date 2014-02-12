package ru.hse.se.shugurov.social_networks;

/**
 * Created by Иван on 11.02.14.
 */
public class VKBoardTopic
{
    private final String title;
    private final String numberOfComments;
    private final String authorName;
    private final String photoURL;
    private final String date;

    public VKBoardTopic(String title, String numberOfComments, String authorName, String photoURL, String date)
    {
        this.title = title;
        this.numberOfComments = numberOfComments;
        this.authorName = authorName;
        this.photoURL = photoURL;
        this.date = date;
    }

    public String getDate()
    {
        return date;
    }

    public String getTitle()
    {
        return title;
    }

    public String getNumberOfComments()
    {
        return numberOfComments;
    }

    public String getAuthorName()
    {
        return authorName;
    }

    public String getPhotoURL()
    {
        return photoURL;
    }
}
