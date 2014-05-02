package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;

import ru.hse.se.shugurov.screens.MapScreen;
import ru.hse.se.shugurov.screens.MarkerWrapper;

/**
 * Created by Иван on 20.04.2014.
 */
public class MapScreenAdapter extends SupportMapFragment
{
    private static final String MARKERS_TAG = "markers";
    private static final String TITLE_TAG = "title";
    private MarkerWrapper[] markerWrappers;
    private String title;

    public MapScreenAdapter()
    {
    }

    public MapScreenAdapter(MapScreen hseView)
    {
        markerWrappers = hseView.getMarkers();
        this.title = hseView.getName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            markerWrappers = (MarkerWrapper[]) savedInstanceState.getParcelableArray(MARKERS_TAG);
            title = savedInstanceState.getString(TITLE_TAG);
            savedInstanceState.remove(MARKERS_TAG);
        }
        super.onCreate(savedInstanceState);
        getActivity().setTitle(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View resultView = super.onCreateView(inflater, container, savedInstanceState);
        final GoogleMap googleMap = getMap();
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (MarkerWrapper marker : markerWrappers)
        {
            googleMap.addMarker(marker.getMarker());//TODO в эмуляторе тут упало(
            builder.include(marker.getMarker().getPosition());
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        googleMap.setMyLocationEnabled(true);
        final LatLngBounds bounds = builder.build();
        if (savedInstanceState == null)
        {
            resultView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
            {
                @Override
                public void onGlobalLayout()
                {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                }
            });
        }
        return resultView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(MARKERS_TAG, markerWrappers);
        outState.putString(TITLE_TAG, title);
    }
}
