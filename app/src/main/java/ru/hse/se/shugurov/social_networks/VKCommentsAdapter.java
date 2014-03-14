package ru.hse.se.shugurov.social_networks;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.utills.ImageLoader;

/**
 * Created by Иван on 26.02.14.
 */
public class VKCommentsAdapter extends BaseAdapter
{
    private LayoutInflater inflater;
    private VKAbstractItem[] comments;
    private ImageLoader imageLoader;

    public VKCommentsAdapter(Context context, VKAbstractItem[] comments)
    {
        this.comments = comments;
        inflater = LayoutInflater.from(context);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount()
    {
        return comments.length;
    }

    @Override
    public VKAbstractItem getItem(int position)
    {
        return comments[position];
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
            convertView = inflater.inflate(R.layout.vk_comment, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.vk_comment_author_name)).setText(comments[position].getAuthor().getFullName());
        ((TextView) convertView.findViewById(R.id.vk_comment_date)).setText(comments[position].getDate().toString());
        ((TextView) convertView.findViewById(R.id.vk_comment_text)).setText(Html.fromHtml(comments[position].getText()));
        return convertView;
    }
}
