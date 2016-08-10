package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.gruppe1.pem.challengeme.helpers.Constants;

/**
 * Color class
 */
public class Color {
   private int m_id;
   private String m_name_en;
   private String m_name_de;
   private String m_hex;

   public Color() {
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

   public String getHexColor() {
      return m_hex;
   }

   public void setHexColor(String color) {
      this.m_hex = color;
   }

   @Override
   public boolean equals(Object o) {
      return (o instanceof Color && getId() == ((Color) o).getId());
   }
}
