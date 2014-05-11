package ru.hse.se.shugurov.gui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.MarkerWrapper;

/**
 * Fragment requires following arguments:
 * <ul>
 * <li>{@link ru.hse.se.shugurov.screens.MarkerWrapper[]} with a key specified by {@code MARKERS_TAG}</li>
 * <li>title as a string with a key specified by {@code TITLE_TAG}</li>
 * </ul>
 * <p/>
 * <strong>Is is assumed that method setArguments is called after putting all arguments in a bundle object</strong>
 * Created by Ivan Shugurov
 */
public class MapFragment extends SupportMapFragment
{
    /*constants used as keys in bundle object*/
    public static final String MARKERS_TAG = "markers";
    public static final String TITLE_TAG = "title";


    private MarkerWrapper[] markerWrappers;
    private String title;
    private Map<Marker, MarkerWrapper> markersToWrappers;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        readStateFomBundle(savedInstanceState);
        super.onCreate(savedInstanceState);
        getActivity().setTitle(title);
    }

    /*gets class fields from a bundle object*/
    private void readStateFomBundle(Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            Parcelable[] parcelableArray = savedInstanceState.getParcelableArray(MARKERS_TAG);
            if (parcelableArray == null)
            {
                markerWrappers = new MarkerWrapper[0];
            } else
            {
                markerWrappers = Arrays.copyOf(parcelableArray, parcelableArray.length, MarkerWrapper[].class);
            }
            title = savedInstanceState.getString(TITLE_TAG);
            savedInstanceState.remove(MARKERS_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View resultView = super.onCreateView(inflater, container, savedInstanceState);
        final GoogleMap googleMap = getMap();
        if (googleMap == null)
        {
            Toast.makeText(getActivity(), "Невозвожно отобразить карту", Toast.LENGTH_SHORT).show();
            return resultView;
        }
        LatLngBounds.Builder builder = LatLngBounds.builder();
        markersToWrappers = new HashMap<Marker, MarkerWrapper>((markerWrappers.length * 3 / 2) + 1);
        for (MarkerWrapper marker : markerWrappers)
        {
            Marker mapMarker = googleMap.addMarker(marker.getMarker());
            builder.include(marker.getMarker().getPosition());
            markersToWrappers.put(mapMarker, marker);
        }

        googleMap.setInfoWindowAdapter(new MarkerWindowAdapter());
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {
            @Override
            public void onInfoWindowClick(Marker marker)
            {
                handleMarkerClick(marker);
            }
        });
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

    /*makes necessary actions when user clicks on map marker*/
    private void handleMarkerClick(Marker marker)
    {
        MarkerWrapper correspondingWrapper = markersToWrappers.get(marker);
        switch (correspondingWrapper.getActionType())
        {
            case OPEN_EXTERNAL_MAPS:
                double latitude = marker.getPosition().latitude;
                double longitude = marker.getPosition().longitude;
                String uriBeginning = String.format("geo: %s, %s", latitude, longitude);
                String query = String.format("%s,%s(%s)", latitude, longitude, marker.getTitle());
                try
                {
                    String encodedQuery = URLEncoder.encode(query, "utf8");
                    String uriString = uriBeginning + "?q=" + encodedQuery + "&z=16";
                    Uri uri = Uri.parse(uriString);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    try
                    {
                        startActivity(intent);
                    } catch (Exception e)
                    {
                        Toast.makeText(getActivity(), "Google maps не установлены", Toast.LENGTH_SHORT).show();
                    }
                } catch (UnsupportedEncodingException e)
                {
                    return;
                }
                break;
            case OPEN_URL:
                Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(correspondingWrapper.getUrl());
                openBrowserIntent.setData(uri);
                getActivity().startActivity(openBrowserIntent);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(MARKERS_TAG, markerWrappers);
        outState.putString(TITLE_TAG, title);
    }

    @Override
    public void setArguments(Bundle args)
    {
        readStateFomBundle(args);
        super.setArguments(args);
    }

    /*used for customizing markers. Changes appearance only for those markers which
    opens external map application when user touches them.*/
    private class MarkerWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        @Override
        public View getInfoWindow(Marker marker)
        {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            View resultView = null;
            MarkerWrapper currentWrapper = markersToWrappers.get(marker);
            if (currentWrapper.getActionType() == MarkerWrapper.ActionTypes.OPEN_URL)
            {
                resultView = getLayoutInflater(null).inflate(R.layout.map_marker, null, false);
                ((TextView) resultView.findViewById(R.id.map_marker_title)).setText(marker.getTitle());
                ((TextView) resultView.findViewById(R.id.map_marker_address)).setText(currentWrapper.getAddress());
                ((TextView) resultView.findViewById(R.id.map_marker_phone)).setText(currentWrapper.getPhone());
                ((TextView) resultView.findViewById(R.id.map_marker_url)).setText(currentWrapper.getUrl());
            }
            return resultView;
        }
    }
}
