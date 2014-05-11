package ru.hse.se.shugurov.social_networks;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Used to wait for a particular link. When desired link is caught, object of the class
 * parses it and gets an access token
 * <p/>
 * Created by Ivan Shugurov
 */
public class AuthorizationWebClient extends WebViewClient
{
    private static final String ACCESS_TOKEN_TAG = "access_token";
    private String waitFor;
    private TokenCallback callback;

    /**
     * url beginning which specifies a url with information about an access token. Access token should have a key access_token.
     * Expiration time should have a key expires_in
     *
     * @param waitFor
     * @param callback
     */
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

    /*creates a new access token*/
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

    /*parses given url for a value with a specified key*/
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

    /**
     * Used to notify caller about access token
     */
    public interface TokenCallback
    {
        /**
         * If access token is gor successfully then passes it, otherwise passes null
         *
         * @param accessToken
         */
        void call(AccessToken accessToken);
    }
}
