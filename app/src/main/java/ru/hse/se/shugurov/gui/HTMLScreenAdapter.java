package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.utills.FileManager;

/**
 * Created by Иван on 15.03.14.
 */
public class HTMLScreenAdapter extends ScreenAdapter
{
    public HTMLScreenAdapter(ActivityCallback callback, HSEView hseView)
    {
        super(hseView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)//TODO подумать над считыаанием в отдельном потоке
    {
        FileManager fileManager = new FileManager(getActivity());
        String HTMLContent = fileManager.getFileContent(getHseView().getKey());
        View root = inflater.inflate(R.layout.html_layout, container, false);
        TextView viewForHTML = (TextView) root.findViewById(R.id.html_content);
        viewForHTML.setText(Html.fromHtml(HTMLContent));
        return root;//TODO почему пропускаются некоторые теги?(
    }
}
