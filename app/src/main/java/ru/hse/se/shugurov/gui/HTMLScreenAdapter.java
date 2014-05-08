package ru.hse.se.shugurov.gui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.HSEView;
import ru.hse.se.shugurov.utills.FileManager;

/**
 * Created by Иван on 15.03.14.
 */
public class HTMLScreenAdapter extends ScreenAdapter
{
    private TextView viewForHTML;

    public HTMLScreenAdapter()
    {
    }

    public HTMLScreenAdapter(HSEView hseView)
    {
        super(hseView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.html_layout, container, false);
        viewForHTML = (TextView) root.findViewById(R.id.html_content);
        Runnable opening = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    FileManager fileManager = FileManager.instance();
                    String htmlContent = fileManager.getFileContent(getHseView().getKey());
                    setText(htmlContent);
                } catch (IOException e)
                {
                    Activity activity = getActivity();
                    if (activity != null)
                    {
                        Toast.makeText(activity, "Не удалось зарузить текст", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        new Thread(opening).start();
        return root;
    }

    private void setText(final String text)
    {
        try
        {
            Thread.sleep(450);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        Activity activity = getActivity();
        if (activity != null)
        {
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    viewForHTML.setText(Html.fromHtml(text));
                }
            });
        }
    }
}

