package ru.hse.se.shugurov.ViewsPackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 28.10.13.
 */
public class HSEViewRSS extends HSEView
{
    private final static String TITLE_TAG = "title";
    private final static String OMITTED_TAG = "omitted";
    private final static String NULL_TAG = "null";
    private final static String SUMMARY_TAG = "summary";
    private final static String LINK_TAG = "link";
    private final static String UPDATED_AT_TAG = "updatedAt";
    private final static String ENTRY_ID = "entryId";
    private String title = "";
    private String omitted = "";
    private String summary = "";
    private String updatedAt = "";
    private String entryId = "";
    private HSERSSType type;

    HSEViewRSS(JSONObject jsonObject, String index)
    {
        super(jsonObject, index);
        try
        {
            title = jsonObject.getString(TITLE_TAG);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        try
        {
            omitted = jsonObject.getString(OMITTED_TAG);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        try
        {
            summary = jsonObject.getString(SUMMARY_TAG);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        try
        {
            url = jsonObject.getString(LINK_TAG);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        try
        {
            updatedAt = jsonObject.getString(UPDATED_AT_TAG);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        try
        {
            entryId = jsonObject.getString(ENTRY_ID);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        if (omitted.equals(NULL_TAG))
        {
            type = HSERSSType.ONLY_TITLE;
            summary = "";
            omitted = "";
        } else
        {
            type = HSERSSType.FULL_RSS;
        }
        clearStyle();
        hseViewType = HSEViewTypes.RSS;
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

    public String getUpdatedAt()
    {
        return updatedAt;
    }

    public String getEntryId()
    {
        return entryId;
    }

    public HSERSSType getType()
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
            if (endIndex < 0)
            {
                return;
            } else
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
