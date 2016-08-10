package com.gruppe1.pem.challengeme;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Compare class
 */
public class Compare implements Serializable {

   private int id;
   private ArrayList<Integer> itemIds;
   private String name;
   private String timestamp;

   public Compare() {
      // TODO: set default attrs
   }

   public void setId(int p_id) {
      this.id = p_id;
   }

   public int getId() {
      return this.id;
   }

   private void addItemId(int p_id) {
      if (itemIds.size() >= 2) {
         itemIds.clear();
      }
      this.itemIds.add(p_id);
   }

   public void setItemIds(ArrayList<Integer> newItems) {
      this.itemIds = newItems;
   }

   public ArrayList<Integer> getItemIds() {
      return this.itemIds;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(String timestamp) {
      this.timestamp = timestamp;
   }
}