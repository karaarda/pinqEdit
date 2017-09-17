package com.pinq.pinqedit;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.pinq.pinqedit.ScrollView.ObservableHorizontalScrollView;
import com.pinq.pinqedit.ScrollView.ObservableScrollView;
import com.pinq.pinqedit.Highlight.Highlighter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class EditorFragment extends Fragment {
    static final String FILE = "file";
    static final String ID = "id";

    static int sId = 0;

    int id;

    File file;

    View rootView;

    EditText lineNumbers, editor;

    String lineNumbersString, fileName;

    boolean edited = false;
    boolean recreate = true;

    public static EditorFragment newInstance(File file) {
        EditorFragment fragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putSerializable(FILE, file);
        args.putInt(ID, sId++);
        fragment.setArguments(args);
        return fragment;
    }

    public EditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            file = (File) getArguments().getSerializable(FILE);
            id = getArguments().getInt(ID);

            Log.d("ID", id + "");

            if(file != null){
                int nameStart = file.toString().lastIndexOf("/");
                fileName = file.toString().substring(nameStart+1);
            } else {
                fileName = getActivity().getString(R.string.unnamed_file);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView        = inflater.inflate(R.layout.fragment_editor, container, false);

        editor          = (EditText) rootView.findViewById(R.id.editor);
        lineNumbers     = (EditText) rootView.findViewById(R.id.line_number);

        lineNumbersString = lineNumbers.getText().toString();

        editor.addTextChangedListener(new TextWatcher() {
            int cursorPos;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cursorPos = editor.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(recreate) {
                    recreate = false;
                } else {
                    ((EditorActivity) getActivity()).setTabText(id, fileName + "*");
                    edited = true;
                }

                if(editor.getSelectionStart() > cursorPos){
                    cursorPos = editor.getSelectionStart();
                    HelperFunctions.codeComplete(editor,cursorPos);
                }

                updateEditor();
                Highlighter.highlight(editor);
            }
        });

        editor.setHorizontallyScrolling(true);

        ((ObservableScrollView) rootView.findViewById(R.id.vertical_scroll))
                .setOnScrollListener(new ObservableScrollView.OnScrollListener() {
                    @Override
                    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldX, int oldY) {

                    }

                    @Override
                    public void onEndScroll(ObservableScrollView scrollView) {
                        Highlighter.highlight(editor);
                    }
                });

        ((ObservableHorizontalScrollView) rootView.findViewById(R.id.horizontal_scroll))
                .setOnScrollListener(new ObservableHorizontalScrollView.OnScrollListener() {
                    @Override
                    public void onScrollChanged(ObservableHorizontalScrollView scrollView, int x, int y, int oldX, int oldY) {

                    }

                    @Override
                    public void onEndScroll(ObservableHorizontalScrollView scrollView) {
                        Highlighter.highlight(editor);
                    }
                });

        if(file == null) {
            fileName = getActivity().getString(R.string.unnamed_file);
            editor.setText(R.string.welcome);
            edited = false;
        }
        else
            loadFile();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    void updateEditor(){
        int newLineCount = editor.getLineCount();
        int currentLineCount = lineNumbers.getLineCount();

        if(newLineCount > currentLineCount){
            for(int i = currentLineCount + 1; i <= newLineCount; ++i){
                lineNumbersString += "\n" + Integer.toString(i);
            }
        } else if(newLineCount < currentLineCount){
            int end = lineNumbersString.indexOf("\n" + Integer.toString(newLineCount+1));

            if(end > 0)
                lineNumbersString = lineNumbersString.substring(0,end);
            else
                lineNumbersString = "1";
        }

        lineNumbers.setText(lineNumbersString);

        ((EditorActivity) getActivity()).totalNumbers.setText(newLineCount + ":" + editor.getText().toString().length());
    }

    boolean loadFile(){
        try {
            FileReader fr;
            BufferedReader br;

            if(!file.exists())
                file.createNewFile();

            fr = new FileReader(file);
            br = new BufferedReader(fr);

            Vector<Byte> buf = new Vector<Byte>();

            byte b;

            while(true){
                b = ((byte) br.read());

                if(b != -1)
                    buf.add(b);
                else break;
            };

            byte[] arrayBuf = new byte[buf.size()];

            for(int i = 0; i < buf.size(); ++i)
                arrayBuf[i] = buf.get(i);

            editor.setText(new String(arrayBuf));

            fr.close();

            if(!file.canWrite()) {
                editor.setEnabled(false);
                Toast.makeText(getActivity().getApplicationContext(), R.string.readOnly, Toast.LENGTH_SHORT).show();
                file = null;
            }
            else
                editor.setEnabled(true);

            edited = false;

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity().getApplicationContext(), R.string.NotFoundNotAuthorized, Toast.LENGTH_SHORT).show();

            file = null;
            editor.setText(R.string.welcome);

            ((EditorActivity) getActivity()).setTabText(id, getActivity().getString(R.string.unnamed_file));

            return false;
        }
    }

    public boolean saveFile(File fileToSave){
        try {
            FileWriter fw;

            if(!fileToSave.exists())
                fileToSave.createNewFile();

            fw = new FileWriter(fileToSave);

            fw.write(editor.getText().toString());

            fw.close();

            file = fileToSave;

            int nameStart = file.toString().lastIndexOf("/");

            fileName = file.toString().substring(nameStart+1);
            ((EditorActivity) getActivity()).setTabText(id, fileName);

            edited = false;
            return true;
        } catch (IOException e) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.error_write, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }
}
