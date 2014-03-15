package ru.hse.se.shugurov.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.ViewsPackage.HSEViewRSS;

/**
 * Created by Иван on 15.03.14.
 */
public class RSSListAdapter extends BaseAdapter
{
    private HSEViewRSS[] rssItems;
    private LayoutInflater inflater;

    public RSSListAdapter(Context context, HSEViewRSS[] rssItems)
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
    public HSEViewRSS getItem(int position)
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
        HSEViewRSS currentRssElement = getItem(position);
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.rss_item, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.rss_item_title)).setText(currentRssElement.getTitle());
        ((TextView) convertView.findViewById(R.id.rss_item_text)).setText(currentRssElement.getSummary());
        return convertView;
    }
}
