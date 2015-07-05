package com.gruppe1.pem.challengeme.helpers;

import android.content.Context;
import android.util.Log;

import com.gruppe1.pem.challengeme.AttributeType;
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Color;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.XmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by simon on 19.06.2015.
 */
public class DefaultSetup {
    private static boolean initated = false;
    private Context m_context;
    private DataBaseHelper m_dbHelper;
    HashMap<String, ArrayList<HashMap<String, String>>> m_setupList;

    public DefaultSetup(Context p_context) {
        this.m_context = p_context;
        this.m_dbHelper = new DataBaseHelper(this.m_context);
        this.m_dbHelper.init();

        if(!initated) {
            this.setup("setup_values.xml");
            initated = true;
        }
        this.m_dbHelper.close();
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

                    if (this.m_setupList.containsKey("colors")) {
                        setupColors(this.m_setupList.get("colors"));
                    }
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
        this.m_dbHelper.close();
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

        Iterator iterator = p_elements.iterator();

        while (iterator.hasNext()) {
            HashMap<String, String> attributeTypeAttributes = (HashMap<String, String>)iterator.next();
            AttributeType defaultAttributeType = new AttributeType(m_context, 0, this.m_dbHelper);
            defaultAttributeType.edit(attributeTypeAttributes);
            defaultAttributeType.save();
        }

    }

    private void setupItems(ArrayList<HashMap<String, String>> p_elements) {
        Log.e("###SETUP##", "items called");

        Iterator iterator = p_elements.iterator();

        while (iterator.hasNext()) {
            HashMap<String, String> itemAttributes = (HashMap<String, String>)iterator.next();
            Item defaultItem= new Item(m_context, 0, this.m_dbHelper);
            defaultItem.edit(itemAttributes);
            defaultItem.save();
        }
    }

    private void setupColors(ArrayList<HashMap<String, String>> p_elements) {
        Log.e("###SETUP##", "colors called");

        Iterator iterator = p_elements.iterator();

        while (iterator.hasNext()) {
            HashMap<String, String> colorAttributes = (HashMap<String, String>)iterator.next();
            Color color = new Color(m_context, 0, this.m_dbHelper);
            color.edit(colorAttributes);
            color.save();
        }
    }
}
