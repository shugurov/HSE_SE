package ru.hse.shugurov.utills;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
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
 * Used to download and cache images. Uses LRUCache to do it. Getting request to show a picture it checks it is is present in cache.
 * If present then immediately displays it, otherwise it starts another thread and download a picture.
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class ImageLoader
{
    private static ImageLoader instance;
    private LruCache<String, Bitmap> memoryCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    private ImageLoader(Context context)
    {
        int memoryClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024 * 1024 * memoryClass / 8;
        memoryCache = new LruCache(cacheSize);
    }

    /**
     * initialize a new instance even if {@code instance} field is not null
     *
     * @param context used to get memory space
     */
    public static void initialize(Context context)
    {
        instance = new ImageLoader(context);
    }

    /**
     * Used to retrieve created instance of a class instead of creating it every time it is needed.
     *
     * @return instance of ImageLoader
     */
    public static ImageLoader instance()
    {
        return instance;
    }

    /**
     * If a picture is present in cache then displays it, otherwise downloads and displays it.
     *
     * @param url
     * @param imageView
     */
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

    /*requests photo downloading*/
    private void queuePhoto(String url, ImageView imageView)
    {
        new LoadBitmapTask().execute(new PhotoToLoad(url, imageView));
    }

    /*reads bitmap from input stream*/
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

    /*checks if image view is reused*/
    private boolean imageViewReused(PhotoToLoad photoToLoad)
    {
        String tag = imageViews.get(photoToLoad.getImageView());
        return tag == null || !tag.equals(photoToLoad.getUrl());
    }

    /*opens Internet connection and gets input stream*/
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

    /*describes a pair of url and imageView*/
    private class PhotoToLoad
    {
        private String url;
        private ImageView imageView;

        private PhotoToLoad(String url, ImageView imageView)
        {
            this.url = url;
            this.imageView = imageView;
        }

        /**
         * @return instance of ImageView which should display a picture
         */
        public ImageView getImageView()
        {
            return imageView;
        }

        /**
         * @return url to a picture object
         */
        public String getUrl()
        {
            return url;
        }

    }

    /*downloads images in separate thread*/
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
