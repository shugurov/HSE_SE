package ru.hse.se.shugurov.utills;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by Иван on 24.10.13.
 */
public class FileManager
{
    Context context;

    public FileManager(Context context)
    {
        this.context = context;
    }

    private static <T> boolean doesArrayContainsElement(T element, T[] array)//TODO а зачем туи обобщённый метод?
    {
        for (T value : array)
        {
            if (element.equals(value))
            {
                return true;
            }
        }
        return false;
    }

    public boolean doesExist(String name)
    {
        try
        {
            FileInputStream fileInputStream = context.openFileInput(name);
            fileInputStream.close();
            return true;
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteFile(String name)
    {
        return context.deleteFile(name);
    }

    public boolean createFile(String name, ArrayList<String> content)
    {
        try
        {
            FileOutputStream outputStream = context.openFileOutput(name, Context.MODE_WORLD_READABLE);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            for (String contentString : content)
            {
                writer.write(contentString);
            }
            writer.flush();
            writer.close();
            outputStream.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean createFile(String name, String content)
    {
        try
        {
            FileOutputStream outputStream = context.openFileOutput(name, Context.MODE_WORLD_READABLE);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            writer.write(content);
            writer.flush();
            writer.close();
            outputStream.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public FileOutputStream create(String name)
    {
        FileOutputStream outputStream = null;
        try
        {
            outputStream = context.openFileOutput(name, Context.MODE_WORLD_READABLE);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return outputStream;
    }

    public void deleteOldFiles(String[] newFiles)
    {
        String[] files = context.fileList();
        for (String file : files)
        {
            if (!doesArrayContainsElement(file, newFiles))
            {
                deleteFile(file);
            }
        }
    }

    public String[] getListOfFiles()
    {
        return context.fileList();
    }

    public int deleteAllFiles()
    {
        int count = 0;
        String[] files = context.fileList();
        for (String file : files)
        {
            context.deleteFile(file);
            count++;
        }
        return count;
    }

    public FileInputStream openFile(String name)
    {
        try
        {
            return context.openFileInput(name);
        } catch (FileNotFoundException e)
        {
            return null;
        }
    }

    public String getFileContent(String name)
    {
        String content = "";
        FileInputStream input = openFile(name);
        if (input == null)
        {
            return "";
        } else
        {
            int BUFFER_SIZE = 2000;
            InputStreamReader reader = new InputStreamReader(input);
            char[] inputBuffer = new char[BUFFER_SIZE];
            int charRead;

            try
            {
                while ((charRead = reader.read(inputBuffer)) > 0)
                {
                    String readString = String.copyValueOf(inputBuffer, 0, charRead);
                    content += readString;
                    inputBuffer = new char[BUFFER_SIZE];
                }
            } catch (IOException e)
            {
                e.printStackTrace();
                try
                {
                    input.close();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
        return content;
    }
}
