package ru.hse.se.shugurov.utills;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Иван on 25.02.14.
 */
public class FlexibleImageView extends ImageView
{
    private ImageView realImage;
    private int width;

    public FlexibleImageView(ImageView realImage, int width)
    {
        super(realImage.getContext());
        this.realImage = realImage;
        this.width = width;
    }


    @Override
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
        if (o instanceof FlexibleImageView)
        {
            return realImage.equals(((FlexibleImageView) o).realImage);
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
