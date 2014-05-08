package ru.hse.se.shugurov.utills;

import android.content.Context;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Иван on 24.10.13.
 */
public class FileManager
{
    private static FileManager fileManager;
    private Context context;

    private FileManager(Context context)
    {
        this.context = context;
    }

    public static FileManager instance()
    {
        return fileManager;
    }

    public static void initialize(Context context)
    {
        fileManager = new FileManager(context);
    }

    public String getFileContent(String name) throws IOException
    {
        InputStreamReader inputStream = new InputStreamReader(context.openFileInput(name));
        StringBuilder builder = new StringBuilder();
        try
            {
                char[] buffer = new char[2048];
                int charsRead;
                while ((charsRead = inputStream.read(buffer)) > -1)
                {
                    builder.append(buffer, 0, charsRead);
                }
            } finally
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
        return builder.toString();
    }
}