package ru.hse.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import ru.hse.shugurov.R;
import ru.hse.shugurov.screens.BaseScreen;

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
 * <li>{@link ru.hse.shugurov.screens.BaseScreen} with a key specified by {@code SCREEN_TAG}</li>
 * </ul>
 * <p/>
 * <strong>Is is assumed that method setArguments is called after putting all arguments in a bundle object</strong>
 * <p/>
 *
 * @author Ivan Shugurov
 */
public abstract class AbstractFragment extends Fragment
{
    /*constants used as keys in bundle object*/
    public static String SCREEN_TAG = "base_screen_abstract_fragment";

    private BaseScreen screen;


    @Override
    public void setArguments(Bundle args)
    {
        super.setArguments(args);
        readStateFromBundle(args);
    }

    /*retrieves necessary fields from a bundle object*/
    private void readStateFromBundle(Bundle args)
    {
        if (args != null)
        {
            screen = (BaseScreen) args.get(SCREEN_TAG);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        readStateFromBundle(savedInstanceState);
    }

    /*sets title and checks if it is a actions to be shown*/
    private void configureActionBar()
    {
        getActivity().setTitle(screen.getName());
        if (screen.isMainScreen())
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
        outState.putParcelable(SCREEN_TAG, screen);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.refresh_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /*getter for {@code screen*/
    protected BaseScreen getScreen()
    {
        return screen;
    }

}
