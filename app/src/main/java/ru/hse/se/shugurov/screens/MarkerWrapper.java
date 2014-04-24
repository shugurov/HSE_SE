package ru.hse.se.shugurov.screens;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;

/**
 * Created by Иван on 19.04.2014.
 */
public class MarkerWrapper implements Serializable/*Parcelable//TODO осмотреть на snippet*/
{
    private final String address;
    private final int actionType; //TODO что это?
    private final String url;
    private final String phone;
    private transient MarkerOptions marker;

    public MarkerWrapper()//TODO delete
    {
        address = null;
        actionType = 0;
        url = null;
        phone = null;
    }

    public MarkerWrapper(String title, String url, int actionType, String phone, double latitude, double longitude, String address)
    {
        this.actionType = actionType;
        this.url = url;
        this.address = address;
        this.phone = phone;
        marker = new MarkerOptions();
        marker.position(new LatLng(latitude, longitude));
        marker.title(title);
    }

    /*public MarkerWrapper(Parcel parcel)//TODO wtf?
    {
        Object[] inputObjects = new Object[5];
        parcel.readArray(ClassLoader.getSystemClassLoader());
        address = (String) inputObjects[0];
        actionType = (Integer) inputObjects[1];
        url = (String) inputObjects[2];
        phone = (String) inputObjects[3];
        marker = (MarkerOptions) inputObjects[4];
    }*/

    public MarkerOptions getMarker()
    {
        return marker;
    }

    public void setMarker(MarkerOptions marker)
    {
        this.marker = marker;
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

    /*@Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeArray(new Object[]{address, actionType, url, phone, marker});
    }TODO*/
}
