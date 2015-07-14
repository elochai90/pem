package com.gruppe1.pem.challengeme.helpers;

import android.content.Context;

import com.gruppe1.pem.challengeme.AttributeType;
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Color;
import com.gruppe1.pem.challengeme.Item;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stores initial data in database
 */
public class DefaultSetup {
    private static boolean initated = false;
    private Context m_context;
    private DataBaseHelper m_dbHelper;

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

    /**
     * receives xml file and parses it to database valid values
     * @param p_xml_file XML-File to be parsed
     */
    private void setup(String p_xml_file) {
        XmlParser parser = new XmlParser();

        try {
            InputStream inStream = this.m_context.getAssets().open(p_xml_file);

            try {
                HashMap<String, ArrayList<HashMap<String, String>>> m_setupList = parser.parse(inStream);

                //no category -> no items!
                if (m_setupList.containsKey("categories")) {
                    setupCategories(m_setupList.get(("categories")));

                    if (m_setupList.containsKey("attribute_types")) {
                        setupAttributeTypes(m_setupList.get("attribute_types"));
                    }

                    if (m_setupList.containsKey("items")) {
                        setupItems(m_setupList.get("items"));
                    }

                    if (m_setupList.containsKey("colors")) {
                        setupColors(m_setupList.get("colors"));
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

    /**
     * Sets up the default categories
     * @param p_elements Category list
     */
    private void setupCategories(ArrayList<HashMap<String, String>> p_elements) {

        for (HashMap<String, String> categoryAttributes : p_elements) {
            Category defaultCategory = new Category(m_context, 0, this.m_dbHelper);
            defaultCategory.edit(categoryAttributes);
            defaultCategory.save();
        }

    }

    /**
     * Sets up the default attributetypes
     * @param p_elements Attribute types list
     */
    private void setupAttributeTypes(ArrayList<HashMap<String, String>> p_elements) {

        for (HashMap<String, String> attributeTypeAttributes : p_elements) {
            AttributeType defaultAttributeType = new AttributeType(m_context, 0, this.m_dbHelper);
            defaultAttributeType.edit(attributeTypeAttributes);
            defaultAttributeType.save();
        }

    }

    /**
     * Sets up the default items(if recommended)
     * @param p_elements List of Items
     */
    private void setupItems(ArrayList<HashMap<String, String>> p_elements) {

        for (HashMap<String, String> itemAttributes : p_elements) {
            Item defaultItem = new Item(m_context, 0, this.m_dbHelper);
            defaultItem.edit(itemAttributes);
            defaultItem.save();
        }
    }

    /**
     * Sets up the default colors
     * @param p_elements List of Colors
     */
    private void setupColors(ArrayList<HashMap<String, String>> p_elements) {

        for (HashMap<String, String> colorAttributes : p_elements) {
            Color color = new Color(m_context, 0, this.m_dbHelper);
            color.edit(colorAttributes);
            color.save();
        }
    }
}
