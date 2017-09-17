package com.pinq.pinqedit;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.widget.EditText;

/**
 * Created by arda on 25.09.2015.
 */
public class HelperFunctions {

    public static void codeComplete(EditText editText, int pos){
        Editable s = editText.getText();

        switch(s.charAt(pos-1)){
//TODO            case '\n':
//                s.replace(pos,pos,autoIntent(s-1, pos, "\n"));
//                break;
            case '{':
                s.insert(pos, autoIntent(s, pos, "\n\n}"));
                break;
            case '[':
                s.insert(pos,"]");
                editText.setSelection(pos);
                break;
            case '(':
                s.insert(pos,")");
                editText.setSelection(pos);
                break;
            case '\'':
                if(pos >= 2 && s.charAt(pos-2) != '\'') {
                    s.insert(pos, "\'");
                    editText.setSelection(pos);
                }
                break;
            case '<':
                s.insert(pos,">");
                editText.setSelection(pos);
                break;
            case '"':
                if(pos >= 2 && s.charAt(pos-2) != '"') {
                    s.insert(pos, "\"");
                    editText.setSelection(pos);
                }
                break;
        }
    }

    public static String autoIntent(Editable s, int pos, String toIntent){
        String textBefore = s.toString().substring(0,pos);

        int i = textBefore.lastIndexOf("\n");

        if(i == -1 || i+1 >= textBefore.length())
            i = 0;

        textBefore = textBefore.substring(i+1);

        int spaceCount = 0;

        if(textBefore.length() != 0)
        for( spaceCount = 0; spaceCount < textBefore.length() && textBefore.charAt(spaceCount) == ' '; ++spaceCount );

        String spacing = "";

        for(i = 0; i < spaceCount; ++i)
            spacing += " ";

        spacing += "    ";

        toIntent = toIntent.replaceAll("\n", "\n" + spacing);
        toIntent = toIntent.replace("    }", "}");

        return toIntent;
    }

    public static String insertIntoString(String string, int start,@NonNull String insert) {
        if( start < 0 ||start  > string.length() )
            throw new IndexOutOfBoundsException("start < 0 || start >= " + string.length() );

        String end = "";

        if(start < string.length() )
            end = string.substring( start );

        String result = string.substring(0, start) + insert + end;

        return result;
    }
}
