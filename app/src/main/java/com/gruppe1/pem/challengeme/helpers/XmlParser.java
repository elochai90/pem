package com.gruppe1.pem.challengeme.helpers;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by simon on 19.06.2015.
 * XML Parser class
 */
public class XmlParser {

    /**
     * initiates the parse
     * @param in InputStream
     * @return HashMap of the dataset
     * @throws XmlPullParserException
     * @throws IOException
     */
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
        int eventType = parser.getEventType();
        int parserDepth;
        ArrayList<HashMap<String,String>> elementValueList = null;
        HashMap<String, ArrayList<HashMap<String, String>>> elementWrapperList = new HashMap<>();
        HashMap<String, String> elementValues = new HashMap<>();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            parserDepth = parser.getDepth();

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;

                case XmlPullParser.START_TAG:
                    name = parser.getName();

                    switch(parserDepth) {
                        case 2:
                            //i.e categories
                            elementValueList = new ArrayList<>();
                            break;

                        case 3:
                            //i.e category
                            elementValues = new HashMap<>();
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
                            if(elementValueList != null) {
                                elementValueList.add(elementValues);
                            }
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

        //just for debugging START -  leave in code for further debugging
//        Set<String> elementTypes = elementWrapperList.keySet();
//
//        for (String elementType : elementTypes) {
//            ArrayList<HashMap<String, String>> elementsList = elementWrapperList.get(elementType);
//            Log.e("###XML wrapper " + iteratorName, elementsList.size() + " element(s)");
//
//            int counter = 0;
//
//            while (counter < elementsList.size()) {
//                HashMap<String, String> elementValuesMap = elementsList.get(counter++);
//
//                Set<String> elementKeys = elementValuesMap.keySet();
//
//                for (String elementKey : elementKeys) {
//                    String elementValueKey = elementKey;
//                    Log.e("###XML values " + elementValueKey, elementValuesMap.get(elementValueKey));
//                }
//            }
//        }
        //just for debugging END

        return elementWrapperList;
    }
}
