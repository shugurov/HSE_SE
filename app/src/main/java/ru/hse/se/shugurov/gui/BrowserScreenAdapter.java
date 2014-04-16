package ru.hse.se.shugurov.gui;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.ViewsPackage.HSEViewTypes;

/**
 * Created by Иван on 15.03.14.
 */
public class BrowserScreenAdapter extends ScreenAdapter
{
    public BrowserScreenAdapter(ActivityCallback callback, HSEView hseView)
    {
        super(callback, hseView);
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
        webViewToAppear = new WebView(getActivity());
        webViewToAppear.loadUrl(getHseView().getUrl());
        webViewToAppear.setWebViewClient(new WebViewClient());
        //changeFragments(); TODO
    }

    private void openBrowser(String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        browserIntent.setData(uri);
        getActivity().startActivity(browserIntent);
    }
}
