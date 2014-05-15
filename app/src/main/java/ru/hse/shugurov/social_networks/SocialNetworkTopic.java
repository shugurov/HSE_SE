package ru.hse.shugurov.social_networks;


import android.os.Parcel;

import java.util.Date;

/**
 * Represents a social network topic. Every topics consist of a title, id, comments and optional attache picture
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class SocialNetworkTopic extends SocialNetworkEntry
{
    /**
     * Used for recreating objects after their serialization
     */
    public static final Creator<SocialNetworkTopic> CREATOR = new Creator<SocialNetworkTopic>()
    {
        @Override
        public SocialNetworkTopic createFromParcel(Parcel source)
        {
            return new SocialNetworkTopic(source);
        }

        @Override
        public SocialNetworkTopic[] newArray(int size)
        {
            return new SocialNetworkTopic[size];
        }
    };
    private String title;
    private String id;
    private int comments;
    private String attachedPicture;

    /**
     * Creates a new instance with all fields specified except attache picture
     *
     * @param title    a title of a topic
     * @param topicId  unique identifier
     * @param author   an author of a topic
     * @param text     text of a topic
     * @param comments a number of comments
     * @param date     a date whet it was published
     */
    public SocialNetworkTopic(String title, String topicId, SocialNetworkProfile author, String text, int comments, Date date)
    {
        super(author, text, date);
        this.id = topicId;
        this.comments = comments;
        this.title = title;
    }

    /**
     * Creates a new instance with all fields specified
     *
     * @param title           a title of a topic
     * @param id              unique identifier
     * @param author          an author of a topic
     * @param text            text of a topic
     * @param comments        a number of comments
     * @param date            a date whet it was published
     * @param attachedPicture a link to a picture
     */
    public SocialNetworkTopic(String title, String id, SocialNetworkProfile author, String text, int comments, Date date, String attachedPicture)
    {
        this(title, id, author, text, comments, date);
        this.attachedPicture = attachedPicture;
    }

    /**
     * Used for recreating objects after their serialization. All subclasses <strong>have to call it first</strong>
     */
    protected SocialNetworkTopic(Parcel source)
    {
        super(source);
        title = source.readString();
        id = source.readString();
        comments = source.readInt();
        attachedPicture = source.readString();
    }

    /**
     * @return a number of comments
     */
    public int getComments()
    {
        return comments;
    }

    /**
     * @return unique identifier of a post
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return a link to a picture
     */
    public String getAttachedPicture()
    {
        return attachedPicture;
    }

    /**
     * @return a mumber of comments plus a correct form of "комментарий"
     */
    public String getCommentsString()
    {
        return comments + " " + getCommentWord();
    }

    /*determines a correct form of "комментарий"*/
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

    /**
     * @return title
     */
    public String getTitle()
    {
        return title;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeString(id);
        dest.writeInt(comments);
        dest.writeString(attachedPicture);
    }
}
