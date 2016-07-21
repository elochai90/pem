package com.gruppe1.pem.challengeme;

import java.util.Date;

/**
 * ListItemIconName class
 */
public class ListItemIconName {

   private int elementId;
   private int icon;
   private String name;
   private Date date;
   private String itemFile;
   // category, item, wishlist, navdrawer
   private String elementType;

   public ListItemIconName(String elementType, int elementId, int icon, String name,
         String itemBitmap) {
      this.elementType = elementType;
      this.elementId = elementId;
      this.icon = icon;
      this.name = name;
      this.itemFile = itemBitmap;
   }

   public ListItemIconName(String elementType, int elementId, int icon, String name,
         String itemFile, Date date) {
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

   public boolean isNavigationDrawerElement() {
      return (elementType.equals("navdrawer"));
   }
}