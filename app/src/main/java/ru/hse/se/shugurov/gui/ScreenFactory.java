package ru.hse.se.shugurov.gui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import java.io.File;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.EventScreen;
import ru.hse.se.shugurov.screens.HSEView;
import ru.hse.se.shugurov.screens.HSEViewTypes;
import ru.hse.se.shugurov.screens.HSEViewWithFile;
import ru.hse.se.shugurov.screens.MapScreen;

/**
 * Created by Иван on 15.03.14.
 */
public class ScreenFactory//TODO экран с браузером падает при перевороте
{
    private static ScreenFactory screenFactory;
    private FragmentActivity activity;
    private boolean isFirstFragment = true;

    private ScreenFactory(FragmentActivity activity, boolean isFirstFragment)
    {
        this.activity = activity;
        this.isFirstFragment = isFirstFragment;
    }

    public static void initFactory(FragmentActivity activity, boolean isFirstFragment)
    {
        screenFactory = new ScreenFactory(activity, isFirstFragment);
    }

    public static ScreenFactory instance()
    {
        return screenFactory;
    }

    public static void changeFragments(FragmentManager manager, Fragment fragmentToAppear)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.content_appearing_movement_to_the_left, R.anim.content_disappearing_movement_to_the_left, R.anim.content_appearing_movement_to_the_right, R.anim.content_disappearing_movement_to_the_right);
        transaction.replace(R.id.main, fragmentToAppear);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showFragment(final HSEView view)
    {
        Fragment adapter;
        switch (view.getHseViewType())
        {
            case HSEViewTypes.HTML_CONTENT:
                adapter = new HTMLScreenAdapter(view);
                break;
            case HSEViewTypes.WEB_PAGE:
                openBrowser(view.getUrl());
                adapter = null;
                break;
            case HSEViewTypes.INNER_WEB_PAGE:
                adapter = new InternalWebScreenAdapter(view);
                break;
            case HSEViewTypes.RSS:
            case HSEViewTypes.RSS_WRAPPER:
                adapter = new RSSScreenAdapter(view);
                break;
            case HSEViewTypes.VK_FORUM:
                adapter = new VKScreenAdapter(view);
                break;
            case HSEViewTypes.VIEW_OF_OTHER_VIEWS:
                adapter = new ViewOfOtherViewsAdapter(view);
                break;
            case HSEViewTypes.MAP:
                adapter = new MapScreenAdapter((MapScreen) view);
                break;
            case HSEViewTypes.EVENTS:
                adapter = new EventScreenAdapter((EventScreen) view);
                break;
            case HSEViewTypes.FILE:
                adapter = null;
                openFile((HSEViewWithFile) view);
                break;
            default:
                throw new IllegalArgumentException("Can't create adapter for this view type");
        }

        if (adapter != null)
        {
            android.support.v4.app.FragmentManager manager = activity.getSupportFragmentManager();
            if (isFirstFragment)
            {
                setFragment(manager, adapter);
                isFirstFragment = false;
            } else
            {
                changeFragments(manager, adapter);
            }
        }
    }

    private void openBrowser(String url)//TODO а стоит ли этому быть тут?
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        browserIntent.setData(uri);
        activity.startActivity(browserIntent);
    }

    private void setFragment(FragmentManager manager, Fragment fragmentToAppear)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main, fragmentToAppear);
        transaction.commit();
    }

    private void openFile(HSEViewWithFile fileView)
    {
        File file = new File(activity.getFilesDir().getAbsolutePath() + "/" + fileView.getFileName());
        if (file.exists())
        {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file), fileView.getFileType());
            Intent chooser = Intent.createChooser(target, "Open a file");
            try
            {
                activity.startActivity(chooser);
            } catch (ActivityNotFoundException e)
            {
                Toast.makeText(activity, "Нет приложений, способных открыть этот тип фалов", Toast.LENGTH_SHORT).show();
            }

        }//TODO то делать,если не существует?


    }
}
