package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.DefaultSize;
import com.gruppe1.pem.challengeme.HSVColorPickerDialog;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.DefaultSizesAdapter;
import com.gruppe1.pem.challengeme.adapters.IconsGridOverlayAdapter;
import com.gruppe1.pem.challengeme.helpers.ClickToSelectEditText;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class NewCategoryActivity extends AppCompatActivity {

    private EditText newCategory_name;
    private Spinner categoryDefaultSize;
    private ClickToSelectEditText categoryParent;
    private ImageView categoryIcon;
    private View categoryColor;
    private Bundle extras;

    private String iconName;
    private SharedPreferences sharedPreferences;

//    private  ParentCategoryAdapter parentAdapter;

    private int categoryId;
    private int parentCategoryId;
    private int categoryColorHex ;
    private Category parentCategory = null;

//    private ColorsGridOverlayAdapter gridColorsAdapter;
//    private AlertDialog colorsAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);
        setupToolbar();
        getSupportActionBar().setTitle(R.string.title_activity_new_category);
        extras = getIntent().getExtras();

        sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);

        categoryId = 0;
        parentCategoryId = -1;
        iconName = "kleiderbuegel";

        newCategory_name = (EditText) findViewById(R.id.categoryName);
        categoryDefaultSize = (Spinner) findViewById(R.id.categoryDefaultSize);
        categoryParent = (ClickToSelectEditText) findViewById(R.id.categoryParent);
        categoryIcon = (ImageView) findViewById(R.id.categoryIcon);
        categoryColor = (View) findViewById(R.id.categoryColor);

        String defaultSize1Value = sharedPreferences.getString(Constants.KEY_DS_1_NAME, "");
        String defaultSize2Value = sharedPreferences.getString(Constants.KEY_DS_2_NAME, "");
        String defaultSize3Value = sharedPreferences.getString(Constants.KEY_DS_3_NAME, "");

        ArrayList<DefaultSize> defaultSizes = new ArrayList<>();
        defaultSizes.add(new DefaultSize(getString(R.string.default_size_none), ""));
        defaultSizes.add(new DefaultSize(getString(R.string.default_size_tops), defaultSize1Value));
        defaultSizes.add(new DefaultSize(getString(R.string.default_size_bottoms), defaultSize2Value));
        defaultSizes.add(new DefaultSize(getString(R.string.default_size_shoes), defaultSize3Value));

        ArrayAdapter<DefaultSize> adapter = new DefaultSizesAdapter(getBaseContext(), android.R.layout.simple_spinner_item,defaultSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryDefaultSize.setAdapter(adapter);

        categoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIconOverlay();
            }
        });

        if (extras != null) {
            if(extras.getInt(Constants.EXTRA_PARENT_CATEGORY_ID) != 0) {
                parentCategoryId = extras.getInt(Constants.EXTRA_PARENT_CATEGORY_ID);
                DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
                dbHelper.init();
                parentCategory = new Category(this, parentCategoryId, dbHelper);
                categoryColorHex = Integer.parseInt(parentCategory.getColor(), 16) + 0xFF000000;
                dbHelper.close();

            }
            if(extras.getInt(Constants.EXTRA_CATEGORY_ID) != 0) {
                //edit category
                categoryId = extras.getInt(Constants.EXTRA_CATEGORY_ID);
                DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
                dbHelper.init();

                Category editCategory = new Category(getApplicationContext(), categoryId, dbHelper);

                setTitle("Edit " + editCategory.getName());
                newCategory_name.setText(editCategory.getName());
                categoryColorHex = Integer.parseInt(editCategory.getColor(), 16) + 0xFF000000;

                iconName = editCategory.getIcon();
                int iconId = getResources().getIdentifier(editCategory.getIcon(), "drawable", "com.gruppe1.pem.challengeme");
                categoryIcon.setImageResource(iconId);

                String defaultSizeTypeString;
                switch(editCategory.getDefaultSizeType()) {
                    case 0:
                        defaultSizeTypeString = getString(R.string.default_size_tops);
                        break;
                    case 1:
                        defaultSizeTypeString = getString(R.string.default_size_bottoms);
                        break;
                    case 2:
                        defaultSizeTypeString = getString(R.string.default_size_shoes);
                        break;
                    default:
                        defaultSizeTypeString = getString(R.string.default_size_none);
                        break;
                }
                int indexOfDefaultSize = defaultSizes.indexOf(new DefaultSize(defaultSizeTypeString, sharedPreferences.getString(defaultSizeTypeString, "")));
                categoryDefaultSize.setSelection(indexOfDefaultSize);

                dbHelper.close();
            }
        } else {
            categoryColorHex = getResources().getColor(R.color.gray05);
        }
        categoryColor.setBackgroundColor(categoryColorHex);
        categoryColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new HSVColorPickerDialog(NewCategoryActivity.this, categoryColorHex,
                      new HSVColorPickerDialog.OnColorSelectedListener() {
                          @Override
                          public void colorSelected(Integer color) {
                              // Do something with the selected color
                              categoryColorHex = color;
                              categoryColor.setBackgroundColor(categoryColorHex);
                          }
                      }).show();
            }
        });

        ArrayList<Category> allCategories = Category.getAllCategories(this);

//        parentAdapter = new ParentCategoryAdapter(this, android.R.layout.simple_spinner_item, allCategories);
//        parentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoryParent.setItems(allCategories);
        categoryParent.setOnItemSelectedListener(new ClickToSelectEditText.OnItemSelectedListener<Category>() {
            @Override
            public void onItemSelectedListener(Category category, int selectedIndex) {
                if(category != null && category.getId() > 0) {
                    parentCategory = category;
                    parentCategoryId = parentCategory.getId();
                } else {
                    parentCategory = null;
                    parentCategoryId = -1;
                }
            }
        });
        if(parentCategory != null) {
            System.out.println("parentCategory.toString()");
            System.out.println(parentCategory.getName());
            categoryParent.setSelection(categoryParent.getPosition(parentCategory));
            categoryDefaultSize.setSelection(parentCategory.getDefaultSizeType()+1);
        }
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * creates the overlay to choose an Icon for the new category
     */
    private void createIconOverlay() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_grid_view, null);
        TextView headline = (TextView)dialogView.findViewById(R.id.dialog_headline);
        headline.setText(R.string.new_category_overlay_title);
        GridView gridView = (GridView) dialogView.findViewById(R.id.gridView);

        ArrayList<String> iconsArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.category_icons_array)));
        builder.setView(dialogView);
        final AlertDialog alert = builder.create();
        final IconsGridOverlayAdapter gridAdapter = new IconsGridOverlayAdapter(this, R.layout.grid_item_overlay_category, iconsArray);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int iconId = getResources().getIdentifier(gridAdapter.getItem(position), "drawable", "com.gruppe1.pem.challengeme");
                categoryIcon.setImageResource(iconId);
                iconName = gridAdapter.getItem(position);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_category, menu);
        return true;
    }

    /**
     * creates and saves the new category
     */
    private void saveNewCategory() {
        DataBaseHelper db_helper = new DataBaseHelper(getApplicationContext());
        db_helper.init();

        Category editCategory = new Category(getApplicationContext(), categoryId, db_helper);
        editCategory.setNameEn(newCategory_name.getText().toString());
        editCategory.setNameDe(newCategory_name.getText().toString());
        editCategory.setDefaultSizeType(categoryDefaultSize.getSelectedItemPosition() - 1);
        editCategory.setColor(String.format("%06X", (0xFFFFFF & categoryColorHex)));
        if(parentCategory == null) {
            editCategory.setParentCategoryId(-1);
        } else {
            editCategory.setParentCategoryId(parentCategory.getId());
        }
        editCategory.setIcon(iconName);
        editCategory.save();

        db_helper.close();

        // sending new Item back to CategoriesFragment for actualizing list view
        Intent i = new Intent();
        i.putExtra("categoryName", editCategory.getName());
        i.putExtra("defaultSize", editCategory.getDefaultSizeType());
        i.putExtra("icon", editCategory.getIcon());
        i.putExtra("categoryId", editCategory.getId());
        i.putExtra("categoryParentId", editCategory.getParentCategoryId());
        i.putExtra("color", editCategory.getColor());

        setResult(Activity.RESULT_OK, i);
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveNewCategory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
