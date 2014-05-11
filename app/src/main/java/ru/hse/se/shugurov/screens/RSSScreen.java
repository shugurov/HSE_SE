package ru.hse.se.shugurov.screens;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 28.10.13.
 */
public class RSSScreen extends BaseScreen//TODO нужно ли наслеование
{
    public static final Creator<RSSScreen> CREATOR = new Creator<RSSScreen>()
    {
        @Override
        public RSSScreen createFromParcel(Parcel source)
        {
            return new RSSScreen(source);
        }

        @Override
        public RSSScreen[] newArray(int size)
        {
            return new RSSScreen[size];
        }
    };
    private final static String TITLE_TAG = "title";
    private final static String OMITTED_TAG = "omitted";
    private final static String NULL_TAG = "null";
    private final static String SUMMARY_TAG = "summary";
    private final static String LINK_TAG = "link";
    private String title = "";
    private String omitted = "";
    private String summary = "";
    private RSSTypes type;

    RSSScreen(JSONObject jsonObject) throws JSONException
    {
        title = jsonObject.getString(TITLE_TAG);
        omitted = jsonObject.getString(OMITTED_TAG);
        summary = jsonObject.getString(SUMMARY_TAG);
        url = jsonObject.getString(LINK_TAG);
        if (omitted.equals(NULL_TAG))
        {
            type = RSSTypes.ONLY_TITLE;
            summary = "";
            omitted = "";
        } else
        {
            type = RSSTypes.FULL_RSS;
        }
        clearStyle();
        screenType = ScreenTypes.RSS;
    }

    private RSSScreen(Parcel input)
    {
        super(input);
        title = input.readString();
        omitted = input.readString();
        summary = input.readString();
        type = (RSSTypes) input.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeString(omitted);
        dest.writeString(summary);
        dest.writeSerializable(type);
    }

    public String getTitle()
    {
        return title;
    }

    public String getOmitted()
    {
        return omitted;
    }

    public String getSummary()
    {
        return summary;
    }

    public RSSTypes getType()
    {
        return type;
    }

    private void clearStyle()
    {
        int startIndex = summary.indexOf("<style");
        int endIndex = summary.indexOf("</style>");
        if (startIndex < 0)
        {
            return;
        }
        if (startIndex == 0)
        {
            if (endIndex >= 0)
            {
                endIndex += 7;
                if (endIndex == summary.length() - 1)
                {
                    summary = "";
                } else
                {
                    summary = summary.substring(endIndex + 1);
                }
            }
        } else
        {
            if (endIndex < startIndex)
            {
                return;
            } else
            {
                endIndex += 7;
                String tempString = "";
                tempString += summary.substring(0, startIndex - 1);
                if (endIndex != summary.length())
                {
                    tempString += summary.substring(endIndex + 1);
                }
                summary = tempString;
            }
        }
    }
}
