package com.gruppe1.pem.challengeme.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class NewItemActivity extends AppCompatActivity {

   private DataBaseHelper db_helper;

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

   //   private int editItemId;
   private Item editItem;

   private ExactColorEditText attrValueColorPicker;
   private DateEditText attrValueDatePicker;

   private ArrayList<AttributeType> attributeTypesList;

   private SharedPreferences sharedPreferences;

   private ArrayList<com.gruppe1.pem.challengeme.Color> allColors;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_new_item);

      getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

      setupToolbar();
      sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);

      db_helper = new DataBaseHelper(this);
      db_helper.init();

      ImgPhoto = (ImageView) findViewById(R.id.itemDetailImage);
      ImgPhoto.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            selectImage();
         }
      });

      ratingBar = (RatingBar) findViewById(R.id.ratingBar);
      LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
      stars.getDrawable(0)
            .setColorFilter(getResources().getColor(R.color.gray02), PorterDuff.Mode.SRC_ATOP);

      attributeTypesList = new ArrayList<>();
      itemNameExitText = (EditText) findViewById(R.id.itemName);
      attributesView = (LinearLayout) findViewById(R.id.itemDetailAttributes);
      attrCategoryValue = (CategoryEditText) findViewById(R.id.attrCategoryValue);
      attrColorValue = (ColorEditText) findViewById(R.id.attrColorValue);
      attrColorIndicator = (View) findViewById(R.id.attrColorIndicator);
      attrWishlistValue = (Switch) findViewById(R.id.attrWishlistValue);

      setupAttributeViews();

      ArrayList<Category> allCategories = Category.getAllCategories(this);
      attrCategoryValue.setItems(this, getDb_helper(), allCategories);
      attrCategoryValue.setOnItemSelectedListener(new CategoryEditText.OnItemSelectedListener() {
         @Override
         public void onItemSelectedListener(Category item, int selectedIndex) {
            ((TextView) attributesView.findViewWithTag("size")).setText(
                  getSizeValueBySizeType(item.getDefaultSizeType()));
         }
      });

      allColors = com.gruppe1.pem.challengeme.Color.getAllColors(this);
      attrColorValue.setItems(this, getDb_helper(), allColors);
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


      //      editItemId = -1;
      //      if (savedInstanceState == null) {
      //         Bundle extras = getIntent().getExtras();
      //         if (extras != null) {
      //            editItemId = extras.getInt(Constants.EXTRA_ITEM_ID);
      //         }
      //      }

      Bundle extras1 = getIntent().getExtras();
      if (extras1 != null && extras1.getBoolean("is_wishlist")) {
         attrWishlistValue.setChecked(true);
      }
      getSupportActionBar().setTitle(R.string.title_activity_new_item);

      int parentCategoryId = -1;
      getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
      editItem = new Item(this, 0, getDb_helper());
      if (extras1 != null && extras1.getInt("category_id") != 0) {
         parentCategoryId = extras1.getInt("category_id");
      }
      if (parentCategoryId != -1) {
         attrCategorySelected =
               new Category(getApplicationContext(), parentCategoryId, getDb_helper());
         attrCategoryValue.setSelection(allCategories.indexOf(attrCategorySelected));
      } else {
         attrCategorySelected = null;
      }
      // Not a new item, but editing an existing item
      //      int parentCategoryId = -1;
      //      int savedColorId = -1;
      //      if (editItemId > 0) {
      //         if (getActionBar() != null) {
      //            getActionBar().setTitle(R.string.title_activity_edit_item);
      //         }
      //         getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
      //         editItem = new Item(this, editItemId, getDb_helper());
      //         parentCategoryId = editItem.getCategoryId();
      //         savedColorId = editItem.getPrimaryColorId();
      //      } else {
      //         if (getActionBar() != null) {
      //            getActionBar().setTitle(R.string.title_activity_new_item);
      //         }
      //         getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
      //         editItem = new Item(this, 0, getDb_helper());
      //         if (extras1 != null && extras1.getInt("category_id") != 0) {
      //            parentCategoryId = extras1.getInt("category_id");
      //         }
      //      }

      //      if (parentCategoryId != -1) {
      //         attrCategorySelected =
      //               new Category(getApplicationContext(), parentCategoryId, getDb_helper());
      //         attrCategoryValue.setSelection(allCategories.indexOf(attrCategorySelected));
      //      } else {
      //         attrCategorySelected = null;
      //      }
      //
      //      if (savedColorId != -1) {
      //         attrColorSelected =
      //               new com.gruppe1.pem.challengeme.Color(getApplicationContext(), savedColorId,
      //                     getDb_helper());
      //         attrColorValue.setSelection(allColors.indexOf(attrColorSelected));
      //      } else {
      //         attrColorSelected = null;
      //      }

      //      if (editItemId > 0) {
      //         setItemData();
      //      }

      Intent intentImage = getIntent();
      Bundle extrasImage = intentImage.getExtras();
      String action = intentImage.getAction();
      if (Intent.ACTION_SEND.equals(action)) {
         if (extrasImage.containsKey(Intent.EXTRA_STREAM)) {
            Uri uri = (Uri) extrasImage.getParcelable(Intent.EXTRA_STREAM);
            String filename = parseUriToFilename(uri);
            if (filename != null) {
               item_imageFile = filename;
               editItem.setImageFile(item_imageFile);
               Bitmap tmpBitmap = ImageLoader.getPicFromFile(editItem.getImageFile(), 500, 500);
               ImgPhoto.setImageBitmap(tmpBitmap);
               int exactColorId = ImageDominantColorExtractor.getInstance()
                     .getDominantColor(tmpBitmap);
               attrValueColorPicker.setExactColorId(exactColorId);
            }
         }
      }
   }

   private void setupToolbar() {
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
   }

   //   /**
   //    * sets the data of the editing item into the formular
   //    */
   //   private void setItemData() {
   //      itemNameExitText.setText(editItem.getName());
   //      ratingBar.setRating(editItem.getRating());
   //
   //      attrCategorySelected =
   //            new Category(getApplicationContext(), editItem.getCategoryId(), getDb_helper());
   //      attrCategoryValue.setText(attrCategorySelected.getName());
   //      attrColorSelected = new com.gruppe1.pem.challengeme.Color(getApplicationContext(),
   //            editItem.getPrimaryColorId(), getDb_helper());
   //      attrColorValue.setSelection(allColors.indexOf(attrColorSelected));
   //
   //      try {
   //         String imgPath = editItem.getImageFile();
   //         File imgFile = new File(imgPath);
   //         if (imgFile.exists()) {
   //            Bitmap tmpBitmap = ImageLoader.getPicFromFile(editItem.getImageFile(), 500, 500);
   //            ImgPhoto.setImageBitmap(tmpBitmap);
   //         }
   //      } catch (Exception e) {
   //         e.printStackTrace();
   //      }
   //      if (editItem.getIsWish() == 0) {
   //         attrWishlistValue.setChecked(false);
   //      } else {
   //         attrWishlistValue.setChecked(true);
   //      }
   //
   //      ArrayList<Attribute> itemAttributes = Attribute.getAttributesByItemId(this, editItem
   // .getId());
   //      for (Attribute tmpItemAttr : itemAttributes) {
   //         attributeTypesList.add(tmpItemAttr.getAttributeType());
   //         setAttributeLayout(tmpItemAttr.getAttributeType(), tmpItemAttr.getValue());
   //      }
   //   }

   /**
    * creates/updates the item
    */
   private void saveItem() {
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
               new Attribute(this, editItem.getId(), tmpItemAttrType.getId(), getDb_helper());
         itemAttribute.edit(itemAttributeValue);
         itemAttribute.save();
      }
      this.getDb_helper()
            .close();
   }

   /**
    * creates the layout for a attribute type with the atrribute value
    *
    * @param attributeType  AttributeType the attribute type
    * @param attributeValue Object the value of the attribute type
    */
   private void setAttributeLayout(AttributeType attributeType, Object attributeValue) {

      TextInputLayout attributeLayout = new TextInputLayout(this);
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
         LayoutInflater inflater = getLayoutInflater();

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
         LayoutInflater inflater = getLayoutInflater();

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
                     if (color != -1) {
                        attrExactColorIndicator.setBackgroundColor(color);
                     }
                  }
               });
         attrValueColorPicker.initialize(this, exactColorId);
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
         attrValueDatePicker = new DateEditText(this);
         ViewGroup.LayoutParams attibuteValueLayoutParams =
               new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                     ViewGroup.LayoutParams.WRAP_CONTENT);
         attrValueDatePicker.setLayoutParams(attibuteValueLayoutParams);
         attrValueDatePicker.setHint(attributeType.getName());

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
         attrValueDatePicker.initialize(this, buyDate);
         attributeValueView = attrValueDatePicker;
      } else {
         EditText textAttributeValue = new EditText(this);
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
      ArrayList<AttributeType> allAttributeTypes =
            AttributeType.getAttributeTypes(getApplicationContext());
      for (AttributeType tmpAttrType : allAttributeTypes) {
         if (!attributeTypesList.contains(tmpAttrType)) {
            attributeTypesList.add(tmpAttrType);
            setAttributeLayout(tmpAttrType, null);
         }
      }
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
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setItems(options, new DialogInterface.OnClickListener() {

         @Override

         public void onClick(DialogInterface dialog, int item) {

            if (options[item].equals(getString(R.string.item_take_photo))) {
               Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
               startActivityForResult(intent, 1);
            } else if (options[item].equals(getString(R.string.item_choose_gellery))) {

               if (ContextCompat.checkSelfPermission(NewItemActivity.this,
                     Manifest.permission.READ_EXTERNAL_STORAGE)
                     != PackageManager.PERMISSION_GRANTED) {
                  if (ActivityCompat.shouldShowRequestPermissionRationale(NewItemActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                  } else {
                     ActivityCompat.requestPermissions(NewItemActivity.this,
                           new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                           Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                  }
               } else {

                  if (Build.VERSION.SDK_INT <19){
                     Intent intent = new Intent();
                     intent.setType("image/jpeg");
                     intent.setAction(Intent.ACTION_GET_CONTENT);
                     startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.item_select_picture)),2);
                  } else {
                     Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                     intent.addCategory(Intent.CATEGORY_OPENABLE);
                     intent.setType("image/jpeg");
                     startActivityForResult(intent, 3);
                  }

               }
            } else if (options[item].equals(getString(R.string.cancel))) {
               dialog.dismiss();
            } else if (options[item].equals(getString(R.string.item_show_image_fullscreen))) {
               Intent i = new Intent(NewItemActivity.this, FullscreenImageActivity.class);
               i.putExtra("imageurl", item_imageFile);
               startActivity(i);
            }
         }
      });
      builder.show();
   }

   @Override
   public void onRequestPermissionsResult(int requestCode,
         String permissions[], int[] grantResults) {
      switch (requestCode) {
         case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               if (Build.VERSION.SDK_INT <19){
                  Intent intent = new Intent();
                  intent.setType("image/jpeg");
                  intent.setAction(Intent.ACTION_GET_CONTENT);
                  startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.item_select_picture)),2);
               } else {
                  Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                  intent.addCategory(Intent.CATEGORY_OPENABLE);
                  intent.setType("image/jpeg");
                  startActivityForResult(intent, 3);
               }
            } else {
               // permission denied, boo! Disable the
               // functionality that depends on this permission.
            }
            return;
         }
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      if (resultCode != Activity.RESULT_OK) return;
      if (data == null) return;
         if (requestCode == 1) {
            try {
               Bitmap photo = (Bitmap) data.getExtras()
                     .get("data");
               ImgPhoto.setImageBitmap(photo);
               int exactColorId = ImageDominantColorExtractor.getInstance()
                     .getDominantColor(photo);
               attrValueColorPicker.setExactColorId(exactColorId);

               // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
               Uri tempUri = getImageUri(getApplicationContext(), photo);

               // CALL THIS METHOD TO GET THE ACTUAL PATH
               File finalFile = new File(getRealPathFromURI(tempUri));
               item_imageFile = finalFile.getAbsolutePath();
               editItem.setImageFile(item_imageFile);
            } catch (Exception e) {
               e.printStackTrace();
            }
         } else if (requestCode == 2) {
            System.out.println(data.getData());
            Uri uri = data.getData();
            String path = ImageLoader.getPath(this, uri);

            System.out.println(path);
            item_imageFile = path;
            editItem.setImageFile(item_imageFile);

            Bitmap tmpBitmap = ImageLoader.getPicFromFile(editItem.getImageFile(), 500, 500);
            ImgPhoto.setImageBitmap(tmpBitmap);
            int exactColorId = ImageDominantColorExtractor.getInstance()
                  .getDominantColor(tmpBitmap);
            attrValueColorPicker.setExactColorId(exactColorId);

         } else if (requestCode == 3) {
            System.out.println(data.getData());
            Uri originalUri = data.getData();

            final int takeFlags = data.getFlags()
                  & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                  | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // Check for the freshest data.
            getContentResolver().takePersistableUriPermission(originalUri, takeFlags);

            String id = originalUri.getLastPathSegment().split(":")[1];
            final String[] imageColumns = {MediaStore.Images.Media.DATA };
            final String imageOrderBy = null;

            Uri uri = getUri();
            String selectedImagePath = "path";

            Cursor imageCursor = managedQuery(uri, imageColumns,
                  MediaStore.Images.Media._ID + "="+id, null, imageOrderBy);

            if (imageCursor.moveToFirst()) {
               selectedImagePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            Log.e("path",selectedImagePath );


            System.out.println(selectedImagePath);
            item_imageFile = selectedImagePath;
            editItem.setImageFile(item_imageFile);

            Bitmap tmpBitmap = ImageLoader.getPicFromFile(editItem.getImageFile(), 500, 500);
            ImgPhoto.setImageBitmap(tmpBitmap);
            int exactColorId = ImageDominantColorExtractor.getInstance()
                  .getDominantColor(tmpBitmap);
            attrValueColorPicker.setExactColorId(exactColorId);
         }

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

   private Uri getUri() {
      String state = Environment.getExternalStorageState();
      if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
         return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

      return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
   }

   /**
    * gets the actual path of a URI
    *
    * @param uri the Uri to which you want the path
    * @return the path to the uri
    */
   private String getRealPathFromURI(Uri uri) {
      Cursor cursor = getContentResolver().query(uri, null, null, null, null);
      cursor.moveToFirst();
      int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
      String idxString = cursor.getString(idx);
      cursor.close();
      return idxString;
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_new_item, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case android.R.id.home:
            super.onBackPressed();
            return true;

         case R.id.action_item_save:
            saveItem();
            // sending new Item back to CompareDetailView for actualizing the item there
            Intent i = new Intent();
            i.putExtra("itemId", editItem.getId());
            i.putExtra("itemImage", editItem.getImageFile());
            i.putExtra("itemName", editItem.getName());

            setResult(Activity.RESULT_OK, i);
            this.finish();
      }
      return super.onOptionsItemSelected(item);
   }

   /**
    * Get the db_helper instance for this class
    *
    * @return DataBaseHelper instance
    */
   private DataBaseHelper getDb_helper() {
      if (!db_helper.isOpen()) {
         System.out.println("db helper was closed");
         db_helper = new DataBaseHelper(this);
         db_helper.init();
      }
      return db_helper;
   }

   public String parseUriToFilename(Uri uri) {
      String selectedImagePath = null;
      String filemanagerPath = uri.getPath();

      String[] projection = { MediaStore.Images.Media.DATA };
      Cursor cursor = managedQuery(uri, projection, null, null, null);

      if (cursor != null) {
         // Here you will get a null pointer if cursor is null
         // This can be if you used OI file manager for picking the media
         int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
         cursor.moveToFirst();
         selectedImagePath = cursor.getString(column_index);
      }

      if (selectedImagePath != null) {
         return selectedImagePath;
      } else if (filemanagerPath != null) {
         return filemanagerPath;
      }
      return null;
   }
}