package ru.hse.se.shugurov.social_networks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ru.hse.se.shugurov.R;


/**
 * Created by Иван on 13.02.14.
 */
public class VKTopicsAdapter extends BaseAdapter
{
    private VKTopic[] topics;
    private LayoutInflater inflater;

    public VKTopicsAdapter(Context context, VKTopic[] topics)
    {
        this.topics = topics;
        inflater = LayoutInflater.from(context);
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
            ((TextView) convertView.findViewById(R.id.vk_topic_author_name)).setText(topics[position].getAuthorName());
            ((TextView) convertView.findViewById(R.id.vk_topic_text)).setText(topics[position].getText());
            ((TextView) convertView.findViewById(R.id.vk_topic_comments)).setText(Integer.toString(topics[position].getComments()));
            //((TextView) convertView.findViewById(R.id.vk_topic_date)).setText(topics[position].getComments());


        }
        return convertView;
    }
}
