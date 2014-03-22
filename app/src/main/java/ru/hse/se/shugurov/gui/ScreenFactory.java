package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import ru.hse.se.shugurov.MainActivity;
import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.ViewsPackage.HSEViewTypes;

/**
 * Created by Иван on 15.03.14.
 */
public class ScreenFactory
{
    private static ScreenFactory screenFactory;
    private MainActivity.MainActivityCallback callback;
    private ViewGroup container;
    private Bundle savedInstanceState;

    private ScreenFactory(MainActivity.MainActivityCallback callback, ViewGroup container, Bundle savedInstanceState)
    {
        this.callback = callback;
        this.container = container;
        this.savedInstanceState = savedInstanceState;
    }

    public static void initFactory(MainActivity.MainActivityCallback callback, ViewGroup container, Bundle savedInstanceState)
    {
        screenFactory = new ScreenFactory(callback, container, savedInstanceState);
    }

    public static ScreenFactory instance()
    {
        return screenFactory;
    }

    public ScreenAdapter createAdapter(HSEView view, View previousView)
    {
        ScreenAdapter adapter;
        switch (view.getHseViewType())
        {
            case HSEViewTypes.HTML_CONTENT:
                adapter = new HTMLScreenAdapter(callback, container, null, view);
                break;
            case HSEViewTypes.WEB_PAGE:
            case HSEViewTypes.INNER_WEB_PAGE:
                adapter = new BrowserScreenAdapter(callback, container, previousView, view);
                break;
            case HSEViewTypes.RSS_WRAPPER:
                adapter = new RSSScreenAdapter(callback, container, previousView, view);
                break;
            case HSEViewTypes.VK_FORUM:
                adapter = new VKScreenAdapter(callback, container, previousView, view);
                break;
            case HSEViewTypes.VIEW_OF_OTHER_VIEWS:
                adapter = new ViewOfOtherViewsAdapter(callback, container, previousView, view, savedInstanceState);
                break;
            default:
                throw new IllegalArgumentException("Can't create adapter for this view type");
        }
        return adapter;
    }
}
