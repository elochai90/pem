package com.gruppe1.pem.challengeme.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.gruppe1.pem.challengeme.R;

/**
 * Stores initial data in database
 */
public class DefaultSetup {
   private static boolean initated = false;
   private Context m_context;
   private CategoryDataSource categoryDataSource;
   private ColorDataSource colorDataSource;
   private AttributeTypeDataSource attributeTypeDataSource;

   public DefaultSetup(Context p_context) {
      this.m_context = p_context;

      categoryDataSource = new CategoryDataSource(m_context);
      colorDataSource = new ColorDataSource(m_context);
      attributeTypeDataSource = new AttributeTypeDataSource(m_context);

      if (!initated) {
         this.setupCategoriesNew();
         this.setupColorsNew();
         this.setupAttributeTypesNew();
         initated = true;
      }
   }

   private void setupCategoriesNew() {
      Resources res = m_context.getResources();
      TypedArray initial_cats_array = res.obtainTypedArray(R.array.initial_cats);
      for (int i = 0; i < initial_cats_array.length(); ++i) {
         int id = initial_cats_array.getResourceId(i, 0);
         if (id > 0) {
            String[] initial_cat = res.getStringArray(id);
            categoryDataSource.createCategory(initial_cat[0], initial_cat[1],
                  Integer.parseInt(initial_cat[2]), Integer.parseInt(initial_cat[3]),
                  initial_cat[4], initial_cat[5]);
         } else {
            // something wrong with the XML
         }
      }
      initial_cats_array.recycle();
   }

   private void setupColorsNew() {
      Resources res = m_context.getResources();
      TypedArray initial_colors_array = res.obtainTypedArray(R.array.initial_colors);
      for (int i = 0; i < initial_colors_array.length(); ++i) {
         int id = initial_colors_array.getResourceId(i, 0);
         if (id > 0) {
            String[] initial_color = res.getStringArray(id);
            colorDataSource.createColor(initial_color[0], initial_color[1], initial_color[2]);
         } else {
            // something wrong with the XML
         }
      }
      initial_colors_array.recycle();
   }

   private void setupAttributeTypesNew() {
      Resources res = m_context.getResources();
      TypedArray initial_attr_types_array = res.obtainTypedArray(R.array.initial_attr_types);
      for (int i = 0; i < initial_attr_types_array.length(); ++i) {
         int id = initial_attr_types_array.getResourceId(i, 0);
         if (id > 0) {
            String[] initial_attr_type = res.getStringArray(id);
            attributeTypeDataSource.createAttributeType(initial_attr_type[0], initial_attr_type[1],
                  Integer.parseInt(initial_attr_type[2]), Integer.parseInt(initial_attr_type[3]));
         } else {
            // something wrong with the XML
         }
      }
      initial_attr_types_array.recycle();
   }
}
