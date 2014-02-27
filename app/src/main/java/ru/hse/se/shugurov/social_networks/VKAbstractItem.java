package ru.hse.se.shugurov.social_networks;

import java.util.Date;

/**
 * Created by Иван on 27.02.14.
 */
public class VKAbstractItem
{
    private final VKProfile author;
    private final String text;
    private final Date date;

    public VKAbstractItem(VKProfile author, String text, Date date)
    {
        this.author = author;
        this.text = text;
        this.date = date;
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

}
