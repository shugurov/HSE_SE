package ru.hse.se.shugurov.social_networks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import ru.hse.se.shugurov.utills.ImageLoader;

/**
 * Created by Иван on 02.05.2014.
 */
public class VkWallPostsAdapter extends BaseAdapter
{
    private LayoutInflater inflater;
    private ImageLoader imageLoader = ImageLoader.instance();
    private VkWallPost[] posts;

    public VkWallPostsAdapter(Context context, VkWallPost[] posts)
    {
        this.posts = posts;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return posts.length;
    }

    @Override
    public VkWallPost getItem(int position)
    {
        return posts[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        return null;
    }
}
