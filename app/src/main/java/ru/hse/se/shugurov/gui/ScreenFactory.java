package ru.hse.se.shugurov.gui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewFragment;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.HSEView;
import ru.hse.se.shugurov.screens.HSEViewTypes;
import ru.hse.se.shugurov.screens.MapScreen;

/**
 * Created by Иван on 15.03.14.
 */
public class ScreenFactory//TODO экран с браузером падает при перевороте
{
    private static ScreenFactory screenFactory;
    private ScreenAdapter.ActivityCallback callback;
    private boolean isFirstFragment = true;

    private ScreenFactory(ScreenAdapter.ActivityCallback callback, boolean isFirstFragment)
    {
        this.callback = callback;
        this.isFirstFragment = isFirstFragment;
    }

    public static void initFactory(ScreenAdapter.ActivityCallback callback, boolean isFirstFragment)
    {
        screenFactory = new ScreenFactory(callback, isFirstFragment);
    }

    public static ScreenFactory instance()
    {
        return screenFactory;
    }

    public void showFragment(final HSEView view)
    {
        Fragment adapter;
        switch (view.getHseViewType())
        {
            case HSEViewTypes.HTML_CONTENT:
                adapter = new HTMLScreenAdapter(view);
                break;
            case HSEViewTypes.WEB_PAGE:
                openBrowser(view.getUrl());
                adapter = null;
                break;
            case HSEViewTypes.INNER_WEB_PAGE:
                adapter = new WebViewFragment()
                {

                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
                    {
                        View resultView = super.onCreateView(inflater, container, savedInstanceState);
                        getWebView().loadUrl(view.getUrl());
                        return resultView;
                    }
                };
                break;
            case HSEViewTypes.RSS:
            case HSEViewTypes.RSS_WRAPPER:
                adapter = new RSSScreenAdapter(view);
                break;
            case HSEViewTypes.VK_FORUM:
                adapter = new VKScreenAdapter(view);
                break;
            case HSEViewTypes.VIEW_OF_OTHER_VIEWS:
                adapter = new ViewOfOtherViewsAdapter(view);
                break;
            case HSEViewTypes.MAP: //TODO может спрятать в отдельный класс?
                adapter = new MapScreenAdapter((MapScreen) view);
                break;
            default:
                throw new IllegalArgumentException("Can't create adapter for this view type");
        }

        if (adapter != null)
        {
            FragmentManager manager = callback.getActivity().getFragmentManager();
            if (isFirstFragment)
            {
                setFragment(manager, adapter);
                isFirstFragment = false;
            } else
            {
                changeFragments(manager, adapter);
            }
        }
    }

    private void openBrowser(String url)//TODO а стоит ли этому быть тут?
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        browserIntent.setData(uri);
        callback.getActivity().startActivity(browserIntent);
    }

    private void changeFragments(FragmentManager manager, Fragment fragmentToAppear)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main, fragmentToAppear);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setFragment(FragmentManager manager, Fragment fragmentToAppear)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main, fragmentToAppear);
        transaction.commit();
    }
}
