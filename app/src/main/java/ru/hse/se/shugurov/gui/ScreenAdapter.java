package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.HSEView;

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && hseView == null)
        {
            hseView = (HSEView) savedInstanceState.get(HSE_VIEW_TAG);
        }
    }

    private void configureActionBar()
    {
        getActivity().setTitle(hseView.getName());
        if (hseView.isMainView())
        {
            setHasOptionsMenu(true);
        } else
        {
            setHasOptionsMenu(false);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        configureActionBar();

    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putSerializable(HSE_VIEW_TAG, hseView);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.refresh_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected HSEView getHseView()
    {
        return hseView;
    }

}
