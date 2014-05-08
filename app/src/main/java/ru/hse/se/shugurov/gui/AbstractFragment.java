package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.HSEView;

/**Base class for the majority of fragments used in app.
 * provides basic facilities:
 * <ul>
 *     <li>sets action bar title. name of current {@code HSEView} is used</li>
 *     <li>stores an object of {@code HSEView} in {@code Bundle} object before the destruction of
 *     the fragment and restores it afterwards</li>
 *     <li>shows button in action bar if provided {@code HSEView} is a view of other views</li>
 * </ul>
 *
 * Generally child classes should call not empty constructor. Restoring of elements is done in onCreate,
 * so child classes should override it carefully as well as onSaveInstanceState where saving state is handled.
 *
 * Created by Ivan Shugurov
 */
public abstract class AbstractFragment extends Fragment
{
    /*constant used for saving fragment state*/
    private static String HSE_VIEW_TAG = "hse_view";

    private HSEView hseView;

    /**
     * Default constructor used by Android for instantiating this class after it was destroyed.
     * Should not be used by developers.
     */
    public AbstractFragment()
    {
    }

    /**
     * @param hseView not null
     */
    public AbstractFragment(HSEView hseView)
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

    /*getter for {@code hseView*/
    protected HSEView getHseView()
    {
        return hseView;
    }

}
