package ru.hse.shugurov.utills;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Makes requests to social networks.
 * <p/>
 * Created by Ivan Shugurov
 */
public class Requester extends AsyncTask<String, Void, String[]>
{
    private RequestResultCallback callback;
    private MultipleRequestResultCallback multipleRequestResultCallback;

    public Requester(RequestResultCallback callback)
    {
        this.callback = callback;
    }

    public Requester(MultipleRequestResultCallback multipleRequestResultCallback)
    {
        this.multipleRequestResultCallback = multipleRequestResultCallback;
    }

    @Override
    protected String[] doInBackground(String[] params)
    {
        String[] result = new String[params.length];
        for (int i = 0; i < params.length; i++)
        {
            result[i] = downloadFromTheInternet(params[i]);
            if (result[i] == null)
            {
                return null;
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String[] result)
    {
        if (callback != null)
        {
            if (result == null)
            {
                callback.pushResult(null);
            } else
            {
                callback.pushResult(result[0]);
            }
        } else if (multipleRequestResultCallback != null)
        {
            multipleRequestResultCallback.pushResult(result);
        }
    }

    private String downloadFromTheInternet(String url)
    {
        InputStream input = OpenHttpUrlConnection(url);
        if (input == null)
        {
            return null;
        }
        int BUFFER_SIZE = 2000;
        InputStreamReader reader = new InputStreamReader(input);
        char[] inputBuffer = new char[BUFFER_SIZE];
        int charRead;
        String content = "";
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
            return null;
        }
        return content;
    }

    private InputStream OpenHttpUrlConnection(String urlString)
    {
        InputStream input = null;
        int response;
        try
        {
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            if (!(urlConnection instanceof HttpURLConnection))
            {
                return null;
            }
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.connect();
            response = httpURLConnection.getResponseCode();
            if (response == httpURLConnection.HTTP_OK)
            {
                input = httpURLConnection.getInputStream();
            }
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return input;
    }

    public interface RequestResultCallback
    {
        void pushResult(String result);
    }

    public interface MultipleRequestResultCallback
    {
        void pushResult(String[] results);
    }
}
