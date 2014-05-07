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
 *
 * @author Ivan Shugurov
 */
public class EventFragment extends ScreenAdapter
{
    public EventFragment()
    {
    }

    public EventFragment(EventScreen eventScreen)
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
