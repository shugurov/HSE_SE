package ru.hse.shugurov.screens;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.hse.shugurov.utills.FileManager;

/**
 * Used to describe a screen with events
 * <p/>
 * Created by Ivan Shugurov
 */
public class EventScreen extends BaseScreen implements HasFile
{
    /**
     * Used for recreating objects after their serialization
     */
    public static final Creator<EventScreen> CREATOR = new Creator<EventScreen>()
    {
        @Override
        public EventScreen createFromParcel(Parcel source)
        {
            return new EventScreen(source);
        }

        @Override
        public EventScreen[] newArray(int size)
        {
            return new EventScreen[size];
        }
    };

    /**
     * Reads data from provided json object and creates a new instance
     *
     * @param jsonObject object which contains all necessary fields to create this class
     * @param serverURL  lin to a server which provides api for getting events
     * @throws JSONException
     */
    public EventScreen(JSONObject jsonObject, String serverURL) throws JSONException
    {
        super(jsonObject);
        url = serverURL + "/api/structure/events/" + getKey();
    }

    /**
     * Used for recreating objects after their serialization. All subclasses <strong>have to call it first</strong>
     */
    protected EventScreen(Parcel input)
    {
        super(input);
    }

    @Override
    public FileDescription getFileDescription()
    {
        return new FileDescription(getKey(), getUrl(), true);
    }

    /**
     * Reads files which contains events, parses them and returns
     *
     * @return array of events. Array has 0 elements if i/o exception occurs
     * @throws JSONException if json stored in file has errors
     */
    public Event[] getEvents() throws JSONException
    {
        FileManager fileManager = FileManager.instance();
        String content;
        try
        {
            content = fileManager.getFileContent(getKey());
            JSONObject jsonObject;
            jsonObject = new JSONObject(content);
            JSONArray jsonArray = jsonObject.getJSONArray("events");
            Event[] events = new Event[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++)
            {
                events[i] = new Event(jsonArray.getJSONObject(i));
            }
            return events;
        } catch (IOException e)
        {
            return new Event[0];
        }
    }
}
