package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import ru.hse.se.shugurov.social_networks.AbstractRequester;

/**
 * Created by Иван on 04.05.2014.
 */
public abstract class SocialNetworkAbstractList extends ListFragment
{
    private static final String GROUP_NAME_TAG = "vk_group_name";
    private static final String GROUP_ID_TAG = "vk_group_id";
    private static final String REQUESTER_TAG = "requester";
    private String groupId;
    private String groupName;
    private AbstractRequester requester;

    public SocialNetworkAbstractList()
    {
    }

    public SocialNetworkAbstractList(String groupId, String groupName, AbstractRequester requester)
    {
        this.groupId = groupId;
        this.groupName = groupName;
        this.requester = requester;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            groupId = savedInstanceState.getString(GROUP_ID_TAG);
            groupName = savedInstanceState.getString(GROUP_NAME_TAG);
            requester = (AbstractRequester) savedInstanceState.getSerializable(REQUESTER_TAG);
        }
        getActivity().setTitle(groupName);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(GROUP_NAME_TAG, groupName);
        outState.putString(GROUP_ID_TAG, groupId);
        outState.putSerializable(REQUESTER_TAG, requester);
    }

    protected String getGroupId()
    {
        return groupId;
    }

    protected String getGroupName()
    {
        return groupName;
    }

    protected AbstractRequester getRequester()
    {
        return requester;
    }
}
