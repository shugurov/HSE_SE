package ru.hse.se.shugurov.gui;


import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.social_networks.AbstractRequester;
import ru.hse.se.shugurov.social_networks.SocialNetworkCommentsAdapter;
import ru.hse.se.shugurov.social_networks.SocialNetworkEntry;

/**
 * Class for demonstrating a list of comments from a specific topic of social network.
 * This class makes actual request for data via requester object, although it does not depend on
 * specific {@code AbstractRequester} subclass.Requester object is provided by constructor in runtime.
 * <p/>
 * <p/>
 * This class shows a form for commenting this topic. After user comment is sent successfully, fragments is refreshed
 * See {@link ru.hse.se.shugurov.social_networks.AbstractRequester}
 *
 * @author Ivan Shugurov
 */
public class SocialNetworkCommentsFragment extends SocialNetworkAbstractList
{
    /*constants used saving fragment state*/
    private final static String TOPIC_ID_TAG = "vk_topic_id_responses";
    private final static String COMMENTS_TAG = "vk_group_comments";
    private final static String COMMENTS_COMMENT_TAG = "vk_group_comments_comment_text";


    private String topicId;
    private SocialNetworkEntry[] comments;
    private View footerView;
    private EditText input;
    private String commentText;

    /**
     * Default constructor used by Android for instantiating this class after it has been destroyed.
     * Should not be used by developers.
     */
    public SocialNetworkCommentsFragment()
    {
    }

    /**
     * @param groupId   id of social network group. Not null.
     * @param groupName name of requested group. Used as title in action bar. Not null.
     * @param topicId   id of social network topic. Not null.
     * @param requester object which makes requests for the data from social networks. Not null
     */
    public SocialNetworkCommentsFragment(String groupId, String groupName, String topicId, AbstractRequester requester)
    {
        super(groupId, groupName, requester);
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
        getListView().setSelector(new StateListDrawable());
    }

    private void loadComments()
    {
        final AbstractRequester requester = getRequester();
        requester.getComments(getGroupId(), topicId, new AbstractRequester.RequestResultListener<SocialNetworkEntry>()
        {
            @Override
            public void resultObtained(SocialNetworkEntry[] resultComments)
            {
                if (resultComments == null)
                {
                    Toast.makeText(getActivity(), "Не удалось загрузить информацию", Toast.LENGTH_SHORT).show();
                } else
                {
                    comments = resultComments;
                    setAdapter();
                }
            }
        });
    }

    /*set ListAdapter and adds footer view*/
    private void setAdapter()
    {
        if (getActivity() != null)
        {
            SocialNetworkCommentsAdapter socialNetworkCommentsAdapter = new SocialNetworkCommentsAdapter(getActivity(), comments);
            if (footerView == null)
            {
                createFooterView();
                getListView().addFooterView(footerView);
            }
            setListAdapter(socialNetworkCommentsAdapter);
            setListShown(true);
        }
    }

    /*Create footer view which shows a form for writing a reply*/
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
                    final AbstractRequester requester = getRequester();
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
        if (input != null)
        {
            outState.putString(COMMENTS_COMMENT_TAG, input.getText().toString());
        }
    }

}
