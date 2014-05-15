package ru.hse.shugurov.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ru.hse.shugurov.R;
import ru.hse.shugurov.screens.RSSScreen;

/**
 * Provides items for {@code ListView}
 * <p/>
 * Created by Ivan Shugurov
 */
public class RSSListAdapter extends BaseAdapter
{
    private RSSScreen[] rssItems;
    private LayoutInflater inflater;

    /**
     * Constructs a new adapter
     *
     * @param context  activity used to inflate views. not null
     * @param rssItems array of rss items to be shown. not null
     */
    public RSSListAdapter(Context context, RSSScreen[] rssItems)
    {
        this.rssItems = rssItems;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return rssItems.length;
    }

    @Override
    public RSSScreen getItem(int position)
    {
        return rssItems[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        RSSScreen currentRssElement = getItem(position);
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.rss_item, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.rss_item_title)).setText(currentRssElement.getTitle());
        ((TextView) convertView.findViewById(R.id.rss_item_text)).setText(currentRssElement.getSummary());
        return convertView;
    }
}
