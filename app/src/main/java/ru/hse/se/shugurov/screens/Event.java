package ru.hse.se.shugurov.screens;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Иван on 24.04.2014.
 */
public class Event extends HSEView implements Serializable
{
    private String telephone;
    private String address;
    private Date date;

    public Event(JSONObject eventObject) throws JSONException
    {
        super(eventObject);
        url = eventObject.getString("url");//TODO what for?
        telephone = eventObject.getString("phone");
        address = eventObject.getString("address");
        long dateNumber = eventObject.getLong("date");
        date = new Date(dateNumber * 1000);
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
