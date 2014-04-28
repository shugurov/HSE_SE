package ru.hse.se.shugurov.screens;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class HSEView implements Serializable
{
    public static final String SERVER_LINK = "http://promoteeducate1.appspot.com";
    public static final String JSON_LINK = "http://promoteeducate1.appspot.com/api/structure/app/fe1222a924fa7c649d33a36c5532594fb239fb6f";//TODO спрятать в xml
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
        getUsualDescriptionOfTheView(jsonObject);
        if (hseViewType == HSEViewTypes.VIEW_OF_OTHER_VIEWS)
        {
            JSONArray jsonArray = jsonObject.getJSONArray(SECTIONS_TAG_IN_JSON);
            parseJSON(jsonArray);
        }
    }

    public static HSEView getView(String json) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(json);
        HSEView viewToReturn = new HSEView(jsonObject);
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

    private void getUsualDescriptionOfTheView(JSONObject jsonObject) throws JSONException
    {

        hseViewType = -1;
        if (jsonObject.has("type"))
        {
            hseViewType = jsonObject.getInt("type");
            isMainView = false;
            description = jsonObject.getString("description");
        } else
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
    }

    private void parseJSON(JSONArray jsonArray) throws JSONException
    {
        ArrayList<HSEView> viewList;
        viewList = new ArrayList<HSEView>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObject;
            jsonObject = jsonArray.getJSONObject(i);
            int type = jsonObject.getInt("type");
            switch (type) //TODO дописать новые вьюхи с отдельными классами
            {
                case HSEViewTypes.FILE:
                    viewList.add(new HSEViewWithFile(jsonObject));
                    break;
                case HSEViewTypes.HTML_CONTENT:
                    viewList.add(new HSEViewHtmlContent(jsonObject));
                    break;
                case HSEViewTypes.RSS_WRAPPER:
                    viewList.add(new HSEViewRSSWrapper(jsonObject));
                    break;
                case HSEViewTypes.VK_FORUM:
                case HSEViewTypes.VK_PUBLIC_PAGE_WALL:
                    viewList.add(new VKHSEView(jsonObject));
                    break;
                case HSEViewTypes.MAP:
                    viewList.add(new MapScreen(jsonObject));
                    break;
                case HSEViewTypes.EVENTS:
                    viewList.add(new EventScreen(jsonObject));
                    break;
                default:
                    viewList.add(new HSEView(jsonObject));
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