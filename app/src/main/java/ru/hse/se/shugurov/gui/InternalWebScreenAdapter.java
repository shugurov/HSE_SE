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
 * Created by Иван on 24.04.2014.
 */
public class InternalWebScreenAdapter extends ScreenAdapter
{
    public InternalWebScreenAdapter()
    {
    }

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
