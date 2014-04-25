package ru.hse.se.shugurov.utills;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import ru.hse.se.shugurov.observer.Observable;
import ru.hse.se.shugurov.observer.Observer;
import ru.hse.se.shugurov.screens.FileDescription;


/**
 * Created by Shugurov Ivan on 21.10.13.
 */
public class Downloader extends AsyncTask<FileDescription, Void, Void> implements Observable
{
    private ArrayList<Observer> observers = new ArrayList<Observer>();
    private ArrayList<FileDescription> fileDescriptions;
    private FileManager fileManager;
    private DownloadStatus status;
    private Context context;

    public Downloader(Context context, DownloadStatus status)
    {
        this.context = context;
        fileManager = new FileManager(context);
        this.status = status;
    }

    public Downloader(Context context, ArrayList<FileDescription> fileDescriptions, DownloadStatus status)
    {
        this.context = context;
        this.fileDescriptions = fileDescriptions;
        fileManager = new FileManager(context);
        this.status = status;
    }

    @Override
    protected Void doInBackground(FileDescription... fileToDownloads)
    {
        if (fileToDownloads.length != 0)
        {
            for (FileDescription description : fileToDownloads)
            {
                downloadFile(description);
                Log.d("download", description.getName());
            }
        } else
        {
            if (fileDescriptions != null)
            {
                for (FileDescription description : fileDescriptions)
                {
                    Log.d("download", "check " + description.getName());
                    if (!fileManager.doesExist(description.getName()))
                    {
                        downloadFile(description);
                        Log.d("download", "download " + description.getName());
                    }
                }//TODO удалять старые файлы, но не тут
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        this.notifyObservers();
    }

   /* private InputStream OpenHttpUrlConnection(String urlString) TODO
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
    }*/

    @Override
    public void addObserver(Observer observer)
    {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer)
    {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers()
    {
        for (Observer observer : observers)
        {
            observer.update();
        }
    }

    public DownloadStatus getDownloadStatus()
    {
        return status;
    }

//    private String downloadFromTheInternet(String url) TODO
//    {
//        InputStream input = OpenHttpUrlConnection(url);
//        if (input == null)
//        {
//            return null;
//        }
//        int BUFFER_SIZE = 2000;//
//        InputStreamReader reader = new InputStreamReader(input);
//        char[] inputBuffer = new char[BUFFER_SIZE];
//        int charRead;
//        String content = "";
//        try
//        {
//            while ((charRead = reader.read(inputBuffer)) > 0)
//            {
//                String readString = String.copyValueOf(inputBuffer, 0, charRead);
//                content += readString;
//            }
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//            try
//            {
//                input.close();
//            } catch (IOException e1)
//            {
//                e1.printStackTrace();
//            }
//            return null;
//        }
//        return content;
//    }

    private void downloadFile(FileDescription description)
    {
        HttpClient client = new DefaultHttpClient();
        try
        {
            HttpEntity entity = client.execute(new HttpGet(description.getUrl())).getEntity();
            OutputStream outputStream = context.openFileOutput(description.getName(), Context.MODE_WORLD_READABLE);
            entity.writeTo(outputStream);
            outputStream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
