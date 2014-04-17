package ru.hse.se.shugurov.gui;

import android.app.Fragment;
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
        super(hseViewRSS);
    }

    private View showListOfRSSItems(final LayoutInflater inflater, final ViewGroup container)
    {
        HSEViewRSS[] rssItems = ((HSEViewRSSWrapper) getHseView()).getConnectedViews();
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
                        Fragment adapter = ScreenFactory.instance().createFragment(selectedItem);
                        changeFragments(getFragmentManager(), adapter);
                        break;
                    case ONLY_TITLE:
                        openBrowser(selectedItem.getUrl());
                        break;
                }
            }
        });
        return rssItemsView;
    }

    private void openBrowser(String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        browserIntent.setData(uri);
        getActivity().startActivity(browserIntent);
    }

    private View showEntireRSS(LayoutInflater inflater, ViewGroup container)
    {
        View rssLayout = inflater.inflate(R.layout.rss_layout, container, false);
        rssLayout.findViewById(R.id.rss_layout_content).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openBrowser(getHseView().getUrl());
            }
        });
        if (getHseView() instanceof HSEViewRSS)
        {
            ((TextView) rssLayout.findViewById(R.id.rss_layout_title)).setText(((HSEViewRSS) getHseView()).getTitle());
            ((TextView) rssLayout.findViewById(R.id.rss_layout_text)).setText(((HSEViewRSS) getHseView()).getOmitted());
        } else
        {
            throw new IllegalStateException("Precondition violated in RssScreenAdapter.showEntireRSS.Inappropriate view type.");
        }
        return rssLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (getHseView() instanceof HSEViewRSSWrapper)
        {
            return showListOfRSSItems(inflater, container);
        } else if (getHseView() instanceof HSEViewRSS)
        {
            return showEntireRSS(inflater, container);
        } else
        {
            throw new IllegalStateException("Precondition violated in RssScreenAdapter.onCreateView().Inappropriate view type.");
        }
    }
}
