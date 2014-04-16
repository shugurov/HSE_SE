package ru.hse.se.shugurov.gui;

import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.ViewsPackage.HSEViewTypes;

/**
 * Created by Иван on 15.03.14.
 */
public class ScreenFactory
{
    private static ScreenFactory screenFactory;
    private ScreenAdapter.ActivityCallback callback;

    private ScreenFactory(ScreenAdapter.ActivityCallback callback)
    {
        this.callback = callback;
    }

    public static void initFactory(ScreenAdapter.ActivityCallback callback)
    {
        screenFactory = new ScreenFactory(callback);
    }

    public static ScreenFactory instance()
    {
        return screenFactory;
    }

    public ScreenAdapter createAdapter(HSEView view)
    {
        ScreenAdapter adapter;
        switch (view.getHseViewType())
        {
            case HSEViewTypes.HTML_CONTENT:
                adapter = new HTMLScreenAdapter(callback, view);
                break;
            case HSEViewTypes.WEB_PAGE:
            case HSEViewTypes.INNER_WEB_PAGE:
                adapter = new BrowserScreenAdapter(callback, view);
                break;
            case HSEViewTypes.RSS_WRAPPER:
                adapter = new RSSScreenAdapter(callback, view);
                break;
            case HSEViewTypes.VK_FORUM:
                adapter = new VKScreenAdapter(callback, view);
                break;
            case HSEViewTypes.VIEW_OF_OTHER_VIEWS:
                adapter = new ViewOfOtherViewsAdapter(callback, view);
                break;
            case HSEViewTypes.MAP:
                adapter = new MapScreenAdapter(callback, view);
                break;
            default:
                throw new IllegalArgumentException("Can't create adapter for this view type");
        }
        return adapter;
    }
}
