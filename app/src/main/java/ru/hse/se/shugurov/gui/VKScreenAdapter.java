package ru.hse.se.shugurov.gui;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewFragment;
import android.widget.ListView;
import android.widget.Toast;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.screens.HSEView;
import ru.hse.se.shugurov.screens.VKHSEView;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.VKAbstractItem;
import ru.hse.se.shugurov.social_networks.VKCommentsAdapter;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.social_networks.VKTopicsAdapter;
import ru.hse.se.shugurov.social_networks.VkWebClient;

/**
 * Created by Иван on 14.03.14.
 */
public class VKScreenAdapter extends ScreenAdapter//TODO а нужен ли main_list вообще?
{
    private static final String ACCESS_TOKEN_TAG = "access_token";
    private static final String SHARED_PREFERENCES_TAG = "social_networks";
    private VKRequester requester;

    public VKScreenAdapter(HSEView vkHseView)//TODO кнопка назад, когда была регистрация
    {
        super(vkHseView);
    }

    private void makeRequestForAccessToken()
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
        //changeFragments(getFragmentManager(), webViewFragment); TODO
    }

    private void setTopicsAdapter(final ListFragment listFragment)
    {
        requester.getTopics(getHseView().getObjectID(), new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String result)
            {
                if (result == null)
                {
                    Toast.makeText(getActivity(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
                } else
                {
                    final VKTopicsAdapter adapter = new VKTopicsAdapter(getActivity(), requester.getTopicsAdapter(result));
                    listFragment.setListAdapter(adapter);
                    /*listFragment.set(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            showResponses(inflater, container, adapter.getItem(position).getTopicID());
                        }
                    });TODO */
                }
            }
        });
        //changeFragments(); TODO
        //changeFragments(getFragmentManager(), listFragment);
    }

    private void showResponses(final LayoutInflater inflater, final ViewGroup container, int topicID)
    {
        requester.getComments(getHseView().getObjectID(), topicID, new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String result)//TODO что делать с пустым результатом
            {
                VKAbstractItem[] comments = requester.getComments(result);
                if (comments == null)
                {
                    //TODO что делать, если массив комментариев пуст?
                } else
                {
                    VKCommentsAdapter vkCommentsAdapter = new VKCommentsAdapter(getActivity(), comments);
                    ListView responsesListView = (ListView) inflater.inflate(R.layout.activity_main_list, container, false);
                    responsesListView.setAdapter(vkCommentsAdapter);
                    //changeFragments(); TODO
                }
            }
        });
    }

    private void registerAccessTokenInPreferences(AccessToken accessToken)
    {
        SharedPreferences preferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(ACCESS_TOKEN_TAG, accessToken.getStringRepresentation());
        preferencesEditor.commit();
    }

    @Override
    protected VKHSEView getHseView()
    {
        return (VKHSEView) super.getHseView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        SharedPreferences preferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
        String serializedToken = preferences.getString(ACCESS_TOKEN_TAG, null);
        if (serializedToken == null)
        {
            makeRequestForAccessToken();
        } else
        {
            AccessToken accessToken = new AccessToken(serializedToken);
            if (accessToken.hasExpired())
            {
                makeRequestForAccessToken();
            } else
            {
                requester = new VKRequester(accessToken);
                setTopicsAdapter(showListFragment());
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private ListFragment showListFragment()
    {
        ListFragment listFragment = new ListFragment();
        // changeFragments(getFragmentManager(), listFragment); TODO
        return listFragment;
    }
}
