package com.pinq.pinqedit.Highlight;

import android.graphics.Color;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by arda on 26.09.2015.
 */
public class HighlightReader extends DefaultHandler {

    static int primitives = 0;
    static int specials = 1;

    Highlighter[] array;

    int last = -1;

    public Highlighter[] parse( File file, Highlighter[] array ){
        this.array = array;

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse( file, this );
        } catch( Exception e ){
            e.printStackTrace();
        }

        return array;
    }

    public void parse( InputStream is, Highlighter[] array ){
        this.array = array;

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse( is, this );
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if(qName.equals("Word")){
            if(last != -1)
                array[last].addWord(attributes.getValue("value"));
        } else if(qName.equals("Primitives")){
            last = primitives;

            array[last].setColor(Color.parseColor(attributes.getValue("color")));
        } else if(qName.equals("Specials")){
            last = specials;

            array[last].setColor(Color.parseColor(attributes.getValue("color")));
        }
    }
}
