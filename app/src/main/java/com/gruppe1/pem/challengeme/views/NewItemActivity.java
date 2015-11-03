package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Attribute;
import com.gruppe1.pem.challengeme.AttributeType;
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.HSVColorPickerDialog;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.CategoriesGridOverlayAdapter;
import com.gruppe1.pem.challengeme.adapters.ColorsGridOverlayAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.ImageDominantColorExtractor;
import com.gruppe1.pem.challengeme.helpers.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class NewItemActivity extends Activity {

    private DataBaseHelper db_helper;

    private ImageView ImgPhoto;
    private RatingBar ratingBar;
    private EditText itemNameExitText;
    private LinearLayout attributesView;

    private TextView attrCategoryName;
    private TextView attrCategoryValue;
    private Category attrCategorySelected;
    private TextView attrColorName;
    private TextView attrColorValue;
    private com.gruppe1.pem.challengeme.Color attrColorSelected;
    private TextView attrWishlistName;
    private Switch attrWishlistValue;
    private String item_imageFile;

    private int editItemId;
    private Item editItem;
    private int parentCategoryId;
    private int savedColorId;

    // Color Picker
    private int exactColorId;
    private TextView attrValueColorPicker;
    private String buyDate;
    private TextView attrValueDatePicker;

    private ArrayList<AttributeType> attributeTypesList;
    private CategoriesGridOverlayAdapter gridCateoriesAdapter;
    private ColorsGridOverlayAdapter gridColorsAdapter;

    private SharedPreferences sharedPreferences;

    private static final int DATE_DIALOG_ID = 999;


    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            buyDate = selectedDay + "." + (selectedMonth+1) + "." + selectedYear;
            attrValueDatePicker.setText(buyDate);
            attrValueDatePicker.setTextColor(getResources().getColor(android.R.color.black));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_collection_items);
        getActionBar().setTitle(R.string.title_activity_new_item);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.gray02), PorterDuff.Mode.SRC_ATOP);

        attributeTypesList = new ArrayList<>();
        itemNameExitText = (EditText) findViewById(R.id.itemName);
        attributesView = (LinearLayout) findViewById(R.id.itemDetailAttributes);
        attrCategoryName = (TextView) findViewById(R.id.attrCategoryName);
        attrCategoryValue = (TextView) findViewById(R.id.attrCategoryValue);
        attrColorName = (TextView) findViewById(R.id.attrColorName);
        attrColorValue = (TextView) findViewById(R.id.attrColorValue);
        attrWishlistName = (TextView) findViewById(R.id.attrWishlistName);
        attrWishlistValue = (Switch) findViewById(R.id.attrWishlistValue);

        attrCategoryName.setText(getString(R.string.item_cateory_label));
        attrColorName.setText(getString(R.string.item_color_label));
        attrWishlistName.setText(getString(R.string.item_wishlist_label));

        // Setup Categories Adapter
        ArrayList<Category> allCategories = Category.getAllCategories(this);
        ArrayList<ListItemIconName> catArray = new ArrayList<>();
        for (Category tmpCat : allCategories) {
            int iconId = getResources().getIdentifier(tmpCat.getIcon(), "drawable", "com.gruppe1.pem.challengeme");
            catArray.add(new ListItemIconName(tmpCat.getId(), iconId, tmpCat.getName(), null));
        }
        gridCateoriesAdapter = new CategoriesGridOverlayAdapter(this, R.layout.grid_item_overlay, catArray);
        attrCategoryValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupCategoryOverlay();
            }
        });

        // Setup Colors Adapter
        ArrayList<com.gruppe1.pem.challengeme.Color> allColors = com.gruppe1.pem.challengeme.Color.getAllColors(this);
        gridColorsAdapter = new ColorsGridOverlayAdapter(this, R.layout.grid_item_overlay, allColors);



        attrCategoryValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupCategoryOverlay();
            }
        });

        attrColorValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupColorsOverlay();
            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                editItemId = -1;
            } else {
                editItemId = extras.getInt(Constants.EXTRA_ITEM_ID);
            }
        } else {
            editItemId = -1;
        }

        Bundle extras1 = getIntent().getExtras();
        if (extras1 != null &&  extras1.getBoolean("is_wishlist")) {
            attrWishlistValue.setChecked(true);
        }
        // Not a new item, but editing an existing item
        if(editItemId > 0) {
            if(getActionBar() != null) {
                getActionBar().setTitle(R.string.title_activity_edit_item);
            }
            getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
            editItem = new Item(this, editItemId, getDb_helper());
            parentCategoryId = editItem.getCategoryId();
            savedColorId = editItem.getPrimaryColorId();

        } else {
            if(getActionBar() != null) {
                getActionBar().setTitle(R.string.title_activity_new_item);
            }
            getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
            editItem = new Item(this, 0, getDb_helper());

            savedColorId = -1;

            if (extras1 != null && extras1.getInt("category_id") != 0) {
                parentCategoryId = extras1.getInt("category_id");
            } else {
                parentCategoryId = -1;
            }
        }

        if(parentCategoryId != -1) {
            attrCategorySelected = new Category(getApplicationContext(), parentCategoryId, getDb_helper());
            attrCategoryValue.setText(attrCategorySelected.getName());
        } else {
            attrCategorySelected = new Category(getApplicationContext(), gridCateoriesAdapter.getItem(0).getElementId(), getDb_helper());
            attrCategoryValue.setText(attrCategorySelected.getName());
        }

        if(savedColorId != -1) {
            attrColorSelected = new com.gruppe1.pem.challengeme.Color(getApplicationContext(), savedColorId, getDb_helper());
            attrColorValue.setText(attrColorSelected.getName());
            attrColorValue.setBackgroundColor(Color.parseColor(attrColorSelected.getHexColor()));
            if(!gridColorsAdapter.isColorLight(Color.parseColor(attrColorSelected.getHexColor()))) {
                attrColorValue.setTextColor(getResources().getColor(android.R.color.white));
            }
        } else {
            attrColorSelected = new com.gruppe1.pem.challengeme.Color(getApplicationContext(), gridColorsAdapter.getItem(0).getId(), getDb_helper());
            attrColorValue.setText(attrColorSelected.getName());
            attrColorValue.setBackgroundColor(Color.parseColor(attrColorSelected.getHexColor()));
            if(!gridColorsAdapter.isColorLight(Color.parseColor(attrColorSelected.getHexColor()))) {
                attrColorValue.setTextColor(getResources().getColor(android.R.color.white));
            }
        }

        if(editItemId > 0) {
            setItemData();
        }
        setupAttributeViews();


        Intent intentImage = getIntent();
        Bundle extrasImage = intentImage.getExtras();
        String action = intentImage.getAction();
        if(Intent.ACTION_SEND.equals(action)) {
            if(extrasImage.containsKey(Intent.EXTRA_STREAM)) {
                Uri uri = (Uri) extrasImage.getParcelable(Intent.EXTRA_STREAM);
                String filename = parseUriToFilename(uri);
                if(filename != null) {
                    item_imageFile = filename;
                    editItem.setImageFile(item_imageFile);
                    Bitmap tmpBitmap = ImageLoader.getPicFromFile(editItem.getImageFile(), 500, 500);
                    ImgPhoto.setImageBitmap(tmpBitmap);
                    exactColorId = ImageDominantColorExtractor.getInstance().getDominantColor(tmpBitmap);
                    attrValueColorPicker.setBackgroundColor(exactColorId);

                }
            }
        }

    }

    /**
     * sets the data of the editing item into the formular
     */
    private void setItemData() {
        itemNameExitText.setText(editItem.getName());
        ratingBar.setRating(editItem.getRating());

        attrCategorySelected = new Category(getApplicationContext(), editItem.getCategoryId(), getDb_helper());
        attrCategoryValue.setText(attrCategorySelected.getName());
        attrColorSelected = new com.gruppe1.pem.challengeme.Color(getApplicationContext(), editItem.getPrimaryColorId(), getDb_helper());
        attrColorValue.setBackgroundColor(Color.parseColor(attrColorSelected.getHexColor()));

        try {
            String imgPath = editItem.getImageFile();
            File imgFile = new File(imgPath);
            if (imgFile.exists()) {
                Bitmap tmpBitmap = ImageLoader.getPicFromFile(editItem.getImageFile(), 500, 500);
                ImgPhoto.setImageBitmap(tmpBitmap);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if(editItem.getIsWish() == 0) {
            attrWishlistValue.setChecked(false);
        } else {
            attrWishlistValue.setChecked(true);
        }

        ArrayList<Attribute> itemAttributes = Attribute.getAttributesByItemId(this, editItem.getId());
        for (Attribute tmpItemAttr : itemAttributes) {
            attributeTypesList.add(tmpItemAttr.getAttributeType());
            setAttributeLayout(tmpItemAttr.getAttributeType(), tmpItemAttr.getValue());
        }
    }

    /**
     * creates/updates the item
     */
    private void saveItem() {
        String item_name = itemNameExitText.getText().toString();
//        Category newParentCatId = (attrCategorySelected != null) ? attrCategorySelected : new Category(getApplicationContext(), (int) gridCateoriesAdapter.getItem(0).elementId,getDb_helper());
        String item_categoryId = "" + attrCategorySelected.getId();
        String item_primaryColor = "" + attrColorSelected.getId();
        String item_rating = Float.toString(ratingBar.getRating());
        String item_isWish = attrWishlistValue.isChecked()  ? "1" : "0";

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
            LinearLayout attributeView = (LinearLayout) attributesView.findViewWithTag(tmpItemAttrType.getId());
            String attributeSaveValue;
            // boolean
            if (tmpItemAttrType.getValueType() == 2) {
                attributeSaveValue = ((Switch) attributeView.findViewById(R.id.boolAttrField)).isChecked() ? "1" : "0";
            }
            // Color Picker
            else if (tmpItemAttrType.getValueType() == 3) {
                attributeSaveValue = exactColorId + "";
            }
            // Date Picker
            else if (tmpItemAttrType.getValueType() == 4) {
                attributeSaveValue = buyDate;
            } else {
                attributeSaveValue = ((EditText) attributeView.findViewById(R.id.stringAttrField)).getText().toString();
            }
            HashMap<String, String> itemAttributeValue = new HashMap<>();
            itemAttributeValue.put("item_id", editItem.getId() + "");
            itemAttributeValue.put("attribute_type_id", tmpItemAttrType.getId() + "");
            itemAttributeValue.put("attribute_value", attributeSaveValue);

            getDb_helper().setTable(Constants.ITEM_ATTR_DB_TABLE);
            Attribute itemAttribute = new Attribute(this, editItem.getId(), tmpItemAttrType.getId(), getDb_helper());
            itemAttribute.edit(itemAttributeValue);
            itemAttribute.save();
        }
        this.getDb_helper().close();
    }

    /**
     * creates the layout for a attribute type with the atrribute value
     * @param attributeType AttributeType the attribute type
     * @param attributeValue Object the value of the attribute type
     */
    private void setAttributeLayout(AttributeType attributeType, Object attributeValue) {

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        int with_background_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

        LinearLayout attributeLayout = new LinearLayout(this);
        attributeLayout.setOrientation(LinearLayout.HORIZONTAL);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        attributeLayout.setPadding(padding, 0, padding, 0);
        LinearLayout.LayoutParams attributeLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        attributeLayout.setLayoutParams(attributeLayoutParams);
        // Set the attributeTypeId for saving
        attributeLayout.setTag(attributeType.getId());

        TextView attributeName = new TextView(this);
        attributeName.setTextSize(18);
        ViewGroup.LayoutParams attibuteNameLayoutParams = new ViewGroup.LayoutParams(width, height);
        attributeName.setLayoutParams(attibuteNameLayoutParams);
        attributeName.setGravity(Gravity.CENTER_VERTICAL);
        attributeName.setText(attributeType.getName() + ":");

        View attributeValueView;
        // attribute is boolean
        if(attributeType.getValueType() == 2) {
            Switch attrValueSwitch = new Switch(this);
            ViewGroup.LayoutParams attibuteValueLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
            attrValueSwitch.setLayoutParams(attibuteValueLayoutParams);
            attrValueSwitch.setShowText(true);
            attrValueSwitch.setTextOn(getResources().getString(R.string.switch_true));
            attrValueSwitch.setTextOff(getResources().getString(R.string.switch_false));
            attrValueSwitch.setId(R.id.boolAttrField);


            if(attributeValue != null) {
                boolean bool = (attributeValue.toString().equals("1"));
                attrValueSwitch.setChecked(bool);
            }

            attributeValueView = attrValueSwitch;

        }
        // attribute is ColorPicker
        else if(attributeType.getValueType() == 3) {
            attrValueColorPicker = new TextView(this);
            ViewGroup.LayoutParams attibuteValueLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, with_background_height);
            attrValueColorPicker.setLayoutParams(attibuteValueLayoutParams);
            attrValueColorPicker.setTextColor(getResources().getColor(android.R.color.white));
            attrValueColorPicker.setTextSize(18);
            attrValueColorPicker.setGravity(Gravity.CENTER);

            if(attributeValue != null) {
                exactColorId = Integer.parseInt(attributeValue.toString());
            } else {
                attrValueColorPicker.setText(R.string.no_exact_color_selected);
                exactColorId = getResources().getColor(R.color.gray01);
            }
            attrValueColorPicker.setBackgroundColor(exactColorId);
            // 0xFF4488CC
            attrValueColorPicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new HSVColorPickerDialog(NewItemActivity.this, exactColorId, new HSVColorPickerDialog.OnColorSelectedListener() {
                        @Override
                        public void colorSelected(Integer color) {
                            // Do something with the selected color
                            exactColorId = color;
                            attrValueColorPicker.setBackgroundColor(exactColorId);
                            attrValueColorPicker.setText("");
                        }
                    }).show();
                }
            });
            attributeValueView = attrValueColorPicker;
        }
        // attribute is DatePicker
        else if(attributeType.getValueType() == 4) {
            attrValueDatePicker = new TextView(this);
            ViewGroup.LayoutParams attibuteValueLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, with_background_height);
            attrValueDatePicker.setLayoutParams(attibuteValueLayoutParams);
            attrValueDatePicker.setTextSize(18);
            attrValueDatePicker.setGravity(Gravity.CENTER);

            if(attributeValue != null) {
                attrValueDatePicker.setTextColor(getResources().getColor(android.R.color.black));
                buyDate = attributeValue.toString();
                attrValueDatePicker.setText(buyDate);
            } else {
                attrValueDatePicker.setTextColor(getResources().getColor(android.R.color.white));
                attrValueDatePicker.setText(R.string.no_buy_date_selected);
                buyDate = "";
            }


            attrValueDatePicker.setBackgroundColor(getResources().getColor(R.color.gray01));
            attrValueDatePicker.setText(buyDate);
            attrValueDatePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DATE_DIALOG_ID);
                }
            });
            attributeValueView = attrValueDatePicker;
        }
        else {
            EditText textAttributeValue = new EditText(this);
            textAttributeValue.setSingleLine(true);
            textAttributeValue.setTextSize(18);
            ViewGroup.LayoutParams attibuteValueLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            textAttributeValue.setLayoutParams(attibuteValueLayoutParams);
            textAttributeValue.setId(R.id.stringAttrField);

            if(attributeType.getName().equals(getString(R.string.attr_type_size_en)) || attributeType.getName().equals(getResources().getString(R.string.attr_type_size_de))) {
                textAttributeValue.setTag("size");
            }
            if(attributeValue != null) {
                textAttributeValue.setText(attributeValue.toString());
            } else if(attributeType.getName().equals(getString(R.string.attr_type_size_en)) || attributeType.getName().equals(getResources().getString(R.string.attr_type_size_de))) {
                textAttributeValue.setText(getSizeValueBySizeType(attrCategorySelected.getDefaultSizeType()));
            }

            attributeValueView = textAttributeValue;
        }
        attributeLayout.addView(attributeName);
        attributeLayout.addView(attributeValueView);

        attributesView.addView(attributeLayout, attributesView.getChildCount());
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                if(buyDate.length()>0) {
                    String[] parts = buyDate.split("\\.");
                    day = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1])-1;
                    year = Integer.parseInt(parts[2]);
                }
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }

    /**
     * get the size value to a size type
     * @param sizeType int the size type
     * @return the size value
     */
    private String getSizeValueBySizeType(int sizeType) {
        String sizeValue;
        switch(sizeType) {
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
        ArrayList<AttributeType> allAttributeTypes = AttributeType.getAttributeTypes(getApplicationContext());
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
        if(item_imageFile != null) {
            options = new CharSequence[] {getString(R.string.item_show_image_fullscreen), getString(R.string.item_take_photo), getString(R.string.item_choose_gellery), getString(R.string.cancel)};

        } else {
            options = new CharSequence[] {getString(R.string.item_take_photo), getString(R.string.item_choose_gellery), getString(R.string.cancel)};
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(getString(R.string.item_take_photo))){
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals(getString(R.string.item_choose_gellery))) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                try {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ImgPhoto.setImageBitmap(photo);
                    exactColorId = ImageDominantColorExtractor.getInstance().getDominantColor(photo);
                    attrValueColorPicker.setBackgroundColor(exactColorId);

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
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();

                item_imageFile = picturePath;
                editItem.setImageFile(item_imageFile);

                Bitmap tmpBitmap = ImageLoader.getPicFromFile(editItem.getImageFile(), 500, 500);
                ImgPhoto.setImageBitmap(tmpBitmap);
                exactColorId = ImageDominantColorExtractor.getInstance().getDominantColor(tmpBitmap);
                attrValueColorPicker.setBackgroundColor(exactColorId);

            }
        }
    }

    /**
     * gets the URI from a bitmap
     * @param inContext the context
     * @param inImage the bitmap image
     * @return the Uri to the image
     */
    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * gets the actual path of a URI
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
                i.putExtra("itemName",editItem.getName());

                setResult(Activity.RESULT_OK, i);
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * creates and shows the overlay to choose a category
     */
    private void setupCategoryOverlay() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_grid, null);
        TextView headline = (TextView)dialogView.findViewById(R.id.dialog_headline);
        headline.setText(getString(R.string.item_select_category_overlay_title));
        GridView gridView = (GridView) dialogView.findViewById(R.id.gridView);

        gridView.setAdapter(gridCateoriesAdapter);

        builder.setView(dialogView);
        final AlertDialog alert = builder.create();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                getDb_helper().setTable(Constants.CATEGORIES_DB_TABLE);
                attrCategorySelected = new Category(getBaseContext(), gridCateoriesAdapter.getItem(position).getElementId(), getDb_helper());
                attrCategoryValue.setText(gridCateoriesAdapter.getItem(position).getName());
                ((TextView)attributesView.findViewWithTag("size")).setText(getSizeValueBySizeType(attrCategorySelected.getDefaultSizeType()));
                alert.dismiss();
            }
        });
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert.show();
    }


    /**
     * creates and shows the overlay to choose a color
     */
    private void setupColorsOverlay() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_grid, null);
        TextView headline = (TextView)dialogView.findViewById(R.id.dialog_headline);
        headline.setText(getString(R.string.item_select_color_overlay_title));
        GridView gridView = (GridView) dialogView.findViewById(R.id.gridView);

        gridView.setAdapter(gridColorsAdapter);

        builder.setView(dialogView);
        final AlertDialog alert = builder.create();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                getDb_helper().setTable(Constants.CATEGORIES_DB_TABLE);
                attrColorSelected = new com.gruppe1.pem.challengeme.Color(getBaseContext(), gridColorsAdapter.getItem(position).getId(), getDb_helper());
                attrColorValue.setText(attrColorSelected.getName());
                attrColorValue.setBackgroundColor(Color.parseColor(attrColorSelected.getHexColor()));
                if(!gridColorsAdapter.isColorLight(Color.parseColor(attrColorSelected.getHexColor()))) {
                    attrColorValue.setTextColor(getResources().getColor(android.R.color.white));
                }
                alert.dismiss();
            }
        });
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    /**
     * Get the db_helper instance for this class
     * @return DataBaseHelper instance
     */
    private DataBaseHelper getDb_helper() {
        if(!db_helper.isOpen()) {
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
        }
        else if (filemanagerPath != null) {
            return filemanagerPath;
        }
        return null;
    }
}