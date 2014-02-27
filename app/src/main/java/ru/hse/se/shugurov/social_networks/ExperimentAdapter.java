package ru.hse.se.shugurov.social_networks;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ru.hse.se.shugurov.R;

/**
 * Created by Иван on 27.02.14.
 */
public class ExperimentAdapter extends BaseAdapter
{
    private LayoutInflater inflater;
    private VKAbstractItem[] comments;

    public ExperimentAdapter(Context context, VKAbstractItem[] comments)
    {
        this.comments = comments;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return comments.length;
    }

    @Override
    public Object getItem(int position)
    {
        return position;
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
            convertView = inflater.inflate(R.layout.experiment_layout, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.experiment_author_name)).setText(comments[position].getAuthor().getFullName());
        ((TextView) convertView.findViewById(R.id.experiment_date)).setText("date");
        ((TextView) convertView.findViewById(R.id.experiment_text)).setText(Html.fromHtml(comments[position].getText()));
        return convertView;
    }
}
