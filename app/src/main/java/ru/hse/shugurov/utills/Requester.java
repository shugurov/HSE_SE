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
 *
 * @author Ivan Shugurov
 */
public class Requester extends AsyncTask<String, Void, String[]>
{
    private RequestResultCallback callback;
    private MultipleRequestResultCallback multipleRequestResultCallback;

    /**
     * Creates instance which downloads 1 item from the Internet
     *
     * @param callback used for notification when downloading is finished
     */
    public Requester(RequestResultCallback callback)
    {
        this.callback = callback;
    }

    /**
     * Creates instance which downloads a number of items from the Internet
     *
     * @param multipleRequestResultCallback used for notification when downloading is finished
     */
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

    /*downloads from the Internet*/
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
            while ((charRead = reader.read(inputBuffer)) != -1)
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

    /*opens internet connection, gets and returns input stream*/
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

    /**
     * Used as a callback for notifying of end of downloading.
     */
    public interface RequestResultCallback
    {
        /**
         * Pushes a single result. Generally, objects which implement this interface should check that
         * obtained value is not null
         *
         * @param result is null if downloading was not successful, otherwise not null.
         */
        void pushResult(String result);
    }

    /**
     * Used as a callback for notifying of end of downloading.
     */
    public interface MultipleRequestResultCallback
    {
        /**
         * Pushes a number of results. Generally, objects which implement this interface should check that
         * obtained array is not null
         *
         * @param results is null if downloading was not successful, otherwise not null.
         */
        void pushResult(String[] results);
    }
}
