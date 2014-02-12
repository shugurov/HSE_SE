package ru.hse.se.shugurov.ViewsPackage;


import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HSEView
{
    public static final String INDEX_OF_THE_MAIN_VIEW = "";
    public static final String SERVER_LINK = "http://promoteeducate1.appspot.com";
    public static final String JSON_LINK = "http://promoteeducate1.appspot.com/api/structure/app/fe1222a924fa7c649d33a36c5532594fb239fb6f";
    protected static final String END_OF_INDEX_TAG = "e";
    private static final String SECTIONS_TAG_IN_JSON = "sections";
    private static boolean isFirstView = true;
    final String JSON_EXCEPTION = "json_exception"; //TODO
    protected String url; //TODO инкапсулировать получение url в подклассы
    protected int hseViewType;
    private boolean IS_THE_FIRST_VIEW;
    private String name;
    private String key;
    private String description;
    private String index;
    private String parentIndex;
    private HSEView[] childViews;

    protected HSEView(JSONObject jsonObject, String index)
    {
        if (isFirstView)
        {
            IS_THE_FIRST_VIEW = true;
        } else
        {
            IS_THE_FIRST_VIEW = false;
        }
        getUsualDescriptionOfTheView(jsonObject);
        this.index = index;
        if (index.length() > 0)
        {
            index = index.substring(0, index.lastIndexOf(END_OF_INDEX_TAG));
            int position;
            if (index.lastIndexOf(END_OF_INDEX_TAG) >= 0)
            {
                index = index.substring(0, index.lastIndexOf(END_OF_INDEX_TAG) + 1);
                this.parentIndex = index;
            } else
            {
                this.parentIndex = INDEX_OF_THE_MAIN_VIEW;
            }

        }
        switch (this.hseViewType)
        {
            case HSEViewTypes.VIEW_OF_OTHER_VIEWS:
                JSONArray jsonArray;
                try
                {
                    jsonArray = jsonObject.getJSONArray(SECTIONS_TAG_IN_JSON);
                    parseJSON(jsonArray, index);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e(JSON_EXCEPTION, "inside HSEView(JSONObject jsonObject)");
                }
                break;
            default:
                break;
        }
    }

    public static HSEView getView(String json) throws JSONException
    {
        isFirstView = true; //TODO удалить, наверное
        JSONObject jsonObject = new JSONObject(json);
        HSEView viewToReturn;
        viewToReturn = new HSEView(jsonObject, "");
        return viewToReturn;
    }

    public static HSEView getView(JSONObject jsonObject, String index)
    {
        HSEView viewToReturn;
        viewToReturn = new HSEView(jsonObject, index);
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

    public String getIndex()
    {
        return index;
    }

    public String getParentIndex()
    {
        if (!IS_THE_FIRST_VIEW)
        {
            return parentIndex;
        } else
        {
            return INDEX_OF_THE_MAIN_VIEW;
        }
    }

    public HSEView[] getChildViews()
    {
        return childViews;
    }

    private void getUsualDescriptionOfTheView(JSONObject jsonObject)
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
        url = "";
        try
        {
            url = jsonObject.getString("url");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        key = "";
        try
        {
            key = jsonObject.getString("key");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        description = "";
        try
        {
            description = jsonObject.getString("description");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        name = "";
        try
        {
            name = jsonObject.getString("name");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void parseJSON(JSONArray jsonArray, String index) throws JSONException
    {
        ArrayList<HSEView> viewList;
        viewList = new ArrayList<HSEView>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObject;
            jsonObject = jsonArray.getJSONObject(i);
            int type = -1;
            try
            {
                type = jsonObject.getInt("type");
            } catch (JSONException e)
            {
                e.printStackTrace();
                Log.e(JSON_EXCEPTION, "unknown type");
                type = -1;
            }
            String newIndex;
            newIndex = this.index + i + END_OF_INDEX_TAG;
            switch (type) //TODO дописать новые вьюхи с отдельными классами
            {
                case HSEViewTypes.FILE:
                    viewList.add(new HSEViewWithFile(jsonObject, newIndex));
                    break;
                case HSEViewTypes.HTML_CONTENT:
                    viewList.add(new HSEViewHtmlContent(jsonObject, newIndex));
                    break;
                case HSEViewTypes.RSS_WRAPPER:
                    viewList.add(new HSEViewRSSWrapper(jsonObject, newIndex));
                    break;
                case HSEViewTypes.VK_FORUM:
                case HSEViewTypes.VK_PUBLIC_PAGE_WALL:
                    viewList.add(new VKHSEView(jsonObject, newIndex));
                    break;
                default:
                    viewList.add(new HSEView(jsonObject, newIndex));
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
        return this.childViews.clone();
    }

    public HSEView getViewByIndex(String index)
    {
        if (index.compareTo(this.index) == 0)
        {
            return this;
        }
        {
            String subString;
            subString = index.substring(0, this.index.length());
            int a = 5;
            if (subString.compareTo(this.index) != 0)
            {
                return null;
            } else
            {
                subString = index.substring(this.index.length());
                int position;
                position = subString.indexOf(END_OF_INDEX_TAG);
                if (position < 0)
                {
                    return null;
                } else
                {
                    int digitalIndex;
                    try
                    {
                        subString = subString.substring(0, position);
                        digitalIndex = Integer.parseInt(subString);
                    } catch (Exception e)
                    {
                        return null;
                    }
                    switch (hseViewType)
                    {
                        case HSEViewTypes.VIEW_OF_OTHER_VIEWS:
                            if (digitalIndex >= this.childViews.length)
                            {
                                return null;
                            } else
                            {
                                return this.childViews[digitalIndex].getViewByIndex(index);
                            }
                        case HSEViewTypes.RSS_WRAPPER:
                            HSEViewRSS[] connectedViews = ((HSEViewRSSWrapper) this).getConnectedViews();
                            if (digitalIndex >= connectedViews.length)
                            {
                                return null;
                            } else
                            {
                                return connectedViews[digitalIndex];
                            }
                        default:
                            return null;
                    }
                }
            }
        }
    }

    public boolean isMainView()
    {
        return IS_THE_FIRST_VIEW;
    }

    public void getDescriptionsOfFiles(ArrayList<FileDescription> descriptions)//TODO дописать то, где ещё файлы подкачиваются
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

    public void notifyAboutFiles(Context context)
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