package ru.hse.se.shugurov;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;

import java.io.FileInputStream;
import java.util.ArrayList;

import ru.hse.se.shugurov.gui.ScreenFactory;
import ru.hse.se.shugurov.observer.Observer;
import ru.hse.se.shugurov.screens.FileDescription;
import ru.hse.se.shugurov.screens.HSEView;
import ru.hse.se.shugurov.utills.DownloadStatus;
import ru.hse.se.shugurov.utills.Downloader;
import ru.hse.se.shugurov.utills.FileManager;

public class MainActivity extends ActionBarActivity implements Observer//TODO в "студентам  бакалавриата" ад(
{//TODO а не слишком ли много оперативки жрёт?
    private static String JSON_FILE_NAME = "json";
    private HSEView hseView;
    private Downloader task;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ScreenFactory.initFactory(this, savedInstanceState == null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
        {
            checkFiles();
        }
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        /*if (screenAdapter != null)TODO
        {
            int menuId = screenAdapter.getMenuId();
            if (menuId > 0)
            {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(menuId, menu);
            }
        }*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /*switch (item.getItemId())
        {TODO
            case R.id.action_refresh:
                startProgressDialog();
                createAsyncTask(DownloadStatus.DOWNLOAD_JSON);
                task.execute(new FileDescription("json", HSEView.JSON_LINK));
                return true;
            case R.id.action_add_message:
                Toast.makeText(this, "I'm working!", Toast.LENGTH_SHORT).show();
                break;
        }*/
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
                try
                {
                    hseView.notifyAboutFileDownloading(this);//TODO что и как тут проиходит?(
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                ScreenFactory.instance().showFragment(hseView);
                progressDialog.cancel();
                break;
        }
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
        if (fileManager.doesExist(JSON_FILE_NAME))
        {
            FileInputStream fileInputStream = fileManager.openFile(JSON_FILE_NAME);
            if (fileInputStream == null)//TODO к чему бы это?
            {
                startProgressDialog();
                createAsyncTask(DownloadStatus.DOWNLOAD_JSON);
                task.execute(new FileDescription(JSON_FILE_NAME, HSEView.JSON_LINK));
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
            task.execute(new FileDescription(JSON_FILE_NAME, HSEView.JSON_LINK));
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
        FileInputStream fileInputStream = fileManager.openFile(JSON_FILE_NAME);
        if (fileInputStream == null)
        {
            Toast.makeText(this, "Неизвестная ошибка", Toast.LENGTH_SHORT).show();

        } else
        {
            String json = fileManager.getFileContent(JSON_FILE_NAME);
            HSEView newView;
            try
            {
                newView = HSEView.getView(json);
            } catch (JSONException e)
            {
                handleJsonException();
                return;
            }
            hseView = newView;
            try
            {
                hseView.notifyAboutFileDownloading(this);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void handleJsonException()
    {
        Toast.makeText(this, "Ощибка загрузки контента", Toast.LENGTH_SHORT).show();
    }

}
