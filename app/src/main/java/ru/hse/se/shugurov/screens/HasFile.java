package ru.hse.se.shugurov.screens;

import android.content.Context;

import org.json.JSONException;

/**
 * Created by Иван on 25.10.13.
 */
public interface HasFile
{
    public FileDescription getFileDescription();

    public void notifyAboutFiles(Context context) throws JSONException;
}