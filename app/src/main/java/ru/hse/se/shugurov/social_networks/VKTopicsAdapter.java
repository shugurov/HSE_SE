package ru.hse.se.shugurov.social_networks;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.gui.FlexibleImageView;
import ru.hse.se.shugurov.utills.ImageLoader;


/**
 * Created by Иван on 13.02.14.
 */
public class VKTopicsAdapter extends BaseAdapter//TODO я вообще не использую количесто комментариев, наверное, стоит поправить
{
    private VKTopic[] topics;
    private LayoutInflater inflater;
    private ImageLoader imageLoader = ImageLoader.instance();
    private DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);

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
    public VKTopic getItem(int position)
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
        authorPhoto.setImageBitmap(null);
        float weightSum = ((LinearLayout) convertView).getWeightSum();
        int width = (int) ((parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight()) * (1 / weightSum));
        FlexibleImageView flexibleImage = new FlexibleImageView(authorPhoto, width);
        imageLoader.displayImage(topics[position].getAuthor().getPhoto(), flexibleImage);
        ((TextView) convertView.findViewById(R.id.vk_topic_author_name)).setText(Html.fromHtml(topics[position].getAuthor().getFullName()));
        ((TextView) convertView.findViewById(R.id.vk_topic_text)).setText(Html.fromHtml(topics[position].getText()));
        String date = format.format(getItem(position).getDate());
        ((TextView) convertView.findViewById(R.id.vk_date)).setText(date);
        return convertView;
    }
}
