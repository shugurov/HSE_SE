package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import ru.hse.se.shugurov.screens.MapScreen;
import ru.hse.se.shugurov.screens.MarkerWrapper;

/**
 * Created by Иван on 20.04.2014.
 */
public class MapScreenAdapter extends SupportMapFragment
{
    private MarkerWrapper[] markerWrappers = new MarkerWrapper[0];//TODO yбрать это создание

    public MapScreenAdapter()
    {
    }

    public MapScreenAdapter(MapScreen hseView)
    {
        markerWrappers = hseView.getMarkers();
        //Bundle instanceState = new Bundle();
        //instanceState.putParcelable("ololo", new Student("", "", ""));
        //setArguments(instanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /*if (savedInstanceState != null && hseView == null)
        {
            hseView = (MapScreen)savedInstanceState.get(SCREEN_TAG);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View resultView = super.onCreateView(inflater, container, savedInstanceState);
        GoogleMap googleMap = getMap();
        for (MarkerWrapper marker : markerWrappers)
        {
            googleMap.addMarker(marker.getMarker());
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        return resultView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        //outState.putAll(getArguments());
        super.onSaveInstanceState(outState);
        //outState.putSerializable("ser", new Student());
    }
}
