package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import ru.hse.se.shugurov.social_networks.AccessToken;

/**
 * Created by Иван on 04.05.2014.
 */
public abstract class SocialNetworkAbstractList extends ListFragment
{
    private static final String GROUP_NAME_TAG = "vk_group_name";
    private static final String GROUP_ID_TAG = "vk_group_id";
    private final static String ACCESS_TOKEN_TAG = "vk_access_token";
    private String groupId;
    private AccessToken accessToken;
    private String groupName;

    public SocialNetworkAbstractList()
    {
    }

    public SocialNetworkAbstractList(String groupId, String groupName, AccessToken accessToken)
    {
        this.groupId = groupId;
        this.groupName = groupName;
        this.accessToken = accessToken;
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
        }
        getActivity().setTitle(groupName);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(GROUP_NAME_TAG, groupName);
        outState.putString(GROUP_ID_TAG, groupId);
        outState.putSerializable(ACCESS_TOKEN_TAG, accessToken);
    }

    protected String getGroupId()
    {
        return groupId;
    }

    protected String getGroupName()
    {
        return groupName;
    }

    protected AccessToken getAccessToken()
    {
        return accessToken;
    }
}
