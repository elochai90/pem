package com.gruppe1.pem.challengeme.views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Attribute;
import com.gruppe1.pem.challengeme.AttributeType;
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.CategoryEditText;
import com.gruppe1.pem.challengeme.helpers.ColorEditText;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.DateEditText;
import com.gruppe1.pem.challengeme.helpers.ExactColorEditText;
import com.gruppe1.pem.challengeme.helpers.ImageDominantColorExtractor;
import com.gruppe1.pem.challengeme.helpers.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bianka on 20.08.2015.
 */
// Instances of this class are fragments representing a single
// object in our collection.
public class CollectionItemsFragment extends Fragment {

   private DataBaseHelper db_helper;

   private CollectionItemsActivity activity;

   private ImageView ImgPhoto;
   private RatingBar ratingBar;
   private EditText itemNameExitText;
   private LinearLayout attributesView;
   private View attrColorIndicator;

   private CategoryEditText attrCategoryValue;
   private Category attrCategorySelected;
   private ColorEditText attrColorValue;
   private com.gruppe1.pem.challengeme.Color attrColorSelected;
   private Switch attrWishlistValue;
   private String item_imageFile;

   private int editItemId = -1;
   private Item editItem;

   private ExactColorEditText attrValueColorPicker;

   private DateEditText attrValueDatePicker;

   private ArrayList<AttributeType> attributeTypesList;

   private SharedPreferences sharedPreferences;

   private ArrayList<com.gruppe1.pem.challengeme.Color> allColors;
   private ArrayList<Category> allCategories;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {

      View rootView = inflater.inflate(R.layout.fragment_collection_items, container, false);
      activity = (CollectionItemsActivity) getActivity();

      sharedPreferences =
            getActivity().getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);

      db_helper = new DataBaseHelper(activity);
      db_helper.init();

      ImgPhoto = (ImageView) rootView.findViewById(R.id.itemDetailImage);
      ImgPhoto.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            selectImage();
         }
      });

      ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
      LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
      stars.getDrawable(0)
            .setColorFilter(getResources().getColor(R.color.gray02), PorterDuff.Mode.SRC_ATOP);

      attributeTypesList = new ArrayList<>();
      itemNameExitText = (EditText) rootView.findViewById(R.id.itemName);
      attributesView = (LinearLayout) rootView.findViewById(R.id.itemDetailAttributes);
      attrCategoryValue = (CategoryEditText) rootView.findViewById(R.id.attrCategoryValue);
      attrColorValue = (ColorEditText) rootView.findViewById(R.id.attrColorValue);
      attrColorIndicator = (View) rootView.findViewById(R.id.attrColorIndicator);
      attrWishlistValue = (Switch) rootView.findViewById(R.id.attrWishlistValue);

      allCategories = Category.getAllCategories(activity);

      allColors = com.gruppe1.pem.challengeme.Color.getAllColors(activity);

      if (savedInstanceState == null && editItemId == -1) {
         Bundle extras = getArguments();
         if (extras != null) {
            editItemId = extras.getInt(Constants.EXTRA_ITEM_ID);
         }
      }

      Bundle extras1 = getArguments();
      if (extras1 != null && extras1.getBoolean("is_wishlist")) {
         attrWishlistValue.setChecked(true);
      }
      // Not a new item, but editing an existing item
      int parentCategoryId = -1;
      int savedColorId = -1;
      if (editItemId > 0) {
         getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
         editItem = new Item(activity, editItemId, getDb_helper());
         parentCategoryId = editItem.getCategoryId();
         savedColorId = editItem.getPrimaryColorId();
      } else {
         getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
         editItem = new Item(activity, 0, getDb_helper());
         if (extras1 != null && extras1.getInt("category_id") != 0) {
            parentCategoryId = extras1.getInt("category_id");
         }
      }

      attrCategoryValue.setItems(getActivity(), getDb_helper(), allCategories);
      attrCategoryValue.setOnItemSelectedListener(new CategoryEditText.OnItemSelectedListener() {
         @Override
         public void onItemSelectedListener(Category item, int selectedIndex) {
            ((TextView) attributesView.findViewWithTag("size")).setText(
                  getSizeValueBySizeType(item.getDefaultSizeType()));
         }
      });
      attrColorValue.setItems(getActivity(), getDb_helper(), allColors);
      attrColorValue.setOnItemSelectedListener(new ColorEditText.OnItemSelectedListener() {
         @Override
         public void onItemSelectedListener(com.gruppe1.pem.challengeme.Color item,
               int selectedIndex) {
            attrColorIndicator.setBackgroundColor(Color.parseColor(item.getHexColor()));
         }
      });

      attrColorIndicator.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            attrColorValue.callOnClick();
         }
      });


      setupAttributeViews();


      if (parentCategoryId != -1) {
         attrCategorySelected = new Category(activity, parentCategoryId, getDb_helper());
         attrCategoryValue.setSelection(allCategories.indexOf(attrCategorySelected));
      } else {
         attrCategorySelected = null;
      }

      if (savedColorId != -1) {
         attrColorSelected =
               new com.gruppe1.pem.challengeme.Color(activity, savedColorId, getDb_helper());
         attrColorValue.setSelection(allColors.indexOf(attrColorSelected));
      } else {
         attrColorSelected = null;
      }

      if (editItemId > 0) {
         setItemData();
      }
      return rootView;
   }

   /**
    * get the size value to a size type
    *
    * @param sizeType int the size type
    * @return the size value
    */
   private String getSizeValueBySizeType(int sizeType) {
      String sizeValue;
      switch (sizeType) {
         case 0:
            sizeValue = sharedPreferences.getString(Constants.KEY_DS_1_NAME, "");
            break;
         case 1:
            sizeValue = sharedPreferences.getString(Constants.KEY_DS_2_NAME, "");
            break;
         case 2:
            sizeValue = sharedPreferences.getString(Constants.KEY_DS_3_NAME, "");
            break;
         default:
            sizeValue = sharedPreferences.getString(Constants.KEY_DS_NONE, "");
            break;
      }
      return sizeValue;
   }

   /**
    * sets up all attribute views
    */
   private void setupAttributeViews() {
      ArrayList<AttributeType> allAttributeTypes = AttributeType.getAttributeTypes(activity);
      for (AttributeType tmpAttrType : allAttributeTypes) {
         if (!attributeTypesList.contains(tmpAttrType)) {
            attributeTypesList.add(tmpAttrType);
            setAttributeLayout(tmpAttrType, null);
         }
      }
   }

   /**
    * Get the db_helper instance for this class
    *
    * @return DataBaseHelper instance
    */
   private DataBaseHelper getDb_helper() {
      if (!db_helper.isOpen()) {
         System.out.println("db helper was closed");
         db_helper = new DataBaseHelper(activity);
         db_helper.init();
      }
      return db_helper;
   }

   /**
    * gets the URI from a bitmap
    *
    * @param inContext the context
    * @param inImage   the bitmap image
    * @return the Uri to the image
    */
   private Uri getImageUri(Context inContext, Bitmap inImage) {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
      String path =
            MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title",
                  null);
      return Uri.parse(path);
   }

   /**
    * gets the actual path of a URI
    *
    * @param uri the Uri to which you want the path
    * @return the path to the uri
    */
   private String getRealPathFromURI(Uri uri) {
      Cursor cursor = getActivity().getContentResolver()
            .query(uri, null, null, null, null);
      cursor.moveToFirst();
      int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
      String idxString = cursor.getString(idx);
      cursor.close();
      return idxString;
   }

   /**
    * creates and shows an AlertDialog with options to choose/take an image
    */
   private void selectImage() {
      final CharSequence[] options;
      if (item_imageFile != null) {
         options = new CharSequence[] { getString(R.string.item_show_image_fullscreen),
               getString(R.string.item_take_photo), getString(R.string.item_choose_gellery),
               getString(R.string.cancel) };
      } else {
         options = new CharSequence[] { getString(R.string.item_take_photo),
               getString(R.string.item_choose_gellery), getString(R.string.cancel) };
      }

      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setItems(options, new DialogInterface.OnClickListener() {

         @Override

         public void onClick(DialogInterface dialog, int item) {

            if (options[item].equals(getString(R.string.item_take_photo))) {
               Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
               activity.startActivityForResult(intent, 1);
            } else if (options[item].equals(getString(R.string.item_choose_gellery))) {
               Intent intent = new Intent(Intent.ACTION_PICK,
                     android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               activity.startActivityForResult(intent, 2);
            } else if (options[item].equals(getString(R.string.cancel))) {
               dialog.dismiss();
            } else if (options[item].equals(getString(R.string.item_show_image_fullscreen))) {
               Intent i = new Intent(getActivity(), FullscreenImageActivity.class);
               i.putExtra("imageurl", item_imageFile);
               startActivity(i);
            }
         }
      });
      builder.show();
   }

   private void setAttributeLayoutData(AttributeType attributeType, Object attributeValue) {
      if(attributeValue != null) {
         LinearLayout layout =  (LinearLayout) attributesView.findViewWithTag(attributeType.getId());
         // attribute is boolean
         if (attributeType.getValueType() == 2) {
            Switch switchValue = (Switch) layout.findViewById(R.id.switchValue);
            boolean bool = (attributeValue.toString()
                  .equals("1"));
            switchValue.setChecked(bool);
            // attribute is ColorPicker
         } else if (attributeType.getValueType() == 3) {
            int exactColorId = Integer.parseInt(attributeValue.toString());
            attrValueColorPicker.setExactColorId(exactColorId);
         // attribute is DatePicker
         } else if (attributeType.getValueType() == 4) {
            // TODO: change; setTag auf richtige Views; alle Types
            String buyDate = attributeValue.toString();
            attrValueDatePicker.setDate(buyDate);
         } else {
            EditText textView = (EditText) layout.findViewById(R.id.stringAttrField);
            textView.setText(attributeValue.toString());
            if (attributeType.getName()
                  .equals(getString(R.string.attr_type_size_en)) || attributeType.getName()
                  .equals(getResources().getString(R.string.attr_type_size_de))) {
               if (attrCategorySelected != null) {
                  textView.setText(
                        getSizeValueBySizeType(attrCategorySelected.getDefaultSizeType()));
               }
            }
         }

      }
   }

   /**
    * creates the layout for a attribute type with the atrribute value
    *
    * @param attributeType  AttributeType the attribute type
    * @param attributeValue Object the value of the attribute type
    */
   private void setAttributeLayout(AttributeType attributeType, Object attributeValue) {

      TextInputLayout attributeLayout = new TextInputLayout(activity);
      LinearLayout.LayoutParams attributeLayoutParams =
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                  ViewGroup.LayoutParams.WRAP_CONTENT);
      attributeLayoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.margin_small);
      attributeLayout.setLayoutParams(attributeLayoutParams);
      // Set the attributeTypeId for saving
      attributeLayout.setTag(attributeType.getId());

      View attributeValueView;
      // attribute is boolean
      if (attributeType.getValueType() == 2) {
         LayoutInflater inflater = getLayoutInflater(null);

         View switchLayout = inflater.inflate(R.layout.formular_layout_switch, null);
         Switch switchValue = (Switch) switchLayout.findViewById(R.id.switchValue);
         final TextView switchLabel = (TextView) switchLayout.findViewById(R.id.switchLabel);

         switchLabel.setText(attributeType.getName());

         if (attributeValue != null) {
            boolean bool = (attributeValue.toString()
                  .equals("1"));
            switchValue.setChecked(bool);
         }

         attributeValueView = switchLayout;
      }
      // attribute is ColorPicker
      else if (attributeType.getValueType() == 3) {
         LayoutInflater inflater = getLayoutInflater(null);

         View exactColorLayout = inflater.inflate(R.layout.formular_layout_color, null);
         attrValueColorPicker =
               (ExactColorEditText) exactColorLayout.findViewById(R.id.attrExactColorValue);
         final View attrExactColorIndicator =
               exactColorLayout.findViewById(R.id.attrExactColorIndicator);

         int exactColorId = -1;
         if (attributeValue != null) {
            exactColorId = Integer.parseInt(attributeValue.toString());
         }
         attrValueColorPicker.setOnColorSelectedListener(
               new ExactColorEditText.OnColorSelectedListener() {
                  @Override
                  public void onColorSelectedListener(int color) {
                     if(color != -1) {
                        attrExactColorIndicator.setBackgroundColor(color);
                     }
                  }
               });
         attrValueColorPicker.initialize(activity, exactColorId);
         attrExactColorIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               attrValueColorPicker.callOnClick();
            }
         });
         attributeValueView = exactColorLayout;
      }
      // attribute is DatePicker
      else if (attributeType.getValueType() == 4) {
         attrValueDatePicker = new DateEditText(activity);
         ViewGroup.LayoutParams attibuteValueLayoutParams =
               new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                     ViewGroup.LayoutParams.WRAP_CONTENT);
         attrValueDatePicker.setLayoutParams(attibuteValueLayoutParams);
         attrValueDatePicker.setHint(attributeType.getName());
         attrValueDatePicker.setId(R.id.dateDialogField);

         String buyDate = "";
         if (attributeValue != null) {
            buyDate = attributeValue.toString();
         }
         attrValueDatePicker.setOnDateSelectedListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth,
                  int selectedDay) {
               String buyDate = selectedDay + "." + (selectedMonth + 1) + "." + selectedYear;
               attrValueDatePicker.setDate(buyDate);
            }
         });
         attrValueDatePicker.initialize(activity, buyDate);
         attributeValueView = attrValueDatePicker;
      } else {
         EditText textAttributeValue = new EditText(activity);
         textAttributeValue.setSingleLine(true);
         ViewGroup.LayoutParams attibuteValueLayoutParams =
               new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                     ViewGroup.LayoutParams.WRAP_CONTENT);
         textAttributeValue.setLayoutParams(attibuteValueLayoutParams);
         textAttributeValue.setId(R.id.stringAttrField);

         if (attributeType.getName()
               .equals(getResources().getString(R.string.attr_type_size_en)) ||
               attributeType.getName()
                     .equals(getResources().getString(R.string.attr_type_size_de))) {
            textAttributeValue.setTag("size");
         }
         textAttributeValue.setHint(attributeType.getName());
         if (attributeValue != null) {
            textAttributeValue.setText(attributeValue.toString());
         } else if (attributeType.getName()
               .equals(getString(R.string.attr_type_size_en)) || attributeType.getName()
               .equals(getResources().getString(R.string.attr_type_size_de))) {
            if (attrCategorySelected != null) {
               textAttributeValue.setText(
                     getSizeValueBySizeType(attrCategorySelected.getDefaultSizeType()));
            }
         }

         attributeValueView = textAttributeValue;
      }
      attributeLayout.addView(attributeValueView);

      attributesView.addView(attributeLayout, attributesView.getChildCount());
   }

   /**
    * sets the data of the editing item into the formular
    */
   private void setItemData() {
      itemNameExitText.setText(editItem.getName());
      ratingBar.setRating(editItem.getRating());

      attrCategorySelected = new Category(activity, editItem.getCategoryId(), getDb_helper());
      attrCategoryValue.setText(attrCategorySelected.getName());
      attrColorSelected =
            new com.gruppe1.pem.challengeme.Color(activity, editItem.getPrimaryColorId(),
                  getDb_helper());
      attrColorValue.setSelection(allColors.indexOf(attrColorSelected));

      try {
         String imgPath = editItem.getImageFile();
         File imgFile = new File(imgPath);
         if (imgFile.exists()) {
            item_imageFile = imgPath;
            Bitmap tmpBitmap = ImageLoader.getPicFromFile(editItem.getImageFile(), 500, 500);
            ImgPhoto.setImageBitmap(tmpBitmap);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      if (editItem.getIsWish() == 0) {
         attrWishlistValue.setChecked(false);
      } else {
         attrWishlistValue.setChecked(true);
      }

      ArrayList<Attribute> itemAttributes =
            Attribute.getAttributesByItemId(activity, editItem.getId());
      for (Attribute tmpItemAttr : itemAttributes) {
         attributeTypesList.add(tmpItemAttr.getAttributeType());
         setAttributeLayoutData(tmpItemAttr.getAttributeType(), tmpItemAttr.getValue());
      }
   }

   /**
    * creates/updates the item
    */
   public void saveItem() {
      String item_name = itemNameExitText.getText()
            .toString();
      String item_categoryId = "" + attrCategoryValue.getSelectedItem()
            .getId();
      String item_primaryColor = "" + attrColorValue.getSelectedItem()
            .getId();
      String item_rating = Float.toString(ratingBar.getRating());
      String item_isWish = attrWishlistValue.isChecked() ? "1" : "0";

      HashMap<String, String> itemAttributes = new HashMap<>();
      itemAttributes.put("name", item_name);
      itemAttributes.put("image_file", editItem.getImageFile());
      itemAttributes.put("category_id", item_categoryId);
      itemAttributes.put("primary_color", item_primaryColor);
      itemAttributes.put("rating", item_rating);
      itemAttributes.put("is_wish", item_isWish);

      getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
      editItem.edit(itemAttributes);
      editItem.save();

      for (AttributeType tmpItemAttrType : attributeTypesList) {
         LinearLayout attributeView =
               (LinearLayout) attributesView.findViewWithTag(tmpItemAttrType.getId());
         String attributeSaveValue;
         // boolean
         if (tmpItemAttrType.getValueType() == 2) {
            attributeSaveValue =
                  ((Switch) attributeView.findViewById(R.id.switchValue)).isChecked() ? "1" : "0";
         }
         // Color Picker
         else if (tmpItemAttrType.getValueType() == 3) {
            attributeSaveValue = attrValueColorPicker.getExactColor() + "";
         }
         // Date Picker
         else if (tmpItemAttrType.getValueType() == 4) {
            attributeSaveValue = attrValueDatePicker.getDate();
         } else {
            attributeSaveValue =
                  ((EditText) attributeView.findViewById(R.id.stringAttrField)).getText()
                        .toString();
         }
         HashMap<String, String> itemAttributeValue = new HashMap<>();
         itemAttributeValue.put("item_id", editItem.getId() + "");
         itemAttributeValue.put("attribute_type_id", tmpItemAttrType.getId() + "");
         itemAttributeValue.put("attribute_value", attributeSaveValue);

         getDb_helper().setTable(Constants.ITEM_ATTR_DB_TABLE);
         Attribute itemAttribute =
               new Attribute(activity, editItem.getId(), tmpItemAttrType.getId(), getDb_helper());
         itemAttribute.edit(itemAttributeValue);
         itemAttribute.save();
      }
      this.db_helper.close();
   }

   public void setBitmap(Bitmap photo) {
      ImgPhoto.setImageBitmap(photo);

      // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
      Uri tempUri = getImageUri(activity, photo);

      // CALL THIS METHOD TO GET THE ACTUAL PATH
      File finalFile = new File(getRealPathFromURI(tempUri));
      item_imageFile = finalFile.getAbsolutePath();
      editItem.setImageFile(item_imageFile);
      int exactColorId = ImageDominantColorExtractor.getInstance()
            .getDominantColor(photo);
      attrValueColorPicker.setExactColorId(exactColorId);
   }

   public void setImageUri(Uri selectedImage) {
      String[] filePath = { MediaStore.Images.Media.DATA };

      Cursor c = activity.getContentResolver()
            .query(selectedImage, filePath, null, null, null);
      c.moveToFirst();
      int columnIndex = c.getColumnIndex(filePath[0]);
      String picturePath = c.getString(columnIndex);
      c.close();

      item_imageFile = picturePath;
      editItem.setImageFile(item_imageFile);

      Bitmap tmpBitmap = ImageLoader.getPicFromFile(editItem.getImageFile(), 500, 500);
      ImgPhoto.setImageBitmap(tmpBitmap);
      int exactColorId = ImageDominantColorExtractor.getInstance()
            .getDominantColor(tmpBitmap);
      attrValueColorPicker.setExactColorId(exactColorId);
   }
}