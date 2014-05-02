package ru.hse.se.shugurov.screens;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.hse.se.shugurov.utills.FileManager;

/**
 * Created by Иван on 24.04.2014.
 */
public class EventScreen extends HSEView implements HasFile
{
    public EventScreen(JSONObject jsonObject, String serverURL) throws JSONException
    {
        super(jsonObject);
        url = serverURL + "/api/structure/events/" + getKey();
    }

    @Override
    public FileDescription getFileDescription()
    {
        return new FileDescription(getKey(), getUrl());
    }

    public Event[] getEvents(Context context) throws JSONException
    {
        FileManager fileManager = new FileManager(context);
        String content = fileManager.getFileContent(getKey());
        JSONObject jsonObject;
        jsonObject = new JSONObject(content);
        JSONArray jsonArray = jsonObject.getJSONArray("events");
        Event[] events = new Event[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++)
        {
            events[i] = new Event(jsonArray.getJSONObject(i));
        }
        return events;
    }
}
