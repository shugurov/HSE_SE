package ru.hse.se.shugurov.social_networks;


import java.util.Date;

/**
 * Created by Иван on 13.02.14.
 */
public class SocialNetworkTopic extends SocialNetworkEntry
{
    private String id;
    private int comments;
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

    public String getCommentsString()
    {
        return comments + " " + getCommentWord();
    }

    private String getCommentWord()
    {
        String result = "";
        switch (comments % 100)
        {
            case 6:
            case 16:
            case 26:
            case 36:
            case 46:
            case 56:
            case 66:
            case 76:
            case 86:
            case 96:
            case 7:
            case 17:
            case 27:
            case 37:
            case 47:
            case 57:
            case 67:
            case 77:
            case 87:
            case 97:
            case 8:
            case 18:
            case 28:
            case 38:
            case 48:
            case 58:
            case 68:
            case 78:
            case 88:
            case 98:
            case 9:
            case 19:
            case 29:
            case 39:
            case 49:
            case 59:
            case 69:
            case 79:
            case 89:
            case 99:
                result = "комментариев";
                break;
            case 3:
            case 23:
            case 33:
            case 43:
            case 53:
            case 63:
            case 73:
            case 83:
            case 93:
            case 4:
            case 24:
            case 34:
            case 44:
            case 54:
            case 64:
            case 74:
            case 84:
            case 94:
                result = "комментария";
                break;
            case 13:
                break;
            case 14:
                break;
        }

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
}
