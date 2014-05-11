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
import ru.hse.se.shugurov.utills.FileManager;

/**
 * Used to demonstrate html documents. Has some limitations due to {@code TextView} which can demonstrate only base tags.
 * <p/>
 * CSS is not supported
 * <p/>
 * For the required arguments see{@link ru.hse.se.shugurov.gui.AbstractFragment}
 * Created byIvan Shugurov
 */
public class HTMLScreenFragment extends AbstractFragment
{
    private TextView viewForHTML;

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
                    String htmlContent = fileManager.getFileContent(getScreen().getKey());
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

    /*check if an activity is available and if so sets text. makes a little pause in order to make fragment animation smother*/
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

