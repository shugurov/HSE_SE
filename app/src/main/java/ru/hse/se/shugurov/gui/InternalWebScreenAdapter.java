package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.HSEView;

/**
 * Class used to demonstrate web pages within application
 * <p/>
 * Created by Ivan Shugurov
 */
public class InternalWebScreenAdapter extends AbstractFragment
{
    /**
     * Default constructor used by Android for instantiating this class after it was destroyed.
     * Should not be used by developers.
     */
    public InternalWebScreenAdapter()
    {
    }

    /**
     * Opens an url stored in hseView in {@code WebView}
     *
     * @param hseView with url to be opened. not null
     */
    public InternalWebScreenAdapter(HSEView hseView)
    {
        super(hseView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        WebView webView = (WebView) inflater.inflate(R.layout.internal_web_view, container, false);
        webView.loadUrl(getHseView().getUrl());
        webView.setWebViewClient(new WebViewClient());
        return webView;
    }


}
