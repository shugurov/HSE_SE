package ru.hse.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import ru.hse.shugurov.social_networks.AbstractRequester;

/**
 * Base class for all fragments used for social networks interacting.
 * <p/>
 * Fragment requires following arguments:
 * <ul>
 * <li>group id as a string with a key specified by {@code GROUP_ID_TAG}</li>
 * <li>group name as a string with a key specified by {@code GROUP_NAME_TAG}</li>
 * <li>{@link ru.hse.shugurov.social_networks.AbstractRequester}with a key specified by {@code REQUESTER_TAG}</li>
 * </ul>
 * <p/>
 * Is is assumed that method setArguments is called after putting all arguments in a bundle object
 * Created by Ivan Shugurov
 */
public abstract class SocialNetworkAbstractList extends ListFragment
{
    /*constants used as keys in bundle object*/
    public static final String GROUP_NAME_TAG = "group_name";
    public static final String GROUP_ID_TAG = "group_id";
    public static final String REQUESTER_TAG = "requester";


    private String groupId;
    private String groupName;
    private AbstractRequester requester;

    @Override
    public void setArguments(Bundle args)
    {
        super.setArguments(args);
        readStateFromBundle(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        readStateFromBundle(savedInstanceState);
        getActivity().setTitle(groupName);
    }

    /*extracts arguments from bundle object*/
    private void readStateFromBundle(Bundle args)
    {
        if (args != null)
        {
            groupId = args.getString(GROUP_ID_TAG);
            groupName = args.getString(GROUP_NAME_TAG);
            requester = (AbstractRequester) args.getSerializable(REQUESTER_TAG);
        }
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
