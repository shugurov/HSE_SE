package ru.hse.se.shugurov.gui;

import android.view.ViewGroup;

import ru.hse.se.shugurov.MainActivity;
import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.ViewsPackage.HSEViewTypes;

/**
 * Created by Иван on 15.03.14.
 */
public class ScreenAdaptersBuilder
{
    private MainActivity.MainActivityCallback callback;
    private ViewGroup container;

    public ScreenAdaptersBuilder(MainActivity.MainActivityCallback callback, ViewGroup container)
    {
        this.callback = callback;
        this.container = container;
    }

    public ScreenAdapter createAdapter(HSEView view)
    {
        ScreenAdapter adapter;
        switch (view.getHseViewType())
        {
            case HSEViewTypes.HTML_CONTENT:
                adapter = new HTMLScreenAdapter(callback, container, null, view);
                break;
            default:
                throw new IllegalArgumentException("Can't create adapter for this view type");
        }
        return adapter;
    }
}
