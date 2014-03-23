package ru.hse.se.shugurov;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import org.json.JSONException;

import java.io.FileInputStream;
import java.util.ArrayList;

import ru.hse.se.shugurov.ViewsPackage.FileDescription;
import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.gui.ScreenAdapter;
import ru.hse.se.shugurov.gui.ScreenFactory;
import ru.hse.se.shugurov.observer.Observer;
import ru.hse.se.shugurov.utills.DownloadStatus;
import ru.hse.se.shugurov.utills.Downloader;
import ru.hse.se.shugurov.utills.FileManager;

public class MainActivity extends ActionBarActivity implements Observer
{
    private HSEView hseView;
    private Downloader task;
    private ProgressDialog progressDialog;
    private ScreenAdapter screenAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScreenFactory.initFactory(new MainActivityCallback(), (ViewGroup) findViewById(R.id.main), savedInstanceState);
        checkFiles();
    }

    @Override
    public void onBackPressed()
    {
        screenAdapter.showPreviousView();
        setActionBar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        /*MenuInflater inflater = getMenuInflater();
        if (doShowRefreshButton)
        {
            inflater.inflate(R.menu.refresh_menu, menu);
        } else
        {
            if (doShowAddMessageButton)
            {
                inflater.inflate(R.menu.message_adding_menu, menu);
            }
        }*/
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
                hseView.notifyAboutFiles(this);//TODO что и как тут проиходит?(
                ScreenFactory factory = ScreenFactory.instance();
                screenAdapter = factory.createAdapter(hseView, null);
                setActionBar();
                progressDialog.cancel();
                break;
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
    }

    private void setActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(screenAdapter.getActionBarTitle());
        /*setTitleToActionBar(currentView.getName());
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
        supportInvalidateOptionsMenu();*/
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
            setActionBar();
        }

        public Context getContext()
        {
            return MainActivity.this;
        }

        public void refreshActionBar()
        {
            setActionBar();
        }

    }

}
