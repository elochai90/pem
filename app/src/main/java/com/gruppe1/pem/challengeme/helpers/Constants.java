package com.gruppe1.pem.challengeme.helpers;

/**
 * holds multiple used constant values
 */
public class Constants {
    public static final int DEFAULT_CATEGORY_ID = -1;
    public static String DB_TABLE_PREFIX = "orga_nice_";
    public static String XML_ELEMENT_EMPTY = "###empty###";

    // Tags of the TabHost Tabs
    public static final String TAG_TAB_1 = "tag01";
    public static final String TAG_TAB_2 = "tag02";
    public static final String TAG_TAB_3 = "tag03";

    // DB Tables
    public static final String ITEMS_DB_TABLE = DB_TABLE_PREFIX + "items";
    public static final String CATEGORIES_DB_TABLE = DB_TABLE_PREFIX + "categories";
    public static final String COMPARES_DB_TABLE = Constants.DB_TABLE_PREFIX + "compares";
    public static final String ATTRIBUTE_TYPES_DB_TABLE = DB_TABLE_PREFIX + "attribute_types";
    public static final String ITEM_ATTR_DB_TABLE = DB_TABLE_PREFIX + "item_attribute_types";
    public static final String COLORS_DB_TABLE = DB_TABLE_PREFIX + "colors";

    public static String EXTRA_CATEGORY_ID = "extra_category_id";
    public static String EXTRA_ITEM_ID = "extra_item_id";
    public static String EXTRA_ITEM_COLLECTION = "extra_item_collection";
    public static String EXTRA_CLICKED_ITEM_POSITION = "extra_clicked_item_position";
    public static String EXTRA_COMPARE_ID = "extra_compare_id";

    // Shared Preferences File
    public static final String MY_PREFERENCES = "Preferences_File";

    // Shared Preferences Keys
    public static final String KEY_VIEW_CATEGORIES_AS_LIST = "CategoriesViewAsList";
    public static final String KEY_VIEW_ITEMS_AS_LIST = "ItemsViewAsList";
    public static final String KEY_VIEW_COMPARE_AS_LIST = "CompareViewAsList";
    public static final String KEY_FIRST_DB_INIT = "FirstDbInit";
    public static final String KEY_WISHLIST_IN_COMPARE = "ShowWishlistItemsInCompares";
    public static final String KEY_FAVORITE_COLORS = "FavoriteColors";
    public static final String KEY_LANGUAGE = "Language";
    public static final String KEY_LANGUAGE_WEATHER = "LanguageWeather";

    // Shared Preferences Keys for default sizes
    public static final String KEY_DS_1_NAME = "Tops";
    public static final String KEY_DS_2_NAME = "Bottoms";
    public static final String KEY_DS_3_NAME = "Shoes";
    public static final String KEY_DS_NONE = "None";

    // Shared Preferences Keys for the weather
    public static final String KEY_W_TODAY_IMAGE = "WeatherTodayImage";
    public static final String KEY_W_TODAY_DESC = "WeatherTodayDescription";
    public static final String KEY_W_TODAY_TEMP = "WeatherTodayTemperature";
    public static final String KEY_W_1_IMAGE = "Weather1Image";
    public static final String KEY_W_1_DAY = "Weather1Day";
    public static final String KEY_W_1_TEMP = "Weather1Temperature";
    public static final String KEY_W_2_IMAGE = "Weather2Image";
    public static final String KEY_W_2_DAY = "Weather2Day";
    public static final String KEY_W_2_TEMP = "Weather2Temperature";
    public static final String KEY_W_3_IMAGE = "Weather3Image";
    public static final String KEY_W_3_DAY = "Weather3Day";
    public static final String KEY_W_3_TEMP = "Weather3Temperature";
    public static final String KEY_W_DATE = "WeatherDate";

    public static final String OWM_API_KEY = "1a3a39cc17be544f3ea143d6bce1ab65";

    public static final String DEFAULT_CAT_ICON = "kleiderbuegel";

    public static final int LIST_VIEW_IMAGE_WIDTH = 150;
    public static final int LIST_VIEW_IMAGE_HEIGHT = 150;

    public static final int LIST_VIEW_IMAGE_WIDTH_BIG = 450;
    public static final int LIST_VIEW_IMAGE_HEIGHT_BIG = 450;


    public static final int COMPARE_IMAGE_WIDTH = 800;
    public static final int COMPARE_IMAGE_HEIGHT = 800;
}
