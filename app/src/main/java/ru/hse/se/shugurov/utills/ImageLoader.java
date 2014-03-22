package ru.hse.se.shugurov.utills;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by Иван on 15.01.14.
 */
public class ImageLoader
{
    private LruCache<String, Bitmap> memoryCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    public ImageLoader(Context context)
    {
        int memoryClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024 * 1024 * memoryClass / 8;
        memoryCache = new LruCache(cacheSize);
    }

    public void displayImage(String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
        {
            imageView.setImageBitmap(bitmap);
        } else
        {
            queuePhoto(url, imageView);
        }
    }

    private void queuePhoto(String url, ImageView imageView)
    {
        new LoadBitmapTask().execute(new PhotoToLoad(url, imageView));
    }

    private Bitmap getBitmap(String url)
    {
        Bitmap bitmap = null;
        InputStream input = openInputStream(url);
        if (input != null)
        {
            bitmap = BitmapFactory.decodeStream(input);
        }
        return bitmap;
    }

    private boolean imageViewReused(PhotoToLoad photoToLoad)
    {
        String tag = imageViews.get(photoToLoad.getImageView());
        return tag == null || !tag.equals(photoToLoad.getUrl());
    }

    private InputStream openInputStream(String urlString)
    {
        InputStream inputStream = null;
        try
        {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            if (!(connection instanceof HttpURLConnection))
            {
                return null;
            }
            try
            {
                connection.connect();
            } catch (Exception ex)
            {
                return null;
            }
            int response = ((HttpURLConnection) connection).getResponseCode();
            if (response == HttpURLConnection.HTTP_OK)
            {
                inputStream = connection.getInputStream();
            }

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return inputStream;
    }

    private class PhotoToLoad
    {
        private String url;
        private ImageView imageView;

        private PhotoToLoad(String url, ImageView imageView)
        {
            this.url = url;
            this.imageView = imageView;
        }

        public ImageView getImageView()
        {
            return imageView;
        }

        public String getUrl()
        {
            return url;
        }

    }

    private class LoadBitmapTask extends AsyncTask<PhotoToLoad, Void, Bitmap>
    {
        private PhotoToLoad photoToLoad;

        @Override
        protected Bitmap doInBackground(PhotoToLoad[] params)
        {
            photoToLoad = params[0];
            if (imageViewReused(photoToLoad))
            {
                return null;
            } else
            {
                Bitmap bitmap = getBitmap(photoToLoad.getUrl());
                if (bitmap == null)
                {
                    return null;
                } else
                {
                    memoryCache.put(photoToLoad.getUrl(), bitmap);
                    return bitmap;

                }
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            if (!imageViewReused(photoToLoad))
            {
                if (bitmap != null)
                {
                    photoToLoad.getImageView().setImageBitmap(bitmap);
                }
            }
        }
    }
}
