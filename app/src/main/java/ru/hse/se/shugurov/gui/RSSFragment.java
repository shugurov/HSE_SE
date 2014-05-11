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
import android.widget.Toast;

import org.json.JSONException;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.RSSScreen;
import ru.hse.se.shugurov.screens.RSSWrapperScreen;

/**
 * Used for demonstrating a list of RSS items via list adapter.
 * <p/>
 * See {@link ru.hse.se.shugurov.gui.RSSListAdapter}
 * <p/>
 * For the required arguments see {@link ru.hse.se.shugurov.gui.AbstractFragment}
 * Created by Ivan Shugurov
 */
public class RSSFragment extends AbstractFragment//TODO здесь валится
{

    private View showListOfRSSItems(final LayoutInflater inflater, final ViewGroup container)
    {
        RSSScreen[] rssItems = null;
        try
        {
            rssItems = ((RSSWrapperScreen) getScreen()).getRSS();
        } catch (JSONException e)
        {

        }
        ListView rssItemsView = (ListView) inflater.inflate(R.layout.activity_main_list, container, false);
        if (rssItems == null)
        {
            Toast.makeText(getActivity(), "Не удалось загрузить контент", Toast.LENGTH_SHORT).show();
            return rssItemsView;
        }
        final RSSListAdapter rssListAdapter = new RSSListAdapter(getActivity(), rssItems);
        rssItemsView.setAdapter(rssListAdapter);
        rssItemsView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                RSSScreen selectedItem = rssListAdapter.getItem(position);
                switch (selectedItem.getType())
                {
                    case FULL_RSS:
                        ScreenFactory.instance().showFragment(selectedItem);
                        break;
                    case ONLY_TITLE:
                        openBrowser(selectedItem.getUrl());
                        break;
                }
            }
        });
        return rssItemsView;
    }

    /*Tries to open provided url in browser*/
    private void openBrowser(String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        browserIntent.setData(uri);
        getActivity().startActivity(browserIntent);
    }

    /*inflates view for rss and sets titles when only 1 element has to be shown*/
    private View showEntireRSS(LayoutInflater inflater, ViewGroup container)
    {
        View rssLayout = inflater.inflate(R.layout.rss_layout, container, false);
        rssLayout.findViewById(R.id.rss_layout_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openBrowser(getScreen().getUrl());
            }
        });
        if (getScreen() instanceof RSSScreen)
        {
            ((TextView) rssLayout.findViewById(R.id.rss_layout_title)).setText(((RSSScreen) getScreen()).getTitle());
            ((TextView) rssLayout.findViewById(R.id.rss_layout_text)).setText(((RSSScreen) getScreen()).getOmitted());
        } else
        {
            throw new IllegalStateException("Precondition violated in RssScreenAdapter.showEntireRSS.Inappropriate view type.");
        }
        return rssLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (getScreen() instanceof RSSWrapperScreen)
        {
            return showListOfRSSItems(inflater, container);
        } else if (getScreen() instanceof RSSScreen)
        {
            return showEntireRSS(inflater, container);
        } else
        {
            throw new IllegalStateException("Precondition violated in RssScreenAdapter.onCreateView().Inappropriate view type.");
        }
    }
}
