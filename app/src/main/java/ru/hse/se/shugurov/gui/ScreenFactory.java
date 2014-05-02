package ru.hse.se.shugurov.gui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import ru.hse.se.shugurov.screens.VKHSEView;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.VkWebClient;

/**
 * Created by Иван on 15.03.14.
 */
public class ScreenFactory//TODO экран с браузером падает при перевороте
{
    private static final String SHARED_PREFERENCES_TAG_SOCIAL = "social_networks";
    private static final String VK_ACCESS_TOKEN_TAG = "vk_access_token";
    private static ScreenFactory screenFactory;
    private FragmentActivity activity;
    private boolean isFirstFragment = true;
    private AuthorizationFragment authorizationFragment;

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
        Fragment adapter = null;
        switch (view.getHseViewType())
        {
            case HSEViewTypes.HTML_CONTENT:
                adapter = new HTMLScreenAdapter(view);
                break;
            case HSEViewTypes.WEB_PAGE:
                openBrowser(view.getUrl());
                break;
            case HSEViewTypes.INNER_WEB_PAGE:
                adapter = new InternalWebScreenAdapter(view);
                break;
            case HSEViewTypes.RSS:
            case HSEViewTypes.RSS_WRAPPER:
                adapter = new RSSScreenAdapter(view);
                break;
            case HSEViewTypes.VK_FORUM:
                VKHSEView vkhseView = (VKHSEView) view;
                AccessToken vkAccessToken = getVkAccessToken(vkhseView);
                if (vkAccessToken != null)
                {
                    VkTopicsScreenAdapter topicsScreenAdapter = new VkTopicsScreenAdapter(vkhseView.getName(), vkhseView.getObjectID(), vkAccessToken);
                    changeFragments(activity.getSupportFragmentManager(), topicsScreenAdapter);
                }
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
                setFragment(adapter);
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

    private void setFragment(Fragment fragmentToAppear)
    {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
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

    private AccessToken getVkAccessToken(VKHSEView vkhseView)
    {
        AccessToken accessToken = null;
        SharedPreferences preferences = activity.getSharedPreferences(SHARED_PREFERENCES_TAG_SOCIAL, Context.MODE_PRIVATE);
        String serializedToken = preferences.getString(VK_ACCESS_TOKEN_TAG, null);
        if (serializedToken == null)
        {
            requestVkToken(vkhseView);
        } else
        {
            accessToken = new AccessToken(serializedToken);
            if (accessToken.hasExpired())
            {
                requestVkToken(vkhseView);
            }
        }

        return accessToken;
    }

    private void requestVkToken(final VKHSEView vkhseView)
    {
        authorizationFragment = new AuthorizationFragment(VkWebClient.OAUTH, new AuthorizationFragment.AccessTokenRequest()
        {
            @Override
            public void receiveToken(AccessToken accessToken)
            {
                if (accessToken == null)
                {
                    activity.getSupportFragmentManager().popBackStack();
                } else
                {
                    SharedPreferences preferences = activity.getSharedPreferences(SHARED_PREFERENCES_TAG_SOCIAL, Context.MODE_PRIVATE);
                    SharedPreferences.Editor preferencesEditor = preferences.edit();
                    preferencesEditor.putString(VK_ACCESS_TOKEN_TAG, accessToken.getStringRepresentation());
                    preferencesEditor.commit();
                    VkTopicsScreenAdapter topicsScreenAdapter = new VkTopicsScreenAdapter(vkhseView.getName(), vkhseView.getObjectID(), accessToken);
                    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                    transaction.remove(authorizationFragment);
                    transaction.commit();
                    changeFragments(activity.getSupportFragmentManager(), topicsScreenAdapter);
                }
            }
        });
        changeFragments(activity.getSupportFragmentManager(), authorizationFragment);
    }
}
