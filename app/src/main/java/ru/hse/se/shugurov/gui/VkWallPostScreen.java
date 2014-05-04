package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.social_networks.VKTopic;
import ru.hse.se.shugurov.social_networks.VkWallPostsAdapter;

/**
 * Created by Иван on 02.05.2014.
 */
public class VkWallPostScreen extends VkAbstractList
{
    private static final String POSTS_ID = "vk_posts_array";
    private VKTopic[] posts;

    public VkWallPostScreen()
    {
    }

    public VkWallPostScreen(String groupId, String groupName, AccessToken accessToken)
    {
        super(groupId, groupName, accessToken);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (posts == null)
        {
            final VKRequester requester = getVkRequester();
            requester.getWallPosts(getGroupId(), new Requester.RequestResultCallback()
            {
                @Override
                public void pushResult(String wallPostsJson)//TODO multithreading
                {
                    if (wallPostsJson == null)
                    {
                        Toast.makeText(getActivity(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        posts = requester.getWallPosts(wallPostsJson);//TODo do in another thread
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
                WallCommentsScreen wallCommentsScreen = new WallCommentsScreen(getGroupId(), getGroupName(), getAccessToken(), posts[position]);
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
            posts = (VKTopic[]) savedInstanceState.getParcelableArray(POSTS_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(POSTS_ID, posts);
    }
}
