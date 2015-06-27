package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.DatabaseMetaData;
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
    private TextView attrColorValue;
    private TextView attrWishlistName;
    private Switch attrWishlistValue;

    private int editItemId;
    private Item editItem;

    private boolean isEdit;

    private ArrayList<AttributeType> attributeTypesList;

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
                thisActivity.finish();
                // TODO: save item
            }
        });

        attributeTypesList = new ArrayList<AttributeType>();
        itemNameExitText = (EditText) findViewById(R.id.itemName);

        attributesView = (LinearLayout) findViewById(R.id.itemDetailAttributes);

        attrCategoryName = (TextView) findViewById(R.id.attrCategoryName);
        attrCategoryValue = (Spinner) findViewById(R.id.attrCategoryValue);
        attrColorName = (TextView) findViewById(R.id.attrColorName);
        attrColorValue = (TextView) findViewById(R.id.attrColorValue);
        attrWishlistName = (TextView) findViewById(R.id.attrWishlistName);
        attrWishlistValue = (Switch) findViewById(R.id.attrWishlistValue);

        attrCategoryName.setText("Category:");
        attrColorName.setText("Color:");
        attrWishlistName.setText("In Wishlist:");

        setupCategoryDropdown();


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
        // Not a new item, but editing an existing item
        if(editItemId > 0) {
            isEdit = true;
            getActionBar().setTitle(R.string.title_activity_edit_item);
            db_helper.setTable(Constants.ITEMS_DB_TABLE);
            editItem = new Item(this, editItemId, db_helper);
            System.out.println("Wish in newItemActivity: " + editItem.getIsWish());
//            editItem.save();
            editItemId = editItem.getId();
            setItemData();
        } else {
            isEdit = false;
            getActionBar().setTitle(R.string.title_activity_new_item);
            db_helper.setTable(Constants.ITEMS_DB_TABLE);
            editItem = new Item(this, 0, db_helper);
//            editItem.save();
            editItemId = editItem.getId();
        }
        setupAttributeViews();

    }

    private void setItemData() {
        // TODO: set Item Data from DB
        itemNameExitText.setText(editItem.getName());

        ratingBar.setRating(editItem.getRating());

        attrCategoryValue.setSelection(((CategoriesDropdownAdapter) attrCategoryValue.getAdapter()).findPositionOfCategoryId(editItem.getCategoryId()));

        attrColorValue.setText(editItem.getPrimaryColor());

        System.out.println("setItemData wish: " + editItem.getIsWish());
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
        String item_imageFile = ImgPhoto.getDrawable().toString();
        String item_categoryId = (attrCategorySelected.getId()) + "";
        String item_primaryColor = attrColorValue.getText().toString();
        String item_rating = Float.toString(ratingBar.getRating());
        String item_isWish = attrWishlistValue.isChecked()  ? "1" : "0";

        HashMap<String, String> itemAttributes = new HashMap<String, String>();
        itemAttributes.put("name", item_name);
        itemAttributes.put("image_file", item_imageFile);
        itemAttributes.put("category_id", item_categoryId);
        itemAttributes.put("primary_color", item_primaryColor);
        itemAttributes.put("rating", item_rating);
        itemAttributes.put("is_wish", item_isWish);

//        Item defaultItem = new Item(this, 0, m_dbHelper);
        db_helper.setTable(Constants.ITEMS_DB_TABLE);
        editItem.edit(itemAttributes);
        editItem.save();
//        For testing what is stored in db
//        System.out.println("Rating: " + editItem.getRating());
//        System.out.println("ImageFile: " + editItem.getImageFile());
//        System.out.println("Prim Color: " + editItem.getPrimaryColor());
//        System.out.println("Wishlist: " + editItem.getIsWish());
//        System.out.println("Name: " + editItem.getName());
//        System.out.println("Id: " + editItem.getId());
//        System.out.println("-----------------------------");
//        Item testItem = new Item(this, editItem.getId(), db_helper);
//        System.out.println("Rating: " + testItem.getRating());
//        System.out.println("ImageFile: " + testItem.getImageFile());
//        System.out.println("Prim Color: " + testItem.getPrimaryColor());
//        System.out.println("Wishlist: " + testItem.getIsWish());
//        System.out.println("Name: " + testItem.getName());
//        System.out.println("Id: " + testItem.getId());


        Iterator allItemAttributesIterator = attributeTypesList.iterator();

        // TODO: get all attributes with values for this Item
        // TODO: get all attributes with values for this Item
        while (allItemAttributesIterator.hasNext()) {
            AttributeType tmpItemAttrType = (AttributeType) allItemAttributesIterator.next();

            LinearLayout attributeView = (LinearLayout) attributesView.findViewWithTag(tmpItemAttrType.getId());
            String attributeSaveValue = "";
            // boolean
            if(tmpItemAttrType.getValueType() == 2) {
                attributeSaveValue  = ((Switch) attributeView.findViewById(R.id.boolAttrField)).isChecked() ? "1" : "0";
            } else {
                attributeSaveValue = ((EditText) attributeView.findViewById(R.id.stringAttrField)).getText().toString();
            }
            HashMap<String, String> itemAttributeValue = new HashMap<String, String>();
            itemAttributeValue.put("item_id",editItemId + "");
            itemAttributeValue.put("attribute_type_id",tmpItemAttrType.getId() + "");
            itemAttributeValue.put("attribute_value",attributeSaveValue);

            db_helper.setTable(Constants.ITEM_ATTR_DB_TABLE);
            Attribute itemAttribute = new Attribute(this, 0, db_helper);
            itemAttribute.edit(itemAttributeValue);
            itemAttribute.save();
        }

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
        // TODO: real attr names and values
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

        } else {
            EditText textAttributeValue = new EditText(this);
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

        attributesView.addView(attributeLayout, attributesView.getChildCount() - 1);
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

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();

                try {
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);

                    ImgPhoto.setImageBitmap(yourSelectedImage);
                }catch(FileNotFoundException e){
                    Log.w("FileNotFoundExeption: ", e.toString());
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    private void setupCategoryDropdown() {

        final ArrayList<Category> allCategories = Category.getAllCategories(this);
        final CategoriesDropdownAdapter adapter = new CategoriesDropdownAdapter (getBaseContext(), android.R.layout.simple_spinner_item,
                allCategories);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        attrCategoryValue.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        attrCategoryValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (view != null) {
                    db_helper.setTable(Constants.CATEGORIES_DB_TABLE);
                    attrCategorySelected = new Category(getBaseContext(), (int) adapter.getItemId(position), db_helper);
                    attrCategoryValue.setSelection(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

}
