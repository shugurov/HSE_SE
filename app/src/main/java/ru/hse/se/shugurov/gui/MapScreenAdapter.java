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
    private static final String MARKERS_TAG = "markers";
    private MarkerWrapper[] markerWrappers;

    public MapScreenAdapter()
    {
    }

    public MapScreenAdapter(MapScreen hseView)
    {
        markerWrappers = hseView.getMarkers();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            markerWrappers = (MarkerWrapper[]) savedInstanceState.getParcelableArray(MARKERS_TAG);
            savedInstanceState.remove(MARKERS_TAG);
        }
        super.onCreate(savedInstanceState);
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
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(MARKERS_TAG, markerWrappers);

    }
}
