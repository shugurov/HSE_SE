package ru.hse.se.shugurov.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.Event;
import ru.hse.se.shugurov.utills.ImageLoader;

/**
 * Created by Иван on 24.04.2014.
 */
public class EventsListAdapter extends BaseAdapter//TODO whu don't we use telephone and url at all?
{
    private Event[] events;
    private LayoutInflater inflater;


    public EventsListAdapter(Event[] events, Context context)
    {
        this.events = events;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return events.length;
    }

    @Override
    public Event getItem(int position)
    {
        return events[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Event eventToBeShown = getItem(position);
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.event, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.event_title)).setText(eventToBeShown.getName());
        ((TextView) convertView.findViewById(R.id.event_address)).setText(eventToBeShown.getAddress());
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((TextView) convertView.findViewById(R.id.event_date)).setText(format.format(eventToBeShown.getDate()));
        ((TextView) convertView.findViewById(R.id.event_text)).setText(eventToBeShown.getDescription());
        try
        {
            String mapPictureUrl = inflater.getContext().getResources().getString(R.string.google_static_map, URLEncoder.encode(eventToBeShown.getAddress(), "utf8")) + "&zoom=14&size=320x180&sensor=false";
            ImageView mapImage = (ImageView) convertView.findViewById(R.id.event_map);
            mapImage.setImageBitmap(null);
            ImageLoader.instance().displayImage(mapPictureUrl, mapImage);
        } catch (UnsupportedEncodingException e)
        {
        }
        return convertView;
    }
}
