package ru.hse.se.shugurov.social_networks;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Иван on 30.10.13.
 */
public class VkWebClient extends WebViewClient//TODO показалось что-то странное при входе в типичную пи после авторизации
{
    public static final String OAUTH = "https://oauth.vk.com/authorize?" +
            "client_id=3965004&" +
            "redirect_uri=https://oauth.vk.com/blank.html&" +
            "display=mobile&" +
            "response_type=token" +
            "&scope=wall";
    private static final String ACCESS_TOKEN_TAG = "access_token";


    private VKCallBack callBack;

    public VkWebClient(VKCallBack callBack)
    {
        this.callBack = callBack;
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView webView, String url)
    {
        if (url.startsWith("http://oauth.vk.com/blank.html") || url.startsWith("https://oauth.vk.com/blank.html"))
        {
            AccessToken accessToken = getAccessToken(url);
            callBack.call(accessToken);
            return true;
        }
        return false;
    }

    private AccessToken getAccessToken(String link)
    {
        AccessToken receivedToken = null;
        if (link.indexOf("denied") < 0)
        {
            String token = parseForArgument(link, ACCESS_TOKEN_TAG);
            String expiresInString = parseForArgument(link, "expires_in");
            receivedToken = new AccessToken(token, Long.parseLong(expiresInString));
        }
        return receivedToken;
    }

    private String parseForArgument(String link, String argument)
    {
        String expression = String.format("%s=(.*?)(&|$)+", argument);
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(link);
        if (matcher.find())
        {
            return matcher.group(1);
        } else
        {
            throw new IllegalArgumentException("Precondition violated in " + "VKWebClient.parseForArgument(). Incorrect link or argument");
        }
    }

    public String parseForValueUsingRegex(String link, String argument)
    {
        return null;
    }

    public interface VKCallBack
    {
        void call(AccessToken accessToken);
    }

}
