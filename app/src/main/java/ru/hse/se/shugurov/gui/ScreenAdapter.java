package ru.hse.se.shugurov.gui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import ru.hse.se.shugurov.ViewsPackage.HSEView;

/**
 * Created by Иван on 14.03.14.
 */
public abstract class ScreenAdapter extends Fragment
{
    private static String HSE_VIEW_TAG = "hse_view";
    private HSEView hseView;

    public ScreenAdapter()
    {
    }

    public ScreenAdapter(HSEView hseView)
    {
        this.hseView = hseView;
        Bundle instanceState = new Bundle();
        instanceState.putSerializable(HSE_VIEW_TAG, hseView);
        setArguments(instanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && hseView == null)//TODO возможно, второе условие лишнее
        {
            hseView = (HSEView) savedInstanceState.get(HSE_VIEW_TAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putAll(getArguments());
        super.onSaveInstanceState(outState);
    }

    protected HSEView getHseView()
    {
        return hseView;
    }


    public String getActionBarTitle()
    {
        return hseView.getName();
    }//TODO зачем?

    public int getMenuId()
    {
        return -1;
    }//TODO что это вообще?

    public interface ActivityCallback
    {

        public Activity getActivity(); //TODO а надо ли вообще?

        public void refreshActionBar();//TODO а здесь ли?

    }

}
