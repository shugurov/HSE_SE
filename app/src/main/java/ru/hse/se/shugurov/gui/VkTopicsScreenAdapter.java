package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.social_networks.VKTopic;
import ru.hse.se.shugurov.social_networks.VKTopicsAdapter;

/**
 * Created by Иван on 02.05.2014.
 */
public class VkTopicsScreenAdapter extends ListFragment//TODo shall I store adapter?
{
    private final static String GROUP_ID_TAG = "vk_group_id_topics";
    private final static String ACCESS_TOKEN_TAG = "vk_access_token_topics";
    private final static String GROUP_NAME_TAG = "vk_group_name_topics";
    private final static String VK_TOPICS_TAG = "vk_topics_array";
    private String groupId;
    private String groupName;
    private AccessToken accessToken;
    private VKTopic[] topics;
    private VKTopicsAdapter adapter;

    public VkTopicsScreenAdapter()
    {
    }

    public VkTopicsScreenAdapter(String groupName, String groupId, AccessToken accessToken)
    {
        this.groupId = groupId;
        this.accessToken = accessToken;
        this.groupName = groupName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            groupId = savedInstanceState.getString(GROUP_ID_TAG);
            accessToken = (AccessToken) savedInstanceState.getSerializable(ACCESS_TOKEN_TAG);
            groupName = savedInstanceState.getString(GROUP_NAME_TAG);
            topics = (VKTopic[]) savedInstanceState.getParcelableArray(VK_TOPICS_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)//TODO fix comments number
    {
        getActivity().setTitle(groupName);
        View resultView = super.onCreateView(inflater, container, savedInstanceState);
        if (topics == null)
        {
            final VKRequester requester = new VKRequester(accessToken);
            requester.getTopics(groupId, new Requester.RequestResultCallback()
            {
                @Override
                public void pushResult(String result)
                {
                    if (result == null)
                    {
                        Toast.makeText(getActivity(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
                        topics = new VKTopic[0];
                    } else
                    {
                        fillList(result, requester);
                    }
                }
            });
        } else
        {
            setAdapter();
        }

        return resultView;
    }

    private void fillList(final String result, final VKRequester requester)
    {
        Runnable parsing = new Runnable()
        {
            @Override
            public void run()
            {
                topics = requester.getTopics(result);
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        setAdapter();
                    }
                });
            }
        };
        new Thread(parsing).start();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ScreenFactory.changeFragments(getFragmentManager(), new VkResponsesScreenAdapter(groupName, groupId, adapter.getItem(position).getTopicID(), accessToken));
            }
        });
    }


    private void setAdapter()
    {
        adapter = new VKTopicsAdapter(getActivity(), topics);
        setListAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(GROUP_ID_TAG, groupId);
        outState.putSerializable(ACCESS_TOKEN_TAG, accessToken);
        outState.putString(GROUP_NAME_TAG, groupName);
        outState.putParcelableArray(VK_TOPICS_TAG, topics);
    }
}
