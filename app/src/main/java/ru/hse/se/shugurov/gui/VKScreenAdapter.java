package ru.hse.se.shugurov.gui;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import ru.hse.se.shugurov.MainActivity;
import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.ViewsPackage.VKHSEView;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.VKAbstractItem;
import ru.hse.se.shugurov.social_networks.VKCommentsAdapter;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.social_networks.VKTopicsAdapter;
import ru.hse.se.shugurov.social_networks.VkWebClient;

/**
 * Created by Иван on 14.03.14.
 */
public class VKScreenAdapter extends ScreenAdapter
{
    private static final String ACCESS_TOKEN_TAG = "access_token";
    private static final String SHARED_PREFERENCES_TAG = "social_networks";
    private VKRequester requester;

    public VKScreenAdapter(MainActivity.MainActivityCallback callback, ViewGroup container, View previousView, HSEView vkHseView)//TODO кнопка назад, когда была регистрация
    {
        super(callback, container, previousView, vkHseView);
        SharedPreferences preferences = getContext().getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
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
                showListOfTopics();
            }
        }

    }

    private void makeRequestForAccessToken()
    {
        WebView vkView;
        vkView = new WebView(getContext());
        vkView.loadUrl(VkWebClient.OAUTH);
        vkView.setWebViewClient(new VkWebClient(new VkWebClient.VKCallBack()
        {
            @Override
            public void call(AccessToken accessToken)//TODO что делать с пустым токеном
            {
                requester = new VKRequester(accessToken);
                registerAccessTokenInPreferences(accessToken);
                showListOfTopics();
            }
        }));
        changeViews(vkView);
    }

    private void showListOfTopics()
    {
        final ListView vkList = (ListView) getLayoutInflater().inflate(R.layout.activity_main_list, getContainer(), false);
        requester.getTopics(getHseView().getObjectID(), new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String result)
            {
                if (result == null)
                {
                    Toast.makeText(getContext(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
                } else
                {
                    final VKTopicsAdapter adapter = new VKTopicsAdapter(getContext(), requester.getTopicsAdapter(result));
                    vkList.setAdapter(adapter);
                    vkList.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            showResponses(adapter.getItem(position).getTopicID());
                        }
                    });
                }
            }
        });
        changeViews(vkList);
    }

    private void showResponses(int topicID)
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
                    VKCommentsAdapter vkCommentsAdapter = new VKCommentsAdapter(getContext(), comments);
                    ListView responsesListView = (ListView) getLayoutInflater().inflate(R.layout.activity_main_list, getContainer(), false);
                    responsesListView.setAdapter(vkCommentsAdapter);
                    changeViews(responsesListView);
                }
            }
        });
    }

    private void registerAccessTokenInPreferences(AccessToken accessToken)
    {
        SharedPreferences preferences = getContext().getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(ACCESS_TOKEN_TAG, accessToken.getStringRepresentation());
        preferencesEditor.commit();
    }

    @Override
    protected VKHSEView getHseView()
    {
        return (VKHSEView) super.getHseView();
    }
}
