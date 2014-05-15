package ru.hse.shugurov.screens;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * used to describe a screen which should open a file in an external application via a mechanism of intents.
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class ScreenWithFile extends BaseScreen implements HasFile
{
    /**
     * Used for recreating objects after their serialization
     */
    public static final Creator<ScreenWithFile> CREATOR = new Creator<ScreenWithFile>()
    {
        @Override
        public ScreenWithFile createFromParcel(Parcel source)
        {
            return new ScreenWithFile(source);
        }

        @Override
        public ScreenWithFile[] newArray(int size)
        {
            return new ScreenWithFile[size];
        }
    };
    private String fileType;
    private String fileName;

    protected ScreenWithFile(JSONObject jsonObject, String serverUrl) throws JSONException
    {
        super(jsonObject, serverUrl);
        fileType = jsonObject.getString("filetype");
        fileName = jsonObject.getString("filename");
        url = serverUrl + url;
    }

    /**
     * Used for recreating objects after their serialization. All subclasses <strong>have to call it first</strong>
     */
    protected ScreenWithFile(Parcel source)
    {
        super(source);
    }

    /**
     * @return file name
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * @return file type
     */
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
