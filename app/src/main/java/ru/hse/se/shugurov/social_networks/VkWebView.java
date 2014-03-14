package ru.hse.se.shugurov.social_networks;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Иван on 30.10.13.
 */
public class VkWebView extends WebViewClient
{
    public static final String OAUTH = "http://oauth.vk.com/authorize?client_id=3965004&response_type=token&scope=groups";
    private static final String ACCESS_TOKEN_TAG = "access_token";


    private VKCallBack callBack;

    public VkWebView(VKCallBack callBack)
    {
        this.callBack = callBack;
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView webView, String url)
    {
        if (url.startsWith("http://oauth.vk.com/blank.html") || url.startsWith("https://oauth.vk.com/blank.html"))//TODO поменять на константы
        {
            callBack.call(getAccessToken(url));
            return true;
        }
        return false;
    }

    private String getAccessToken(String link)//а что возвращается при ошибке?
    {
        if (link == null)
        {
            return null;
        }
        if (link.contains(ACCESS_TOKEN_TAG))
        {
            link = link.substring(link.indexOf(ACCESS_TOKEN_TAG), link.length() - 1);
            link = link.replace(ACCESS_TOKEN_TAG + "=", "");
            int position = link.indexOf("&");
            if (position > 0)
            {
                link = link.substring(0, position);
            }
            return link;
        } else
        {
            return null;
        }
    }

    public interface VKCallBack
    {
        void call(String accessToken);
    }


}
