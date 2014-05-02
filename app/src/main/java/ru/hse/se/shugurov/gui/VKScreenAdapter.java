package ru.hse.se.shugurov.gui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.HSEView;
import ru.hse.se.shugurov.screens.VKHSEView;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.social_networks.VkWebClient;

/**
 * Created by Иван on 14.03.14.
 */
public class VKScreenAdapter extends ScreenAdapter
{
    private static final String SHARED_PREFERENCES_TAG = "social_networks";
    private VKRequester requester;
    private AccessToken accessToken;

    public VKScreenAdapter()
    {
    }

    public VKScreenAdapter(HSEView vkHseView)//TODO кнопка назад, когда была регистрация
    {
        super(vkHseView);
    }

   /* private void makeRequestForAccessToken()
    {
        WebViewFragment webViewFragment = new WebViewFragment()
        {
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {//TODO (
                View result = super.onCreateView(inflater, container, savedInstanceState);
                getWebView().setWebViewClient(new VkWebClient(new VkWebClient.VKCallBack()
                {
                    @Override
                    public void call(AccessToken accessToken)
                    {
                        requester = new VKRequester(accessToken);
                        registerAccessTokenInPreferences(accessToken);
                        setTopicsAdapter(showListFragment());
                    }
                }));
                return result;
            }
        };
        VkWebClient  vkWebClient= new VkWebClient(new VkWebClient.VKCallBack()
        {
            @Override
            public void call(AccessToken accessToken)
            {

            }
        });
        InternalWebPage internalWebPage = new InternalWebPage(VkWebClient.OAUTH, vkWebClient);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(internalWebPage, null);
        transaction.commit();
        //changeFragments(getFragmentManager(), webViewFragment);
    }TODO I don't save access token*/




    /*private void registerAccessTokenInPreferences(AccessToken accessToken)
    {
        SharedPreferences preferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(ACCESS_TOKEN_TAG, accessToken.getStringRepresentation());
        preferencesEditor.commit();
    } TODO*/

    @Override
    protected VKHSEView getHseView()
    {
        return (VKHSEView) super.getHseView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //SharedPreferences preferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
        /*String serializedToken = preferences.getString(ACCESS_TOKEN_TAG, null);
        if (serializedToken == null)
        {
            return getAccessToken(inflater, container);
        } else
        {
            AccessToken accessToken = new AccessToken(serializedToken);
            if (accessToken.hasExpired())
            {
                return getAccessToken(inflater, container);
            } else
            {
                requester = new VKRequester(accessToken);
                setTopicsAdapter(showListFragment());
            }
        }*/
        //return super.onCreateView(inflater, container, savedInstanceState); TODO
        return getAccessToken(inflater, container);//TODO delete

    }

    private WebView getAccessToken(LayoutInflater inflater, final ViewGroup container)//TODO apply token saving
    {
        final WebView webView = (WebView) inflater.inflate(R.layout.internal_web_view, container, false);
        VkWebClient vkWebClient = new VkWebClient(new VkWebClient.VKCallBack()
        {
            @Override
            public void call(AccessToken accessToken)
            {
                if (accessToken == null)
                {
                    Toast.makeText(getActivity(), "Null", Toast.LENGTH_SHORT).show();//TODO delete
                    getFragmentManager().popBackStack();
                } else
                {
                    //BEGIN TODO
                    Toast.makeText(getActivity(), "Not null", Toast.LENGTH_SHORT).show();
                    ScreenFactory.changeFragments(getFragmentManager(), new VkTopicsScreenAdapter(getHseView().getObjectID(), accessToken));
                    //END TODO
                }
            }
        });
        webView.setWebViewClient(vkWebClient);
        webView.loadUrl(VkWebClient.OAUTH);
        return webView;
    }

}
