package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.gruppe1.pem.challengeme.helpers.Constants;

/**
 * AttributeType class
 */
public class AttributeType {
   private int m_id;
   private String m_name_en;
   private String m_name_de;
   private int m_valueType;
   private int m_isUnique;

   public AttributeType() {
      // TODO: set default attrs
   }

   public int getId() {
      return m_id;
   }

   public void setId(int m_Id) {
      this.m_id = m_Id;
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

   public int getValueType() {
      return m_valueType;
   }

   public void setValueType(int m_valueType) {
      this.m_valueType = m_valueType;
   }

   public int getIsUnique() {
      return m_isUnique;
   }

   public void setIsUnique(int is_unique) {
      this.m_isUnique = is_unique;
   }

   @Override
   public boolean equals(Object o) {
      return (o instanceof AttributeType && getId() == ((AttributeType) o).getId());
   }
}
