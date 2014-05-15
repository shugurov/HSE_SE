package ru.hse.shugurov.social_networks;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Represents a social network entry like a comment which has an author, a date of publishing and a text
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class SocialNetworkEntry implements Parcelable
{

    /**
     * Used for recreating objects after their serialization
     */
    public static Creator<SocialNetworkEntry> CREATOR = new Creator<SocialNetworkEntry>()
    {
        @Override
        public SocialNetworkEntry createFromParcel(Parcel source)
        {
            return new SocialNetworkEntry(source);
        }

        @Override
        public SocialNetworkEntry[] newArray(int size)
        {
            return new SocialNetworkTopic[size];
        }
    };

    private SocialNetworkProfile author;
    private String text;
    private Date date;


    /**
     * Creates a new instance with specified parameters
     *
     * @param author who published it
     * @param text
     * @param date   when it was published
     */
    public SocialNetworkEntry(SocialNetworkProfile author, String text, Date date)
    {
        this.author = author;
        this.text = text;
        this.date = date;
    }

    /**
     * Used for recreating objects after their serialization. All subclasses <strong>have to call it first</strong>
     */
    protected SocialNetworkEntry(Parcel source)
    {
        author = source.readParcelable(SocialNetworkProfile.class.getClassLoader());
        text = source.readString();
        date = new Date(source.readLong());
    }

    /**
     * @return a date when it was published
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * @return an author who published it
     */
    public SocialNetworkProfile getAuthor()
    {
        return author;
    }

    /**
     * @return a text of an entry
     */
    public String getText()
    {
        return text;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(author, flags);
        dest.writeString(text);
        dest.writeLong(date.getTime());
    }

}
