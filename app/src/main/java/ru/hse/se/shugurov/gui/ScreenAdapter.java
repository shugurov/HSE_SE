package ru.hse.se.shugurov.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Stack;

import ru.hse.se.shugurov.MainActivity;
import ru.hse.se.shugurov.ViewsPackage.HSEView;

/**
 * Created by Иван on 14.03.14.
 */
public abstract class ScreenAdapter
{
    private LayoutInflater inflater;
    private MainActivity.MainActivityCallback callback;
    private ViewGroup container;
    private Stack<View> previousViews;
    private HSEView hseView;

    public ScreenAdapter(MainActivity.MainActivityCallback callback, ViewGroup container, View previousView, HSEView hseView)
    {
        this.callback = callback;
        this.container = container;
        this.hseView = hseView;
        inflater = LayoutInflater.from(callback.getContext());
        previousViews = new Stack<View>();
        if (previousView != null)
        {
            previousViews.add(previousView);
        }
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
        return previousViews.size() > 1;
    }

    public void showPreviousView()
    {
        if (hasPreviousView())
        {
            callback.changeViews(container, previousViews.pop(), previousViews.peek(), true);
        }
    }

    protected HSEView getHseView()
    {
        return hseView;
    }

    public View getCurrentView()
    {
        if (previousViews.size() > 0)
        {
            return previousViews.peek();
        } else
        {
            return null;
        }
    }

    protected void setView(View view)
    {
        container.addView(view);
        previousViews.add(view);
    }

    public String getActionBarTitle()
    {
        return hseView.getName();
    }


    protected void refreshActionBar()
    {
        callback.refreshActionBar();
    }

    public int getMenuId()
    {
        return -1;
    }

}
