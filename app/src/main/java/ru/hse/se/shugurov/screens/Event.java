package ru.hse.se.shugurov.screens;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Class represents specific event
 * <p/>
 * Created byIvan Shugurov
 */
public class Event extends BaseScreen implements Serializable//TODO  должен ли он быть serializable
{
    private String telephone;
    private String address;
    private Date date;

    /**
     * Constructs event class reading fields from provided object
     *
     * @param eventObject json object which contains all essential fields for creating an event object
     * @throws JSONException if json object is incorrect
     */
    public Event(JSONObject eventObject) throws JSONException
    {
        super(eventObject);
        telephone = eventObject.getString("phone");
        address = eventObject.getString("address");
        long dateNumber = eventObject.getLong("date");
        date = new Date(dateNumber * 1000);
    }

    /**
     * Getter for telephone field. The field is instantiated in constructor
     *
     * @return telephone number
     */
    public String getTelephone()
    {
        return telephone;
    }

    /**
     * Getter for address field. The field is instantiated in constructor
     *
     * @return address
     */
    public String getAddress()
    {
        return address;
    }

    /**
     * Getter for date field. The field is instantiated in constructor and shows when this event is scheduled to happen
     *
     * @return telephone number
     */
    public Date getDate()
    {
        return date;
    }


}
