package ru.hse.se.shugurov.ViewsPackage;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.hse.se.shugurov.utills.FileManager;


/**
 * Created by Иван on 28.10.13.
 */
public class HSEViewRSSWrapper extends HSEView implements HasFile
{

    private String url;
    private HSEViewRSS[] conecctedViews;


    HSEViewRSSWrapper(JSONObject jsonObject, String index)
    {
        super(jsonObject, index);
        this.url = HSEView.SERVER_LINK + "/api/structure/rss/" + getKey();
    }

    @Override
    public void notifyAboutFiles(Context context)
    {
        FileManager fileManager = new FileManager(context);
        String content = fileManager.getFileContent(getKey());  //TODO а можно ли использовать key как идентификатор?
        JSONObject jsonObject = null;   //TODO не забыть чистить старые файлы!!!! но не тут, а вообще
        try
        {
            jsonObject = new JSONObject(content);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return;
        }
        JSONArray jsonArray = null;
        try
        {
            jsonArray = jsonObject.getJSONArray("entries");
        } catch (JSONException e)
        {
            e.printStackTrace();
            return;
        }
        if (jsonArray != null)
        {
            conecctedViews = new HSEViewRSS[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++)
            {
                try
                {
                    conecctedViews[i] = new HSEViewRSS(jsonArray.getJSONObject(i), this.getIndex() + new Integer(i).toString() + HSEView.END_OF_INDEX_TAG);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        } else
        {
            conecctedViews = new HSEViewRSS[0];
        }
        int a = 7;
    }


    @Override
    public FileDescription getFileDescription()
    {
        return new FileDescription(getKey(), url);
    }

    public HSEViewRSS[] getConnectedViews()
    {
        return conecctedViews;
    }
}
