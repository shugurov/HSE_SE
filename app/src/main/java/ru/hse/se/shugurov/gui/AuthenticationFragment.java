package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.Serializable;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.AuthorizationWebClient;
import ru.hse.se.shugurov.social_networks.FacebookRequester;
import ru.hse.se.shugurov.social_networks.VKRequester;

/**
 * This class is used for authentication in social networks. It is assumed that these networks uses OAuth 2.
 * In order to ger access token custom {@code WebViewClient}  is provided to {@code WebView}.
 *
 * @author Ivan Shugurov
 */
public class AuthenticationFragment extends Fragment
{
    /*constants used for saving fragment state*/
    private final String URL_TAG = "authorization_url_tag";
    private final String TOKEN_REQUEST_TAG = "access_token_request_tag";

    private String url;
    private AccessTokenRequest accessTokenRequest;

    /**
     * Default constructor used by Android for instantiating this class after it was destroyed.
     * Should not be used by developers.
     */
    public AuthenticationFragment()
    {
    }

    /**
     * Constructs new {@code AuthorizationFragment}
     *
     * @param url                OAuth url. not null
     * @param accessTokenRequest object for callback. not null
     */
    public AuthenticationFragment(String url, AccessTokenRequest accessTokenRequest)
    {
        this.url = url;
        this.accessTokenRequest = accessTokenRequest;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && url == null)
        {
            url = savedInstanceState.getString(URL_TAG);
            accessTokenRequest = (AccessTokenRequest) savedInstanceState.getSerializable(TOKEN_REQUEST_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        WebView webView = (WebView) inflater.inflate(R.layout.internal_web_view, container, false);
        webView.loadUrl(url);
        webView.setWebViewClient(createWebViewClient(url));
        return webView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(URL_TAG, url);
        outState.putSerializable(TOKEN_REQUEST_TAG, accessTokenRequest);
    }

    /*factory method for creating WebViewClient. Analyses given Oauth url and decides which
    * subclass of WebViewClient to use*/
    private WebViewClient createWebViewClient(String url)
    {
        if (url.contains("vk"))
        {
            return new AuthorizationWebClient(VKRequester.REDIRECTION_URL, new AuthorizationWebClient.TokenCallback()
            {
                @Override
                public void call(AccessToken accessToken)
                {
                    accessTokenRequest.receiveToken(accessToken);
                }
            });
        }
        if (url.contains("facebook"))
        {
            return new AuthorizationWebClient(FacebookRequester.REDIRECTION_URL, new AuthorizationWebClient.TokenCallback()
            {
                @Override
                public void call(AccessToken accessToken)
                {
                    accessTokenRequest.receiveToken(accessToken);
                }
            });
        } else
        {
            return new WebViewClient();
        }
    }

    /**
     * Interface for callback objects. These objects are used for notifying listener about obtained access token
     *
     * @author Ivan Shugurov
     */
    public interface AccessTokenRequest extends Serializable
    {
        /**
         * Sends notification whereas request for access token was successful and if it was sends obtained token
         *
         * @param accessToken {@code null} if access token was not obtained.
         */
        void receiveToken(AccessToken accessToken);
    }
}
