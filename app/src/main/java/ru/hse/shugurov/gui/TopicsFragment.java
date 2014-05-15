package ru.hse.shugurov.gui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.hse.shugurov.R;
import ru.hse.shugurov.social_networks.AbstractRequester;
import ru.hse.shugurov.social_networks.SocialNetworkTopic;
import ru.hse.shugurov.social_networks.StateListener;
import ru.hse.shugurov.social_networks.TopicsAdapter;

/**
 * Used for requesting and showing a list of social network topics
 * <p/>
 * For the required arguments see{@link ru.hse.shugurov.gui.SocialNetworkAbstractList}
 * <p/>
 * Created by Ivan Shugurov
 */
public class TopicsFragment extends SocialNetworkAbstractList
{
    /*constants used as keys in bundle object*/
    private final static String VK_TOPICS_TAG = "topics_array";
    private final static String COMMENTS_STATE = "comments_change-state";


    private SocialNetworkTopic[] topics;
    private TopicsAdapter adapter;
    private AtomicBoolean stateChanged = new AtomicBoolean(false);

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (topics == null || stateChanged.get())
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
                        stateChanged.set(false);
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
            Parcelable[] parcelables = savedInstanceState.getParcelableArray(VK_TOPICS_TAG);
            if (parcelables == null)
            {
                topics = new SocialNetworkTopic[0];
            } else
            {
                topics = Arrays.copyOf(parcelables, parcelables.length, SocialNetworkTopic[].class);
            }
            stateChanged = (AtomicBoolean) savedInstanceState.getSerializable(COMMENTS_STATE);
        }
        setHasOptionsMenu(getRequester().canAddPosts());
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
        Fragment topicCreation = new TopicCreationFragment();
        Bundle arguments = new Bundle();
        arguments.putString(TopicCreationFragment.TITLE_TAG, getGroupName());
        arguments.putString(TopicCreationFragment.GROUP_ID_TAG, getGroupId());
        arguments.putSerializable(TopicCreationFragment.REQUESTER_TAG, getRequester());
        StateListener stateListener = new StateListener(stateChanged);
        arguments.putSerializable(TopicCreationFragment.STATE_LISTENER_TAG, stateListener);
        topicCreation.setArguments(arguments);
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
                StateListener listener = new StateListener(stateChanged);
                CommentsFragment commentsFragment = new CommentsFragment();
                Bundle arguments = new Bundle();
                arguments.putString(SocialNetworkAbstractList.GROUP_ID_TAG, getGroupId());
                arguments.putString(SocialNetworkAbstractList.GROUP_NAME_TAG, getGroupName());
                arguments.putString(CommentsFragment.TOPIC_ID_TAG, adapter.getItem(position).getId());
                arguments.putSerializable(SocialNetworkAbstractList.REQUESTER_TAG, getRequester());
                arguments.putSerializable(CommentsFragment.COMMENTS_LISTENER_TAG, listener);
                arguments.putString(CommentsFragment.TOPIC_TITLE_TAG, adapter.getItem(position).getTitle());
                commentsFragment.setArguments(arguments);
                ScreenFactory.changeFragments(getFragmentManager(), commentsFragment);
            }
        });
    }

    /*sets adapter to a list view, also checks if activity is not null*/
    private void setAdapter()
    {
        if (getActivity() != null)
        {
            adapter = new TopicsAdapter(getActivity(), topics, getRequester().showCommentsQuantity());
            setListAdapter(adapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(VK_TOPICS_TAG, topics);
        outState.putSerializable(COMMENTS_STATE, stateChanged);
    }
}
