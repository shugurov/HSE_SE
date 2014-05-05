package ru.hse.se.shugurov.gui;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.SocialNetworkEntry;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.social_networks.VKResponsesAdapter;

/**
 * Created by Иван on 02.05.2014.
 */
public class VkResponsesScreenAdapter extends SocialNetworkAbstractList
{
    private final static String TOPIC_ID_TAG = "vk_topic_id_responses";
    private final static String COMMENTS_TAG = "vk_group_comments";
    private final static String COMMENTS_COMMENT_TAG = "vk_group_comments_comment_text";
    private String topicId;
    private SocialNetworkEntry[] comments;
    private View footerView;
    private EditText input;
    private String commentText;

    public VkResponsesScreenAdapter()
    {
    }

    public VkResponsesScreenAdapter(String groupId, String groupName, String topicId, AccessToken accessToken)//TODO why do groupId and topicId  have differentTypes?
    {
        super(groupId, groupName, accessToken);
        this.topicId = topicId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && comments == null)
        {
            topicId = savedInstanceState.getString(TOPIC_ID_TAG);
            comments = (SocialNetworkEntry[]) savedInstanceState.getParcelableArray(COMMENTS_TAG);
            commentText = savedInstanceState.getString(COMMENTS_COMMENT_TAG);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
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
        final VKRequester requester = new VKRequester(getAccessToken());
        requester.getComments(getGroupId(), topicId, new Requester.RequestResultCallback()
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
        if (isVisible())
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
                    final VKRequester requester = new VKRequester(getAccessToken());
                    setListShown(false);
                    Toast.makeText(getActivity(), "Отправка комментария", Toast.LENGTH_SHORT).show();
                    requester.addCommentToTopic(getGroupId(), topicId, commentText, new Requester.RequestResultCallback()
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
        outState.putString(TOPIC_ID_TAG, topicId);
        outState.putParcelableArray(COMMENTS_TAG, comments);
        outState.putString(COMMENTS_COMMENT_TAG, input.getText().toString());
    }


}
