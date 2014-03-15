package ru.hse.se.shugurov.gui;

import android.view.View;
import android.view.ViewGroup;

import ru.hse.se.shugurov.MainActivity;
import ru.hse.se.shugurov.ViewsPackage.HSEView;

/**
 * Created by Иван on 15.03.14.
 */
public class ViewOfOtherViewsAdapter extends ScreenAdapter
{
    public ViewOfOtherViewsAdapter(MainActivity.MainActivityCallback callback, ViewGroup container, View previousView, HSEView hseView)
    {
        super(callback, container, previousView, hseView);
    }
}
