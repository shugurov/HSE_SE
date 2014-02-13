package ru.hse.se.shugurov.utills;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import ru.hse.se.shugurov.ViewsPackage.FileDescription;
import ru.hse.se.shugurov.observer.Observable;
import ru.hse.se.shugurov.observer.Observer;


/**
 * Created by Shugurov Ivan on 21.10.13.
 */
public class Downloader extends AsyncTask<FileDescription, Void, Void> implements Observable
{
    private ArrayList<Observer> observers = new ArrayList<Observer>();
    private ArrayList<FileDescription> fileDescriptions;
    private Context context;
    private FileManager fileManager;
    private DownloadStatus status;
    private String result;
    private String url;

    public Downloader(String url)
    {
        result = "";
        status = DownloadStatus.DOWNLOAD_WITHOUT_SAVING;
        this.url = url;
    }

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
        switch (status)
        {
            case DOWNLOAD_WITHOUT_SAVING:
                if (fileToDownloads == null)
                {
                    return null;
                } else
                {
                    result = downloadFromTheInternet(url);
                }
                break;
            default:
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

    private void downloadFile(FileDescription description)
    {
        String content = downloadFromTheInternet(description.getUrl());
        if (content != null)
        {
            fileManager.createFile(description.getName(), content);
        }
    }

    private InputStream OpenHttpUrlConnection(String urlString)
    {
        InputStream input = null;
        int response = -1;
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

    public String getResult()
    {
        return result;
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
}
