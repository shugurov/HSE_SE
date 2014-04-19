package ru.hse.se.shugurov.ViewsPackage;


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
    private static boolean isFirstView = true;
    protected String url; //TODO инкапсулировать получение url в подклассы
    protected int hseViewType;
    private boolean IS_THE_FIRST_VIEW;
    private String name;
    private String key;
    private String description;
    private HSEView[] childViews;

    protected HSEView(JSONObject jsonObject) throws JSONException
    {
        if (isFirstView)
        {
            IS_THE_FIRST_VIEW = true;
        } else
        {
            IS_THE_FIRST_VIEW = false;
        }
        getUsualDescriptionOfTheView(jsonObject);
        if (hseViewType == HSEViewTypes.VIEW_OF_OTHER_VIEWS)
        {
            JSONArray jsonArray = jsonObject.getJSONArray(SECTIONS_TAG_IN_JSON);
            parseJSON(jsonArray);
        }
    }

    public static HSEView getView(String json) throws JSONException
    {
        isFirstView = true;
        JSONObject jsonObject = new JSONObject(json);
        HSEView viewToReturn;
        viewToReturn = new HSEView(jsonObject);
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

    public String getDescription()
    {
        return description;
    }

    private void getUsualDescriptionOfTheView(JSONObject jsonObject) throws JSONException
    {

        hseViewType = -1;
        if (isFirstView)
        {
            hseViewType = HSEViewTypes.VIEW_OF_OTHER_VIEWS;
            isFirstView = false;
        } else
        {
            try
            {
                hseViewType = jsonObject.getInt("type");
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        url = jsonObject.getString("url");
        key = jsonObject.getString("key");
        description = jsonObject.getString("description");
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
        return IS_THE_FIRST_VIEW;
    } //TODO а надо ли?

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

    public void notifyAboutFiles(Context context) throws JSONException
    {
        if (hseViewType == HSEViewTypes.VIEW_OF_OTHER_VIEWS)
        {
            for (HSEView view : childViews)
            {
                view.notifyAboutFiles(context);
            }
        }
    }
}