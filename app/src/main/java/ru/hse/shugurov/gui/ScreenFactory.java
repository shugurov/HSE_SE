package ru.hse.shugurov.gui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;

import ru.hse.shugurov.R;
import ru.hse.shugurov.screens.BaseScreen;
import ru.hse.shugurov.screens.MapScreen;
import ru.hse.shugurov.screens.ScreenTypes;
import ru.hse.shugurov.screens.ScreenWithFile;
import ru.hse.shugurov.screens.SocialNetworkScreen;
import ru.hse.shugurov.social_networks.AccessToken;
import ru.hse.shugurov.social_networks.FacebookRequester;
import ru.hse.shugurov.social_networks.VKRequester;

/**
 * Class helps developers to create and show new fragments without writing redundant code.
 * This class has only 3 public methods^ one for creating an instanceof factory, second for getting an instance of the class and one factory method.
 * <p/>
 * Method {@code initFactory} should be called only once and before than other methods of the class.
 * <p/>
 * Design of this class was strongly influenced by singleton design pattern.
 * <p/>
 * Created by Ivan Shugurov
 */
public class ScreenFactory implements Serializable //todo remove serializable
{
    private static final String SHARED_PREFERENCES_TAG_SOCIAL = "social_networks";
    private static final String VK_ACCESS_TOKEN_TAG = "vk_access_token";
    private static final String FACEBOOK_ACCESS_TOKEN_TAG = "facebook_access_token";
    private static ScreenFactory screenFactory;
    private transient FragmentActivity activity; //TODO transient
    private boolean isFirstFragment = true;
    private transient AuthenticationFragment authenticationFragment; //TODO transient

    /*crete an instance of the class*/
    private ScreenFactory(FragmentActivity activity, boolean isFirstFragment)
    {
        this.activity = activity;
        this.isFirstFragment = isFirstFragment;
    }

    /*creates new instance of the class and stores it in screenFactory field every time it is called*/
    public static void initFactory(FragmentActivity activity, boolean isFirstFragment)
    {
        screenFactory = new ScreenFactory(activity, isFirstFragment);
    }

    /**
     * Used to get instance of a factory
     *
     * @return actual instance of factory or null if method initFactory was not called
     */
    public static ScreenFactory instance()
    {
        return screenFactory;
    }

    /**
     * Changes 2 fragments with animation
     *
     * @param manager          is used to change fragments. Not null
     * @param fragmentToAppear will replace current fragment. Not null
     */
    public static void changeFragments(FragmentManager manager, Fragment fragmentToAppear)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.content_appearing_movement_to_the_left, R.anim.content_disappearing_movement_to_the_left, R.anim.content_appearing_movement_to_the_right, R.anim.content_disappearing_movement_to_the_right);
        transaction.replace(R.id.main, fragmentToAppear);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Factory method which creates and shows a fragment if necessary. Also can open browser and open files.
     * <p/>
     * Type of new fragment is determined based on view provided by a caller
     *
     * @param view view is about to be shown
     */
    public void showFragment(final BaseScreen view)
    {
        Fragment fragment = null;
        Bundle arguments = new Bundle();
        switch (view.getScreenType())
        {
            case ScreenTypes.HTML_CONTENT:
                fragment = new HTMLScreenFragment();
                arguments.putParcelable(HTMLScreenFragment.HSE_VIEW_TAG, view);
                break;
            case ScreenTypes.WEB_PAGE:
                openBrowser(view.getUrl());
                break;
            case ScreenTypes.INNER_WEB_PAGE:
                fragment = new InternalWebFragment();
                arguments.putParcelable(InternalWebFragment.HSE_VIEW_TAG, view);
                break;
            case ScreenTypes.RSS:
            case ScreenTypes.RSS_WRAPPER:
                fragment = new RSSFragment();
                arguments.putParcelable(RSSFragment.HSE_VIEW_TAG, view);
                break;
            case ScreenTypes.VK_FORUM:
                SocialNetworkScreen socialNetworkScreen = (SocialNetworkScreen) view;
                AccessToken vkAccessToken = getVkAccessToken();
                if (vkAccessToken != null)
                {
                    fragment = new TopicsFragment();
                    arguments.putString(SocialNetworkAbstractList.GROUP_ID_TAG, socialNetworkScreen.getObjectId());
                    arguments.putString(SocialNetworkAbstractList.GROUP_NAME_TAG, socialNetworkScreen.getName());
                    arguments.putSerializable(SocialNetworkAbstractList.REQUESTER_TAG, new VKRequester(vkAccessToken));
                }
                break;
            case ScreenTypes.VK_PUBLIC_PAGE_WALL:
                socialNetworkScreen = (SocialNetworkScreen) view;
                vkAccessToken = getVkAccessToken();
                if (vkAccessToken != null)
                {
                    fragment = new WallPostScreen();
                    arguments.putString(SocialNetworkAbstractList.GROUP_ID_TAG, socialNetworkScreen.getObjectId());
                    arguments.putString(SocialNetworkAbstractList.GROUP_NAME_TAG, socialNetworkScreen.getName());
                    arguments.putSerializable(SocialNetworkAbstractList.REQUESTER_TAG, new VKRequester(vkAccessToken));
                }
                break;
            case ScreenTypes.VIEW_OF_OTHER_VIEWS:
                fragment = new ViewOfOtherViewsAdapter();
                arguments.putParcelable(ViewOfOtherViewsAdapter.HSE_VIEW_TAG, view);
                break;
            case ScreenTypes.MAP:
                fragment = new MapFragment();
                arguments.putString(MapFragment.TITLE_TAG, view.getName());
                arguments.putParcelableArray(MapFragment.MARKERS_TAG, ((MapScreen) view).getMarkers());
                break;
            case ScreenTypes.EVENTS:
                fragment = new EventFragment();
                arguments.putParcelable(EventFragment.HSE_VIEW_TAG, view);
                break;
            case ScreenTypes.FILE:
                openFile((ScreenWithFile) view);
                break;
            case ScreenTypes.FACEBOOK:
                SocialNetworkScreen facebookView = (SocialNetworkScreen) view;
                AccessToken facebookAccessToken = getFacebookAccessToken();
                if (facebookAccessToken != null)
                {
                    fragment = new TopicsFragment();
                    arguments.putString(SocialNetworkAbstractList.GROUP_ID_TAG, facebookView.getObjectId());
                    arguments.putString(SocialNetworkAbstractList.GROUP_NAME_TAG, facebookView.getName());
                    arguments.putSerializable(SocialNetworkAbstractList.REQUESTER_TAG, new FacebookRequester(facebookAccessToken));
                }
                break;
            default:
                return;
        }

        if (fragment != null)
        {
            android.support.v4.app.FragmentManager manager = activity.getSupportFragmentManager();
            fragment.setArguments(arguments);
            if (isFirstFragment)
            {
                setFragment(fragment);
                isFirstFragment = false;
            } else
            {
                changeFragments(manager, fragment);
            }
        }
    }

    /*makes request to open web page via intent. If this action is unavailable then tells ser about it*/
    private void openBrowser(String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        browserIntent.setData(uri);
        try
        {
            activity.startActivity(browserIntent);
        } catch (ActivityNotFoundException e)
        {
            Toast.makeText(activity, "Нет приложений, способных открыть этот тип фалов", Toast.LENGTH_SHORT).show();
        }
    }

    /*shows a new fragment without animation and doe not add this fragment to back stack*/
    private void setFragment(Fragment fragmentToAppear)
    {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main, fragmentToAppear);
        transaction.commit();
    }

    /*Tries to open file in external applications. If file type is not supported by any of installed
     applications then tells user about it*/
    private void openFile(ScreenWithFile fileView)
    {
        File file = new File(activity.getFilesDir(), fileView.getFileName());
        if (file.exists())
        {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file), fileView.getFileType());
            try
            {
                Intent chooser = Intent.createChooser(target, "Open a file");
                activity.startActivity(chooser);
            } catch (ActivityNotFoundException e)
            {
                Toast.makeText(activity, "Нет приложений, способных открыть этот тип фалов", Toast.LENGTH_SHORT).show();
            }

        } else
        {
            Toast.makeText(activity, "Не удалось открыть файл", Toast.LENGTH_SHORT).show();
        }
    }

    /*gets vk access token stored in shared preferences. Checks if derived token is not null and not expired.
     If token is null or expired it requests a new token*/
    private AccessToken getVkAccessToken()
    {
        AccessToken accessToken = null;
        SharedPreferences preferences = activity.getSharedPreferences(SHARED_PREFERENCES_TAG_SOCIAL, Context.MODE_PRIVATE);
        String serializedToken = preferences.getString(VK_ACCESS_TOKEN_TAG, null);
        if (serializedToken == null)
        {
            requestVkToken();
        } else
        {
            accessToken = new AccessToken(serializedToken);
            if (accessToken.hasExpired())
            {
                requestVkToken();
            }
        }

        return accessToken;
    }

    /*gets vk access token stored in shared preferences. Checks if derived token is not null and not expired.
      If token is null or expired it requests a new token*/
    private AccessToken getFacebookAccessToken()
    {
        AccessToken accessToken = null;
        SharedPreferences preferences = activity.getSharedPreferences(SHARED_PREFERENCES_TAG_SOCIAL, Context.MODE_PRIVATE);
        String serializedToken = preferences.getString(FACEBOOK_ACCESS_TOKEN_TAG, null);
        if (serializedToken == null)
        {
            requestFacebookToken();

        } else
        {
            accessToken = new AccessToken(serializedToken);
            if (accessToken.hasExpired())
            {
                requestFacebookToken();
            }
        }
        return accessToken;
    }

    /*shows authentication fragment ans provides FacebookRequester to it*/
    private void requestFacebookToken()
    {
        AuthenticationFragment.AccessTokenRequest request = new AuthenticationFragment.AccessTokenRequest()
        {
            @Override
            public void receiveToken(AccessToken accessToken)
            {
                if (accessToken != null)
                {
                    writeToSharePreferences(FACEBOOK_ACCESS_TOKEN_TAG, accessToken.getStringRepresentation());
                    notifyAboutAccessTokenReceiving();
                }
            }
        };
        authenticationFragment = new AuthenticationFragment();
        Bundle arguments = new Bundle();
        arguments.putString(AuthenticationFragment.URL_TAG, FacebookRequester.AUTH);
        arguments.putSerializable(AuthenticationFragment.TOKEN_REQUEST_TAG, request);
        authenticationFragment.setArguments(arguments);
        changeFragments(activity.getSupportFragmentManager(), authenticationFragment);
    }

    /*writes given string to shared preferences(using private mode)*/
    private void writeToSharePreferences(String tag, String content)
    {
        if (activity == null)
        {
            ScreenFactory.instance().writeToSharePreferences(tag, content);
        } else
        {
            SharedPreferences preferences = activity.getSharedPreferences(SHARED_PREFERENCES_TAG_SOCIAL, Context.MODE_PRIVATE);
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            preferencesEditor.putString(tag, content);
            preferencesEditor.commit();
        }
    }

    /*shows authentication fragment ans provides VkRequester to it*/
    private void requestVkToken()
    {
        AuthenticationFragment.AccessTokenRequest request = new AuthenticationFragment.AccessTokenRequest()
        {
            @Override
            public void receiveToken(AccessToken accessToken)
            {
                if (accessToken != null)
                {
                    writeToSharePreferences(VK_ACCESS_TOKEN_TAG, accessToken.getStringRepresentation());
                    notifyAboutAccessTokenReceiving();

                }
            }
        };
        authenticationFragment = new AuthenticationFragment();
        Bundle arguments = new Bundle();
        arguments.putString(AuthenticationFragment.URL_TAG, VKRequester.OAUTH);
        arguments.putSerializable(AuthenticationFragment.TOKEN_REQUEST_TAG, request);
        authenticationFragment.setArguments(arguments);
        changeFragments(activity.getSupportFragmentManager(), authenticationFragment);
    }

    /*tells a user about receiving a token*/
    private void notifyAboutAccessTokenReceiving()
    {
        if (activity == null)
        {
            ScreenFactory factory = ScreenFactory.instance();
            Toast.makeText(factory.activity, "Авторизация успешна", Toast.LENGTH_SHORT).show();
        } else
        {
            Toast.makeText(activity, "Авторизация успешна", Toast.LENGTH_SHORT).show();
        }
    }
}
