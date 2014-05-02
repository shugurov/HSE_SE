package ru.hse.se.shugurov.social_networks;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Иван on 27.02.14.
 */
public class VKAbstractItem implements Parcelable
{
    public static Creator<VKAbstractItem> CREATOR = new Creator<VKAbstractItem>()
    {
        @Override
        public VKAbstractItem createFromParcel(Parcel source)
        {
            return new VKAbstractItem(source);
        }

        @Override
        public VKAbstractItem[] newArray(int size)
        {
            return new VKTopic[size];
        }
    };
    private VKProfile author;
    private String text;
    private Date date;

    public VKAbstractItem(VKProfile author, String text, Date date)
    {
        this.author = author;
        this.text = text;
        this.date = date;
    }


    private VKAbstractItem(Parcel parcel)
    {
    }

    public Date getDate()
    {
        return date;
    }

    public VKProfile getAuthor()
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
