package ru.hse.se.shugurov.gui;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ru.hse.se.shugurov.MainActivity;
import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.ViewsPackage.HSEViewTypes;

/**
 * Created by Иван on 15.03.14.
 */
public class BrowserScreenAdapter extends ScreenAdapter
{
    public BrowserScreenAdapter(MainActivity.MainActivityCallback callback, ViewGroup container, View previousView, HSEView hseView)
    {
        super(callback, container, previousView, hseView);
        switch (hseView.getHseViewType())
        {
            case HSEViewTypes.INNER_WEB_PAGE:
                showViewWithBrowser();
                break;
            case HSEViewTypes.WEB_PAGE:
                openBrowser(hseView.getUrl());
                break;
        }
    }

    private void showViewWithBrowser()
    {
        WebView webViewToAppear;
        webViewToAppear = new WebView(getContext());
        webViewToAppear.loadUrl(getHseView().getUrl());
        webViewToAppear.setWebViewClient(new WebViewClient());
        changeViews(webViewToAppear);
    }

    private void openBrowser(String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        browserIntent.setData(uri);
        getContext().startActivity(browserIntent);
    }
}
