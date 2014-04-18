package ru.hse.se.shugurov.gui;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.ViewsPackage.HSEViewTypes;

/**
 * Created by Иван on 15.03.14.
 */
public class ViewOfOtherViewsAdapter extends ScreenAdapter implements View.OnClickListener//TODO поправить отступы в отображении, интересные статьи
{
    private static final int MINIMUM_WIDTH_OF_THE_ELEMENT = 300;
    private String CURRENT_VIEW_TAG = "current_view";
    private HSEView currentView;

    public ViewOfOtherViewsAdapter()
    {
    }

    public ViewOfOtherViewsAdapter(HSEView hseView)
    {
        super(hseView);
        currentView = hseView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            currentView = (HSEView) savedInstanceState.get(CURRENT_VIEW_TAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CURRENT_VIEW_TAG, currentView);
    }

    @Override
    public String getActionBarTitle()
    {
        return currentView.getName();
    }

    @Override
    public int getMenuId()
    {
        int menuId;
        if (currentView.isMainView())
        {
            menuId = R.menu.refresh_menu;
        } else
        {
            menuId = super.getMenuId();
        }
        return menuId;
    }


    private LinearLayout getLinearLayoutWithScreenItems(LayoutInflater inflater, HSEView[] elements, int screenWidth)
    {
        int numberOfViewsInRow = 1;
        while (screenWidth / numberOfViewsInRow > (MINIMUM_WIDTH_OF_THE_ELEMENT + 20))
        {
            numberOfViewsInRow++;
        }
        numberOfViewsInRow--;
        LinearLayout content = new LinearLayout(getActivity());
        content.setOrientation(LinearLayout.VERTICAL);
        int rows = elements.length / numberOfViewsInRow;
        if (elements.length % numberOfViewsInRow != 0)
        {
            rows++;
        }
        int idOfCurrentView = 0;
        for (int i = 0; i < rows; i++)
        {
            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            final ViewGroup[] items;
            int currentAmount = elements.length - i * numberOfViewsInRow >= numberOfViewsInRow ? numberOfViewsInRow : (elements.length - i * numberOfViewsInRow) % numberOfViewsInRow;
            items = new ViewGroup[currentAmount];
            for (int j = 0; j < currentAmount; j++)
            {


                int indexOfCurrentItem;
                indexOfCurrentItem = i * numberOfViewsInRow + j;
                items[j] = (ViewGroup) inflater.inflate(R.layout.item, null, false);
                ((TextView) items[j].findViewById(R.id.item_text_id)).setText(elements[indexOfCurrentItem].getName());
                Drawable image = getIconDrawable(elements[indexOfCurrentItem].getHseViewType());
                ((ImageView) items[j].findViewById(R.id.item_image_id)).setImageDrawable(image);
                items[j].setId(idOfCurrentView);
                idOfCurrentView++;
                items[j].setOnClickListener(this);
                items[j].setLayoutParams(new RelativeLayout.LayoutParams(screenWidth / numberOfViewsInRow, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            for (ViewGroup item : items)
            {
                linearLayout.addView(item);
            }
            content.addView(linearLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        return content;
    }

    private Drawable getIconDrawable(int HSEViewType) throws IllegalArgumentException
    {
        int drawableID;
        switch (HSEViewType)
        {
            case HSEViewTypes.INNER_WEB_PAGE:
                drawableID = R.drawable.section1;
                break;
            case HSEViewTypes.WEB_PAGE:
                drawableID = R.drawable.section2;
                break;
            case HSEViewTypes.FILE:
                drawableID = R.drawable.section3;
                break;
            case HSEViewTypes.HTML_CONTENT:
                drawableID = R.drawable.section4;
                break;
            case HSEViewTypes.FACEBOOK:
                drawableID = R.drawable.section5;
                break;
            case HSEViewTypes.VK_PUBLIC_PAGE_WALL:
                drawableID = R.drawable.section6;
                break;
            case HSEViewTypes.VK_FORUM:
                drawableID = R.drawable.section6;
                break;
            case HSEViewTypes.FAQ:
                drawableID = R.drawable.section7;
                break;
            case HSEViewTypes.RSS_WRAPPER:
                drawableID = R.drawable.section8;
                break;
            case HSEViewTypes.EVENTS:
                drawableID = R.drawable.section9;
                break;
            case HSEViewTypes.VIEW_OF_OTHER_VIEWS:
                drawableID = R.drawable.section10;
                break;
            case HSEViewTypes.LINKEDIN:
                drawableID = R.drawable.section12;
                break;
            case HSEViewTypes.MAP:
                drawableID = R.drawable.section13;
                break;
            default:
                throw new IllegalArgumentException("Unknown ID");
        }
        Resources resources = getActivity().getResources();
        return resources.getDrawable(drawableID);
    }


    @Override
    public void onClick(View view)
    {
        HSEView selectedView = currentView.getViewElements()[view.getId()];
        ScreenFactory.instance().showFragment(selectedView);
    }

    @TargetApi(13)
    private int getScreenSizeAfterAPI13(Display display)
    {
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @SuppressWarnings("deprecation")
    private int getScreenSizeBeforeAPI13(Display display)
    {
        return display.getWidth();
    }

    private int getScreenWidth()
    {
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 13)
        {
            return getScreenSizeAfterAPI13(display);
        } else
        {
            return getScreenSizeBeforeAPI13(display);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int screenWidth = container.getWidth();
        if (screenWidth == 0)
        {
            screenWidth = getScreenWidth();
        }
        View content = getLinearLayoutWithScreenItems(inflater, currentView.getViewElements(), screenWidth);
        ScrollView scrollView = (ScrollView) inflater.inflate(R.layout.activity_main_scroll, container, false);
        scrollView.addView(content);
        return scrollView;
    }
}
