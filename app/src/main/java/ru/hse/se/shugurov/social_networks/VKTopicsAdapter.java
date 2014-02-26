package ru.hse.se.shugurov.social_networks;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.utills.ImageLoader;


/**
 * Created by Иван on 13.02.14.
 */
public class VKTopicsAdapter extends BaseAdapter
{
    private VKTopic[] topics;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;

    public VKTopicsAdapter(Context context, VKTopic[] topics)
    {
        this.topics = topics;
        inflater = LayoutInflater.from(context);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount()
    {
        return topics.length;
    }

    @Override
    public Object getItem(int position)
    {
        return topics[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.vk_topic, parent, false);
        }
        ImageView authorPhoto = (ImageView) convertView.findViewById(R.id.vk_topic_author_photo);
        imageLoader.displayImage(topics[position].getAuthorPhoto(), authorPhoto);
        ((TextView) convertView.findViewById(R.id.vk_topic_author_name)).setText(Html.fromHtml(topics[position].getAuthorName()));
        ((TextView) convertView.findViewById(R.id.vk_topic_text)).setText(Html.fromHtml(topics[position].getText()));
        ((TextView) convertView.findViewById(R.id.vk_topic_comments)).setText(Integer.toString(topics[position].getComments()));
        ((TextView) convertView.findViewById(R.id.vk_topic_date)).setText(topics[position].getDate().toString());
        return convertView;
    }
}
