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
import ru.hse.se.shugurov.social_networks.AbstractRequester;
import ru.hse.se.shugurov.social_networks.SocialNetworkTopic;
import ru.hse.se.shugurov.social_networks.StateListener;
import ru.hse.se.shugurov.social_networks.TopicsListAdapter;

/**
 * Created by Иван on 02.05.2014.
 */
public class SocialNetworkTopicsFragment extends SocialNetworkAbstractList
{
    private final static String VK_TOPICS_TAG = "vk_topics_array";
    private final static String COMMENTS_STATE = "comments_change-state";
    private SocialNetworkTopic[] topics;
    private TopicsListAdapter adapter;
    private boolean commentsChanged = false;

    public SocialNetworkTopicsFragment()
    {
    }

    public SocialNetworkTopicsFragment(String groupId, String groupName, AbstractRequester requester)
    {
        super(groupId, groupName, requester);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (topics == null || commentsChanged)
        {
            getRequester().getTopics(getGroupId(), new AbstractRequester.RequestResultListener<SocialNetworkTopic>()
            {
                @Override
                public void resultObtained(SocialNetworkTopic[] resultTopics)
                {
                    if (resultTopics == null)
                    {
                        Toast.makeText(getActivity(), "Не удалось загрузить информацию", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        topics = resultTopics;
                        commentsChanged = false;
                        setAdapter();
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
            topics = (SocialNetworkTopic[]) savedInstanceState.getParcelableArray(VK_TOPICS_TAG);
            commentsChanged = savedInstanceState.getBoolean(COMMENTS_STATE);
        }
        setHasOptionsMenu(true);
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
        Fragment topicCreation = new TopicCreationScreen(getGroupName(), getGroupId(), getRequester());
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
                StateListener listener = new StateListener()
                {
                    @Override
                    public void stateChanged()
                    {
                        commentsChanged = false;
                    }
                };
                ScreenFactory.changeFragments(getFragmentManager(), new SocialNetworkCommentsFragment(getGroupId(), getGroupName(), adapter.getItem(position).getId(), getRequester(), listener));
            }
        });
    }


    private void setAdapter()
    {
        if (getActivity() != null)
        {
            adapter = new TopicsListAdapter(getActivity(), topics);
            setListAdapter(adapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(VK_TOPICS_TAG, topics);
        outState.putBoolean(COMMENTS_STATE, commentsChanged);
    }
}
