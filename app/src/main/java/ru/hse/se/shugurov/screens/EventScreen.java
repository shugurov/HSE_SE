package ru.hse.se.shugurov.screens;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 24.04.2014.
 */
public class EventScreen extends HSEView implements HasFile
{
    private final static String URL_BEGINNING = "http://promoteeducate1.appspot.com/api/structure/events/"; //TODO убрать отсюда

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

    @Override
    public void notifyAboutFiles(Context context) throws JSONException
    {

    }
}
