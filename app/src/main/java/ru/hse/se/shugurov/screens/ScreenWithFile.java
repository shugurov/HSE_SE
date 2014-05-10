package ru.hse.se.shugurov.screens;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 25.10.13.
 */
public class ScreenWithFile extends BaseScreen implements HasFile
{
    private String fileType;
    private String fileName;

    ScreenWithFile(JSONObject jsonObject, String serverUrl) throws JSONException
    {
        super(jsonObject, serverUrl);
        fileType = jsonObject.getString("filetype");
        fileName = jsonObject.getString("filename");
        url = serverUrl + url;
    }

    public String getFileName()
    {
        return fileName;
    }

    public String getFileType()
    {
        return fileType;
    }

    @Override
    public FileDescription getFileDescription()
    {
        return new FileDescription(fileName, getUrl());
    }

}
