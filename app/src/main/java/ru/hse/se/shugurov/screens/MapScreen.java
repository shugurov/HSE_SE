package ru.hse.se.shugurov.screens;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 19.04.2014.
 */
public class MapScreen extends HSEView
{
    private transient MarkerWrapper[] markers;

    protected MapScreen(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject);
        JSONArray markersArray = jsonObject.getJSONArray("markers");
        parseMarkers(markersArray);
    }

    private void parseMarkers(JSONArray markersArray) throws JSONException
    {
        markers = new MarkerWrapper[markersArray.length()];
        for (int i = 0; i < markers.length; i++)
        {
            markers[i] = parseMarker(markersArray.getJSONObject(i));
        }
    }

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

    public MarkerWrapper[] getMarkers()
    {
        return markers;
    }
}
