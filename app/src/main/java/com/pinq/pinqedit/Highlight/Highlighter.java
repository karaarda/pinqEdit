package com.pinq.pinqedit.Highlight;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Editable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by arda on 26.09.2015.
 */
public class Highlighter {
    static Highlighter[] lighters;

    static String Tag = "HIGHLIGHTER";

    LinkedList<String> words;
    int color;

    static LinkedList<ForegroundColorSpan> spans;

    public static void load(Activity activity){
        //TODO load preference

        try {
            lighters = new Highlighter[2];
            lighters[0] = new Highlighter();
            lighters[1] = new Highlighter();
            new HighlightReader().parse(activity.getResources().getAssets().open("pinqHighlight.xml"), lighters);
        } catch (IOException e) {
            e.printStackTrace();
        }

        spans = new LinkedList<ForegroundColorSpan>();
    }

    public static void highlight2(EditText view){
        Editable spannable = view.getText();

        Rect frame = new Rect();

        view.getLocalVisibleRect(frame);

        float startX    = frame.left;
        float startY    = frame.top;

        float width     = frame.width();
        float height    = frame.height();

        float endX      = startX + width;
        float endY      = startY + height;

        int start       = view.getOffsetForPosition(startX, startY);
        int end         = view.getOffsetForPosition(endX, endY);

        if(start > end || start < 0)
            return;

        String toHighlight = spannable.toString().substring(start, end);

        for (ForegroundColorSpan span : spans) {
            spannable.removeSpan(span);
        }

        for (Map.Entry<String, Highlight> entry : Patterns.patterns.entrySet()) {
            Matcher matcher = entry.getValue().pattern.matcher(toHighlight);


            while(matcher.find()){
                spans.add(new ForegroundColorSpan(Color.parseColor(entry.getValue().color)));

                spannable.setSpan(spans.getLast(), start + matcher.start(), start + matcher.end(), 0);
            }
        }
    }

    public static void highlight(EditText view){

        highlight2(view);

        if(true)
         return;

        Editable spannable = view.getText();

        if(spannable.length() == 0)
            return;

        Rect frame = new Rect();

        view.getLocalVisibleRect(frame);

        float startX    = frame.left;
        float startY    = frame.top;

        float width     = frame.width();
        float height    = frame.height();

        float endX      = startX + width;
        float endY      = startY + height;

        int start       = view.getOffsetForPosition(startX, startY);
        int end         = view.getOffsetForPosition(endX, endY);

        if(start > end || start < 0)
            return;

        String toHighlight = " " + spannable.toString().substring(start, end) + " ";

        for (ForegroundColorSpan span : spans) {
            spannable.removeSpan(span);
        }

        if(lighters == null) {
            Log.d(Tag, "Lighters are null");
            return;
        }

        for (Highlighter lighter : lighters) {
            if(lighter == null || lighter.words.isEmpty()) {
                Log.d(Tag, "Lighter is null or empty");
                continue;
            }

            String regex= "[^a-z_A-Z_0-9][\\s]*(";

            for (int i = 0; i < lighter.words.size(); i++) {
                if(i != 0)
                    regex += "|";
                regex += lighter.words.get(i);
            }

            regex += ")[\\s]*[^a-z_A-Z_0-9]";

            Matcher matcher = Pattern.compile(regex).matcher(toHighlight);

            int index = 0;

            while(matcher.find(index)){
                spans.add(new ForegroundColorSpan(lighter.color));

                Log.d(Tag, matcher.group());

                spannable.setSpan(spans.getLast(), start + matcher.start(1) - 1, start + matcher.end(1) - 1, 0);

                index = matcher.end(1);
            }
        }
    }

    public Highlighter(){
        words = new LinkedList<String>();
        color = Color.parseColor("#663333");
    }

    public void addWord(String word){
        if(!words.contains(word))
            words.add(word);
    }

    public void setColor(int c){
        if(color != c)
            color = c;
    }
}