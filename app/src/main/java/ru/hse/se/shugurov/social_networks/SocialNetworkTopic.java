package ru.hse.se.shugurov.social_networks;


import android.os.Parcel;

import java.util.Date;

/**
 * Created by Иван on 13.02.14.
 */
public class SocialNetworkTopic extends SocialNetworkEntry
{
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

    private SocialNetworkTopic(Parcel source)
    {
        super(source);
        title = source.readString();
        id = source.readString();
        comments = source.readInt();
        attachedPicture = source.readString();
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
