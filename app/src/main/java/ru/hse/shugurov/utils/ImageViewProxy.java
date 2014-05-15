package ru.hse.shugurov.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Proxy class used to make sure that images downloaded from the Internet scale correctly. It takes actual instance of a {@code ImageView}
 * and redirects setImageBitmap method calls to this instance. The same goes for hashCode, equals and invalidate.
 * Other methods are not supported by this proxy class
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class ImageViewProxy extends ImageView
{
    private ImageView realImage;
    private int width;

    /**
     * Creates a new instance
     *
     * @param realImage actual image view which is displayed on a screen
     * @param width     a width of image view
     */
    public ImageViewProxy(ImageView realImage, int width)
    {
        super(realImage.getContext());
        this.realImage = realImage;
        this.width = width;
    }


    @Override
    /**
     * calculates aspect ration and sets a high to actual image view
     *
     */
    public void setImageBitmap(Bitmap bitmap)
    {
        double aspectRatio = ((double) bitmap.getHeight()) / bitmap.getWidth();
        int height = (int) Math.round(width * aspectRatio);
        realImage.getLayoutParams().height = height;
        realImage.setImageBitmap(bitmap);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof ImageViewProxy)
        {
            return realImage.equals(((ImageViewProxy) o).realImage);
        } else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return realImage.hashCode() + 15;
    }

    @Override
    public void invalidate()
    {
        realImage.invalidate();
    }
}
