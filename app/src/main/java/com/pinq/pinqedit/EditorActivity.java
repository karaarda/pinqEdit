package com.pinq.pinqedit;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.pinq.pinqedit.Highlight.Highlighter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class EditorActivity extends ActionBarActivity implements android.support.v7.app.ActionBar.TabListener {

    TextView totalNumbers, clock;

    Calendar c;

    SimpleDateFormat formatter;

    LinkedList<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        fragments = new LinkedList<Fragment>();

        formatter = (SimpleDateFormat) DateFormat.getTimeFormat(EditorActivity.this);

        totalNumbers    = (TextView) findViewById(R.id.total_numbers);
        clock           = (TextView) findViewById(R.id.clock);

        addNewTab(null);

        Highlighter.load(this);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        c = new GregorianCalendar();

                        clock.setText(formatter.format(c.getTime()));
                    }
                });

            }
        }, 0, 5000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(this, SettingsActivity.class);

                startActivity(intent);

                return true;
            case R.id.action_new:
                addNewTab(null);
                return true;
            case R.id.action_close:
                int i = getSupportActionBar().getSelectedNavigationIndex();
                if(!((EditorFragment)fragments.get(i)).edited) {
                    fragments.remove(i);
                    getSupportActionBar().removeTabAt(i);
                    --EditorFragment.sId;
                    if(fragments.size() == 0){
                        addNewTab(null);
                    }
                }
                return true;
            case R.id.action_save:
                if(((EditorFragment)fragments.get(getSupportActionBar().getSelectedNavigationIndex())).file != null) {
                    saveFile(((EditorFragment)fragments.get(getSupportActionBar().getSelectedNavigationIndex())).file);
                    return true;
                }
            case R.id.action_save_as:
                selectToSaveFile();
                return true;
            case R.id.action_load:
//                selectToLoadFile();
                intent = new Intent(this, SelectFileActivity.class);
                startActivityForResult(intent, 1);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void addNewTab(File file){

        String title;

        if(file != null){
            int nameStart = file.toString().lastIndexOf("/");
            title = file.toString().substring(nameStart+1);
        } else title = getString(R.string.unnamed_file);

        Fragment newFragment = EditorFragment.newInstance(file);
        fragments.add(newFragment);

        getSupportActionBar().addTab(getSupportActionBar().newTab()
                .setText(title)
                .setTabListener(this));

        getSupportActionBar().setSelectedNavigationItem(getSupportActionBar().getTabCount() - 1);
    }

    void changeFragment(int i){
        FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment, fragments.get(i));

        transaction.commit();
    }

    void selectFile(String title, final OnFileSelectedListener listener){
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_file_commander);
        dialog.setCancelable(false);

        ((Toolbar)dialog.findViewById(R.id.dialog_toolbar)).setTitle(title);
        ((Toolbar)dialog.findViewById(R.id.dialog_toolbar)).setTitleTextColor(Color.WHITE);
        ((Toolbar)dialog.findViewById(R.id.dialog_toolbar)).setSubtitleTextColor(Color.WHITE);
        ((Toolbar)dialog.findViewById(R.id.dialog_toolbar)).setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        String fileName = ((EditorFragment)fragments.get(getSupportActionBar().getSelectedNavigationIndex())).fileName;

        if(!fileName.isEmpty())
            ((EditText) dialog.findViewById(R.id.file_name)).setText(fileName);
        else
            ((EditText) dialog.findViewById(R.id.file_name)).setText(getString(R.string.unnamed_file));

        final FileSystemAdapter fsa = new FileSystemAdapter(true, this, new OnFileSelectedListener() {
            @Override
            public boolean onFileSelected(File file) {

                if(file.isDirectory()) {
                    ((Toolbar) dialog.findViewById(R.id.dialog_toolbar)).setSubtitle(file.toString());
                    return true;
                } else if(file.isFile()) {

                    int nameStart = file.toString().lastIndexOf("/");

                    if (nameStart == -1)
                        nameStart = 0;

                    ((EditText) dialog.findViewById(R.id.file_name)).setText(file.toString().substring(nameStart + 1));
                    return true;
                }

                return false;
            }
        });

        ((Toolbar)dialog.findViewById(R.id.dialog_toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fsa.upperDirectory();
            }
        });

        ((ListView)dialog.findViewById(R.id.file_list)).setAdapter(fsa);

        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File currentFile = fsa.getCurrentFile();

                String dir = currentFile.toString() + "/" +
                        ((EditText) dialog.findViewById(R.id.file_name)).getText().toString();

                File file = new File(dir);

                if(listener.onFileSelected(file))
                    dialog.dismiss();
            }
        });

        dialog.show();
    }

    void selectToSaveFile(){
        selectFile(getString(R.string.action_save_as), new OnFileSelectedListener() {
            @Override
            public boolean onFileSelected(File fileToSave) {
                return saveFile(fileToSave);
            }
        });
    }

    void selectToLoadFile(){
        selectFile(getString(R.string.action_load), new OnFileSelectedListener() {
            @Override
            public boolean onFileSelected(File fileToLoad) {
                return loadFile(fileToLoad);
            }
        });
    }

    boolean saveFile(File fileToSave){
        return ((EditorFragment)fragments.get(getSupportActionBar().getSelectedNavigationIndex())).saveFile(fileToSave);
    }

    boolean loadFile(File fileToLoad){
        addNewTab(fileToLoad);
        return true;
    }

    void setTabText(int i, String s){
        getSupportActionBar().getTabAt(i).setText(s);
    }

    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if(fragments.size() == 0)
            return;

        ((EditorFragment) fragments.get(tab.getPosition())).recreate = true;

        changeFragment(tab.getPosition());
    }

    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        ((EditorFragment) fragments.get(tab.getPosition())).recreate = true;
    }

    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
