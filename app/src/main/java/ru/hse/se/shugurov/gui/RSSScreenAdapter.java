package ru.hse.se.shugurov.gui;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import ru.hse.se.shugurov.MainActivity;
import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.ViewsPackage.HSEViewRSS;
import ru.hse.se.shugurov.ViewsPackage.HSEViewRSSWrapper;

/**
 * Created by Иван on 15.03.14.
 */
public class RSSScreenAdapter extends ScreenAdapter
{
    public RSSScreenAdapter(MainActivity.MainActivityCallback callback, ViewGroup container, View previousView, HSEView hseViewRSS)
    {
        super(callback, container, previousView, hseViewRSS);
        displayListOfRSSItems();
    }

    private void displayListOfRSSItems()
    {
        HSEViewRSS[] rssItems = getHseView().getConnectedViews();
        ListView rssItemsView = (ListView) getLayoutInflater().inflate(R.layout.activity_main_list, getContainer(), false);
        final RSSListAdapter rssListAdapter = new RSSListAdapter(getContext(), rssItems);
        rssItemsView.setAdapter(rssListAdapter);
        rssItemsView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                HSEViewRSS selectedItem = rssListAdapter.getItem(position);
                switch (selectedItem.getType())
                {
                    case FULL_RSS:
                        showEntireRSS(selectedItem);
                        break;
                    case ONLY_TITLE:
                        openBrowser(selectedItem.getUrl());
                        break;
                }
            }
        });
        changeViews(rssItemsView);
    }

    @Override
    protected HSEViewRSSWrapper getHseView()
    {
        return (HSEViewRSSWrapper) super.getHseView();
    }

    private void openBrowser(String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        browserIntent.setData(uri);
        getContext().startActivity(browserIntent);
    }

    private void showEntireRSS(final HSEViewRSS hseViewRSS)
    {
        View rssLayout = getLayoutInflater().inflate(R.layout.rss_layout, getContainer(), false);
        rssLayout.findViewById(R.id.rss_layout_content).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openBrowser(hseViewRSS.getUrl());
            }
        });
        ((TextView) rssLayout.findViewById(R.id.rss_layout_title)).setText(hseViewRSS.getTitle());
        ((TextView) rssLayout.findViewById(R.id.rss_layout_text)).setText(hseViewRSS.getOmitted());
        changeViews(rssLayout);
    }
}
