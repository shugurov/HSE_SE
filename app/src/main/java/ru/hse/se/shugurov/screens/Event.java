package ru.hse.se.shugurov.screens;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Class represents specific event
 * <p/>
 * Created byIvan Shugurov
 */
public class Event extends BaseScreen
{
    public static final Creator<Event> CREATOR = new Creator<Event>()
    {
        @Override
        public Event createFromParcel(Parcel source)
        {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size)
        {
            return new Event[size];
        }
    };
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

    private Event(Parcel source)
    {
        super(source);
        telephone = source.readString();
        address = source.readString();
        date = new Date(source.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeString(telephone);
        dest.writeString(address);
        dest.writeLong(date.getTime());
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
