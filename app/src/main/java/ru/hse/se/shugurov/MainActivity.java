package ru.hse.se.shugurov;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import ru.hse.se.shugurov.ViewsPackage.FileDescription;
import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.ViewsPackage.HSEViewRSS;
import ru.hse.se.shugurov.ViewsPackage.HSEViewRSSWrapper;
import ru.hse.se.shugurov.ViewsPackage.HSEViewTypes;
import ru.hse.se.shugurov.ViewsPackage.HSEViewWithFile;
import ru.hse.se.shugurov.ViewsPackage.VKHSEView;
import ru.hse.se.shugurov.gui.ScreenAdapter;
import ru.hse.se.shugurov.gui.VKScreenAdapter;
import ru.hse.se.shugurov.observer.Observer;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.social_networks.VkWebView;
import ru.hse.se.shugurov.utills.DownloadStatus;
import ru.hse.se.shugurov.utills.Downloader;
import ru.hse.se.shugurov.utills.FileManager;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, Observer
{
    private static final int MINIMUM_WIDTH_OF_THE_ELEMENT = 300;
    private static int freeId = 1;
    private HSEView hseView;
    private HSEView currentView;
    private HSEView[] elements;  //TODO может можно удалить это поле
    private int[] indexes;
    private int screenWidth;
    private int contentViewId;
    private Downloader task;
    private ProgressDialog progressDialog;
    private Bundle savedInstanceState;
    private boolean doShowRefreshButton = false;
    private boolean isRefreshButtonShown = false;
    private boolean doShowAddMessageButton = false;
    private ScreenAdapter screenAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;
        checkFiles();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (outState != null)
        {
            outState.putString("index", currentView.getIndex());//TODO почему падает тут?(
        }
    }

    @Override
    public void onBackPressed()
    {
        if (currentView.isMainView())
        {
            return;
        } else
        {
            if (screenAdapter != null && screenAdapter.hasPreviousView())
            {
                screenAdapter.showPreviousView();
            } else
            {
                openPreviousView();
            }
        }
    }

    private LinearLayout getLinearLayoutWithScreenItems(HSEView[] elements)//TODO исправить использование поля indexes
    {
        indexes = new int[elements.length];
        int numberOfViewsInRow = 1;
        while (screenWidth / numberOfViewsInRow > (MINIMUM_WIDTH_OF_THE_ELEMENT + 20))
        {
            numberOfViewsInRow++;
        }
        numberOfViewsInRow--;
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        int rows = elements.length / numberOfViewsInRow;
        if (elements.length % numberOfViewsInRow != 0)
        {
            rows++;
        }
        for (int i = 0; i < rows; i++)
        {
            LinearLayout linearLayout;
            linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setPadding(10, 0, 10, 10);
            final RelativeLayout[] items;
            int currentAmount;
            currentAmount = elements.length - i * numberOfViewsInRow >= numberOfViewsInRow ? numberOfViewsInRow : (elements.length - i * numberOfViewsInRow) % numberOfViewsInRow;
            items = new RelativeLayout[currentAmount];
            for (int j = 0; j < currentAmount; j++)
            {


                int indexOfCurrentItem;
                indexOfCurrentItem = i * numberOfViewsInRow + j;
                items[j] = (RelativeLayout) getLayoutInflater().inflate(R.layout.item, null);
                ((TextView) items[j].findViewById(R.id.item_text_id)).setText(elements[indexOfCurrentItem].getName());
                ((ImageView) items[j].findViewById(R.id.item_image_id)).setImageDrawable(getDrawable(elements[indexOfCurrentItem].getHseViewType()));
                indexes[indexOfCurrentItem] = getFreeId();
                items[j].setId(indexes[indexOfCurrentItem]);
                items[j].setOnClickListener(this);
                items[j].setLayoutParams(new RelativeLayout.LayoutParams(screenWidth / numberOfViewsInRow - 20, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            for (RelativeLayout item : items)
            {
                linearLayout.addView(item);
            }
            content.addView(linearLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        return content;
    }

    private int getScreenWidth()
    {
        Display display;
        display = getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

    private int getFreeId()
    {
        freeId++;
        while (findViewById(freeId) != null)
        {
            freeId++;
        }
        return freeId;
    }

    private void openViewOfOtherViews(HSEView givenView, boolean isButtonBackClicked)
    {
        LinearLayout viewToAppear;
        viewToAppear = new LinearLayout(this);
        viewToAppear.setOrientation(LinearLayout.VERTICAL);
        HSEView[] newViewElements;
        currentView = givenView;
        newViewElements = givenView.getViewElements();
        this.elements = newViewElements;
        viewToAppear = getLinearLayoutWithScreenItems(newViewElements);
        View viewToDisappear;
        viewToDisappear = findViewById(contentViewId);
        ScrollView parentView;
        parentView = (ScrollView) findViewById(R.id.main_scroll);
        changeViews(parentView, viewToDisappear, viewToAppear, isButtonBackClicked);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        if (doShowRefreshButton)
        {
            inflater.inflate(R.menu.refresh_menu, menu);
        } else
        {
            if (doShowAddMessageButton)
            {
                inflater.inflate(R.menu.message_adding_menu, menu);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_refresh:
                startProgressDialog();
                createAsyncTask(DownloadStatus.DOWNLOAD_JSON);
                task.execute(new FileDescription("json", HSEView.JSON_LINK));
                return true;
            case R.id.action_add_message:
                Toast.makeText(this, "I'm working!", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void update()
    {
        switch (task.getDownloadStatus())
        {
            case DOWNLOAD_JSON:
                setJsonField();
                checkFiles();
                break;
            case DOWNLOAD_FILES:
                setGeneralScreen();
                hseView.notifyAboutFiles(this);//TODO что и как тут проиходит?(
                progressDialog.cancel();
                break;
        }
    }

    @Override
    public void onClick(View view)
    {
        int givenId;
        givenId = view.getId();
        final HSEView givenView = findViewUsingID(givenId);
        if (givenView == null)
        {
            Toast.makeText(this, "Неизвестная ошибка", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (givenView.getHseViewType()) //TODO видимо, дописать надо
        {
            case HSEViewTypes.VIEW_OF_OTHER_VIEWS:
                openViewOfOtherViews(givenView, false);
                break;
            case HSEViewTypes.HTML_CONTENT:
                FileManager fileManager = new FileManager(this);
                String HTMLContent = fileManager.getFileContent(givenView.getKey());
                String mime = "text/html";
                String encoding = "utf-8";
                WebView webView;
                webView = new WebView(this);
                webView.loadDataWithBaseURL(null, HTMLContent, mime, encoding, null);
                webView.setWebViewClient(new WebViewClient());
                changeViews(((ScrollView) findViewById(R.id.main_scroll)), findViewById(contentViewId), webView, false);
                currentView = givenView;
                break;
            case HSEViewTypes.INNER_WEB_PAGE:
                WebView webViewToAppear;
                webViewToAppear = new WebView(this);
                webViewToAppear.loadUrl(givenView.getUrl());
                webViewToAppear.setWebViewClient(new WebViewClient());
                changeViews(((ScrollView) findViewById(R.id.main_scroll)), findViewById(contentViewId), webViewToAppear, false);
                currentView = givenView;
                break;
            case HSEViewTypes.EVENTS:
                break;
            case HSEViewTypes.FACEBOOK:
                break;
            case HSEViewTypes.FAQ:
                break;
            case HSEViewTypes.LINKEDIN:
                break;
            case HSEViewTypes.FILE:
                HSEViewWithFile viewWithFile = (HSEViewWithFile) givenView;
                String path = getFilesDir() + File.separator + viewWithFile.getFileName();
                /*File file = new File(path);
                if (file.exists())
                {
                    Uri filePath = Uri.fromFile(file);
                    Intent fileIntent = new Intent(Intent.ACTION_VIEW);
                    fileIntent.setDataAndType(filePath, "application/pdf");
                    try
                    {
                        startActivity(fileIntent);
                    } catch (ActivityNotFoundException ex)
                    {
                        Toast.makeText(this, "Не найдено приложений, способных открыть файл", Toast.LENGTH_LONG).show();
                    }
                }//TODO что делать, если файл не существует?*/
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
                //intent.setType("application/pdf");
                PackageManager pm = getPackageManager();
                List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
                if (activities.size() > 0)
                {
                    startActivity(intent);
                } else
                {
                    Toast.makeText(this, "Не найдено приложений, способных открыть файл", Toast.LENGTH_LONG).show();
                }

                break;
            case HSEViewTypes.RSS_WRAPPER:
                LinearLayout viewToAppear = new LinearLayout(this);
                viewToAppear.setOrientation(LinearLayout.VERTICAL);
                if (givenView instanceof HSEViewRSSWrapper)
                {
                    HSEViewRSS[] connectedViews = ((HSEViewRSSWrapper) givenView).getConnectedViews();
                    int count = 0;
                    indexes = new int[connectedViews.length];
                    elements = new HSEView[connectedViews.length];
                    for (HSEViewRSS viewRSS : connectedViews)
                    {
                        LinearLayout item = (LinearLayout) getLayoutInflater().inflate(R.layout.rss_item, null);
                        ((TextView) item.findViewById(R.id.rss_item_title)).setText(viewRSS.getTitle());
                        ((TextView) item.findViewById(R.id.rss_item_text)).setText(viewRSS.getSummary());
                        item.setOnClickListener(this);
                        int id = getFreeId();
                        indexes[count] = id;
                        elements[count] = viewRSS;
                        count++;
                        item.setId(id);
                        viewToAppear.addView(item);
                    }
                } else
                {
                    return;
                }
                changeViews((ScrollView) findViewById(R.id.main_scroll), findViewById(contentViewId), viewToAppear, false);
                currentView = givenView;
                break;
            case HSEViewTypes.VK_FORUM:
            case HSEViewTypes.VK_PUBLIC_PAGE_WALL:
                /*currentView = givenView;
                if (vkRequester == null)//TODO проверять существование токена и не надо каждый раз показывать веб вью(
                {
                    WebView vkView;
                    vkView = new WebView(this);
                    vkView.loadUrl(VkWebView.OAUTH);
                    final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                    final ListView vkList = (ListView) inflater.inflate(R.layout.activity_main_list, (LinearLayout) findViewById(R.id.main), false);
                    vkView.setWebViewClient(new VkWebView(new VkWebView.VKCallBack()
                    {
                        @Override
                        public void call(String token)
                        {
                            vkRequester = new VKRequester(token);
                            vkRequester.getTopics(((VKHSEView) currentView).getObjectID(), new Requester.RequestResultCallback()
                            {
                                @Override
                                public void pushResult(String result)
                                {
                                    final VKTopicsAdapter adapter = new VKTopicsAdapter(MainActivity.this, vkRequester.getTopicsAdapter(result));
                                    doShowAddMessageButton = true;
                                    setActionBar();
                                    vkList.setAdapter(adapter);
                                    vkList.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                    {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                                        {
                                            vkRequester.getComments(((VKHSEView) currentView).getObjectID(), adapter.getItem(position).getTopicID(), new Requester.RequestResultCallback()
                                            {
                                                @Override
                                                public void pushResult(String result)
                                                {
                                                    VKAbstractItem[] comments = vkRequester.getComments(result);
                                                    if (comments == null)
                                                    {
                                                        //TODO что делать, если массив комментариев пуст?
                                                    } else
                                                    {
                                                        VKCommentsAdapter vkCommentsAdapter = new VKCommentsAdapter(MainActivity.this, comments);
                                                        ListView responsesListView = (ListView) inflater.inflate(R.layout.activity_main_list, (LinearLayout) findViewById(R.id.main), false);
                                                        responsesListView.setAdapter(vkCommentsAdapter);
                                                        changeViews((LinearLayout) findViewById(R.id.main), vkList, responsesListView, false);
                                                    }
                                                }
                                            });

                                        }
                                    });
                                }
                            });
                        }
                    }));
                    changeViews((LinearLayout) findViewById(R.id.main), findViewById(R.id.main_scroll), vkList, false);
                    return;
                } else//TODO то делать, если requester есть
                {

                }*/
                screenAdapter = new VKScreenAdapter(new MainActivityCallback(), (ViewGroup) findViewById(R.id.main), findViewById(R.id.main_scroll), (VKHSEView) givenView);
                break;
            case HSEViewTypes.WEB_PAGE:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(givenView.getUrl()));
                startActivity(intent);
                break;
            case HSEViewTypes.RSS:
                if (currentView instanceof HSEViewRSSWrapper)
                {
                    HSEViewRSS[] connectedViews = ((HSEViewRSSWrapper) currentView).getConnectedViews();
                    HSEViewRSS nextView = null;
                    int id = view.getId();
                    for (int i = 0; i < indexes.length; i++)
                    {
                        if (id == indexes[i])
                        {
                            try
                            {
                                nextView = connectedViews[i];
                                break;
                            } catch (IllegalArgumentException e)
                            {
                                e.printStackTrace();
                            }
                            return;
                        }
                    }
                    if (nextView == null)
                    {
                        return;
                    } else
                    {
                        switch (nextView.getType())
                        {
                            case FULL_RSS:
                                LinearLayout anotherViewToAppear = new LinearLayout(this);
                                anotherViewToAppear.setOrientation(LinearLayout.VERTICAL);
                                LinearLayout RSSViewToAppear = (LinearLayout) getLayoutInflater().inflate(R.layout.rss_item_full, null);
                                ((TextView) RSSViewToAppear.findViewById(R.id.rss_item_full_title)).setText(((HSEViewRSS) givenView).getTitle());
                                ((TextView) RSSViewToAppear.findViewById(R.id.rss_item_full_text)).setText(((HSEViewRSS) givenView).getOmitted());
                                RSSViewToAppear.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Intent intent;
                                        intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(currentView.getUrl()));
                                        startActivity(intent);
                                    }
                                });
                                anotherViewToAppear.addView(RSSViewToAppear);
                                changeViews((ScrollView) findViewById(R.id.main_scroll), findViewById(contentViewId), anotherViewToAppear, false);
                                currentView = givenView;
                                break;
                            case ONLY_TITLE:
                                Intent intentForBrowser;
                                intentForBrowser = new Intent(Intent.ACTION_VIEW);
                                intentForBrowser.setData(Uri.parse(nextView.getUrl()));
                                startActivity(intentForBrowser);
                                break;
                            default:
                                return;
                        }
                    }
                    return;
                }
                break;
        }
        setActionBar();
    }

    private HSEView findViewUsingID(int givenId)
    {
        int elementIndex = -1;
        for (int i = 0; i < indexes.length; i++)
        {
            if (indexes[i] == givenId)
            {
                elementIndex = i;
                break;
            }
        }
        if (elementIndex == -1)
        {
            return null;
        } else
        {
            return elements[elementIndex];
        }
    }

    private Drawable getDrawable(int HSEViewType) throws IllegalArgumentException
    {
        switch (HSEViewType)
        {
            case HSEViewTypes.VIEW_OF_OTHER_VIEWS:
                return getResources().getDrawable(R.drawable.section10);
            case HSEViewTypes.FILE:
                return getResources().getDrawable(R.drawable.section3);
            case HSEViewTypes.FAQ:
                return getResources().getDrawable(R.drawable.section7);
            case HSEViewTypes.FACEBOOK:
                return getResources().getDrawable(R.drawable.section5);
            case HSEViewTypes.VK_PUBLIC_PAGE_WALL:
                return getResources().getDrawable(R.drawable.section6);
            case HSEViewTypes.INNER_WEB_PAGE:
                return getResources().getDrawable(R.drawable.section1);
            case HSEViewTypes.HTML_CONTENT:
                return getResources().getDrawable(R.drawable.section4);
            case HSEViewTypes.WEB_PAGE:
                return getResources().getDrawable(R.drawable.section2);
            case HSEViewTypes.VK_FORUM:
                return getResources().getDrawable(R.drawable.section6);
            case HSEViewTypes.EVENTS:
                return getResources().getDrawable(R.drawable.section9);
            case HSEViewTypes.RSS_WRAPPER:
                return getResources().getDrawable(R.drawable.section8);
            case HSEViewTypes.MAP:
                return getResources().getDrawable(R.drawable.section13);
            case HSEViewTypes.LINKEDIN:
                return getResources().getDrawable(R.drawable.section12);
            default:
                throw new IllegalArgumentException("Unknown ID");
        }
    }

    private void makeViewChangeAnimation(View viewToHide, View viewToAppear, boolean isButtonBackClicked)
    {
        Animation animationDisappearing;
        if (isButtonBackClicked)
        {
            animationDisappearing = AnimationUtils.loadAnimation(this, R.anim.content_disappearing_movement_to_the_right);
        } else
        {
            animationDisappearing = AnimationUtils.loadAnimation(this, R.anim.content_disappearing_movement_to_the_left);
        }
        animationDisappearing.reset();
        viewToHide.setAnimation(animationDisappearing);
        Animation animationAppearing;
        if (isButtonBackClicked)
        {
            animationAppearing = AnimationUtils.loadAnimation(this, R.anim.content_appearing_movement_to_the_right);
        } else
        {
            animationAppearing = AnimationUtils.loadAnimation(this, R.anim.content_appearing_movement_to_the_left);
        }
        animationAppearing.reset();
        viewToAppear.setAnimation(animationAppearing);
    }

    private void changeViews(ViewGroup parentView, View viewToDisappear, View viewToAppear, boolean isButtonBackClicked)
    {
        viewToAppear.setVisibility(View.INVISIBLE);
        viewToDisappear.setVisibility(View.INVISIBLE);
        makeViewChangeAnimation(viewToDisappear, viewToAppear, isButtonBackClicked);
        parentView.removeAllViews();
        parentView.addView(viewToAppear);
        viewToAppear.setVisibility(View.VISIBLE);
        this.contentViewId = getFreeId();
        viewToAppear.setId(contentViewId);
    }

    private void setActionBar()
    {
        setTitleToActionBar(currentView.getName());
        if (currentView.isMainView() && !isRefreshButtonShown)
        {
            doShowRefreshButton = true;
            isRefreshButtonShown = true;
        } else
        {
            if (!currentView.isMainView() && isRefreshButtonShown)
            {
                doShowRefreshButton = false;

                isRefreshButtonShown = false;
            }
        }
        supportInvalidateOptionsMenu();
    }

    private void setTitleToActionBar(String title)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
    }

    private void openPreviousView()
    {
        switch (currentView.getHseViewType())
        {
            case HSEViewTypes.RSS://TODO поправить анимацию нажатия
                currentView = hseView.getViewByIndex(currentView.getParentIndex());
                LinearLayout viewToAppear;
                viewToAppear = new LinearLayout(this);
                viewToAppear.setOrientation(LinearLayout.VERTICAL);
                HSEViewRSS[] newViewElements;
                if (!(currentView instanceof HSEViewRSSWrapper))
                {
                    return;
                }
                newViewElements = ((HSEViewRSSWrapper) currentView).getConnectedViews();
                this.elements = newViewElements;
                int count = 0;
                indexes = new int[elements.length];
                for (HSEViewRSS viewRSS : newViewElements)
                {
                    LinearLayout item = (LinearLayout) getLayoutInflater().inflate(R.layout.rss_item, null);
                    ((TextView) item.findViewById(R.id.rss_item_title)).setText(viewRSS.getTitle());
                    ((TextView) item.findViewById(R.id.rss_item_text)).setText(viewRSS.getSummary());
                    item.setOnClickListener(this);
                    int id = getFreeId();
                    indexes[count] = id;
                    elements[count] = viewRSS;
                    count++;
                    item.setId(id);
                    viewToAppear.addView(item);
                }
                changeViews((ScrollView) findViewById(R.id.main_scroll), findViewById(contentViewId), viewToAppear, true);

                break;
            default:
                openViewOfOtherViews(hseView.getViewByIndex(currentView.getParentIndex()), true);
        }
        setActionBar();
    }

    private void createAsyncTask(DownloadStatus status)
    {
        task = new Downloader(this, status);
        task.addObserver(this);
    }

    private void createAsyncTask(ArrayList<FileDescription> descriptions, DownloadStatus status)
    {
        task = new Downloader(this, descriptions, status);
        task.addObserver(this);
    }

    private void checkFiles()
    {
        FileManager fileManager = new FileManager(this);
        if (fileManager.doesExist("json"))
        {
            FileInputStream fileInputStream = fileManager.openFile("json");
            if (fileInputStream == null)
            {
                startProgressDialog();
                createAsyncTask(DownloadStatus.DOWNLOAD_JSON);
                task.execute(new FileDescription("json", HSEView.JSON_LINK));
            } else
            {
                setJsonField();
                ArrayList<FileDescription> files = new ArrayList<FileDescription>();
                hseView.getDescriptionsOfFiles(files);
                startProgressDialog();
                createAsyncTask(files, DownloadStatus.DOWNLOAD_FILES);
                task.execute();
            }
        } else
        {
            createAsyncTask(DownloadStatus.DOWNLOAD_JSON);
            task.execute(new FileDescription("json", HSEView.JSON_LINK));
        }
    }

    private void startProgressDialog()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void setGeneralScreen()
    {
        String index;
        index = HSEView.INDEX_OF_THE_MAIN_VIEW;
        if (savedInstanceState != null)
        {
            index = savedInstanceState.getString("index", HSEView.INDEX_OF_THE_MAIN_VIEW); //TODO заменитб на просто get()
        }
        screenWidth = getScreenWidth();
        currentView = hseView.getViewByIndex(index);
        View content;
        ScrollView scrollView;
        scrollView = (ScrollView) findViewById(R.id.main_scroll);
        switch (currentView.getHseViewType())  //TODO тут пишу код для переворота экрана
        {
            case HSEViewTypes.VIEW_OF_OTHER_VIEWS:
                elements = currentView.getViewElements();
                indexes = new int[elements.length];
                content = getLinearLayoutWithScreenItems(elements);
                break;
            case HSEViewTypes.HTML_CONTENT:
                FileManager fileManager = new FileManager(this);
                String HTMLContent = fileManager.getFileContent(currentView.getKey());
                String mime = "text/html";
                String encoding = "utf-8";
                WebView webView;
                webView = new WebView(this);
                webView.loadDataWithBaseURL(null, HTMLContent, mime, encoding, null);
                webView.setWebViewClient(new WebViewClient());
                content = webView;
                break;
            case HSEViewTypes.INNER_WEB_PAGE:
                WebView webViewToAppear;
                webViewToAppear = new WebView(this);
                webViewToAppear.loadUrl(currentView.getUrl());
                webViewToAppear.setWebViewClient(new WebViewClient());
                content = webViewToAppear;
                break;
            case HSEViewTypes.RSS:
                LinearLayout item = (LinearLayout) getLayoutInflater().inflate(R.layout.rss_item_full, null);
                ((TextView) item.findViewById(R.id.rss_item_full_title)).setText(((HSEViewRSS) currentView).getTitle());
                ((TextView) item.findViewById(R.id.rss_item_full_text)).setText(((HSEViewRSS) currentView).getOmitted());
                content = item;
                item.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent;
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(currentView.getUrl()));
                        startActivity(intent);
                    }
                });
                break;
            case HSEViewTypes.RSS_WRAPPER:
                HSEViewRSS[] connectedViews = ((HSEViewRSSWrapper) currentView).getConnectedViews();
                int count = 0;
                indexes = new int[connectedViews.length];
                elements = new HSEView[connectedViews.length];
                content = new LinearLayout(this);
                for (HSEViewRSS viewRSS : connectedViews)
                {
                    ((LinearLayout) content).setOrientation(LinearLayout.VERTICAL);
                    LinearLayout itemRSS = (LinearLayout) getLayoutInflater().inflate(R.layout.rss_item, null);
                    ((TextView) itemRSS.findViewById(R.id.rss_item_title)).setText(viewRSS.getTitle());
                    ((TextView) itemRSS.findViewById(R.id.rss_item_text)).setText(viewRSS.getSummary());
                    itemRSS.setOnClickListener(this);
                    int id = getFreeId();
                    indexes[count] = id;
                    elements[count] = viewRSS;
                    count++;
                    itemRSS.setId(id);
                    ((LinearLayout) content).addView(itemRSS);
                }
                break;
            case HSEViewTypes.VK_FORUM:
            case HSEViewTypes.VK_PUBLIC_PAGE_WALL:
                WebView vkView;
                vkView = new WebView(this);
                vkView.loadUrl(VkWebView.OAUTH);
                vkView.setWebViewClient(new VkWebView(new VkWebView.VKCallBack()
                {
                    @Override
                    public void call(final String accessToken)
                    {
                        Requester requester = new Requester(new Requester.RequestResultCallback()
                        {
                            @Override
                            public void pushResult(String result)
                            {
                                String lol = "change me(";//TODO
                            }
                        });
                        VKRequester vkRequester = new VKRequester(accessToken);
                    }
                }));
                vkView.loadUrl(VkWebView.OAUTH);
                content = vkView;
                break;
            default:
                content = new LinearLayout(this);
        }
        this.contentViewId = getFreeId();
        content.setId(this.contentViewId);
        scrollView.removeAllViews();
        scrollView.addView(content);
        setActionBar();
    }

    private void setJsonField()//TODO может разнести проверку json и файлов в разные методы?
    {
        FileManager fileManager = new FileManager(this);
        FileInputStream fileInputStream = fileManager.openFile("json");
        if (fileInputStream == null)
        {
            Toast.makeText(this, "Неизвестная ошибка", Toast.LENGTH_SHORT).show();

        } else
        {
            String json = fileManager.getFileContent("json");
            HSEView newView;
            try
            {
                newView = HSEView.getView(json);
            } catch (JSONException e)
            {
                e.printStackTrace();
                Toast.makeText(this, "Неизвестная ошибка", Toast.LENGTH_SHORT).show();
                return;
            }
            hseView = newView;
            hseView.notifyAboutFiles(this);
        }
    }

    public class MainActivityCallback
    {

        public void changeViews(ViewGroup parentView, View viewToDisappear, View viewToAppear, boolean isButtonBackClicked)
        {
            MainActivity.this.changeViews(parentView, viewToDisappear, viewToAppear, isButtonBackClicked);
        }

        public Context getContext()
        {
            return MainActivity.this;
        }

        public void setActionBarTitle(String title)
        {
            setTitleToActionBar(title);
        }

    }

}
