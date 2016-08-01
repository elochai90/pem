package com.gruppe1.pem.challengeme.views;

import android.Manifest;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.gruppe1.pem.challengeme.helpers.ColorHelper;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.DateEditText;
import com.gruppe1.pem.challengeme.helpers.ExactColorEditText;
import com.gruppe1.pem.challengeme.helpers.ImageDominantColorExtractor;
import com.gruppe1.pem.challengeme.helpers.ImageLoader;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bianka on 20.08.2015.
 */
// Instances of this class are fragments representing a single
// object in our collection.
public class CollectionItemsFragment extends Fragment {

   private DataBaseHelper db_helper;

   private CollectionItemsActivity activity;


   @Bind(R.id.itemDetailImage)
   ImageView imgPhoto;
   @Bind(R.id.ratingBar)
   RatingBar ratingBar;
   @Bind(R.id.itemName)
   TextInputEditText itemNameExitText;
   @Bind(R.id.itemDetailAttributes)
   LinearLayout attributesView;
   @Bind(R.id.attrCategoryValue)
   CategoryEditText attrCategoryValue;
   @Bind(R.id.attrColorValue)
   ColorEditText attrColorValue;
   @Bind(R.id.attrWishlistValue)
   Switch attrWishlistValue;
   @Bind(R.id.attrWishlistName)
   TextView attrWishlistName;
   @Bind(R.id.attrColorIndicator)
   View attrColorIndicator;
   @Bind(R.id.itemNameLayout)
   TextInputLayout itemNameLayout;
   @Bind(R.id.attrCategoryLayout)
   TextInputLayout attrCategoryLayout;


   private Category attrCategorySelected;
   private com.gruppe1.pem.challengeme.Color attrColorSelected;
   private String item_imageFile;

   private int editItemId = -1;
   private Item editItem;

   private ExactColorEditText attrValueColorPicker;
   private TextInputLayout attrLayoutColorPicker;

   private DateEditText attrValueDatePicker;

   private ArrayList<AttributeType> attributeTypesList;

   private SharedPreferences sharedPreferences;
   private PicassoSingleton picassoSingleton;

   private ArrayList<com.gruppe1.pem.challengeme.Color> allColors;
   private ArrayList<Category> allCategories;

   private String capturedImagePath;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {

      View rootView = inflater.inflate(R.layout.fragment_collection_items, container, false);
      ButterKnife.bind(this, rootView);
      activity = (CollectionItemsActivity) getActivity();

      sharedPreferences =
            getActivity().getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);

      picassoSingleton = PicassoSingleton.getInstance(activity);

      attributeTypesList = new ArrayList<>();


      db_helper = new DataBaseHelper(activity);
      db_helper.init();



      itemNameExitText.addTextChangedListener(new MyTextWatcher(itemNameExitText));
      attrCategoryValue.addTextChangedListener(new MyTextWatcher(attrCategoryValue));


      imgPhoto.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            selectImage();
         }
      });


      attrWishlistName.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            attrWishlistValue.setChecked(!attrWishlistValue.isChecked());
         }
      });

      allCategories = Category.getAllCategories(activity);

      allColors = com.gruppe1.pem.challengeme.Color.getAllColors(activity);
      int parentCategoryId = -1;

      Bundle extras = getArguments();
      if(extras != null) {
         if (savedInstanceState == null && editItemId == -1) {
            editItemId = extras.getInt(Constants.EXTRA_ITEM_ID);
         }
         if (extras.getBoolean(Constants.EXTRA_ITEM_IS_WISHLIST)) {
            attrWishlistValue.setChecked(true);
         }
         if (extras.getInt(Constants.EXTRA_CATEGORY_ID, -1) != -1) {
            parentCategoryId = extras.getInt(Constants.EXTRA_CATEGORY_ID);
         }
      }
      // Not a new item, but editing an existing item
      if (editItemId > 0) {
         getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
         editItem = new Item(activity, editItemId, getDb_helper());
         parentCategoryId = editItem.getCategoryId();

      } else {
         getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
         editItem = new Item(activity, 0, getDb_helper());
      }

      attrCategoryValue.setItems(getActivity(), getDb_helper(), allCategories);
      attrCategoryValue.setOnItemSelectedListener(new CategoryEditText.OnItemSelectedListener() {
         @Override
         public void onItemSelectedListener(Category item, int selectedIndex) {
            if (((TextView) attributesView.findViewWithTag("size")).getText()
                  .length() <= 0) {
               ((TextView) attributesView.findViewWithTag("size")).setText(
                     getSizeValueBySizeType(item.getDefaultSizeType()));
            }
            int categoryColorHex = Integer.parseInt(item.getColor(), 16) + 0xFF000000;
            attrValueColorPicker.setExactColorId(categoryColorHex);
            com.gruppe1.pem.challengeme.Color closestColor =
                  ColorHelper.getClosestColor(item.getColor(), allColors);
            attrColorValue.setSelection(allColors.indexOf(closestColor));
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

      if (editItem.getPrimaryColorId() > 0) {
         attrColorSelected =
               new com.gruppe1.pem.challengeme.Color(activity, editItem.getPrimaryColorId(), getDb_helper());
         attrColorValue.setSelection(allColors.indexOf(attrColorSelected));
      } else {
         attrColorSelected = null;
      }

      if (editItemId > 0) {
         setItemData();
         activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
               Date date = new Date();
               String timeStamp =
                     new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());

               File imagesFolder = new File(Environment.getExternalStorageDirectory().getPath(),
                     getString(R.string.app_name));
               imagesFolder.mkdirs();

               File image = new File(imagesFolder, "QR_image_" + timeStamp + ".jpg");
               Uri uriSavedImage = Uri.fromFile(image);

               capturedImagePath = uriSavedImage.getPath();

               intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
               startActivityForResult(intent, 1);
            } else if (options[item].equals(getString(R.string.item_choose_gellery))) {
               if (ContextCompat.checkSelfPermission(activity,
                     Manifest.permission.READ_EXTERNAL_STORAGE) !=
                     PackageManager.PERMISSION_GRANTED) {
                  if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                  } else {
                     ActivityCompat.requestPermissions(activity,
                           new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                           Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                  }
               } else {

                  if (Build.VERSION.SDK_INT < 19) {
                     Intent intent = new Intent();
                     intent.setType("image/jpeg");
                     intent.setAction(Intent.ACTION_GET_CONTENT);
                     startActivityForResult(Intent.createChooser(intent,
                           getResources().getString(R.string.item_select_picture)), 2);
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
               Intent i = new Intent(getActivity(), FullscreenImageActivity.class);
               i.putExtra("imageurl", item_imageFile);
               startActivity(i);
            }
         }
      });
      builder.show();
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, String permissions[],
         int[] grantResults) {
      switch (requestCode) {
         case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               if (Build.VERSION.SDK_INT < 19) {
                  Intent intent = new Intent();
                  intent.setType("image/jpeg");
                  intent.setAction(Intent.ACTION_GET_CONTENT);
                  startActivityForResult(Intent.createChooser(intent,
                        getResources().getString(R.string.item_select_picture)), 2);
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
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (resultCode != activity.RESULT_OK || data == null) {
         return;
      }
      if (requestCode == 1) {
         setImagePath(capturedImagePath);
      }
      if (requestCode == 2) {
         Uri uri = data.getData();
         String path = ImageLoader.getPath(activity, uri);
         setImagePath(path);
      } else if (requestCode == 3) {
         Uri originalUri = data.getData();

         final int takeFlags = data.getFlags() &
               (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
         // Check for the freshest data.
         activity.getContentResolver()
               .takePersistableUriPermission(originalUri, takeFlags);

         String id = originalUri.getLastPathSegment()
               .split(":")[1];
         final String[] imageColumns = { MediaStore.Images.Media.DATA };
         final String imageOrderBy = null;

         Uri uri = getUri();
         String selectedImagePath = "path";

         Cursor imageCursor =
               activity.managedQuery(uri, imageColumns, MediaStore.Images.Media._ID + "=" + id,
                     null, imageOrderBy);

         if (imageCursor.moveToFirst()) {
            selectedImagePath =
                  imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
         }
         Log.e("path", selectedImagePath);

         setImagePath(selectedImagePath);
      }
   }

   private Uri getUri() {
      String state = Environment.getExternalStorageState();
      if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
         return MediaStore.Images.Media.INTERNAL_CONTENT_URI;
      }

      return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
   }

   private void setAttributeLayoutData(AttributeType attributeType, Object attributeValue) {
      if (attributeValue != null) {
         LinearLayout layout = (LinearLayout) attributesView.findViewWithTag(attributeType.getId());
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
            String buyDate = attributeValue.toString();
            attrValueDatePicker.setDate(buyDate);
         } else {
            TextInputEditText textView = (TextInputEditText) layout.findViewById(R.id.stringAttrField);
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
      attributeLayoutParams.bottomMargin =
            getResources().getDimensionPixelSize(R.dimen.margin_small);
      attributeLayout.setLayoutParams(attributeLayoutParams);
      // Set the attributeTypeId for saving
      attributeLayout.setTag(attributeType.getId());

      View attributeValueView;
      // attribute is boolean
      if (attributeType.getValueType() == 2) {
         LayoutInflater inflater = getLayoutInflater(null);

         View switchLayout = inflater.inflate(R.layout.formular_layout_switch, null);
         final Switch switchValue = (Switch) switchLayout.findViewById(R.id.switchValue);
         final TextView switchLabel = (TextView) switchLayout.findViewById(R.id.switchLabel);

         switchLabel.setText(attributeType.getName());

         switchLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               switchValue.setChecked(!switchValue.isChecked());
            }
         });

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
         attrLayoutColorPicker = (TextInputLayout) exactColorLayout.findViewById(R.id.attrExactColorLayout);
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
                        com.gruppe1.pem.challengeme.Color closestColor =
                              ColorHelper.getClosestColor(color, allColors);
                        attrColorValue.setSelection(allColors.indexOf(closestColor));
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

         attrValueColorPicker.addTextChangedListener(new MyTextWatcher(attrValueColorPicker));
         attributeValueView = exactColorLayout;
      }
      // attribute is DatePicker
      else if (attributeType.getValueType() == 4) {
         attrValueDatePicker = new DateEditText(activity);
         ViewGroup.LayoutParams attibuteValueLayoutParams =
               new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                     ViewGroup.LayoutParams.WRAP_CONTENT);
         attrValueDatePicker.setLayoutParams(attibuteValueLayoutParams);
         attrValueDatePicker.setTextAppearance(activity, R.style.OrgaNice_Text_RobotoLight);
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
         TextInputEditText textAttributeValue = new TextInputEditText(activity);
         textAttributeValue.setMaxLines(1);
         textAttributeValue.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
         ViewGroup.LayoutParams attibuteValueLayoutParams =
               new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                     ViewGroup.LayoutParams.WRAP_CONTENT);
         textAttributeValue.setTextAppearance(activity, R.style.OrgaNice_Text_RobotoLight);
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

      if (editItem.getImageFile() != null) {
         String imgPath = editItem.getImageFile();
         item_imageFile = imgPath;

         picassoSingleton.setImageFit(imgPath, imgPhoto,
               activity.getDrawable(R.drawable.kleiderbuegel),
               activity.getDrawable(R.drawable.addcamera2));
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
   private void saveItem() {
      String item_name = itemNameExitText.getText()
            .toString();
      String item_categoryId = (attrCategoryValue.getSelectedItem() == null) ? "-1" :
            attrCategoryValue.getSelectedItem()
                  .getId() + "";
      String item_primaryColor = (attrColorValue.getSelectedItem() == null) ? "-1" :
            attrColorValue.getSelectedItem()
                  .getId() + "";
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
                  ((TextInputEditText) attributeView.findViewById(R.id.stringAttrField)).getText()
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
      Intent i = new Intent();
      i.putExtra(Constants.EXTRA_ITEM_NAME, item_name);
      i.putExtra(Constants.EXTRA_ITEM_ID, editItem.getId());
      i.putExtra(Constants.EXTRA_ITEM_FILE, editItem.getImageFile());
      i.putExtra(Constants.EXTRA_CATEGORY_ID, item_categoryId);
      i.putExtra(Constants.EXTRA_ITEM_PRIMARY_COLOR_ID, item_primaryColor);
      i.putExtra(Constants.EXTRA_ITEM_RATING, item_rating);
      i.putExtra(Constants.EXTRA_ITEM_IS_WISHLIST, item_isWish);
      activity.setResult(Activity.RESULT_OK, i);
      activity.finish();
   }

   public void setBitmap(Bitmap photo) {
      Uri tempUri = getImageUri(activity, photo);
      File finalFile = new File(getRealPathFromURI(tempUri));
      setImagePath(finalFile.getAbsolutePath());
      setExactColor(photo);
   }

   public void setImagePath(String path) {
      item_imageFile = path;
      editItem.setImageFile(item_imageFile);

      picassoSingleton.setImageFit(path, imgPhoto, activity.getDrawable(R.drawable.kleiderbuegel),
            activity.getDrawable(R.drawable.addcamera2));
      setExactColor(ImageLoader.getPicFromFile(path, 500, 500));
   }

   private void setExactColor(Bitmap bitmap) {
      int exactColorId = ImageDominantColorExtractor.getInstance()
            .getDominantColor(bitmap);
      attrValueColorPicker.setExactColorId(exactColorId);
   }

   /**
    * Validating form
    */
   public void submitForm() {
      if (!validateName() || !validateCategory() || !validateExactColor()) {
         return;
      }

      saveItem();
   }

   private boolean validateName() {
      if (itemNameExitText.getText()
            .toString()
            .trim()
            .isEmpty()) {
         // TODO: extract strings
         itemNameLayout.setError("Geben Sie einen Namen an");
         itemNameExitText.requestFocus();
         return false;
      } else {
         itemNameLayout.setErrorEnabled(false);
      }
      return true;
   }

   private boolean validateCategory() {
      if (attrCategoryValue.getSelectedItem() == null) {
         // TODO: extract strings
         attrCategoryLayout.setError("Geben Sie eine Kategorie an");
         attrCategoryValue.requestFocus();
         return false;
      } else {
         attrCategoryLayout.setErrorEnabled(false);
      }
      return true;
   }


   private boolean validateExactColor() {
      if (attrValueColorPicker.getExactColor() == -1) {
         // TODO: extract strings
         attrLayoutColorPicker.setError("Geben Sie die Farbe des Kleidungsstückes an");
         attrValueColorPicker.requestFocus();
         return false;
      } else {
         attrLayoutColorPicker.setErrorEnabled(false);
      }
      return true;
   }


   private class MyTextWatcher implements TextWatcher {

      private View view;

      private MyTextWatcher(View view) {
         this.view = view;
      }

      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      public void afterTextChanged(Editable editable) {
         switch (view.getId()) {
            case R.id.itemName:
               validateName();
               break;
            case R.id.attrCategoryValue:
               validateCategory();
               break;
            case R.id.attrExactColorValue:
               validateExactColor();
               break;
         }
      }
   }
}