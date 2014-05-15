package ru.hse.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import ru.hse.shugurov.R;
import ru.hse.shugurov.social_networks.AbstractRequester;
import ru.hse.shugurov.social_networks.StateListener;
import ru.hse.shugurov.utills.Requester;

/**
 * Class used to show a from which allows user to create a topic. Makes request to publish this
 * topic via provided requester object
 * <p/>
 * Fragment requires following arguments:
 * <ul>
 * <li>group id as String with a key specified by {@code GROUP_ID_TAG}</li>
 * <li>title as String with a key specified by {@code TITLE_TAG}</li>
 * <li>{@link ru.hse.shugurov.social_networks.AbstractRequester} with a key specified by {@code REQUESTER_TAG}. Object is passed as a serializable object</li>
 * <li>{@link ru.hse.shugurov.social_networks.StateListener} with a key specified by {@code STATE_LISTENER_TAG}. Object is passed as a serializable object</li>
 * </ul>
 * <p/>
 * Created by Ivan Shugurov
 */
public class TopicCreationFragment extends Fragment
{
    /*constants used as keys in bundle object*/
    public final static String GROUP_ID_TAG = "group_id_topics_creation";
    public final static String TITLE_TAG = "title_topics_creation";
    public final static String REQUESTER_TAG = "requester_topics_creation";
    public final static String STATE_LISTENER_TAG = "state_listener_topics_creation";


    private String groupId;
    private EditText titleInput;
    private EditText textInput;
    private String actionBarTitle;
    private AbstractRequester requester;
    private StateListener stateListener;

    @Override
    public void setArguments(Bundle args)
    {
        super.setArguments(args);
        readFromBundle(args);
    }

    private void readFromBundle(Bundle args)
    {
        if (args != null)
        {
            groupId = args.getString(GROUP_ID_TAG);
            actionBarTitle = args.getString(TITLE_TAG);
            requester = (AbstractRequester) args.getSerializable(REQUESTER_TAG);
            stateListener = (StateListener) args.getSerializable(STATE_LISTENER_TAG);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        readFromBundle(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        getActivity().setTitle(actionBarTitle);
        View topicCreationForm = inflater.inflate(R.layout.topic_creation_form, container, false);
        titleInput = (EditText) topicCreationForm.findViewById(R.id.topic_creation_title);
        textInput = (EditText) topicCreationForm.findViewById(R.id.topic_creation_text);
        topicCreationForm.findViewById(R.id.topic_creation_button_create).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String title = titleInput.getText().toString();
                if (title.length() == 0)
                {
                    Toast.makeText(getActivity(), "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show();
                    return;
                }
                String text = textInput.getText().toString();
                if (text.length() == 0)
                {
                    Toast.makeText(getActivity(), "Тескт не может быть пустым", Toast.LENGTH_SHORT).show();
                    return;
                }

                requester.addTopic(groupId, title, text, new Requester.RequestResultCallback()
                {
                    @Override
                    public void pushResult(String result)
                    {
                        if (result != null && result.contains("response"))
                        {
                            stateListener.stateChanged();
                            getFragmentManager().popBackStack();
                        } else
                        {
                            Toast.makeText(getActivity(), "Не удалось создать тему", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return topicCreationForm;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(GROUP_ID_TAG, groupId);
        outState.putString(TITLE_TAG, actionBarTitle);
        outState.putSerializable(REQUESTER_TAG, requester);
        outState.putSerializable(STATE_LISTENER_TAG, stateListener);
    }

}
