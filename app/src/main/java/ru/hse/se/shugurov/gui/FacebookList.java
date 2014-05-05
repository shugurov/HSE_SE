package ru.hse.se.shugurov.gui;


import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.FacebookRequester;
import ru.hse.se.shugurov.social_networks.SocialNetworkTopic;
import ru.hse.se.shugurov.social_networks.VKTopicsAdapter;

/**
 * Created by Иван on 05.05.2014.
 */
public class FacebookList extends SocialNetworkAbstractList
{
    private final String FACEBOOK_TOPIC_TAG = "facebook_topic_tag";
    private final String TOPIC_COMPLETNESS_TAG = "facebook_topics_complete";
    private SocialNetworkTopic[] topics;
    private boolean topicsAreComplete;

    public FacebookList()
    {
    }

    public FacebookList(String groupId, String groupName, AccessToken accessToken)
    {
        super(groupId, groupName, accessToken);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && topics == null)
        {
            topics = (SocialNetworkTopic[]) savedInstanceState.getParcelableArray(FACEBOOK_TOPIC_TAG);
            topicsAreComplete = savedInstanceState.getBoolean(TOPIC_COMPLETNESS_TAG);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (topics == null)
        {
            requestTopics();
        } else
        {
            if (topicsAreComplete)
            {
                setListAdapter(new VKTopicsAdapter(getActivity(), topics));
            } else
            {
                requestPhotos();
            }
        }

    }

    private void requestTopics()
    {
        FacebookRequester.getPage(getGroupId(), getAccessToken(), new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String result)
            {
                if (result == null)
                {
                    Toast.makeText(getActivity(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
                } else
                {
                    if (result.contains("error"))
                    {
                        Toast.makeText(getActivity(), "Не удалось загрузить информацию", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        handleTopicsObtaining(result);
                    }
                }
            }
        });
    }

    private void handleTopicsObtaining(String result)
    {
        if (isVisible())
        {
            topics = FacebookRequester.getPage(result); //TODO do in different thread
            requestPhotos();
        }
    }

    private void requestPhotos()
    {
        FacebookRequester.getGroupPictureUrl(getGroupId(), new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String result)
            {
                FacebookRequester.fillPhotos(result, topics);
                topicsAreComplete = true;
                if (isVisible())
                {
                    setListAdapter(new VKTopicsAdapter(getActivity(), topics));
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(FACEBOOK_TOPIC_TAG, topics);
        outState.putBoolean(TOPIC_COMPLETNESS_TAG, topicsAreComplete);
    }

}
