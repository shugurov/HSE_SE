package ru.hse.se.shugurov.gui;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewFragment;

import com.google.android.gms.maps.MapFragment;

import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.ViewsPackage.HSEViewTypes;

/**
 * Created by Иван on 15.03.14.
 */
public class ScreenFactory
{
    private static ScreenFactory screenFactory;
    private ScreenAdapter.ActivityCallback callback;

    private ScreenFactory(ScreenAdapter.ActivityCallback callback)
    {
        this.callback = callback;
    }

    public static void initFactory(ScreenAdapter.ActivityCallback callback)
    {
        screenFactory = new ScreenFactory(callback);
    }

    public static ScreenFactory instance()
    {
        return screenFactory;
    }

    public Fragment createFragment(final HSEView view)
    {
        Fragment adapter;
        switch (view.getHseViewType())
        {
            case HSEViewTypes.HTML_CONTENT:
                adapter = new HTMLScreenAdapter(callback, view);
                break;
            case HSEViewTypes.WEB_PAGE:
                openBrowser(view.getUrl());
                adapter = null;//TODO
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
                adapter = new RSSScreenAdapter(callback, view);
                break;
            case HSEViewTypes.VK_FORUM:
                adapter = new VKScreenAdapter(callback, view);
                break;
            case HSEViewTypes.VIEW_OF_OTHER_VIEWS:
                adapter = new ViewOfOtherViewsAdapter(view);
                break;
            case HSEViewTypes.MAP:
                adapter = new MapFragment();
                break;
            default:
                throw new IllegalArgumentException("Can't create adapter for this view type");
        }
        return adapter;
    }

    private void openBrowser(String url)//TODO а стоит ли этому быть тут?
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        browserIntent.setData(uri);
        callback.getContext().startActivity(browserIntent);
    }
}
