package ru.hse.se.shugurov.gui;


import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.social_networks.AbstractRequester;
import ru.hse.se.shugurov.social_networks.CommentsAdapter;
import ru.hse.se.shugurov.social_networks.SocialNetworkEntry;
import ru.hse.se.shugurov.social_networks.StateListener;
import ru.hse.se.shugurov.utills.Requester;

/**
 * Class for demonstrating a list of comments from a specific topic of social network.
 * This class makes actual request for data via requester object, although it does not depend on
 * specific {@code AbstractRequester} subclass.Requester object is provided by constructor in runtime.
 * <p/>
 * <p/>
 * This class shows a form for commenting this topic. After user comment is sent successfully, fragments is refreshed
 * See {@link ru.hse.se.shugurov.social_networks.AbstractRequester}
 * <p/>
 * Fragment requires following arguments:
 * <ul>
 * <li>topic id as String with a key specified by {@code TOPIC_ID_TAG}.</li>
 * <li>{@link ru.hse.se.shugurov.social_networks.SocialNetworkEntry[]} of comments with a key specified by {@code COMMENTS_TAG}.
 * Passed as parcelable array</li>
 * <li>{@link ru.hse.se.shugurov.social_networks.StateListener} with a key specified by {@code COMMENTS_LISTENER_TAG}.
 * Passed as a serializable object</li>
 * <li>Topic title as String with a key specified by {@code TOPIC_TITLE_TAG}.</li>
 * </ul>
 * For other required arguments see {@link ru.hse.se.shugurov.gui.SocialNetworkAbstractList}
 *
 * @author Ivan Shugurov
 */
public class CommentsFragment extends SocialNetworkAbstractList//в vk нету заголовка темы(
{
    /*constants used as keys in bundle object*/
    public final static String TOPIC_ID_TAG = "topic_id_responses";
    public final static String COMMENTS_TAG = "group_comments";
    public final static String COMMENTS_LISTENER_TAG = "comments_listener_tag";
    public final static String TOPIC_TITLE_TAG = "comments_topic_title";
    private final static String COMMENTS_COMMENT_TAG = "group_comments_reply_text";


    private String topicId;
    private SocialNetworkEntry[] comments;
    private View footerView;
    private EditText input;
    private String commentText;
    private StateListener stateListener;
    private String topicTitle;
    private TextView headerView;


    @Override
    public void setArguments(Bundle args)
    {
        super.setArguments(args);
        if (args != null)
        {
            topicId = args.getString(TOPIC_ID_TAG);
            getCommentsFromBundle(args);
            stateListener = (StateListener) args.getSerializable(COMMENTS_LISTENER_TAG);
            topicTitle = args.getString(TOPIC_TITLE_TAG);
        }
    }

    private void getCommentsFromBundle(Bundle args)
    {
        Parcelable[] parcelables = args.getParcelableArray(COMMENTS_TAG);
        if (parcelables != null)
        {
            comments = Arrays.copyOf(parcelables, parcelables.length, SocialNetworkEntry[].class);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            topicId = savedInstanceState.getString(TOPIC_ID_TAG);
            getCommentsFromBundle(savedInstanceState);
            commentText = savedInstanceState.getString(COMMENTS_COMMENT_TAG);
            stateListener = (StateListener) savedInstanceState.getSerializable(COMMENTS_LISTENER_TAG);
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

    /*requests comments via requester object*/
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
        CommentsAdapter commentsAdapter = new CommentsAdapter(getActivity(), comments);
        if (footerView == null)
        {
            createFooterView();
        }
        if (headerView == null)
        {
            createHeaderView();
        }
        try
        {
            getListView().addFooterView(footerView);
            getListView().addHeaderView(headerView);
            setListAdapter(commentsAdapter);
            setListShown(true);
        } catch (Exception ex)
        {
        }

    }

    /*Create header text view which shows topic title*/
    private void createHeaderView()
    {
        headerView = new TextView(getActivity());
        //headerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int padding = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        headerView.setPadding(padding, 0, padding, 0);
        headerView.setText(topicTitle);
        headerView.setTypeface(null, Typeface.BOLD);
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
                                stateListener.stateChanged();
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
        outState.putSerializable(COMMENTS_LISTENER_TAG, stateListener);
        if (input != null)
        {
            outState.putString(COMMENTS_COMMENT_TAG, input.getText().toString());
        }
        outState.putString(TOPIC_TITLE_TAG, topicTitle);
    }

}
