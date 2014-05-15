package ru.hse.shugurov.utills;

import android.content.Context;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Used for convenient reading from a file. Design is strongly influenced by singleton design pattern
 * <p/>
 * Created by Иван Ivan Shugurov
 */
public class FileManager
{
    private static FileManager fileManager;
    private Context context;

    /**
     * Creates a new instance which uses supplied context object to store files in a local storage
     *
     * @param context used to gain access to a local storage.
     */
    private FileManager(Context context)
    {
        this.context = context;
    }

    /**
     * used to retrieve created instance of a class instead of creating it every time it is needed.
     *
     * @return file manager
     */
    public static FileManager instance()
    {
        return fileManager;
    }

    /**
     * initialize a new instance even if {@code fileManager} field is not null
     *
     * @param context used to gain access to a local storage.
     */
    public static void initialize(Context context)
    {
        fileManager = new FileManager(context);
    }

    /**
     * Reads a file specified by its name
     *
     * @param name name of a file in a local storage
     * @return content of a file
     * @throws IOException if some i/o errors occur
     */
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