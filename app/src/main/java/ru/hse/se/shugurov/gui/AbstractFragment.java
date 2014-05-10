package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.BaseScreen;

/**
 * Base class for the majority of fragments used in app.
 * provides basic facilities:
 * <ul>
 * <li>sets action bar title. name of current {@code HSEView} is used</li>
 * <li>stores an object of {@code HSEView} in {@code Bundle} object before the destruction of
 * the fragment and restores it afterwards</li>
 * <li>shows button in action bar if provided {@code HSEView} is a view of other views</li>
 * </ul>
 * <p/>
 * Fragment requires following arguments:
 * <ul>
 * <li>{@link ru.hse.se.shugurov.screens.BaseScreen} with a key specified by {@code HSE_VIEW_TAG}</li>
 * </ul>
 * <p/>
 * <strong>Is is assumed that method setArguments is called after putting all arguments in a bundle object</strong>
 * <p/>
 * Created by Ivan Shugurov
 */
public abstract class AbstractFragment extends Fragment
{
    /*constants used as keys in bundle object*/
    public static String HSE_VIEW_TAG = "hse_view";

    private BaseScreen baseScreen;


    @Override
    public void setArguments(Bundle args)
    {
        super.setArguments(args);
        readStateFromBundle(args);
    }

    private void readStateFromBundle(Bundle args)
    {
        if (args != null)
        {
            baseScreen = (BaseScreen) args.get(HSE_VIEW_TAG);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        readStateFromBundle(savedInstanceState);
    }

    private void configureActionBar()
    {
        getActivity().setTitle(baseScreen.getName());
        if (baseScreen.isMainView())
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
        outState.putParcelable(HSE_VIEW_TAG, baseScreen);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.refresh_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /*getter for {@code hseView*/
    protected BaseScreen getBaseScreen()
    {
        return baseScreen;
    }

}
