package ru.hse.se.shugurov.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Stack;

import ru.hse.se.shugurov.MainActivity;

/**
 * Created by Иван on 14.03.14.
 */
public abstract class ScreenAdapter
{
    private LayoutInflater inflater;
    private MainActivity.MainActivityCallback callback;
    private ViewGroup container;
    private Stack<View> previousViews;

    public ScreenAdapter(MainActivity.MainActivityCallback callback, ViewGroup container, View previousView)
    {
        this.callback = callback;
        this.container = container;
        inflater = LayoutInflater.from(callback.getContext());
        previousViews = new Stack<View>();
        previousViews.add(previousView);
    }

    protected LayoutInflater getLayoutInflater()
    {
        return inflater;
    }

    protected Context getContext()
    {
        return callback.getContext();
    }

    protected void changeViews(View viewToAppear)
    {
        callback.changeViews(container, previousViews.peek(), viewToAppear, false);
        previousViews.add(viewToAppear);
    }

    protected ViewGroup getContainer()
    {
        return container;
    }

    public boolean hasPreviousView()
    {
        return (previousViews.size() > 1);
    }

    public void showPreviousView()
    {
        callback.changeViews(container, previousViews.pop(), previousViews.peek(), true);
    }

    protected void setActionBarTitle(String title)
    {
        callback.setActionBarTitle(title);
    }
}
