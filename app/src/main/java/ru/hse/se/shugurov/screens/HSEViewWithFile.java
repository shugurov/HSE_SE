package ru.hse.se.shugurov.screens;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 25.10.13.
 */
public class HSEViewWithFile extends HSEView implements HasFile
{
    private String fileType;
    private String fileName;

    HSEViewWithFile(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject);
        fileType = jsonObject.getString("filetype");
        fileName = jsonObject.getString("filename");
    }

    public String getFileName()
    {
        return fileName;
    }//TODO не использую никак

    public String getFileType()
    {
        return fileType;
    }//TODO не использую никак

    @Override
    public FileDescription getFileDescription()
    {
        return new FileDescription(fileName, SERVER_LINK + getUrl());
    }

}
