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
 * This class is used to show list of events.
 *
 * @author Ivan Shugurov
 */
public class EventFragment extends AbstractFragment
{
    /**
     * Default constructor used by Android for instantiating this class after it was destroyed.
     * Should not be used by developers.
     */
    public EventFragment()
    {
    }

    /**
     * @param eventScreen object that stores information about events. Not Null.
     */
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
            listView.setAdapter(new EventsListAdapter(((EventScreen) getHseView()).getEvents(), getActivity()));
        } catch (JSONException e)
        {
            Toast.makeText(getActivity(), "Не удалось загрузить контент", Toast.LENGTH_SHORT).show();
        }
        return listView;
    }


}
