package ru.hse.shugurov.utils;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import ru.hse.shugurov.screens.FileDescription;


/**
 * Downloads files and stores them in a local storage
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class Downloader extends AsyncTask<FileDescription, Void, Void>
{
    private Collection<FileDescription> fileDescriptions;
    private Context context;
    private DownloadCallback callback;

    /**
     * Creates a new instance which will download from links supplied as execute method parameters
     *
     * @param context  is used to create files if local storage
     * @param callback notifies in the end of downloading. null is acceptable
     */
    public Downloader(Context context, DownloadCallback callback)
    {
        this.context = context;
        this.callback = callback;
    }

    /**
     * Creates a new instance which will download from links supplied as execute method parameters
     * and files supplied in {@code fileDescription} collection
     *
     * @param context          is used to create files if local storage
     * @param fileDescriptions descriptions of files to be downloaded
     * @param callback         notifies in the end of downloading. null is acceptable
     */
    public Downloader(Context context, Collection<FileDescription> fileDescriptions, DownloadCallback callback)
    {
        this(context, callback);
        this.fileDescriptions = fileDescriptions;
    }

    @Override
    protected Void doInBackground(FileDescription... fileToDownloads)
    {
        if (fileToDownloads.length != 0)
        {
            for (FileDescription description : fileToDownloads)
            {
                if (isCancelled())
                {
                    return null;
                }
                downloadFile(description);
            }
        }
        if (fileDescriptions != null)
        {
            for (FileDescription description : fileDescriptions)
            {
                if (isCancelled())
                {
                    return null;
                }
                downloadFile(description);
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        if (callback != null && !isCancelled())
        {
            callback.downloadFinished();
        }
    }


    /*downloads and stores a file*/
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

    public interface DownloadCallback
    {
        void downloadFinished();
    }
}
