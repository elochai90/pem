package com.gruppe1.pem.challengeme.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.gruppe1.pem.challengeme.AttributeType;
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Color;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.R;

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
            this.setupCategoriesNew();
            this.setupColorsNew();
            this.setupAttributeTypesNew();
//            this.setup("setup_values.xml");
            this.m_dbHelper.close();
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
//                    setupCategories(m_setupList.get(("categories")));
//
//                    if (m_setupList.containsKey("attribute_types")) {
//                        setupAttributeTypes(m_setupList.get("attribute_types"));
//                    }
//
//                    if (m_setupList.containsKey("items")) {
//                        setupItems(m_setupList.get("items"));
//                    }

//                    if (m_setupList.containsKey("colors")) {
//                        setupColors(m_setupList.get("colors"));
//                    }
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

    private void setupCategoriesNew() {
        Resources res = m_context.getResources();
        TypedArray initial_cats_array = res.obtainTypedArray(R.array.initial_cats);
        for (int i = 0; i < initial_cats_array.length(); ++i) {
            int id = initial_cats_array.getResourceId(i, 0);
            if (id > 0) {
                String[] initial_cat = res.getStringArray(id);
                HashMap<String, String> cat_attributes = new HashMap<>();
                cat_attributes.put("name_en", initial_cat[0]);
                cat_attributes.put("name_de", initial_cat[1]);
                cat_attributes.put("parent_category_id", initial_cat[2]);
                cat_attributes.put("default_attribute_type", initial_cat[3]);
                cat_attributes.put("icon", initial_cat[4]);

                Category defaultCategory = new Category(m_context, 0, this.m_dbHelper);
                defaultCategory.edit(cat_attributes);
                defaultCategory.save();
            } else {
                // something wrong with the XML
            }
        }
        initial_cats_array.recycle(); // Important!
    }


    private void setupColorsNew() {
        Resources res = m_context.getResources();
        TypedArray initial_colors_array = res.obtainTypedArray(R.array.initial_colors);
        for (int i = 0; i < initial_colors_array.length(); ++i) {
            int id = initial_colors_array.getResourceId(i, 0);
            if (id > 0) {
                String[] initial_color = res.getStringArray(id);
                HashMap<String, String> colors_attributes = new HashMap<>();
                colors_attributes.put("name_en", initial_color[0]);
                colors_attributes.put("name_de", initial_color[1]);
                colors_attributes.put("hex", initial_color[2]);

                Color color = new Color(m_context, 0, this.m_dbHelper);
                color.edit(colors_attributes);
                color.save();
            } else {
                // something wrong with the XML
            }
        }
        initial_colors_array.recycle(); // Important!
    }

    private void setupAttributeTypesNew() {
        Resources res = m_context.getResources();
        TypedArray initial_attr_types_array = res.obtainTypedArray(R.array.initial_attr_types);
        for (int i = 0; i < initial_attr_types_array.length(); ++i) {
            int id = initial_attr_types_array.getResourceId(i, 0);
            if (id > 0) {
                String[] initial_attr_type = res.getStringArray(id);
                HashMap<String, String> attr_types_attributes = new HashMap<>();
                attr_types_attributes.put("name", initial_attr_type[0]);
                attr_types_attributes.put("value_type", initial_attr_type[1]);
                attr_types_attributes.put("is_unique", initial_attr_type[2]);

                AttributeType defaultAttributeType = new AttributeType(m_context, 0, this.m_dbHelper);
                defaultAttributeType.edit(attr_types_attributes);
                defaultAttributeType.save();
            } else {
                // something wrong with the XML
            }
        }
        initial_attr_types_array.recycle(); // Important!
    }
}
