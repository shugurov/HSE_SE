package ru.hse.se.shugurov.gui;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.VKAbstractItem;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.social_networks.VKResponsesAdapter;

/**
 * Created by Иван on 02.05.2014.
 */
public class VkResponsesScreenAdapter extends ListFragment//TODO dates!!!
{//TODO images
    //TODO multithreading
    private static String GROUP_ID_TAG = "vk_group_id_responses";
    private static String TOPIC_ID_TAG = "vk_topic_id_responses";
    private static String ACCESS_TOKEN_TAG = "vk_access_token_responses";
    private String groupId;
    private int topicId;
    private AccessToken accessToken;

    public VkResponsesScreenAdapter()
    {
    }

    public VkResponsesScreenAdapter(String groupID, int topicId, AccessToken accessToken)//TODO why do groupId and topicId  have differentTypes?
    {
        this.groupId = groupID;
        this.topicId = topicId;
        this.accessToken = accessToken;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && groupId == null)
        {
            groupId = savedInstanceState.getString(GROUP_ID_TAG);
            topicId = savedInstanceState.getInt(TOPIC_ID_TAG);
            accessToken = (AccessToken) savedInstanceState.getSerializable(ACCESS_TOKEN_TAG);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        final VKRequester requester = new VKRequester(accessToken);
        requester.getComments(groupId, topicId, new Requester.RequestResultCallback()
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
                    VKResponsesAdapter vkResponsesAdapter = new VKResponsesAdapter(getActivity(), comments);
                    setListAdapter(vkResponsesAdapter);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(GROUP_ID_TAG, groupId);
        outState.putInt(TOPIC_ID_TAG, topicId);
        outState.putSerializable(ACCESS_TOKEN_TAG, accessToken);
    }


}
