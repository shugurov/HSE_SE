package ru.hse.se.shugurov;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ru.hse.se.shugurov.gui.ScreenFactory;
import ru.hse.se.shugurov.observer.Observer;
import ru.hse.se.shugurov.screens.FileDescription;
import ru.hse.se.shugurov.screens.HSEView;
import ru.hse.se.shugurov.utills.DownloadStatus;
import ru.hse.se.shugurov.utills.Downloader;
import ru.hse.se.shugurov.utills.ImageLoader;

public class MainActivity extends ActionBarActivity implements Observer
{
    private static String DOWNLOAD_COMPLETENESS = "download_completeness";
    private static String JSON_FILE_NAME = "json";
    private HSEView hseView;
    private Downloader task;
    private ProgressDialog progressDialog;
    private boolean wasDownloadedCompletely;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ScreenFactory.initFactory(this, savedInstanceState == null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null)
        {
            wasDownloadedCompletely = savedInstanceState.getBoolean(DOWNLOAD_COMPLETENESS);
        }
        if (!wasDownloadedCompletely)
        {
            startProgressDialog();
            checkFiles();
        }
        ImageLoader.initialize(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DOWNLOAD_COMPLETENESS, wasDownloadedCompletely);
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
        menu.clear();
        if (!wasDownloadedCompletely)
        {
            getMenuInflater().inflate(R.menu.refresh_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_refresh:
                refreshContent();
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
                boolean isSuccessful = setJsonField();
                if (isSuccessful)
                {
                    checkFiles();
                } else
                {
                    Toast.makeText(this, "Не удалось загрузить структуру", Toast.LENGTH_SHORT).show();
                }
                break;
            case DOWNLOAD_FILES:
                try
                {
                    hseView.notifyAboutFileDownloading(this);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                ScreenFactory.initFactory(this, true);
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

    private void createAsyncTask(Collection<FileDescription> descriptions, DownloadStatus status)
    {
        task = new Downloader(this, descriptions, status);
        task.addObserver(this);
    }

    private void checkFiles()
    {
        String jsonUrl = getString(R.string.json_url);
        File jsonFile = new File(getFilesDir(), JSON_FILE_NAME);
        if (jsonFile.exists())
        {
            setJsonField();
            Set<FileDescription> files = new HashSet<FileDescription>();
            hseView.getDescriptionsOfFiles(files);
            Set<String> essentialFiles = new HashSet<String>((files.size() * 2 / 3) + 1);
            for (FileDescription description : files)
            {
                essentialFiles.add(description.getName());
            }
            essentialFiles.add(JSON_FILE_NAME);
            Set<String> existingFiles = new HashSet<String>(Arrays.asList(fileList()));
            Iterator<String> existingFilesIterator = existingFiles.iterator();
            while (existingFilesIterator.hasNext())
            {
                String fileName = existingFilesIterator.next();
                if (!essentialFiles.contains(fileName))
                {
                    new File(fileName).delete();
                    existingFilesIterator.remove();
                }
            }
            Iterator<FileDescription> fileDescriptionsIterator = files.iterator();
            while (fileDescriptionsIterator.hasNext())
            {
                FileDescription description = fileDescriptionsIterator.next();
                if (existingFiles.contains(description.getName()))
                {
                    fileDescriptionsIterator.remove();
                }
            }
            if (files.isEmpty())
            {
                try
                {
                    hseView.notifyAboutFileDownloading(this);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                ScreenFactory.initFactory(this, true);
                ScreenFactory.instance().showFragment(hseView);
                progressDialog.cancel();
                wasDownloadedCompletely = true;
            } else
            {
                createAsyncTask(files, DownloadStatus.DOWNLOAD_FILES);
                task.execute();
            }
        } else
        {
            createAsyncTask(DownloadStatus.DOWNLOAD_JSON);
            task.execute(new FileDescription(JSON_FILE_NAME, jsonUrl));
        }
    }

    private void startProgressDialog()
    {
        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private boolean setJsonField()
    {
        File jsonFile = new File(getFilesDir(), JSON_FILE_NAME);
        StringBuilder builder = new StringBuilder();
        InputStreamReader inputStream = null;
        try
        {
            try
            {
                inputStream = new InputStreamReader(new FileInputStream(jsonFile));
                char[] buffer = new char[2048];
                int charsRead;
                while ((charsRead = inputStream.read(buffer)) > -1)
                {
                    builder.append(buffer, 0, charsRead);
                }
            } finally
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
        } catch (IOException e)
        {
            return false;
        }
        String json = builder.toString();
        HSEView newView;
        try
        {
            newView = HSEView.getView(json, getString(R.string.server_url));
        } catch (JSONException e)
        {
            handleJsonException();
            return false;
        }
        hseView = newView;
        try
        {
            hseView.notifyAboutFileDownloading(this);
        } catch (JSONException e)
        {
            return false;
        }
        return true;
    }

    private void handleJsonException()
    {
        Toast.makeText(this, "Не удалось загрузить контент", Toast.LENGTH_SHORT).show();
    }

    private void refreshContent()
    {
        startProgressDialog();
        requestJson();
    }

    private void requestJson()
    {
        createAsyncTask(DownloadStatus.DOWNLOAD_JSON);
        String jsonUrl = getString(R.string.json_url);
        task.execute(new FileDescription(JSON_FILE_NAME, jsonUrl));
    }

}
