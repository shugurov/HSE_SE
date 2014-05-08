package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import ru.hse.se.shugurov.social_networks.AbstractRequester;
import ru.hse.se.shugurov.social_networks.SocialNetworkTopic;
import ru.hse.se.shugurov.social_networks.StateListener;
import ru.hse.se.shugurov.social_networks.VkWallPostsAdapter;

/**
 * Created by Иван on 02.05.2014.
 */
public class WallPostScreen extends SocialNetworkAbstractList
{
    private static final String POSTS_ID = "vk_posts_array";
    private static final String COMMENTS_STATE_TAG = "comments_state_tag";
    private SocialNetworkTopic[] posts;
    private boolean commentsChanged = false;

    public WallPostScreen()
    {
    }

    public WallPostScreen(String groupId, String groupName, AbstractRequester requester)
    {
        super(groupId, groupName, requester);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (posts == null || commentsChanged)
        {
            getRequester().getWallPosts(getGroupId(), new AbstractRequester.RequestResultListener<SocialNetworkTopic>()
            {
                @Override
                public void resultObtained(SocialNetworkTopic[] resultPosts)
                {
                    if (resultPosts == null)
                    {
                        Toast.makeText(getActivity(), "Не удалось загрузить информацию", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        posts = resultPosts;
                        fillList();
                    }
                }
            });
        } else
        {
            fillList();
        }
    }

    private void fillList()
    {
        commentsChanged = false;
        if (isAdded())
        {
            setListAdapter(new VkWallPostsAdapter(getActivity(), posts));
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    StateListener stateListener = new StateListener()
                    {
                        @Override
                        public void stateChanged()
                        {
                            commentsChanged = true;
                        }
                    };
                    WallCommentsFragment wallCommentsFragment = new WallCommentsFragment(getGroupId(), getGroupName(), posts[position], getRequester(), stateListener);
                    ScreenFactory.changeFragments(getFragmentManager(), wallCommentsFragment);
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            posts = (SocialNetworkTopic[]) savedInstanceState.getParcelableArray(POSTS_ID);
            commentsChanged = savedInstanceState.getBoolean(COMMENTS_STATE_TAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(POSTS_ID, posts);
        outState.putBoolean(COMMENTS_STATE_TAG, commentsChanged);
    }
}
