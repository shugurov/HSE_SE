package ru.hse.shugurov.screens;


import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Base class for all screen classes. This class provides a number of methods for parsing json and
 * creating a graph of object which represents application structure
 */
public class BaseScreen implements Parcelable
{
    /**
     * Used for recreating objects after their serialization
     */
    public static final Creator<BaseScreen> CREATOR = new Creator<BaseScreen>()
    {
        @Override
        public BaseScreen createFromParcel(Parcel source)
        {
            return new BaseScreen(source);
        }

        @Override
        public BaseScreen[] newArray(int size)
        {
            return new BaseScreen[size];
        }
    };
    private static final String SECTIONS_TAG_IN_JSON = "sections";
    protected String url;
    protected int screenType;
    private boolean isMainView;
    private String name;
    private String key;
    private BaseScreen[] childViews;
    private String description;

    /**
     * Empty constructor for rare occasions when a class that is subclass of {@code HSEView} does
     * not need to parse all usual information about a screen.
     */
    protected BaseScreen()
    {
    }

    /**
     * Constructor which parses all usual information about a screen.
     *
     * @param jsonObject object to be parsed
     * @throws JSONException if jsonObject has errors or does not have required fields
     */
    protected BaseScreen(JSONObject jsonObject) throws JSONException
    {
        parseUsualDescriptionOfTheView(jsonObject);
    }

    /**
     * Constructor which parses all usual information about a screen.
     * Subclasses should typically call this constructor
     *
     * @param jsonObject object to be parsed
     * @param serverURL  link to a server which provides api
     * @throws JSONException if jsonObject has errors or does not have required fields
     */
    protected BaseScreen(JSONObject jsonObject, String serverURL) throws JSONException
    {
        parseUsualDescriptionOfTheView(jsonObject);
        if (screenType == ScreenTypes.VIEW_OF_OTHER_VIEWS)
        {
            JSONArray jsonArray = jsonObject.getJSONArray(SECTIONS_TAG_IN_JSON);
            parseJSON(jsonArray, serverURL);
        }
    }

    /**
     * Used for recreating objects after their serialization. All subclasses <strong>have to call it first</strong>
     */
    protected BaseScreen(Parcel input)
    {
        url = input.readString();
        screenType = input.readInt();
        boolean[] values = new boolean[1];
        input.readBooleanArray(values);
        isMainView = values[0];
        name = input.readString();
        key = input.readString();
        Parcelable[] parcelables = input.readParcelableArray(BaseScreen.class.getClassLoader());
        if (parcelables != null)
        {
            childViews = Arrays.copyOf(parcelables, parcelables.length, BaseScreen[].class);
        }
        description = input.readString();
    }

    /**
     * factory method for creating
     *
     * @param json      a string which contains a json representation of application structure
     * @param serverURL link to a server which provides api
     * @return first screen which contain array of child screens
     * @throws JSONException if json representation has errors or does not have required fields
     */
    public static BaseScreen getView(String json, String serverURL) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(json);
        BaseScreen viewToReturn = new BaseScreen(jsonObject, serverURL);
        return viewToReturn;
    }

    /**
     * Returns an url specified in json
     *
     * @return url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Returns screen type specified by api
     *
     * @return screen type
     */
    public int getScreenType()
    {
        return screenType;
    }

    /**
     * Returns name of a screen which should be used as a title in acton bar
     *
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return unique key of a screen
     */
    public String getKey()
    {
        return key;
    }

    /*parse the following fields: type, url, key, name, description*/
    private void parseUsualDescriptionOfTheView(JSONObject jsonObject) throws JSONException
    {

        screenType = -1;
        if (jsonObject.has("type"))
        {
            screenType = jsonObject.getInt("type");
            isMainView = false;
        } else if (jsonObject.has(SECTIONS_TAG_IN_JSON))
        {
            isMainView = true;
            screenType = ScreenTypes.VIEW_OF_OTHER_VIEWS;
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

    /*iterates through an array of json objects where each of them represents a screen and optional;y child views
    * Calls constructor for every, therefore it is indirect recursion.*/
    private void parseJSON(JSONArray jsonArray, String serverURL) throws JSONException
    {
        ArrayList<BaseScreen> viewList;
        viewList = new ArrayList<BaseScreen>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObject;
            jsonObject = jsonArray.getJSONObject(i);
            int type = jsonObject.getInt("type");
            switch (type)
            {
                case ScreenTypes.FILE:
                    viewList.add(new ScreenWithFile(jsonObject, serverURL));
                    break;
                case ScreenTypes.HTML_CONTENT:
                    viewList.add(new HtmlContentScreen(jsonObject, serverURL));
                    break;
                case ScreenTypes.RSS_WRAPPER:
                    viewList.add(new RSSWrapperScreen(jsonObject, serverURL));
                    break;
                case ScreenTypes.FACEBOOK:
                case ScreenTypes.VK_FORUM:
                case ScreenTypes.VK_PUBLIC_PAGE_WALL:
                    viewList.add(new SocialNetworkScreen(jsonObject));
                    break;
                case ScreenTypes.MAP:
                    viewList.add(new MapScreen(jsonObject, serverURL));
                    break;
                case ScreenTypes.EVENTS:
                    viewList.add(new EventScreen(jsonObject, serverURL));
                    break;
                case ScreenTypes.INNER_WEB_PAGE:
                case ScreenTypes.RSS:
                case ScreenTypes.VIEW_OF_OTHER_VIEWS:
                case ScreenTypes.WEB_PAGE:
                    viewList.add(new BaseScreen(jsonObject, serverURL));
                default:
                    continue;
            }

        }
        if (viewList.size() != 0)
        {
            this.childViews = new BaseScreen[viewList.size()];
            int i = 0;
            for (BaseScreen view : viewList)
            {
                childViews[i] = view;
                i++;
            }
        } else
        {
            this.childViews = null;
        }
    }

    /**
     * Returns screens which follow requested in application logic
     *
     * @return child elements or null
     */
    public BaseScreen[] getViewElements()
    {
        return this.childViews;
    }

    /**
     * Checks if requested screen is main(first screen which a user sees when opens app for the first time)
     *
     * @return whether requested screen is main or not
     */
    public boolean isMainView()
    {
        return isMainView;
    }

    /**
     * Iterates through the entire graph seeking screens which have files. If a screen has a file
     * then provided collection is enhanced by adding a file description.
     *
     * @param descriptions
     */
    public void getDescriptionsOfFiles(Collection<FileDescription> descriptions)
    {
        if (descriptions == null)
        {
            descriptions = new ArrayList<FileDescription>();
        }
        if (screenType == ScreenTypes.VIEW_OF_OTHER_VIEWS)
        {
            for (BaseScreen view : childViews)
            {
                if (view instanceof HasFile)
                {
                    descriptions.add(((HasFile) view).getFileDescription());
                } else if (view.getScreenType() == ScreenTypes.VIEW_OF_OTHER_VIEWS)
                {
                    view.getDescriptionsOfFiles(descriptions);
                }
            }
        }
    }

    /**
     * Returns description retrieved from  JSON. Although it exists in almost every JSON object,
     * only a few of them contains non-empty values
     *
     * @return screen description
     */
    public String getDescription()
    {
        return description;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(url);
        dest.writeInt(screenType);
        dest.writeBooleanArray(new boolean[]{isMainView()});
        dest.writeString(name);
        dest.writeString(key);
        dest.writeParcelableArray(childViews, flags);
        dest.writeString(description);
    }
}