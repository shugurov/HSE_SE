package ru.hse.se.shugurov.screens;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Иван on 19.04.2014.
 */
public class MarkerWrapper implements Parcelable //TODO осмотреть на snippet
{
    public static Creator<MarkerWrapper> CREATOR = new Creator<MarkerWrapper>()
    {
        @Override
        public MarkerWrapper createFromParcel(Parcel source)
        {
            return new MarkerWrapper(source);
        }

        @Override
        public MarkerWrapper[] newArray(int size)
        {
            return new MarkerWrapper[size];
        }
    };
    private String address;
    private int actionType; //TODO что это?
    private String url;
    private String phone;
    private MarkerOptions marker;

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

    private MarkerWrapper(Parcel parcel)
    {

    }

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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {

    }
}
