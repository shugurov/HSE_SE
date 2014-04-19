package ru.hse.se.shugurov.ViewsPackage;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Иван on 19.04.2014.
 */
public class MarkerWrapper//TODO осмотреть на snippet
{
    private final String address;
    private final int actionType; //TODO что это?
    private final String url;
    private final String phone;
    private final MarkerOptions marker = new MarkerOptions();

    public MarkerWrapper(String title, String url, int actionType, String phone, double latitude, double longitude, String address)
    {
        this.actionType = actionType;
        this.url = url;
        this.address = address;
        this.phone = phone;
        marker.position(new LatLng(latitude, longitude));
        marker.title(title);
    }

    public MarkerOptions getMarker()
    {
        return marker;
    }

    public String getAddress()
    {
        return address;
    }

    public int getActionType()
    {
        return actionType;
    }

    public String getUrl()
    {
        return url;
    }

    public String getPhone()
    {
        return phone;
    }
}
