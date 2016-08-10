package com.gruppe1.pem.challengeme;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
   private int m_id;
   private String m_name;
   private String m_imageFile;
   private int m_categoryId;
   private int m_isWish;
   private int m_primaryColorId;
   private Float m_rating;

   public Item() {
      m_name = "";
      m_imageFile = "";
      m_categoryId = -1;
      m_isWish = 0;
      m_primaryColorId = -1;
      m_rating = 0.0f;
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
      dest.writeStringArray(
            new String[] { this.m_id + "", this.m_name, this.m_imageFile, this.m_categoryId + "",
                  this.m_isWish + "", this.m_primaryColorId + "", this.m_rating + "" });
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
