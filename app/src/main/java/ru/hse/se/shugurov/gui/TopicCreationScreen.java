package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.social_networks.AbstractRequester;

/**
 * Created by Иван on 04.05.2014.
 */
public class TopicCreationScreen extends Fragment
{
    private final static String GROUP_ID_TAG = "vk_group_id_topics_creation";
    private String groupId;
    private EditText titleInput;
    private EditText textInput;
    private String actionBarTitle;
    private AbstractRequester requester;

    public TopicCreationScreen()
    {
    }

    public TopicCreationScreen(String title, String groupId, AbstractRequester requester)
    {
        this.groupId = groupId;
        this.actionBarTitle = title;
        this.requester = requester;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && groupId == null)
        {
            groupId = savedInstanceState.getString(GROUP_ID_TAG);
        }
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
    }

}
