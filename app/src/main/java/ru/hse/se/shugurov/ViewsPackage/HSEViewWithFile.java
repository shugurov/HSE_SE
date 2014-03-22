package ru.hse.se.shugurov.ViewsPackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 25.10.13.
 */
public class HSEViewWithFile extends HSEView implements HasFile
{
    private String fileType;
    private String fileName;

    HSEViewWithFile(JSONObject jsonObject, String index)
    {
        super(jsonObject, index);
        try
        {
            fileType = jsonObject.getString("filetype");
        } catch (JSONException e)
        {
            e.printStackTrace();
            fileType = "";
        }
        try
        {
            fileName = jsonObject.getString("filename");
        } catch (JSONException e)
        {
            e.printStackTrace();
            fileName = "";
        }
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
        return new FileDescription(fileName, SERVER_LINK + getUrl());
    }
}
