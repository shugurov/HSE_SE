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
 * Created by Иван on 02.05.2014.
 */
public class AuthorizationFragment extends Fragment
{
    private String url;
    private AccessTokenRequest accessTokenRequest;

    public AuthorizationFragment()
    {
    }

    public AuthorizationFragment(String url, AccessTokenRequest accessTokenRequest)
    {
        this.url = url;
        this.accessTokenRequest = accessTokenRequest;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        WebView webView = (WebView) inflater.inflate(R.layout.internal_web_view, container, false);
        webView.loadUrl(url);
        webView.setWebViewClient(createWebViewClient(url));
        return webView;
    }


    private WebViewClient createWebViewClient(String url)//TODO fix return value and add other web clients
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
            return null;
        }
    }

    public interface AccessTokenRequest extends Serializable
    {
        void receiveToken(AccessToken accessToken);
    }
}
