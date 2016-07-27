package com.gruppe1.pem.challengeme;

import android.content.Context;

import java.util.Date;

/**
 * ListItemIconName class
 */
public class ListItemIconName {

   private Context context;
   private int elementId;
   private int icon;
   private String name;
   private Date date;
   private String itemFile;
   // category, item, wishlist
   private String elementType;

   public ListItemIconName(Context context, String elementType, int elementId, int icon,
         String name, String itemBitmap) {
      this.context = context;
      this.elementType = elementType;
      this.elementId = elementId;
      this.icon = icon;
      this.name = name;
      this.itemFile = itemBitmap;
   }

   public ListItemIconName(Context context, String elementType, int elementId, int icon,
         String name, String itemFile, Date date) {
      this.context = context;
      this.elementType = elementType;
      this.elementId = elementId;
      this.icon = icon;
      this.name = name;
      this.itemFile = itemFile;
      this.date = date;
   }

   public int getElementId() {
      return elementId;
   }

   public int getIcon() {
      return icon;
   }

   public String getName() {
      return name;
   }

   public Date getDate() {
      return date;
   }

   public String getItemFile() {
      return itemFile;
   }

   public boolean isCategoryElement() {
      return (elementType.equals("category"));
   }

   public boolean isItemElement() {
      return (elementType.equals("item"));
   }

   public boolean isWishlistElement() {
      return (elementType.equals("wishlist"));
   }
}