package ru.hse.se.shugurov.social_networks;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Иван on 27.02.14.
 */
public class SocialNetworkEntry implements Parcelable
{

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


    public SocialNetworkEntry(SocialNetworkProfile author, String text, Date date)
    {
        this.author = author;
        this.text = text;
        this.date = date;
    }

    private SocialNetworkEntry(Parcel parcel)
    {
    }

    public Date getDate()
    {
        return date;
    }

    public SocialNetworkProfile getAuthor()
    {
        return author;
    }

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

    }

}
