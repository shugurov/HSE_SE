package ru.hse.shugurov;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import ru.hse.shugurov.gui.ScreenFactory;
import ru.hse.shugurov.screens.BaseScreen;
import ru.hse.shugurov.screens.FileDescription;
import ru.hse.shugurov.utils.Downloader;
import ru.hse.shugurov.utils.FileManager;
import ru.hse.shugurov.utils.ImageLoader;

/**
 * @author Ivan Shugurov
 */
public class MainActivity extends ActionBarActivity {
    private static String DOWNLOAD_COMPLETENESS = "download_completeness";
    private static String JSON_FILE_NAME = "json";
    private BaseScreen baseScreen;
    private Downloader task;
    private ProgressDialog progressDialog;
    private boolean wasDownloadedCompletely;

    @Override
    protected void onResume() {
        super.onResume();
        if (!wasDownloadedCompletely) {
            startProgressDialog();
            checkFiles();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG)
            Crashlytics.start(this);
        SharedPreferences prefs = this.getSharedPreferences("ru.hse.se", Context.MODE_PRIVATE);
        if (this.getSharedPreferences("ru.hse.se",
                Context.MODE_PRIVATE).getBoolean(
                "first_launch", true)) {
            HSETracker.sendEvent(this,
                    "first_launch");
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putBoolean("first_launch", false);
            prefsEditor.putString("firstLaunchDate",
                    new SimpleDateFormat("yyyyMMdd").format(System
                            .currentTimeMillis())
            );
            prefsEditor.commit();

        }
        FileManager.initialize(this);
        ImageLoader.initialize(this);
        ScreenFactory.initFactory(this, savedInstanceState == null);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            wasDownloadedCompletely = savedInstanceState.getBoolean(DOWNLOAD_COMPLETENESS);
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2c5491")));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DOWNLOAD_COMPLETENESS, wasDownloadedCompletely);
        if (task != null) {
            task.cancel(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        if (!wasDownloadedCompletely) {
            getMenuInflater().inflate(R.menu.refresh_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshContent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*checks if all files in JSON are present. If necessary requests downloading missing files.*/
    private void checkFiles() {
        String jsonUrl = getString(R.string.json_url);
        File jsonFile = new File(getFilesDir(), JSON_FILE_NAME);
        if (jsonFile.exists()) {
            setJsonField();
            Set<FileDescription> files = new HashSet<FileDescription>();
            baseScreen.getDescriptionsOfFiles(files);
            Set<String> essentialFiles = new HashSet<String>((files.size() * 2 / 3) + 1);
            Set<String> changeableFileNames = new HashSet<String>();
            for (FileDescription description : files) {
                essentialFiles.add(description.getName());
                if (description.isProneToChanges()) {
                    changeableFileNames.add(description.getName());
                }
            }
            essentialFiles.add(JSON_FILE_NAME);
            Set<String> existingFiles = new HashSet<String>(Arrays.asList(fileList()));
            existingFiles.removeAll(changeableFileNames);
            Iterator<String> existingFilesIterator = existingFiles.iterator();
            while (existingFilesIterator.hasNext()) {
                String fileName = existingFilesIterator.next();
                if (!essentialFiles.contains(fileName)) {
                    new File(fileName).delete();
                    existingFilesIterator.remove();
                }
            }
            Iterator<FileDescription> fileDescriptionsIterator = files.iterator();
            while (fileDescriptionsIterator.hasNext()) {
                FileDescription description = fileDescriptionsIterator.next();
                if (existingFiles.contains(description.getName())) {
                    fileDescriptionsIterator.remove();
                }
            }
            if (files.isEmpty()) {
                ScreenFactory.initFactory(this, true);
                ScreenFactory.instance().showFragment(baseScreen);
                progressDialog.cancel();
                wasDownloadedCompletely = true;
            } else {
                Downloader.DownloadCallback downloadCallback = new Downloader.DownloadCallback() {
                    @Override
                    public void downloadFinished() {
                        ScreenFactory.initFactory(MainActivity.this, true);
                        ScreenFactory.instance().showFragment(baseScreen);
                        progressDialog.cancel();
                        wasDownloadedCompletely = true;

                    }
                };
                task = new Downloader(this, files, downloadCallback);
                task.execute();
            }
        } else {
            Downloader.DownloadCallback downloadCallback = getCallbackForJsonDownloading();
            task = new Downloader(this, downloadCallback);
            task.execute(new FileDescription(JSON_FILE_NAME, jsonUrl));
        }
    }

    /*creates progress dialog and shows it*/
    private void startProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Загрузка данных");
        }
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    /*requests parsing of server responses, initializes UI*/
    private boolean setJsonField() {
        FileManager fileManager = FileManager.instance();
        if (fileManager == null) {
            FileManager.initialize(this);
            fileManager = FileManager.instance();
        }
        String json;
        try {
            json = fileManager.getFileContent(JSON_FILE_NAME);
        } catch (IOException e) {
            return false;
        }
        BaseScreen newView;
        try {
            newView = BaseScreen.getScreen(json, getString(R.string.server_url));
        } catch (Exception e) {
            handleJsonException();
            return false;
        }
        baseScreen = newView;
        return true;
    }

    /*tells a user about exception*/
    private void handleJsonException() {
        Toast.makeText(this, "Не удалось загрузить контент", Toast.LENGTH_SHORT).show();
    }

    /*requests a new JSON, starts progress dialog*/
    private void refreshContent() {
        startProgressDialog();
        requestJson();
    }

    /*requests a new JSON*/
    private void requestJson() {
        Downloader.DownloadCallback downloadCallback = getCallbackForJsonDownloading();
        task = new Downloader(this, downloadCallback);
        String jsonUrl = getString(R.string.json_url);
        task.execute(new FileDescription(JSON_FILE_NAME, jsonUrl));
    }

    /*creates callback for jJSON downloading*/
    private Downloader.DownloadCallback getCallbackForJsonDownloading() {
        return new Downloader.DownloadCallback() {
            @Override
            public void downloadFinished() {
                boolean isSuccessful = setJsonField();
                if (isSuccessful) {
                    checkFiles();
                } else {
                    Toast.makeText(MainActivity.this, "Не удалось загрузить структуру", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
}