package ru.hse.se.shugurov.gui;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import ru.hse.se.shugurov.ViewsPackage.HSEView;

/**
 * Created by Иван on 15.04.2014.
 */
public class MapScreenAdapter extends ScreenAdapter
{
    public MapScreenAdapter(ActivityCallback callback, HSEView hseView)//TODO ActivityCallback не должен быть частью MainView
    {
        super(callback, hseView);
        MapFragment mapFragment = new MapFragment();
        GoogleMap googleMap = mapFragment.getMap();
        Context context;
    }
}
