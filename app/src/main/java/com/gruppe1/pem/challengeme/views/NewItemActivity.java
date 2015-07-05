package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.gruppe1.pem.challengeme.Attribute;
import com.gruppe1.pem.challengeme.AttributeType;
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.HSVColorPickerDialog;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.ImageLoader;
import com.gruppe1.pem.challengeme.adapters.CategoriesDropdownAdapter;
import com.gruppe1.pem.challengeme.adapters.ColorsDropdownAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class NewItemActivity extends Activity {

    private DataBaseHelper db_helper;

    private ImageView ImgPhoto;
    private RatingBar ratingBar;
    private EditText itemNameExitText;
    private LinearLayout attributesView;

    private TextView attrCategoryName;
    private Spinner attrCategoryValue;
    private Category attrCategorySelected;
    private TextView attrColorName;
    private Spinner attrColorValue;
    private com.gruppe1.pem.challengeme.Color attrColorSelected;
    private TextView attrWishlistName;
    private Switch attrWishlistValue;
    String item_imageFile;

    private int editItemId;
    private Item editItem;
    private int parentCategoryId;
    private int savedColorId;

    // Color Picker
    private int exactColorId;
    private TextView attrValueColorPicker;

    private boolean isEdit;

    private ArrayList<AttributeType> attributeTypesList;
    private ArrayList<Category> allCategories;
    private CategoriesDropdownAdapter categoriesDropdownAdapter;
    private ArrayList<com.gruppe1.pem.challengeme.Color> allColors;
    private ColorsDropdownAdapter colorsDropdownAdapter;

    private Bundle extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);


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
        stars.getDrawable(2).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        final Activity thisActivity = this;
        FloatingActionButton saveItemFAB = (FloatingActionButton) findViewById(R.id.save_item);
        saveItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
                setResult(RESULT_OK);
                thisActivity.finish();
            }
        });

        attributeTypesList = new ArrayList<AttributeType>();
        itemNameExitText = (EditText) findViewById(R.id.itemName);

        attributesView = (LinearLayout) findViewById(R.id.itemDetailAttributes);

        attrCategoryName = (TextView) findViewById(R.id.attrCategoryName);
        attrCategoryValue = (Spinner) findViewById(R.id.attrCategoryValue);
        attrColorName = (TextView) findViewById(R.id.attrColorName);
        attrColorValue = (Spinner) findViewById(R.id.attrColorValue);
        attrWishlistName = (TextView) findViewById(R.id.attrWishlistName);
        attrWishlistValue = (Switch) findViewById(R.id.attrWishlistValue);

        attrCategoryName.setText("Category:");
        attrColorName.setText("Color:");
        attrWishlistName.setText("In Wishlist:");


        extras = getIntent().getExtras();

        allCategories = Category.getAllCategories(this);
        categoriesDropdownAdapter = new CategoriesDropdownAdapter (getBaseContext(), android.R.layout.simple_spinner_item, allCategories);
        allColors = com.gruppe1.pem.challengeme.Color.getAllColors(this);
        colorsDropdownAdapter = new ColorsDropdownAdapter(getBaseContext(), android.R.layout.simple_spinner_item, allColors);



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


        if (extras != null &&  extras.getBoolean("is_wishlist")) {
            attrWishlistValue.setChecked(true);
        }
        // Not a new item, but editing an existing item
        if(editItemId > 0) {
            isEdit = true;
            getActionBar().setTitle(R.string.title_activity_edit_item);
            getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
            editItem = new Item(this, editItemId, getDb_helper());
            parentCategoryId = editItem.getCategoryId();
            savedColorId = editItem.getPrimaryColorId();

        } else {
            isEdit = false;
            getActionBar().setTitle(R.string.title_activity_new_item);
            getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
            editItem = new Item(this, 0, getDb_helper());

            savedColorId = -1;

            if (extras != null && extras.getInt("category_id") != 0) {
                parentCategoryId = extras.getInt("category_id");
            } else {
                parentCategoryId = -1;
            }
        }

        setupCategoryDropdown();
        setupColorsDropdown();

        if(editItemId > 0) {
            setItemData();
        }
        setupAttributeViews();
    }

    private void setItemData() {
        // TODO: set Item Data from DB
        itemNameExitText.setText(editItem.getName());

        ratingBar.setRating(editItem.getRating());

        attrCategoryValue.setSelection(((CategoriesDropdownAdapter) attrCategoryValue.getAdapter()).findPositionOfCategoryId(editItem.getCategoryId()));
        attrColorValue.setSelection(((ColorsDropdownAdapter) attrColorValue.getAdapter()).findPositionOfColorId(editItem.getPrimaryColorId()));

//        attrColorValue.setText(editItem.getPrimaryColor());

        try {
            String imgPath = editItem.getImageFile();
            Log.d("ImageFile", imgPath);
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
        Iterator allItemAttributesIterator = itemAttributes.iterator();

        // TODO: get all attributes with values for this Item
        while (allItemAttributesIterator.hasNext()) {
            Attribute tmpItemAttr = (Attribute) allItemAttributesIterator.next();
            attributeTypesList.add(tmpItemAttr.getAttributeType());
            setAttributeLayout(tmpItemAttr.getAttributeType(), tmpItemAttr.getValue());
        }
    }

    private void saveItem() {

        String item_name = itemNameExitText.getText().toString();

        Category newParentCatId = (attrCategorySelected != null) ? attrCategorySelected : new Category(getApplicationContext(), (int) categoriesDropdownAdapter.getItemId(0),getDb_helper());

        String item_categoryId = "" + attrCategorySelected.getId();

        String item_primaryColor = "" + attrColorSelected.getId();

        String item_rating = Float.toString(ratingBar.getRating());
        String item_isWish = attrWishlistValue.isChecked()  ? "1" : "0";

        HashMap<String, String> itemAttributes = new HashMap<String, String>();
        itemAttributes.put("name", item_name);
        itemAttributes.put("image_file", editItem.getImageFile());
        itemAttributes.put("category_id", item_categoryId);
        itemAttributes.put("primary_color", item_primaryColor);
        itemAttributes.put("rating", item_rating);
        itemAttributes.put("is_wish", item_isWish);

        getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
        editItem.edit(itemAttributes);
        editItem.save();

        Iterator allItemAttributesIterator = attributeTypesList.iterator();


        // TODO: get all attributes with values for this Item
        while (allItemAttributesIterator.hasNext()) {
            AttributeType tmpItemAttrType = (AttributeType) allItemAttributesIterator.next();

            LinearLayout attributeView = (LinearLayout) attributesView.findViewWithTag(tmpItemAttrType.getId());
            String attributeSaveValue = "";
            // boolean
            if(tmpItemAttrType.getValueType() == 2) {
                attributeSaveValue  = ((Switch) attributeView.findViewById(R.id.boolAttrField)).isChecked() ? "1" : "0";
            }
            // Color Picker
            else if (tmpItemAttrType.getValueType() == 3) {
                attributeSaveValue  = exactColorId + "";
            }
            else {
                attributeSaveValue = ((EditText) attributeView.findViewById(R.id.stringAttrField)).getText().toString();
            }
            HashMap<String, String> itemAttributeValue = new HashMap<String, String>();
            itemAttributeValue.put("item_id",editItem.getId() + "");
            itemAttributeValue.put("attribute_type_id",tmpItemAttrType.getId() + "");
            itemAttributeValue.put("attribute_value",attributeSaveValue);

            getDb_helper().setTable(Constants.ITEM_ATTR_DB_TABLE);
            Attribute itemAttribute = new Attribute(this, editItem.getId(), tmpItemAttrType.getId(), getDb_helper());
            itemAttribute.edit(itemAttributeValue);
            itemAttribute.save();
        }

        this.db_helper.close();

    }

    private void setAttributeLayout(AttributeType attributeType, Object attributeValue) {

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
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        ViewGroup.LayoutParams attibuteNameLayoutParams = new ViewGroup.LayoutParams(width, height);
        attributeName.setLayoutParams(attibuteNameLayoutParams);
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
                boolean bool = Boolean.parseBoolean(attributeValue.toString());
                attrValueSwitch.setChecked(bool);
            }
            attributeValueView = attrValueSwitch;

        }
        // attribute is ColorPicker
        else if(attributeType.getValueType() == 3) {
            attrValueColorPicker = new TextView(this);
            ViewGroup.LayoutParams attibuteValueLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            attrValueColorPicker.setLayoutParams(attibuteValueLayoutParams);
            attrValueColorPicker.setTextColor(getResources().getColor(android.R.color.white));
            attrValueColorPicker.setTextSize(18);
            attrValueColorPicker.setGravity(Gravity.CENTER);

            if(attributeValue != null) {
                exactColorId = Integer.parseInt(attributeValue.toString());
            } else {
                attrValueColorPicker.setText(R.string.no_exact_color_selected);
                exactColorId = getResources().getColor(R.color.color_picker_initial);
            }
            attrValueColorPicker.setBackgroundColor(exactColorId);
            // 0xFF4488CC
            attrValueColorPicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final HSVColorPickerDialog cpd = new HSVColorPickerDialog( NewItemActivity.this, exactColorId, new HSVColorPickerDialog.OnColorSelectedListener() {
                        @Override
                        public void colorSelected(Integer color) {
                            // Do something with the selected color
                            exactColorId = color;
                            attrValueColorPicker.setBackgroundColor(exactColorId);
                            attrValueColorPicker.setText("");
                        }
                    });
                    cpd.setTitle( "Pick the exact color" );
                    cpd.show();
                }
            });
            attributeValueView = attrValueColorPicker;
        }
        else {
            EditText textAttributeValue = new EditText(this);
            textAttributeValue.setSingleLine(true);
            textAttributeValue.setTextSize(18);
            ViewGroup.LayoutParams attibuteValueLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
//              if(value is set) TODO: check if value was already set / if edit or new
            textAttributeValue.setLayoutParams(attibuteValueLayoutParams);
            textAttributeValue.setId(R.id.stringAttrField);

            if(attributeValue != null) {
                textAttributeValue.setText(attributeValue.toString());
            }

            attributeValueView = textAttributeValue;
        }
        attributeLayout.addView(attributeName);
        attributeLayout.addView(attributeValueView);

        attributesView.addView(attributeLayout, attributesView.getChildCount());
    }



    private void setupAttributeViews() {
        ArrayList<AttributeType> allAttributeTypes = AttributeType.getAttributeTypes(getApplicationContext());
        Iterator allAttrTypesIterator =  allAttributeTypes.iterator();
        // TODO: get all attributes with values for this Item
        while(allAttrTypesIterator.hasNext()) {
            AttributeType tmpAttrType = (AttributeType) allAttrTypesIterator.next();
            if(!attributeTypesList.contains(tmpAttrType)) {
                attributeTypesList.add(tmpAttrType);
                setAttributeLayout(tmpAttrType, null);
            }
        }
    }



    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")){
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
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

                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    Uri tempUri = getImageUri(getApplicationContext(), photo);

                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    File finalFile = new File(getRealPathFromURI(tempUri));
                    Log.d("path: ", finalFile.getAbsolutePath());
                    item_imageFile = finalFile.getAbsolutePath();
                    editItem.setImageFile(item_imageFile);


                } catch (Exception e) {
                    e.printStackTrace();
                }



            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();

                String[] filePath = {MediaStore.Images.Media.DATA};
                Log.d("path: ", filePath[0]);
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);

                c.moveToFirst();

                int columnIndex = c.getColumnIndex(filePath[0]);

                String picturePath = c.getString(columnIndex);

                c.close();

                item_imageFile = picturePath;
                editItem.setImageFile(item_imageFile);
                Log.w("path of image", item_imageFile + "");

                Bitmap tmpBitmap = ImageLoader.getPicFromFile(editItem.getImageFile(), 500, 500);
                ImgPhoto.setImageBitmap(tmpBitmap);
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
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
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupCategoryDropdown() {
        // Specify the layout to use when the list of choices appears
        categoriesDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        attrCategoryValue.setAdapter(categoriesDropdownAdapter);

        if(parentCategoryId != -1) {
            attrCategorySelected = new Category(getApplicationContext(), parentCategoryId, getDb_helper());
            int activeIndex = this.getIndex(attrCategoryValue, parentCategoryId);
            attrCategoryValue.setSelection(activeIndex);
        } else {
            attrCategorySelected = new Category(getApplicationContext(), (int)categoriesDropdownAdapter.getItemId(0), getDb_helper());
        }

        categoriesDropdownAdapter.notifyDataSetChanged();
        attrCategoryValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    getDb_helper().setTable(Constants.CATEGORIES_DB_TABLE);
                    attrCategorySelected = new Category(getBaseContext(), (int) categoriesDropdownAdapter.getItemId(position), getDb_helper());
                    attrCategoryValue.setSelection(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }



    private void setupColorsDropdown() {
        // Specify the layout to use when the list of choices appears
        colorsDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        attrColorValue.setAdapter(colorsDropdownAdapter);

        if(savedColorId != -1) {
            attrColorSelected = new com.gruppe1.pem.challengeme.Color(getApplicationContext(), savedColorId, getDb_helper());
            int activeIndex = this.getIndex(attrColorValue, savedColorId);
            attrColorValue.setSelection(activeIndex);
        } else {
            attrColorSelected = new com.gruppe1.pem.challengeme.Color(getApplicationContext(), (int)colorsDropdownAdapter.getItemId(0), getDb_helper());
        }

        colorsDropdownAdapter.notifyDataSetChanged();
        attrColorValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    getDb_helper().setTable(Constants.COLORS_DB_TABLE);
                    attrColorSelected = new com.gruppe1.pem.challengeme.Color(getBaseContext(), (int) colorsDropdownAdapter.getItemId(position), getDb_helper());
                    attrColorValue.setSelection(position);
                    attrColorValue.setBackgroundColor(Color.parseColor(colorsDropdownAdapter.getColorAtPosition(position).getHexColor()));
                    if(!colorsDropdownAdapter.isColorLight(Color.parseColor(colorsDropdownAdapter.getColorAtPosition(position).getHexColor()))) {
                        ((TextView) attrColorValue.findViewById(android.R.id.text1)).setTextColor(getResources().getColor(android.R.color.white));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    private int getIndex(Spinner spinner, int p_catId) {
        int index = 0;
        Category targetCategory = new Category(getApplicationContext(), p_catId, getDb_helper());

        for (int i=0; i<spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(targetCategory.getName())){
                index = i;
                attrCategorySelected = targetCategory;
                break;
            }
        }

        return index;
    }

    private DataBaseHelper getDb_helper() {
        if(!db_helper.isOpen()) {
            System.out.println("db helper was closed");
            db_helper = new DataBaseHelper(this);
            db_helper.init();
        }
        return db_helper;
    }

}
