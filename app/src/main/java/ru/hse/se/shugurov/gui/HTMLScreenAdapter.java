package ru.hse.se.shugurov.gui;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ru.hse.se.shugurov.MainActivity;
import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.utills.FileManager;

/**
 * Created by Иван on 15.03.14.
 */
public class HTMLScreenAdapter extends ScreenAdapter
{
    public HTMLScreenAdapter(MainActivity.MainActivityCallback callback, ViewGroup container, View previousView, HSEView hseView)
    {
        super(callback, container, previousView, hseView);
        showHTML();
    }

    private void showHTML()
    {
        FileManager fileManager = new FileManager(getContext());
        String HTMLContent = fileManager.getFileContent(getHseView().getKey());
        String mime = "text/html";
        String encoding = "utf-8";
        WebView webView;
        webView = new WebView(getContext());
        webView.loadDataWithBaseURL(null, HTMLContent, mime, encoding, null);
        webView.setWebViewClient(new WebViewClient());
        changeViews(webView);
    }
}
