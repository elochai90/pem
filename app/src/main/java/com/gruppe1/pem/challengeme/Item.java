package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class Item implements Parcelable{
    private int m_id;
    private String m_name;
    private String m_imageFile;
    private int m_categoryId;
    private int m_isWish;
    private int m_primaryColorId;
    private Float m_rating;
    private Context m_context;
    private DataBaseHelper m_dbHelper;


    public Item(Context p_context, int p_id, DataBaseHelper p_dbHelper) {
        this.m_id = p_id;
        this.m_context = p_context;
        this.m_dbHelper = p_dbHelper;
        this.m_dbHelper.setTable(Constants.ITEMS_DB_TABLE);

        if(p_id > 0) {
            // get data from existing item
            this.m_dbHelper.setColumns(new String[]{"*"});
            this.m_dbHelper.setWhere("", new String[]{"_id=" + p_id});
            Cursor itemData = this.m_dbHelper.select();

            if(itemData.moveToFirst()) {
                this.m_id = itemData.getInt(0);
                this.m_name = itemData.getString(1);
                this.m_imageFile = itemData.getString(2);
                this.m_categoryId = itemData.getInt(3);
                this.m_isWish = itemData.getInt(4);
                this.m_primaryColorId = itemData.getInt(5);
                this.m_rating= itemData.getFloat(6);
            }
            itemData.close();
        }
    }

    public int getId() {
        return m_id;
    }

    public void setId(int p_id) {
        this.m_id = p_id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String p_name) {
        this.m_name = p_name;
    }

    public String getImageFile() {
        return m_imageFile;
    }

    public void setImageFile(String p_imageFile) {
        this.m_imageFile = p_imageFile;
    }

    public int getCategoryId() {
        return m_categoryId;
    }

    public void setCategoryId(int p_categoryId) {
        this.m_categoryId = p_categoryId;
    }

    public int getIsWish() {
        return m_isWish;
    }

    public void setIsWish(int p_isWish) {
        this.m_isWish = p_isWish;
    }

    public int getPrimaryColorId() {
        return m_primaryColorId;
    }

    public void setPrimaryColorId(int m_primaryColorId) {
        this.m_primaryColorId = m_primaryColorId;
    }

    public Float getRating() {
        return m_rating;
    }

    public void setRating(Float p_rating) {
        this.m_rating = p_rating;
    }


    /**
     * gets all items of specific category
     * @param p_context applicaton context
     * @param p_categoryId parent category id
     * @param showAlsoWishlistItems decides wether wishlist items are to be shown as well
     * @return ArrayList of Items
     */
    public static ArrayList<Item> getItemsByCategoryId(Context p_context, int p_categoryId, boolean showAlsoWishlistItems) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.ITEMS_DB_TABLE);
        dbHelper.setColumns(new String[]{"*"});

        if (showAlsoWishlistItems) {
            dbHelper.setWhere("", new String[]{"category_id='" + p_categoryId + "'"});
        } else {
            dbHelper.setWhere("", new String[]{"category_id='" + p_categoryId + "' AND is_wish=0"});
        }

        dbHelper.setOrderBy("name ASC");
        Cursor cursor = dbHelper.select();

        ArrayList<Item> itemList = new ArrayList<>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            itemList.add(new Item(p_context, cursor.getInt(0), dbHelper));
            cursor.moveToNext();
        }
        cursor.close();
        return itemList;
    }

    /**
     * gets all Items
     * @param p_context applicaton context
     * @param p_wishListItems decides wether wishlist items are to be shown as well
     * @return returns all items
     */
    public static ArrayList<Item> getAllItems(Context p_context, boolean p_wishListItems) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.ITEMS_DB_TABLE);
        dbHelper.setColumns(new String[]{"*"});

        if(p_wishListItems) {
            dbHelper.setWhere("", new String[]{"is_wish=1"});
        } else {
            dbHelper.setWhere("", new String[]{"is_wish=0"});
        }

        dbHelper.setOrderBy("name ASC");
        Cursor cursor = dbHelper.select();

        ArrayList<Item> itemList = new ArrayList<>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            itemList.add(new Item(p_context, cursor.getInt(0), dbHelper));
            cursor.moveToNext();
        }

        cursor.close();
        dbHelper.close();
        return itemList;
    }

    /**
     * gets count of items in category (not really smart...has to be changed in combination with other method)
     * @param p_context application context
     * @param p_categoryId parent category id
     * @param showAlsoWishlistItems decides wether wishlist items are to be shown as well
     * @return count of items in category
     */
    public static int getItemsCountByCategoryId(Context p_context, int p_categoryId, boolean showAlsoWishlistItems) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.ITEMS_DB_TABLE);
        dbHelper.setColumns(new String[]{"COUNT(*)"});

        ArrayList<Integer> allUnderCategoriesIds = Category.getAllUnderCategoriesIds(p_context, p_categoryId);
        allUnderCategoriesIds.add(p_categoryId);
        String categoriesIdsExpression = "";
        if(allUnderCategoriesIds.size() > 0) {
            String categoriesIdsString = allUnderCategoriesIds.toString();
            categoriesIdsString = categoriesIdsString.replace("[","(");
            categoriesIdsString = categoriesIdsString.replace("]",")");
            categoriesIdsExpression = ("category_id in " + categoriesIdsString);
        }
        String wishlistExpression = "";
        if(!showAlsoWishlistItems) {
            wishlistExpression = (" AND is_wish=0");
        }
        dbHelper.setWhere("", new String[]{categoriesIdsExpression + wishlistExpression});


//        if (showAlsoWishlistItems) {
//            dbHelper.setWhere("", new String[]{"category_id='" + p_categoryId + "'"});
//        } else {
//            dbHelper.setWhere("", new String[]{"category_id='" + p_categoryId + "' AND is_wish=0"});
//        }

        Cursor cursor = dbHelper.select();

        int itemsCount = 0;

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            itemsCount = cursor.getInt(0);
            cursor.moveToNext();
        }

        cursor.close();
        dbHelper.close();
        return itemsCount;
    }

    /**
     * edit the item
     * @param p_values value to be edited
     */
    public void edit(HashMap<String, String> p_values) {
        Set<String> keys = p_values.keySet();

        for (String key : keys) {
            String dbColumnValue = p_values.get(key);

            switch (key) {
                case "name":
                    this.setName(dbColumnValue);
                    break;
                case "image_file":
                    this.setImageFile(dbColumnValue);
                    break;
                case "category_id":
                    this.setCategoryId(Integer.parseInt(dbColumnValue));
                    break;
                case "is_wish":
                    this.setIsWish(Integer.parseInt(dbColumnValue));
                    break;
                case "primary_color":
                    this.setPrimaryColorId(Integer.parseInt(dbColumnValue));
                    break;
                case "rating":
                    this.setRating(Float.parseFloat(dbColumnValue));
                    break;
            }
        }
    }

    /**
     * save the item
     */
    public void save() {
        if(this.m_id == 0) {
            // insert as new item
            m_dbHelper.deleteValues();
            this.m_dbHelper.setWhere("", new String[]{"name='" + this.m_name + "'"});
            Cursor existingRowCursor = this.m_dbHelper.select();
            existingRowCursor.moveToFirst();
            int rowId;

            try {
                rowId = existingRowCursor.getInt(0);
            } catch (Exception e) {
                rowId = 0;
            }

            existingRowCursor.close();

            if(rowId == 0) {
                this.setAllValuesToDbHelper();
                int id = this.m_dbHelper.insert();

                if (id > -1) {
                    this.m_id = id;
                }
            }
        } else {
            //save changes to existing item
            this.m_dbHelper.setWhere("", new String[] {"_id=" + this.m_id});
            this.setAllValuesToDbHelper();
            this.m_dbHelper.update();
        }
    }

    /**
     * sets all values to database helper
     */
    private void setAllValuesToDbHelper() {
        this.m_dbHelper.setStringValue("name", this.m_name);
        this.m_dbHelper.setStringValue("image_file", this.m_imageFile);
        this.m_dbHelper.setIntegerValue("category_id", this.m_categoryId);
        this.m_dbHelper.setIntegerValue("is_wish", this.m_isWish);
        this.m_dbHelper.setIntegerValue("primary_color", this.m_primaryColorId);
        this.m_dbHelper.setFloatValue("rating", this.m_rating);
    }

    /**
     * deletes the item and concerning compares
     */
    public void delete() {
        this.m_dbHelper.setWhere("", new String[]{"_id=" + this.m_id});
        this.m_dbHelper.delete();
        this.m_dbHelper.close();

        Compare.deleteComparesByItemId(this.m_context, this.m_id);
    }

    public static ArrayList<Item> getSearchResults(Context p_context, String query, ArrayList<Integer> filterCategoryIds, boolean filterWishlist, int filterRating, ArrayList<Integer> filterColorIds) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.ITEMS_DB_TABLE);
        dbHelper.setColumns(new String[]{"*"});
        String filterWishlistExpression = filterWishlist ? " AND is_wish=1" : "";
        String filterRatingExpression = (filterRating != -1) ? (" AND rating=" + filterRating) : "";
        String filterColorExpression = "";
        if(filterColorIds.size() > 0) {
            String filterColorIdsString = filterColorIds.toString();
            filterColorIdsString = filterColorIdsString.replace("[","(");
            filterColorIdsString = filterColorIdsString.replace("]",")");
            filterColorExpression = (" AND primary_color in " + filterColorIdsString);
        }
        String filterCategoryExpression = "";
        if(filterCategoryIds.size() > 0) {
            String filterCategoryIdsString = filterCategoryIds.toString();
            filterCategoryIdsString = filterCategoryIdsString.replace("[","(");
            filterCategoryIdsString = filterCategoryIdsString.replace("]",")");
            filterCategoryExpression = (" AND category_id in " + filterCategoryIdsString);
        }
        dbHelper.setWhere("", new String[]{"name LIKE '%" + query + "%'" + filterCategoryExpression + filterWishlistExpression + filterRatingExpression + filterColorExpression});

        Cursor cursor = dbHelper.select();

        ArrayList<Item> searchItems = new ArrayList<>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            searchItems.add(new Item(p_context, cursor.getInt(0), dbHelper));
            cursor.moveToNext();
        }

        cursor.close();
        dbHelper.close();
        return searchItems;
    }

    public Item(Parcel in) {
        String[] data = new String[7];

        in.readStringArray(data);
        this.m_id = Integer.parseInt(data[0]);
        this.m_name = data[1];
        this.m_imageFile = data[2];
        this.m_categoryId = Integer.parseInt(data[3]);
        this.m_isWish = Integer.parseInt(data[4]);
        this.m_primaryColorId = Integer.parseInt(data[5]);
        this.m_rating = Float.parseFloat(data[6]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.m_id + "",
                this.m_name,
                this.m_imageFile,
                this.m_categoryId + "",
                this.m_isWish + "",
                this.m_primaryColorId + "",
                this.m_rating + ""
        });
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        return (o instanceof Item && getId() == ((Item) o).getId());
    }
}
