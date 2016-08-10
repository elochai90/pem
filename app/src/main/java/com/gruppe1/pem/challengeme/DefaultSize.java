package com.gruppe1.pem.challengeme;

/**
 * DefaultSize class
 */
public class DefaultSize {

   private String defaultSizeName;
   private String defaultSizeValue;

   public DefaultSize(String defaultSizeName, String defaultSizeValue) {
      this.defaultSizeName = defaultSizeName;
      this.defaultSizeValue = defaultSizeValue;
   }

   public String getDefaultSizeName() {
      return defaultSizeName;
   }

   public String getDefaultSizeValue() {
      return defaultSizeValue;
   }

   @Override
   public boolean equals(Object o) {
      return (o instanceof DefaultSize &&
            getDefaultSizeName().equals(((DefaultSize) o).getDefaultSizeName()));
   }
}