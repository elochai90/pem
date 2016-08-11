package com.gruppe1.pem.challengeme.helpers;

/**
 * holds multiple used constant values
 */
public class Constants {
   public static final int DEFAULT_CATEGORY_ID = -1;
   public static String DB_TABLE_PREFIX = "orga_nice_";
   public static String XML_ELEMENT_EMPTY = "###empty###";

   // DB Tables
   public static final String ITEMS_DB_TABLE = DB_TABLE_PREFIX + "items";
   public static final String CATEGORIES_DB_TABLE = DB_TABLE_PREFIX + "categories";
   public static final String COMPARES_DB_TABLE = Constants.DB_TABLE_PREFIX + "compares";
   public static final String ATTRIBUTE_TYPES_DB_TABLE = DB_TABLE_PREFIX + "attribute_types";
   public static final String ITEM_ATTR_DB_TABLE = DB_TABLE_PREFIX + "item_attribute_types";
   public static final String COLORS_DB_TABLE = DB_TABLE_PREFIX + "colors";

   // DB Items Columns
   public static final String DB_COLUMN_ITEM_ID = "_id";
   public static final String DB_COLUMN_ITEM_NAME = "name";
   public static final String DB_COLUMN_ITEM_IMAGE_FILE = "image_file";
   public static final String DB_COLUMN_ITEM_CATEGORY_ID = "category_id";
   public static final String DB_COLUMN_ITEM_IS_WISH = "is_wish";
   public static final String DB_COLUMN_ITEM_PRIMARY_COLOR = "primary_color";
   public static final String DB_COLUMN_ITEM_RATING = "rating";

   // DB Category Columns
   public static final String DB_COLUMN_CATEGORY_ID = "_id";
   public static final String DB_COLUMN_CATEGORY_NAME_EN = "name_en";
   public static final String DB_COLUMN_CATEGORY_NAME_DE = "name_de";
   public static final String DB_COLUMN_CATEGORY_PARENT_CATEGORY_ID = "parent_category_id";
   public static final String DB_COLUMN_CATEGORY_DEFAULT_ATTR_TYPE = "default_attribute_type";
   public static final String DB_COLUMN_CATEGORY_ICON = "icon";
   public static final String DB_COLUMN_CATEGORY_COLOR = "color";

   // DB Attribute Columns
   public static final String DB_COLUMN_ATTRIBUTE_ID = "_id";
   public static final String DB_COLUMN_ATTRIBUTE_ITEM_ID = "item_id";
   public static final String DB_COLUMN_ATTRIBUTE_ATTRIBUTE_TYPE_ID = "attribute_type_id";
   public static final String DB_COLUMN_ATTRIBUTE_ATTRIBUTE_VALUE = "attribute_value";

   // DB Attribute Type Columns
   public static final String DB_COLUMN_ATTRIBUTE_TYPE_ID = "_id";
   public static final String DB_COLUMN_ATTRIBUTE_TYPE_NAME_EN = "name_en";
   public static final String DB_COLUMN_ATTRIBUTE_TYPE_NAME_DE = "name_de";
   public static final String DB_COLUMN_ATTRIBUTE_TYPE_VALUE_TYPE = "value_type";
   public static final String DB_COLUMN_ATTRIBUTE_TYPE_IS_UNIQUE = "is_unique";

   // DB Compare Columns
   public static final String DB_COLUMN_COMPARE_ID = "_id";
   public static final String DB_COLUMN_COMPARE_NAME = "name";
   public static final String DB_COLUMN_COMPARE_ITEM_IDS = "item_ids";
   public static final String DB_COLUMN_COMPARE_SAVE_DATE = "save_date";

   // DB Color Columns
   public static final String DB_COLUMN_COLOR_ID = "_id";
   public static final String DB_COLUMN_COLOR_NAME_EN = "name_en";
   public static final String DB_COLUMN_COLOR_NAME_DE = "name_de";
   public static final String DB_COLUMN_COLOR_HEX = "hex";

   public static String EXTRA_CATEGORY_ID = "extra_category_id";
   public static String EXTRA_PARENT_CATEGORY_ID = "extra_parent_category_id";
   public static String EXTRA_ITEM_EDIT = "extra_is_item_edit";
   public static String EXTRA_ITEM_ID = "extra_item_id";
   public static String EXTRA_ITEM_PRIMARY_COLOR_ID = "extra_item_primary_color_id";
   public static String EXTRA_ITEM_RATING = "extra_item_rating";
   public static String EXTRA_ITEM_NAME = "extra_item_name";
   public static String EXTRA_ITEM_FILE = "extra_item_file";
   public static String EXTRA_ITEM_IS_WISHLIST = "extra_is_wishlist";
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
   public static final String KEY_OLD_DB_VERSION = "old_db_version";
   public static final String KEY_NEW_DB_VERSION = "new_db_version";

   // Shared Preferences Keys for default sizes
   public static final String KEY_DS_1_NAME = "Tops";
   public static final String KEY_DS_2_NAME = "Bottoms";
   public static final String KEY_DS_3_NAME = "Shoes";
   public static final String KEY_DS_NONE = "None";

   public static final String DEFAULT_CAT_ICON = "kleiderbuegel";

   public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 5;
}
