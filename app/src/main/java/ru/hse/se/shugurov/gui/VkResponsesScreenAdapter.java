package ru.hse.se.shugurov.gui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.VKAbstractItem;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.social_networks.VKResponsesAdapter;

/**
 * Created by Иван on 02.05.2014.
 */
public class VkResponsesScreenAdapter extends ListFragment
{
    private final static String GROUP_ID_TAG = "vk_group_id_responses";
    private final static String TOPIC_ID_TAG = "vk_topic_id_responses";
    private final static String ACCESS_TOKEN_TAG = "vk_access_token_responses";
    private final static String GROUP_NAME_TAG = "vk_group_name_responses";
    private final static String COMMENTS_TAG = "vk_group_comments";
    private String groupId;
    private int topicId;
    private AccessToken accessToken;
    private String groupName;
    private VKAbstractItem[] comments;
    private View footerView;
    private EditText input;
    private String commentText;

    public VkResponsesScreenAdapter()
    {
    }

    public VkResponsesScreenAdapter(String groupName, String groupID, int topicId, AccessToken accessToken)//TODO why do groupId and topicId  have differentTypes?
    {
        this.groupId = groupID;
        this.topicId = topicId;
        this.accessToken = accessToken;
        this.groupName = groupName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && groupId == null)
        {
            groupId = savedInstanceState.getString(GROUP_ID_TAG);
            topicId = savedInstanceState.getInt(TOPIC_ID_TAG);
            accessToken = (AccessToken) savedInstanceState.getSerializable(ACCESS_TOKEN_TAG);
            groupName = savedInstanceState.getString(GROUP_NAME_TAG);
            comments = (VKAbstractItem[]) savedInstanceState.getParcelableArray(COMMENTS_TAG);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        getActivity().setTitle(groupName);
        super.onViewCreated(view, savedInstanceState);
        if (comments == null)
        {
            loadComments();
        } else
        {
            setAdapter();
        }
    }

    private void loadComments()
    {
        final VKRequester requester = new VKRequester(accessToken);
        requester.getComments(groupId, topicId, new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String result)
            {
                if (result == null)
                {
                    Toast.makeText(getActivity(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
                } else
                {
                    parseResponses(result, requester);
                }
            }
        });
    }

    private void parseResponses(final String result, final VKRequester requester)
    {
        Runnable parsing = new Runnable()
        {
            @Override
            public void run()
            {
                comments = requester.getComments(result);
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        setAdapter();
                        setListShown(true);
                    }
                });
            }
        };
        new Thread(parsing).start();

    }

    private void setAdapter()
    {
        VKResponsesAdapter vkResponsesAdapter = new VKResponsesAdapter(getActivity(), comments);
        if (footerView == null)
        {
            createFooterView();
            getListView().addFooterView(footerView);
        }
        setListAdapter(vkResponsesAdapter);
    }

    private void createFooterView()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        footerView = inflater.inflate(R.layout.send_form, null, false);
        input = (EditText) footerView.findViewById(R.id.send_form_text);
        input.setText(commentText);
        Button sendButton = (Button) footerView.findViewById(R.id.send_form_button);
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                commentText = input.getText().toString();
                if (commentText.length() == 0)
                {
                    Toast.makeText(getActivity(), "Нельзя добавлять пустой комментарий", Toast.LENGTH_SHORT).show();
                } else
                {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    final VKRequester requester = new VKRequester(accessToken);
                    setListShown(false);
                    Toast.makeText(getActivity(), "Отправка комментария", Toast.LENGTH_SHORT).show();
                    requester.addCommentToTopic(groupId, topicId, commentText, new Requester.RequestResultCallback()
                    {
                        @Override
                        public void pushResult(String result)
                        {
                            if (result == null || (result != null && result.contains("error")))
                            {
                                Toast.makeText(getActivity(), "Не удалось добавить комментарий", Toast.LENGTH_SHORT).show();
                                setListShown(true);
                            } else
                            {
                                commentText = null;
                                input.setText("");
                                comments = null;
                                loadComments();
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(GROUP_ID_TAG, groupId);
        outState.putInt(TOPIC_ID_TAG, topicId);
        outState.putSerializable(ACCESS_TOKEN_TAG, accessToken);
        outState.putString(GROUP_NAME_TAG, groupName);
        outState.putParcelableArray(COMMENTS_TAG, comments);
    }


}
