package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.HSEView;

/**
 * Created by Иван on 22.04.2014.
 */
public class EventScreenAdapter extends ScreenAdapter
{
    public EventScreenAdapter()
    {
    }

    public EventScreenAdapter(HSEView hseView)
    {
        super(hseView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View eventView = inflater.inflate(R.layout.event, container, false);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.event_container, new SupportMapFragment());
        transaction.commit();
        return eventView;
    }


}
