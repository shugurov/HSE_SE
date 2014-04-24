package ru.hse.se.shugurov.screens;

import java.util.Date;

/**
 * Created by Иван on 24.04.2014.
 */
public class Event
{
    private final String name;
    private final String url;
    private final String telephone;
    private final String address;
    private final Date date;
    private final String description;

    public Event(String name, String url, String telephone, String address, long date, String description)
    {
        this.name = name;
        this.url = url;
        this.telephone = telephone;
        this.address = address;
        this.date = new Date(date);
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public String getName()
    {
        return name;
    }

    public String getUrl()
    {
        return url;
    }

    public String getTelephone()
    {
        return telephone;
    }

    public String getAddress()
    {
        return address;
    }

    public Date getDate()
    {
        return date;
    }


}
