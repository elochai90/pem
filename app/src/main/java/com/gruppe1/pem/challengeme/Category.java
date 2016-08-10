package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.gruppe1.pem.challengeme.helpers.Constants;

/**
 * Category class
 */
public class Category {
   private int m_id;
   private String m_name_en;
   private String m_name_de;
   private int m_parent_category_id = Constants.DEFAULT_CATEGORY_ID;
   private String m_icon;
   private String m_color = "5d5d5d";
   // TODO add default size to sql init
   private int m_defaultSizeType;

   public Category() {
      // TODO: set default attrs
   }

   public int getId() {
      return m_id;
   }

   public void setId(int m_id) {
      this.m_id = m_id;
   }

   public String getName(Context context) {
      SharedPreferences prefs =
            context.getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
      String language = prefs.getString(Constants.KEY_LANGUAGE, "");
      switch (language) {
         case "en":
            return m_name_en;
         case "de":
            return m_name_de;
         default:
            return m_name_en;
      }
   }

   public void setNameEn(String m_name) {
      this.m_name_en = m_name;
   }

   public void setNameDe(String m_name) {
      this.m_name_de = m_name;
   }

   public int getParentCategoryId() {
      return m_parent_category_id;
   }

   public void setParentCategoryId(int m_parent_cat_id) {
      this.m_parent_category_id = m_parent_cat_id;
   }

   public String getColor() {
      return m_color;
   }

   public void setColor(String m_color) {
      this.m_color = m_color;
   }

   public int getDefaultSizeType() {
      return m_defaultSizeType;
   }

   public void setDefaultSizeType(int m_defaultSizeType) {
      this.m_defaultSizeType = m_defaultSizeType;
   }

   public String getIcon() {
      return m_icon;
   }

   public void setIcon(String m_icon) {
      this.m_icon = m_icon;
   }

   @Override
   public boolean equals(Object o) {
      return (o instanceof Category && getId() == ((Category) o).getId());
   }
}
