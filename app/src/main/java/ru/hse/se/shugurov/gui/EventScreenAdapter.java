package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.EventScreen;

/**
 * Created by Иван on 22.04.2014.
 */
public class EventScreenAdapter extends ScreenAdapter
{
    public EventScreenAdapter()
    {
    }

    public EventScreenAdapter(EventScreen eventScreen)
    {
        super(eventScreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ListView listView = (ListView) inflater.inflate(R.layout.activity_main_list, container, false);
        try
        {
            listView.setAdapter(new EventsListAdapter(((EventScreen) getHseView()).getEvents(getActivity()), getActivity()));
        } catch (JSONException e)
        {
            Toast.makeText(getActivity(), "Не удалось загрузить контент", Toast.LENGTH_SHORT).show();
        }
        return listView;
    }


}
