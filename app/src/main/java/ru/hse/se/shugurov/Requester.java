package ru.hse.se.shugurov;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Иван on 14.02.14.
 */
public class Requester extends AsyncTask<String, Void, String>
{
    private RequestResultCallback callback;

    public Requester(RequestResultCallback callback)
    {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params)
    {
        return downloadFromTheInternet(params[0]);
    }

    @Override
    protected void onPostExecute(String string)
    {
        callback.pushResult(string);
    }

    private String downloadFromTheInternet(String url)//TODo должен быть другой путь(
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
        void pushResult(String/**/ result);
    }
}
