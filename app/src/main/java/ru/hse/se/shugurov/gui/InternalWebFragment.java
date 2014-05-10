package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ru.hse.se.shugurov.R;

/**
 * Class used to demonstrate web pages within application
 * <p/>
 * For the required arguments see {@link ru.hse.se.shugurov.gui.AbstractFragment}
 * Created by Ivan Shugurov
 */
public class InternalWebFragment extends AbstractFragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        WebView webView = (WebView) inflater.inflate(R.layout.internal_web_view, container, false);
        webView.loadUrl(getBaseScreen().getUrl());
        webView.setWebViewClient(new WebViewClient());
        return webView;
    }


}
