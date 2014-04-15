package ru.hse.se.shugurov.gui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import ru.hse.se.shugurov.MainActivity;
import ru.hse.se.shugurov.ViewsPackage.HSEView;

/**
 * Created by Иван on 15.04.2014.
 */
public class MapScreenAdapter extends ScreenAdapter
{
    public MapScreenAdapter(MainActivity.MainActivityCallback callback, ViewGroup container, View previousView, HSEView hseView)//TODO MainActivityCallback не должен быть частью MainView
    {
        super(callback, container, previousView, hseView);
        MapFragment mapFragment = new MapFragment();
        GoogleMap googleMap = mapFragment.getMap();
        Context context;
    }
}
