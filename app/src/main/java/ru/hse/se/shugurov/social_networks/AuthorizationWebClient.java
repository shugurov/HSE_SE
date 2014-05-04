package ru.hse.se.shugurov.social_networks;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Иван on 04.05.2014.
 */
public class AuthorizationWebClient extends WebViewClient
{
    private static final String ACCESS_TOKEN_TAG = "access_token";
    private String waitFor;
    private TokenCallback callback;

    public AuthorizationWebClient(String waitFor, TokenCallback callback)
    {
        this.waitFor = waitFor;
        this.callback = callback;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        if (url.startsWith(waitFor))
        {
            AccessToken accessToken = getAccessToken(url);
            callback.call(accessToken);
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

    public interface TokenCallback
    {
        void call(AccessToken accessToken);
    }
}
