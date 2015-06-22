package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by simon on 19.06.2015.
 */
public class DefaultSetup {
    private Context m_context;
    private DataBaseHelper m_dbHelper;
    HashMap<String, ArrayList<HashMap<String, String>>> m_setupList;

    public DefaultSetup(Context p_context) {
        this.m_context = p_context;
        this.m_dbHelper = new DataBaseHelper(this.m_context);
        this.m_dbHelper.init();
    }

    public void setup(String p_xml_file) {
        XmlParser parser = new XmlParser();

        try {
            InputStream inStream = this.m_context.getAssets().open(p_xml_file);

            try {
                this.m_setupList = parser.parse(inStream);

                //no category -> no items!
                if (this.m_setupList.containsKey("categories")) {
                    setupCategories(this.m_setupList.get(("categories")));

                    if (this.m_setupList.containsKey("attribute_types")) {
                        setupAttributeTypes(this.m_setupList.get("attribute_types"));
                    }

                    if (this.m_setupList.containsKey("items")) {
                        setupItems(this.m_setupList.get("items"));
                    }
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void setupCategories(ArrayList<HashMap<String, String>> p_elements) {
        Log.e("###SETUP##", "categories called");

        Iterator iterator = p_elements.iterator();

        while (iterator.hasNext()) {
            HashMap<String, String> categoryAttributes = (HashMap<String, String>)iterator.next();
            Category defaultCategory = new Category(m_context, 0, this.m_dbHelper);
            defaultCategory.edit(categoryAttributes);
            defaultCategory.save();
        }

    }

    private void setupAttributeTypes(ArrayList<HashMap<String, String>> p_elements) {
        Log.e("###SETUP##", "attributes called");

    }

    private void setupItems(ArrayList<HashMap<String, String>> p_elements) {
        Log.e("###SETUP##", "items called");

    }

}
