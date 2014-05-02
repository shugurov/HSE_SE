package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.social_networks.VKTopicsAdapter;

/**
 * Created by Иван on 02.05.2014.
 */
public class VkTopicsScreenAdapter extends ListFragment//TODo shall I store adapter?
{
    private static String GROUP_ID_TAG = "vk_group_id_topics";
    private static String ACCESS_TOKEN_TAG = "vk_access_token_topics";
    private String groupId;
    private AccessToken accessToken;

    public VkTopicsScreenAdapter()
    {
    }

    public VkTopicsScreenAdapter(String groupId, AccessToken accessToken)
    {
        this.groupId = groupId;
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View resultView = super.onCreateView(inflater, container, savedInstanceState);
        final VKRequester requester = new VKRequester(accessToken);
        requester.getTopics(groupId, new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String result)
            {
                if (result == null)
                {
                    Toast.makeText(getActivity(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
                } else
                {
                    final VKTopicsAdapter adapter = new VKTopicsAdapter(getActivity(), requester.getTopicsAdapter(result));
                    setListAdapter(adapter);
                    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            ScreenFactory.changeFragments(getFragmentManager(), new VkResponsesScreenAdapter(groupId, adapter.getItem(position).getTopicID(), accessToken));
                        }
                    });
                }
            }
        });
        return resultView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(GROUP_ID_TAG, groupId);
        outState.putSerializable(ACCESS_TOKEN_TAG, accessToken);
    }
}
