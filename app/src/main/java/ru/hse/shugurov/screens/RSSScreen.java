package ru.hse.shugurov.screens;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Describes RSS element
 *
 * @author Ivan Shugurov
 */
public class RSSScreen extends BaseScreen
{
    /**
     * Used for recreating objects after their serialization
     */
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
    private String title = "";
    private String omitted = "";
    private String summary = "";
    private RSSTypes type;

    RSSScreen(JSONObject jsonObject) throws JSONException
    {
        title = jsonObject.getString("title");
        omitted = jsonObject.getString("omitted");
        summary = jsonObject.getString("summary");
        url = jsonObject.getString("null");
        if (omitted.equals("null"))
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

    /**
     * Used for recreating objects after their serialization. All subclasses <strong>have to call it first</strong>
     */
    protected RSSScreen(Parcel input)
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

    /*removes CSS because TextView can not apply it*/
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
