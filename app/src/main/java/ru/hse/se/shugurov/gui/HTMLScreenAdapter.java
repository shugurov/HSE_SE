package ru.hse.se.shugurov.gui;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.utills.FileManager;

/**
 * Created by Иван on 15.03.14.
 */
public class HTMLScreenAdapter extends ScreenAdapter
{
    public HTMLScreenAdapter(ActivityCallback callback, HSEView hseView)
    {
        super(callback, hseView);
        showHTML();
    }

    private void showHTML()
    {
        FileManager fileManager = new FileManager(getActivity());
        String HTMLContent = fileManager.getFileContent(getHseView().getKey());
        String mime = "text/html";
        String encoding = "utf-8";
        WebView webView;
        webView = new WebView(getActivity());
        webView.loadDataWithBaseURL(null, HTMLContent, mime, encoding, null);
        webView.setWebViewClient(new WebViewClient());
    }
}
