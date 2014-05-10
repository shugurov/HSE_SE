package ru.hse.se.shugurov.screens;


import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**Class represents a map screen which has several map markers
 *
 * Created by Ivan Shugurov
 */
public class MapScreen extends BaseScreen
{
    public static final Creator<MapScreen> CREATOR = new Creator<MapScreen>()
    {
        @Override
        public MapScreen createFromParcel(Parcel source)
        {
            return new MapScreen(source);
        }

        @Override
        public MapScreen[] newArray(int size)
        {
            return new MapScreen[size];
        }
    };
    private MarkerWrapper[] markers;

    /**
     * Parses json object and creates a new instance. Most of the work is done by superclass,
     * this class is responsible for parsing information only about map markers
     *
     * @param jsonObject holds information about screen
     * @param serverURL  link to a server which provides api
     * @throws JSONException if json representation has errors or does not have required fields
     */
    protected MapScreen(JSONObject jsonObject, String serverURL) throws JSONException
    {
        super(jsonObject, serverURL);
        JSONArray markersArray = jsonObject.getJSONArray("markers");
        parseMarkers(markersArray);
    }

    protected MapScreen(Parcel input)
    {
        super(input);
        Parcelable[] parcelables = input.readParcelableArray(MarkerWrapper.class.getClassLoader());
        if (parcelables == null)
        {
            markers = new MarkerWrapper[0];
        } else
        {
            markers = Arrays.copyOf(parcelables, parcelables.length, MarkerWrapper[].class);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeParcelableArray(markers, flags);
    }

    /*creates an array of json object which represent markers, then iterates through them and calls parseMarker()*/
    private void parseMarkers(JSONArray markersArray) throws JSONException
    {
        markers = new MarkerWrapper[markersArray.length()];
        for (int i = 0; i < markers.length; i++)
        {
            markers[i] = parseMarker(markersArray.getJSONObject(i));
        }
    }

    /*parses json object which represents a marker*/
    private MarkerWrapper parseMarker(JSONObject markerObject) throws JSONException
    {
        String title = markerObject.getString("title");
        String url = markerObject.getString("url");
        String phone = markerObject.getString("phone");
        MarkerWrapper.ActionTypes actionType;
        switch (markerObject.getInt("actionType"))
        {
            case 1:
                actionType = MarkerWrapper.ActionTypes.DO_NOTHING;
                break;
            case 2:
                actionType = MarkerWrapper.ActionTypes.OPEN_EXTERNAL_MAPS;
                break;
            case 3:
                actionType = MarkerWrapper.ActionTypes.OPEN_URL;
                break;
            default:
                throw new IllegalArgumentException("Precondition violated in MapScreen.parseMarker(). " + "Marker json object has unknown action type");
        }
        JSONObject locationObject = markerObject.getJSONObject("location");
        Double latitude = locationObject.getDouble("lat");
        Double longitude = locationObject.getDouble("lng");
        String address = markerObject.getString("address");

        return new MarkerWrapper(title, url, actionType, phone, latitude, longitude, address);
    }

    /**
     *
     * @return map markers
     */
    public MarkerWrapper[] getMarkers()
    {
        return markers;
    }
}
