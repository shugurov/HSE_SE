package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.social_networks.VkWallPost;
import ru.hse.se.shugurov.social_networks.VkWallPostsAdapter;

/**
 * Created by Иван on 02.05.2014.
 */
public class VkWallPostScreen extends ListFragment
{
    private static final String TITLE_TAG = "vk_wall_posts_title";
    private static final String GROUP_ID_TAG = "vk_wall_posts_group_id";
    private static final String POSTS_ID = "vk_posts_array";
    private String title;
    private String groupId;
    private VkWallPost[] posts;
    private AccessToken accessToken;

    public VkWallPostScreen()
    {
    }

    public VkWallPostScreen(String title, String groupId, AccessToken accessToken)
    {
        this.title = title;
        this.groupId = groupId;
        this.accessToken = accessToken;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(title);
        if (posts == null)
        {
            final VKRequester requester = new VKRequester(accessToken);
            requester.getWallPosts(groupId, new Requester.RequestResultCallback()
            {
                @Override
                public void pushResult(String wallPostsJson)//TODO check if it is not null
                {
                    posts = requester.getWallPosts(wallPostsJson);
                    setListAdapter(new VkWallPostsAdapter(getActivity(), posts));
                }
            });
        } else
        {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            title = savedInstanceState.getString(TITLE_TAG);
            groupId = savedInstanceState.getString(GROUP_ID_TAG);
            posts = (VkWallPost[]) savedInstanceState.getParcelableArray(POSTS_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(GROUP_ID_TAG, groupId);
        outState.putString(TITLE_TAG, title);
        outState.putParcelableArray(POSTS_ID, posts);
    }
}
