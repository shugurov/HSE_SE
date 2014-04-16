package ru.hse.se.shugurov.gui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.ViewsPackage.HSEViewRSS;
import ru.hse.se.shugurov.ViewsPackage.HSEViewRSSWrapper;

/**
 * Created by Иван on 15.03.14.
 */
public class RSSScreenAdapter extends ScreenAdapter
{
    public RSSScreenAdapter(ActivityCallback callback, HSEView hseViewRSS)
    {
        super(callback, hseViewRSS);
    }

    private View displayListOfRSSItems(final LayoutInflater inflater, final ViewGroup container)
    {
        HSEViewRSS[] rssItems = getHseView().getConnectedViews();
        ListView rssItemsView = (ListView) inflater.inflate(R.layout.activity_main_list, container, false);
        final RSSListAdapter rssListAdapter = new RSSListAdapter(getActivity(), rssItems);
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
                        showEntireRSS(inflater, container, selectedItem);
                        break;
                    case ONLY_TITLE:
                        openBrowser(selectedItem.getUrl());
                        break;
                }
            }
        });
        return rssItemsView;
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
        getActivity().startActivity(browserIntent);
    }

    private View showEntireRSS(LayoutInflater inflater, ViewGroup container, final HSEViewRSS hseViewRSS)//TODO это боль и ад,надо править(
    {
        View rssLayout = inflater.inflate(R.layout.rss_layout, container, false);
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
        return rssLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return displayListOfRSSItems(inflater, container);
    }
}
