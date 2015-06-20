package com.gruppe1.pem.challengeme;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Created by simon on 19.06.2015.
 */
public class XmlParser {
    // We don't use namespaces
    private static final String ns = null;

    public HashMap<String, ArrayList<HashMap<String, String>>> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readXml(parser);
        } finally {
            in.close();
        }
    }

    /**
     *
     * @param parser XmlPullParser
     * @return Hashmap with element types, each containing ArrayList with elements of that type,
     *         each containing HashMap with key value pair of element attributes
     * @throws XmlPullParserException
     * @throws IOException
     */
    private HashMap<String, ArrayList<HashMap<String, String>>> readXml(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList elements = null;
        int eventType = parser.getEventType();
        int parserDepth = 0;
        ArrayList<HashMap<String,String>> elementValueList = null;
        HashMap<String, ArrayList<HashMap<String, String>>> elementWrapperList = new HashMap<String, ArrayList<HashMap<String, String>>>();
        HashMap<String, String> elementValues = new HashMap<String, String>();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = null;
            parserDepth = parser.getDepth();
            String elementTypeWrapper = "";
            String elementType = "";

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    elements = new ArrayList();
                    break;

                case XmlPullParser.START_TAG:
                    name = parser.getName();

                    switch(parserDepth) {
                        case 2:
                            //i.e categories
                            elementTypeWrapper = name;
                            elementValueList = new ArrayList<HashMap<String, String>>();
                            break;

                        case 3:
                            //i.e category
                            elementType = name;
                            elementValues = new HashMap<String, String>();
                            break;

                        case 4:
                            //i.e name
                            String text = parser.nextText();
                            String storeValue = (text.isEmpty()) ? Constants.XML_ELEMENT_EMPTY : text;
                            elementValues.put(name, storeValue);
                            break;
                    }

                    break;

                case XmlPullParser.END_TAG:
                    name = parser.getName();

                    switch (parserDepth) {
                        case 3: {
                            // i.e category element is closed -> add element values to ArrayList
                            elementValueList.add(elementValues);
                            break;
                        }

                        case 2: {
                            /* i.e categories element is closed -> add list of all child elements (categories) to hashmap with
                             'categories' as key */
                            elementWrapperList.put(name, elementValueList);
                        }
                    }
            }

            eventType = parser.next();
        }

        //just for debugging START
        Set<String> elementTypes = elementWrapperList.keySet();
        Iterator iterator = elementTypes.iterator();

        while (iterator.hasNext()) {
            String iteratorName = iterator.next().toString();
            ArrayList<HashMap<String, String>> elementsList = elementWrapperList.get(iteratorName);
            //Log.e("###XML wrapper " + iteratorName, elementsList.size() + " element(s)");

            int counter = 0;

            while (counter < elementsList.size()) {
                HashMap<String, String> elementValuesMap = elementsList.get(counter++);

                Set<String> elementKeys = elementValuesMap.keySet();
                Iterator elementValueIterator = elementKeys.iterator();

                while (elementValueIterator.hasNext()) {
                    String elementValueKey = elementValueIterator.next().toString();
                    //Log.e("###XML values " + elementValueKey, elementValuesMap.get(elementValueKey));
                }
            }
        }
        //just for debugging END

        return elementWrapperList;
    }
}
