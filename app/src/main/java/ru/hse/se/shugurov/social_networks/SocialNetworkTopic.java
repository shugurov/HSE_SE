package ru.hse.se.shugurov.social_networks;


import java.util.Date;

/**
 * Created by Иван on 13.02.14.
 */
public class SocialNetworkTopic extends SocialNetworkEntry
{
    private String title;
    private String id;
    private int comments;
    private String attachedPicture;

    public SocialNetworkTopic(String title, String topicId, SocialNetworkProfile author, String text, int comments, Date date)
    {
        super(author, text, date);
        this.id = topicId;
        this.comments = comments;
        this.title = title;
    }

    public SocialNetworkTopic(String title, String id, SocialNetworkProfile author, String text, int comments, Date date, String attachedPicture)
    {
        this(title, id, author, text, comments, date);
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

    public String getCommentsString()
    {
        return comments + " " + getCommentWord();
    }

    private String getCommentWord()
    {
        String result = "";
        switch (comments % 10)
        {
            case 1:
                if (comments % 11 == 0)
                {
                    result = "комментариев";
                } else
                {
                    result = "комментарий";
                }
                break;
            case 2:
            case 3:
            case 4:
                boolean isUnusual = comments % 12 == 0 && comments % 13 == 0 && comments % 14 == 0;
                if (isUnusual)
                {
                    result = "комментариев";
                } else
                {
                    result = "комментария";
                }
                break;
            case 0:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                result = "комментариев";
                break;
        }
        return result;
    }

    public String getTitle()
    {
        return title;
    }
}
