package com.pinq.pinqedit;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by arda on 25.09.2015.
 */
public class FileSystemAdapter extends BaseAdapter {
    boolean showFiles;
    ArrayList<File> filesInDir;
    ArrayList<File> dirsInDir;

    Activity activity;

    OnFileSelectedListener listener;

    File currentFile;

    public FileSystemAdapter(boolean showFiles, Activity activity, OnFileSelectedListener listener) {
        this.activity = activity;
        this.showFiles = showFiles;
        this.listener = listener;

        File storage = new File("/storage");

        if(storage.exists())
            setFile(storage);
        else
            setFile(Environment.getExternalStorageDirectory());
    }

    @Override
    public int getCount() {
        return dirsInDir.size() + filesInDir.size();
    }

    @Override
    public Object getItem(int position) {
        return dirsInDir.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) activity.getApplication()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        DirectoryViewHolder holder;
        View view;

        if (convertView == null) {
            view = new TextView(activity);

            view = inflater.inflate(R.layout.directory_layout, parent, false);
            holder = new DirectoryViewHolder();
            holder.directoryName = (TextView) view.findViewById(R.id.directory_name);

            view.setTag(holder);
        } else {
            holder = (DirectoryViewHolder) convertView.getTag();

            view = convertView;
        }

        if (position < dirsInDir.size()) {

            int nameStart = dirsInDir.get(position).toString().lastIndexOf("/");

            holder.directoryName.setText(dirsInDir.get(position).toString().substring(nameStart+1));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFile(dirsInDir.get(position));
                }
            });

        } else {
            final int index = position - dirsInDir.size();
            int nameStart = filesInDir.get(index).toString().lastIndexOf("/");

            if(nameStart == -1)
                nameStart = 0;

            holder.directoryName.setText(filesInDir.get(index).toString().substring(nameStart + 1));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFileSelected(filesInDir.get(index));
                }
            });

        }

        return view;
    }

    public void setFile(File f) {
        if(!f.exists() || !f.isDirectory() || !f.canRead()) {
            Toast.makeText(activity.getApplicationContext(), R.string.NotFoundNotAuthorized, Toast.LENGTH_SHORT).show();
            return;
        }

        dirsInDir = new ArrayList<File>();
        filesInDir = new ArrayList<File>();

        listener.onFileSelected(f);

        currentFile = f;

        for( File file : f.listFiles() ){

            if(!file.isAbsolute() || file.isHidden() || !file.canRead())
                continue;
            if(file.isDirectory())
                dirsInDir.add(file);
            else if(file.isFile())
                filesInDir.add(file);
        }

        notifyDataSetChanged();
    }

    File getCurrentFile(){
        return currentFile;
    }

    public void upperDirectory() {
        if(currentFile == Environment.getExternalStorageDirectory() )
            return;

        int lastSlash = currentFile.toString().lastIndexOf("/");

        if(lastSlash == -1)
            return;

        File toSet = new File(currentFile.toString().substring(0,lastSlash));

        setFile(toSet);
    }

    public static class DirectoryViewHolder {
        public TextView directoryName;
    }
}
