package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.social_networks.VKTopic;
import ru.hse.se.shugurov.social_networks.VKTopicsAdapter;

/**
 * Created by Иван on 02.05.2014.
 */
public class VkTopicsScreenAdapter extends VkAbstractList
{
    private final static String VK_TOPICS_TAG = "vk_topics_array";
    private VKTopic[] topics;
    private VKTopicsAdapter adapter;

    public VkTopicsScreenAdapter()
    {
    }

    public VkTopicsScreenAdapter(String groupId, String groupName, AccessToken accessToken)
    {
        super(groupId, groupName, accessToken);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (topics == null)
        {
            final VKRequester requester = getVkRequester();
            requester.getTopics(getGroupId(), new Requester.RequestResultCallback()
            {
                @Override
                public void pushResult(String result)
                {
                    if (result == null)
                    {
                        Toast.makeText(getActivity(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            topics = (VKTopic[]) savedInstanceState.getParcelableArray(VK_TOPICS_TAG);
        }
        setHasOptionsMenu(true);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.message_adding_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Fragment topicCreation = new VkTopicCreationScreen(getGroupName(), getGroupId(), getAccessToken());
        ScreenFactory.changeFragments(getFragmentManager(), topicCreation);
        return super.onOptionsItemSelected(item);
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
                ScreenFactory.changeFragments(getFragmentManager(), new VkResponsesScreenAdapter(getGroupId(), getGroupName(), adapter.getItem(position).getId(), getAccessToken()));
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
        outState.putParcelableArray(VK_TOPICS_TAG, topics);
    }
}
