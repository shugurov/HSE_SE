package ru.hse.se.shugurov.screens;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class HSEView implements Serializable
{
    private static final String SECTIONS_TAG_IN_JSON = "sections";
    protected String url;
    protected int hseViewType;
    private boolean isMainView;
    private String name;
    private String key;
    private HSEView[] childViews;
    private String description;

    protected HSEView()
    {
    }

    protected HSEView(JSONObject jsonObject) throws JSONException
    {
        parseUsualDescriptionOfTheView(jsonObject);
    }

    protected HSEView(JSONObject jsonObject, String serverURL) throws JSONException
    {
        parseUsualDescriptionOfTheView(jsonObject);
        if (hseViewType == HSEViewTypes.VIEW_OF_OTHER_VIEWS)
        {
            JSONArray jsonArray = jsonObject.getJSONArray(SECTIONS_TAG_IN_JSON);
            parseJSON(jsonArray, serverURL);
        }
    }

    public static HSEView getView(String json, String serverURL) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(json);
        HSEView viewToReturn = new HSEView(jsonObject, serverURL);
        return viewToReturn;
    }

    public String getUrl()
    {
        return url;
    }

    public int getHseViewType()
    {
        return hseViewType;
    }

    public String getName()
    {
        return name;
    }

    public String getKey()
    {
        return key;
    }

    private void parseUsualDescriptionOfTheView(JSONObject jsonObject) throws JSONException
    {

        hseViewType = -1;
        if (jsonObject.has("type"))
        {
            hseViewType = jsonObject.getInt("type");
            isMainView = false;
        } else if (jsonObject.has(SECTIONS_TAG_IN_JSON))
        {
            isMainView = true;
            hseViewType = HSEViewTypes.VIEW_OF_OTHER_VIEWS;
        }
        if (jsonObject.has("url"))
        {
            url = jsonObject.getString("url");
        }
        if (jsonObject.has("key"))
        {
            key = jsonObject.getString("key");
        }
        name = jsonObject.getString("name");
        if (jsonObject.has("description"))
        {
            description = jsonObject.getString("description");
        }
    }

    private void parseJSON(JSONArray jsonArray, String serverURL) throws JSONException
    {
        ArrayList<HSEView> viewList;
        viewList = new ArrayList<HSEView>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObject;
            jsonObject = jsonArray.getJSONObject(i);
            int type = jsonObject.getInt("type");
            switch (type)
            {
                case HSEViewTypes.FILE:
                    viewList.add(new HSEViewWithFile(jsonObject, serverURL));
                    break;
                case HSEViewTypes.HTML_CONTENT:
                    viewList.add(new HSEViewHtmlContent(jsonObject, serverURL));
                    break;
                case HSEViewTypes.RSS_WRAPPER:
                    viewList.add(new HSEViewRSSWrapper(jsonObject, serverURL));
                    break;
                case HSEViewTypes.FACEBOOK:
                case HSEViewTypes.VK_FORUM:
                case HSEViewTypes.VK_PUBLIC_PAGE_WALL:
                    viewList.add(new SocialNetworkView(jsonObject));
                    break;
                case HSEViewTypes.MAP:
                    viewList.add(new MapScreen(jsonObject, serverURL));
                    break;
                case HSEViewTypes.EVENTS:
                    viewList.add(new EventScreen(jsonObject, serverURL));
                    break;
                default:
                    viewList.add(new HSEView(jsonObject, serverURL));
            }

        }
        if (viewList.size() != 0)
        {
            this.childViews = new HSEView[viewList.size()];
            int i = 0;
            for (HSEView view : viewList)
            {
                childViews[i] = view;
                i++;
            }
        } else
        {
            this.childViews = null;
        }
    }

    public HSEView[] getViewElements()
    {
        return this.childViews;
    }


    public boolean isMainView()
    {
        return isMainView;
    }

    public void getDescriptionsOfFiles(ArrayList<FileDescription> descriptions)
    {
        if (descriptions == null)
        {
            descriptions = new ArrayList<FileDescription>();
        }
        if (hseViewType == HSEViewTypes.VIEW_OF_OTHER_VIEWS)
        {
            for (HSEView view : childViews)
            {
                if (view instanceof HasFile)
                {
                    descriptions.add(((HasFile) view).getFileDescription());
                } else if (view.getHseViewType() == HSEViewTypes.VIEW_OF_OTHER_VIEWS)
                {
                    view.getDescriptionsOfFiles(descriptions);
                }
            }
        }
    }

    public final void notifyAboutFileDownloading(Context context) throws JSONException
    {
        if (hseViewType == HSEViewTypes.VIEW_OF_OTHER_VIEWS)
        {
            for (HSEView view : childViews)
            {
                view.notifyAboutFileDownloading(context);
            }
        }
    }

    public String getDescription()
    {
        return description;
    }
}