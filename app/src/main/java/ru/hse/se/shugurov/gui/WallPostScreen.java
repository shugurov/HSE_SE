package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import ru.hse.se.shugurov.social_networks.AbstractRequester;
import ru.hse.se.shugurov.social_networks.SocialNetworkTopic;
import ru.hse.se.shugurov.social_networks.VkWallPostsAdapter;

/**
 * Created by Иван on 02.05.2014.
 */
public class WallPostScreen extends SocialNetworkAbstractList
{
    private static final String POSTS_ID = "vk_posts_array";
    private SocialNetworkTopic[] posts;

    public WallPostScreen()
    {
    }

    public WallPostScreen(String groupId, String groupName, AbstractRequester requester)
    {
        super(groupId, groupName, requester);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (posts == null)
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
        setListAdapter(new VkWallPostsAdapter(getActivity(), posts));
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                WallCommentsScreen wallCommentsScreen = new WallCommentsScreen(getGroupId(), getGroupName(), posts[position], getRequester());
                ScreenFactory.changeFragments(getFragmentManager(), wallCommentsScreen);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            posts = (SocialNetworkTopic[]) savedInstanceState.getParcelableArray(POSTS_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(POSTS_ID, posts);
    }
}
