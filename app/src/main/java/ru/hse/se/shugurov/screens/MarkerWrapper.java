package ru.hse.se.shugurov.screens;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;

/**
 * Instance of this class wraps an instance of  {@link com.google.android.gms.maps.model.MarkerOptions}
 * in order to extends MarkerOptions with several additional fields
 * <p/>
 * Created by Ivan Shugurov
 */
public class MarkerWrapper implements Parcelable
{
    /*used to parse instances of this class*/
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
    private ActionTypes actionType;
    private String url;
    private String phone;
    private MarkerOptions marker;

    /**
     * Creates a new instance
     *
     * @param title
     * @param url
     * @param actionType
     * @param phone
     * @param latitude
     * @param longitude
     * @param address
     */
    public MarkerWrapper(String title, String url, ActionTypes actionType, String phone, double latitude, double longitude, String address)
    {
        this.actionType = actionType;
        this.url = url;
        this.address = address;
        this.phone = phone;
        marker = new MarkerOptions();
        marker.position(new LatLng(latitude, longitude));
        marker.title(title);
        switch (actionType)
        {
            case DO_NOTHING:
            case OPEN_EXTERNAL_MAPS:
                marker.snippet(address);
                break;
            case OPEN_URL:
                marker.snippet(url);
                break;
        }
    }

    /*should be called when */
    private MarkerWrapper(Parcel parcel)
    {
        address = parcel.readString();
        actionType = (ActionTypes) parcel.readSerializable();
        url = parcel.readString();
        phone = parcel.readString();
        marker = parcel.readParcelable(MarkerOptions.class.getClassLoader());
    }

    public MarkerOptions getMarker()
    {
        return marker;
    }

    public String getAddress()
    {
        return address;
    }

    public ActionTypes getActionType()
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
        dest.writeString(address);
        dest.writeSerializable(actionType);
        dest.writeString(url);
        dest.writeString(phone);
        dest.writeParcelable(marker, flags);
    }

    public enum ActionTypes implements Serializable
    {
        DO_NOTHING, OPEN_EXTERNAL_MAPS, OPEN_URL;
    }
}
