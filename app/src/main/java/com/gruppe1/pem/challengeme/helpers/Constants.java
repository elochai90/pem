package com.gruppe1.pem.challengeme.helpers;

import com.gruppe1.pem.challengeme.R;

/**
 * Created by Simon on 13.06.2015.
 */
public class Constants {
    public static final int DEFAULT_CATEGORY_ID = 0;
    public static String DB_TABLE_PREFIX = "orga_nice_";
    public static String XML_ELEMENT_EMPTY = "###empty###";
    public static int DEFAULT_CATEGORY_THUMBNAIL = R.drawable.hose;

    // DB Tables
    public static final String ITEMS_DB_TABLE = DB_TABLE_PREFIX + "items";
    public static final String CATEGORIES_DB_TABLE = DB_TABLE_PREFIX + "categories";
    public static final String COMPARES_DB_TABLE = Constants.DB_TABLE_PREFIX + "compares";
    public static final String ATTRIBUTE_TYPES_DB_TABLE = DB_TABLE_PREFIX + "attribute_types";
    public static final String ITEM_ATTR_DB_TABLE = DB_TABLE_PREFIX + "item_attribute_types";
    public static final String COLORS_DB_TABLE = DB_TABLE_PREFIX + "colors";

    public static String EXTRA_CATEGORY_ID = "extra_category_id";
    public static String EXTRA_ITEM_ID = "extra_item_id";

    // Shared Preferences File
    public static final String MY_PREFERENCES = "Preferences_File";

    // Shared Preferences Keys
    public static final String KEY_VIEW_CATEGORIES_AS_LIST = "CategoriesViewAsList";
    public static final String KEY_VIEW_ITEMS_AS_LIST = "ItemsViewAsList";
    public static final String KEY_VIEW_COMPARE_AS_LIST = "CompareViewAsList";
    public static final String KEY_FIRST_DB_INIT = "FirstDbInit";

    public static final String DEFAULT_CAT_ICON = "kleiderbuegel";

    public static final int LIST_VIEW_IMAGE_WIDTH = 150;
    public static final int LIST_VIEW_IMAGE_HEIGHT = 150;


    public static final int COMPARE_IMAGE_WIDTH = 800;
    public static final int COMPARE_IMAGE_HEIGHT = 800;
}
