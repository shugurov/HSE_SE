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
    private final static String URL_BEGINNING = "http://promoteeducate1.appspot.com/api/structure/events/"; //TODO убрать отсюда
    private Event[] events;

    public EventScreen(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject);
        url = URL_BEGINNING + getKey();
    }

    @Override
    public FileDescription getFileDescription()
    {
        return new FileDescription(getKey(), getUrl());
    }

    public Event[] getEvents(Context context) throws JSONException
    {
        if (events == null)
        {
            FileManager fileManager = new FileManager(context);
            String content = fileManager.getFileContent(getKey());
            JSONObject jsonObject;
            jsonObject = new JSONObject(content);
            JSONArray jsonArray = jsonObject.getJSONArray("entries");
            events = new Event[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++)
            {
                events[i] = new Event(jsonArray.getJSONObject(i));
            }
            return events;
        } else
        {
            return events;
        }
    }
}
