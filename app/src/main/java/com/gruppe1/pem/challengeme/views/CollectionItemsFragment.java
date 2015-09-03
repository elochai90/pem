package com.gruppe1.pem.challengeme.views;

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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private TextView attrCategoryName;
    private TextView attrCategoryValue;
    private Category attrCategorySelected;
    private TextView attrColorName;
    private TextView attrColorValue;
    private com.gruppe1.pem.challengeme.Color attrColorSelected;
    private TextView attrWishlistName;
    private Switch attrWishlistValue;
    private String item_imageFile;

    private int editItemId = -1;
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


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_collection_items, container, false);
        activity = (CollectionItemsActivity) getActivity();

        sharedPreferences = getActivity().getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);


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
        stars.getDrawable(2).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        attributeTypesList = new ArrayList<>();
        itemNameExitText = (EditText) rootView.findViewById(R.id.itemName);
        attributesView = (LinearLayout) rootView.findViewById(R.id.itemDetailAttributes);
        attrCategoryName = (TextView) rootView.findViewById(R.id.attrCategoryName);
        attrCategoryValue = (TextView) rootView.findViewById(R.id.attrCategoryValue);
        attrColorName = (TextView) rootView.findViewById(R.id.attrColorName);
        attrColorValue = (TextView) rootView.findViewById(R.id.attrColorValue);
        attrWishlistName = (TextView) rootView.findViewById(R.id.attrWishlistName);
        attrWishlistValue = (Switch) rootView.findViewById(R.id.attrWishlistValue);

        attrCategoryName.setText("Category:");
        attrColorName.setText("Color:");
        attrWishlistName.setText("In Wishlist:");

        // Setup Categories Adapter
        ArrayList<Category> allCategories = Category.getAllCategories(activity);
        ArrayList<ListItemIconName> catArray = new ArrayList<>();
        for (Category tmpCat : allCategories) {
            int iconId = getResources().getIdentifier(tmpCat.getIcon(), "drawable", "com.gruppe1.pem.challengeme");
            catArray.add(new ListItemIconName(tmpCat.getId(), iconId, tmpCat.getName(), null));
        }
        gridCateoriesAdapter = new CategoriesGridOverlayAdapter(activity, R.layout.grid_item_overlay, catArray);
        attrCategoryValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupCategoryOverlay();
            }
        });

        // Setup Colors Adapter
        ArrayList<com.gruppe1.pem.challengeme.Color> allColors = com.gruppe1.pem.challengeme.Color.getAllColors(activity);
        gridColorsAdapter = new ColorsGridOverlayAdapter(activity, R.layout.grid_item_overlay, allColors);



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


        if (savedInstanceState == null && editItemId == -1) {
            Bundle extras = getArguments();
            if(extras != null) {
                editItemId = extras.getInt(Constants.EXTRA_ITEM_ID);
            }
        }

        Bundle extras1 = getArguments();
        if (extras1 != null &&  extras1.getBoolean("is_wishlist")) {
            attrWishlistValue.setChecked(true);
        }
        // Not a new item, but editing an existing item
        if(editItemId > 0) {
            getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
            editItem = new Item(activity, editItemId, getDb_helper());
            parentCategoryId = editItem.getCategoryId();
            savedColorId = editItem.getPrimaryColorId();

        } else {
            getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
            editItem = new Item(activity, 0, getDb_helper());

            savedColorId = -1;

            if (extras1 != null && extras1.getInt("category_id") != 0) {
                parentCategoryId = extras1.getInt("category_id");
            } else {
                parentCategoryId = -1;
            }
        }

        if(parentCategoryId != -1) {
            attrCategorySelected = new Category(activity, parentCategoryId, getDb_helper());
            attrCategoryValue.setText(attrCategorySelected.getName());
        } else {
            attrCategorySelected = new Category(activity, gridCateoriesAdapter.getItem(0).getElementId(), getDb_helper());
            attrCategoryValue.setText(attrCategorySelected.getName());
        }

        if(savedColorId != -1) {
            attrColorSelected = new com.gruppe1.pem.challengeme.Color(activity, savedColorId, getDb_helper());
            attrColorValue.setText(attrColorSelected.getName());
            attrColorValue.setBackgroundColor(Color.parseColor(attrColorSelected.getHexColor()));
            if(!gridColorsAdapter.isColorLight(Color.parseColor(attrColorSelected.getHexColor()))) {
                attrColorValue.setTextColor(getResources().getColor(android.R.color.white));
            }
        } else {
            attrColorSelected = new com.gruppe1.pem.challengeme.Color(activity, gridColorsAdapter.getItem(0).getId(), getDb_helper());
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

        return rootView;
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
        ArrayList<AttributeType> allAttributeTypes = AttributeType.getAttributeTypes(activity);
        for (AttributeType tmpAttrType : allAttributeTypes) {
            if (!attributeTypesList.contains(tmpAttrType)) {
                attributeTypesList.add(tmpAttrType);
                setAttributeLayout(tmpAttrType, null);
            }
        }
    }



    /**
     * creates and shows the overlay to choose a category
     */
    private void setupCategoryOverlay() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = getLayoutInflater(null);

        final View dialogView = inflater.inflate(R.layout.dialog_grid, null);
        TextView headline = (TextView)dialogView.findViewById(R.id.dialog_headline);
        headline.setText("Select a category");
        GridView gridView = (GridView) dialogView.findViewById(R.id.gridView);

        gridView.setAdapter(gridCateoriesAdapter);

        builder.setView(dialogView);
        final AlertDialog alert = builder.create();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                getDb_helper().setTable(Constants.CATEGORIES_DB_TABLE);
                attrCategorySelected = new Category(activity, gridCateoriesAdapter.getItem(position).getElementId(), getDb_helper());
                attrCategoryValue.setText(gridCateoriesAdapter.getItem(position).getName());
                ((TextView) attributesView.findViewWithTag("size")).setText(getSizeValueBySizeType(attrCategorySelected.getDefaultSizeType()));
                alert.dismiss();
            }
        });
        alert.show();
    }

    /**
     * creates and shows the overlay to choose a color
     */
    private void setupColorsOverlay() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = getLayoutInflater(null);

        final View dialogView = inflater.inflate(R.layout.dialog_grid, null);
        TextView headline = (TextView)dialogView.findViewById(R.id.dialog_headline);
        headline.setText("Select a color");
        GridView gridView = (GridView) dialogView.findViewById(R.id.gridView);

        gridView.setAdapter(gridColorsAdapter);

        builder.setView(dialogView);
        final AlertDialog alert = builder.create();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                getDb_helper().setTable(Constants.CATEGORIES_DB_TABLE);
                attrColorSelected = new com.gruppe1.pem.challengeme.Color(activity, gridColorsAdapter.getItem(position).getId(), getDb_helper());
                attrColorValue.setText(attrColorSelected.getName());
                attrColorValue.setBackgroundColor(Color.parseColor(attrColorSelected.getHexColor()));
                if(!gridColorsAdapter.isColorLight(Color.parseColor(attrColorSelected.getHexColor()))) {
                    attrColorValue.setTextColor(getResources().getColor(android.R.color.white));
                }
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
            db_helper = new DataBaseHelper(activity);
            db_helper.init();
        }
        return db_helper;
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
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
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
        if(item_imageFile != null) {
            options = new CharSequence[] {"Show Fullscreen", "Take Photo", "Choose from Gallery", "Cancel"};

        } else {
            options = new CharSequence[] {"Take Photo", "Choose from Gallery", "Cancel"};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")){
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    activity.startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activity.startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                } else if (options[item].equals("Show Fullscreen")) {
                    Intent i = new Intent(getActivity(), FullscreenImageActivity.class);
                    i.putExtra("imageurl", item_imageFile);
                    startActivity(i);
                }
            }

        });
        builder.show();
    }


    /**
     * creates the layout for a attribute type with the atrribute value
     * @param attributeType AttributeType the attribute type
     * @param attributeValue Object the value of the attribute type
     */
    private void setAttributeLayout(AttributeType attributeType, Object attributeValue) {

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        int with_background_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

        LinearLayout attributeLayout = new LinearLayout(activity);
        attributeLayout.setOrientation(LinearLayout.HORIZONTAL);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        attributeLayout.setPadding(padding, 0, padding, 0);
        LinearLayout.LayoutParams attributeLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        attributeLayout.setLayoutParams(attributeLayoutParams);
        // Set the attributeTypeId for saving
        attributeLayout.setTag(attributeType.getId());

        TextView attributeName = new TextView(activity);
        attributeName.setTextSize(18);
        ViewGroup.LayoutParams attibuteNameLayoutParams = new ViewGroup.LayoutParams(width, height);
        attributeName.setLayoutParams(attibuteNameLayoutParams);
        attributeName.setGravity(Gravity.CENTER_VERTICAL);
        attributeName.setText(attributeType.getName() + ":");

        View attributeValueView;
        // attribute is boolean
        if(attributeType.getValueType() == 2) {
            Switch attrValueSwitch = new Switch(activity);
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
            attrValueColorPicker = new TextView(activity);
            ViewGroup.LayoutParams attibuteValueLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, with_background_height);
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
                    final HSVColorPickerDialog cpd = new HSVColorPickerDialog(activity, exactColorId, new HSVColorPickerDialog.OnColorSelectedListener() {
                        @Override
                        public void colorSelected(Integer color) {
                            // Do something with the selected color
                            exactColorId = color;
                            attrValueColorPicker.setBackgroundColor(exactColorId);
                            attrValueColorPicker.setText("");
                        }
                    });
                    cpd.setTitle("Pick the exact color");
                    cpd.show();
                }
            });
            attributeValueView = attrValueColorPicker;
        }
        // attribute is DatePicker
        else if(attributeType.getValueType() == 4) {
            attrValueDatePicker = new TextView(activity);
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


            attrValueDatePicker.setBackgroundColor(getResources().getColor(R.color.color_picker_initial));
            attrValueDatePicker.setText(buyDate);
            attrValueDatePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogFragment newFragment = new SelectDateFragment();
                    newFragment.show(getFragmentManager(), "DatePicker");
//                    activity.showDialog(DATE_DIALOG_ID);
                }
            });
            attributeValueView = attrValueDatePicker;
        }
        else {
            EditText textAttributeValue = new EditText(activity);
            textAttributeValue.setSingleLine(true);
            textAttributeValue.setTextSize(18);
            ViewGroup.LayoutParams attibuteValueLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            textAttributeValue.setLayoutParams(attibuteValueLayoutParams);
            textAttributeValue.setId(R.id.stringAttrField);

            if(attributeType.getName().equals("Size")) {
                textAttributeValue.setTag("size");
            }
            if(attributeValue != null) {
                textAttributeValue.setText(attributeValue.toString());
            } else if(attributeType.getName().equals("Size")) {
                textAttributeValue.setText(getSizeValueBySizeType(attrCategorySelected.getDefaultSizeType()));
            }

            attributeValueView = textAttributeValue;
        }
        attributeLayout.addView(attributeName);
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
        attrColorSelected = new com.gruppe1.pem.challengeme.Color(activity, editItem.getPrimaryColorId(), getDb_helper());
        attrColorValue.setBackgroundColor(Color.parseColor(attrColorSelected.getHexColor()));

        try {
            String imgPath = editItem.getImageFile();
            File imgFile = new File(imgPath);
            if (imgFile.exists()) {
                item_imageFile = imgPath;
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

        ArrayList<Attribute> itemAttributes = Attribute.getAttributesByItemId(activity, editItem.getId());
        for (Attribute tmpItemAttr : itemAttributes) {
            attributeTypesList.add(tmpItemAttr.getAttributeType());
            setAttributeLayout(tmpItemAttr.getAttributeType(), tmpItemAttr.getValue());
        }

    }


    /**
     * creates/updates the item
     */
    public void saveItem() {
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
            Attribute itemAttribute = new Attribute(activity, editItem.getId(), tmpItemAttrType.getId(), getDb_helper());
            itemAttribute.edit(itemAttributeValue);
            itemAttribute.save();
        }
        this.db_helper.close();
    }


    class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int day, month, year;
            if(buyDate.length() > 0) {
                String[] parts = buyDate.split("\\.");
                day = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1])-1;
                year = Integer.parseInt(parts[2]);
            } else {
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);
            }
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            buyDate = dd + "." + (mm+1) + "." + yy;
            attrValueDatePicker.setText(buyDate);
            attrValueDatePicker.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    public void setBitmap(Bitmap photo) {
        ImgPhoto.setImageBitmap(photo);

        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(activity, photo);

        // CALL THIS METHOD TO GET THE ACTUAL PATH
        File finalFile = new File(getRealPathFromURI(tempUri));
        item_imageFile = finalFile.getAbsolutePath();
        editItem.setImageFile(item_imageFile);
        exactColorId = ImageDominantColorExtractor.getInstance().getDominantColor(photo);
        attrValueColorPicker.setBackgroundColor(exactColorId);
    }

    public void setImageUri(Uri selectedImage) {
        String[] filePath = {MediaStore.Images.Media.DATA};

        Cursor c = activity.getContentResolver().query(selectedImage, filePath, null, null, null);
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