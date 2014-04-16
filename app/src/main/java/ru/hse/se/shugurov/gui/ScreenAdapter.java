package ru.hse.se.shugurov.gui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.ViewsPackage.HSEView;

/**
 * Created by Иван on 14.03.14.
 */
public abstract class ScreenAdapter extends Fragment
{
    private ActivityCallback callback;
    private HSEView hseView;

    public ScreenAdapter(ActivityCallback callback, HSEView hseView)
    {
        this.callback = callback;
        this.hseView = hseView;
    }

    public static void changeFragments(FragmentManager manager, Fragment fragmentToAppear)//TODO править доступ и статику
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main, fragmentToAppear);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void setFragment(FragmentManager manager, Fragment fragmentToAppear)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main, fragmentToAppear);
        transaction.commit();
    }

    protected HSEView getHseView()
    {
        return hseView;
    }


    public String getActionBarTitle()
    {
        return hseView.getName();
    }


    protected void refreshActionBar()//TODO  а надо ли?
    {
        callback.refreshActionBar();
    }

    public int getMenuId()
    {
        return -1;
    }

    public interface ActivityCallback
    {

    /*public void changeViews(ViewGroup parentView, View viewToDisappear, View viewToAppear, boolean isButtonBackClicked)
    {
        MainActivity.this.changeViews(parentView, viewToDisappear, viewToAppear, isButtonBackClicked);
        setActionBar();
    }TODO delete*/

        public Context getContext(); //TODO а надо ли вообще?

        public void refreshActionBar();//TODO а здесь ли?

    }

}
