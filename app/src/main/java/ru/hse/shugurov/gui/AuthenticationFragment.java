package ru.hse.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.Serializable;

import ru.hse.shugurov.R;
import ru.hse.shugurov.social_networks.AccessToken;
import ru.hse.shugurov.social_networks.AuthorizationWebClient;
import ru.hse.shugurov.social_networks.FacebookRequester;
import ru.hse.shugurov.social_networks.VKRequester;

/**
 * This class is used for authentication in social networks. It is assumed that these networks uses OAuth 2.
 * In order to ger access token custom {@code WebViewClient}  is provided to {@code WebView}.
 * <p/>
 * Fragment requires  following arguments:
 * <ul>
 * <li>{@link java.lang.String} url with a key specified by {@code URL_TAG}. This url should lead
 * to authentication form</li>
 * <li>{@link ru.hse.shugurov.gui.AuthenticationFragment.AccessTokenRequest} with a key
 * specified by {@code TOKEN_REQUEST_TAG}.</li>
 * </ul>
 *
 * @author Ivan Shugurov
 */
public class AuthenticationFragment extends Fragment
{
    /*constants used as keys in bundle object*/
    public final static String URL_TAG = "authorization_url_tag";
    public final static String TOKEN_REQUEST_TAG = "access_token_request_tag";

    private String url;
    private AccessTokenRequest accessTokenRequest;

    @Override
    public void setArguments(Bundle args)
    {
        super.setArguments(args);
        readStateFromBundle(args);
    }

    /*retrieves arguments from Bundle object and makes necessary casts*/
    private void readStateFromBundle(Bundle args)
    {
        if (args != null)
        {
            url = args.getString(URL_TAG);
            accessTokenRequest = (AccessTokenRequest) args.getSerializable(TOKEN_REQUEST_TAG);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        readStateFromBundle(savedInstanceState);
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
                    getFragmentManager().popBackStack();
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
                    getFragmentManager().popBackStack();
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
