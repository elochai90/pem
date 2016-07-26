package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.DefaultSize;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.CategoryEditText;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.DefaultSizesEditText;
import com.gruppe1.pem.challengeme.helpers.ExactColorEditText;
import com.gruppe1.pem.challengeme.helpers.IconEditText;

import java.util.ArrayList;
import java.util.Arrays;

public class NewCategoryActivity extends AppCompatActivity {

   private EditText newCategory_name;
   private DefaultSizesEditText categoryDefaultSize;
   private CategoryEditText categoryParent;
   private ImageView categoryIconIndicator;
   private IconEditText categoryIcon;
   private View categoryColorIndicator;
   private ExactColorEditText categoryColor;
   private Bundle extras;

   //   private String iconName;
   private SharedPreferences sharedPreferences;

   private int categoryId;
   private int parentCategoryId;
   private int categoryColorHex;
   private Category parentCategory = null;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_new_category);
      setupToolbar();
      getSupportActionBar().setTitle(R.string.title_activity_new_category);
      extras = getIntent().getExtras();

      sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);

      categoryId = -1;
      parentCategoryId = -1;
      categoryColorHex = -1;

      newCategory_name = (EditText) findViewById(R.id.categoryName);
      categoryDefaultSize = (DefaultSizesEditText) findViewById(R.id.categoryDefaultSize);
      categoryParent = (CategoryEditText) findViewById(R.id.categoryParent);
      categoryIconIndicator = (ImageView) findViewById(R.id.categoryIconIndicator);
      categoryIcon = (IconEditText) findViewById(R.id.categoryIcon);
      categoryColor = (ExactColorEditText) findViewById(R.id.attrExactColorValue);
      categoryColorIndicator = (View) findViewById(R.id.attrExactColorIndicator);

      String defaultSize1Value = sharedPreferences.getString(Constants.KEY_DS_1_NAME, "");
      String defaultSize2Value = sharedPreferences.getString(Constants.KEY_DS_2_NAME, "");
      String defaultSize3Value = sharedPreferences.getString(Constants.KEY_DS_3_NAME, "");

      ArrayList<DefaultSize> defaultSizes = new ArrayList<>();
      defaultSizes.add(new DefaultSize(getString(R.string.default_size_none), ""));
      defaultSizes.add(new DefaultSize(getString(R.string.default_size_tops), defaultSize1Value));
      defaultSizes.add(
            new DefaultSize(getString(R.string.default_size_bottoms), defaultSize2Value));
      defaultSizes.add(new DefaultSize(getString(R.string.default_size_shoes), defaultSize3Value));

      categoryDefaultSize.setItems(this, defaultSizes);

      final ArrayList<String> iconsArray = new ArrayList<>(
            Arrays.asList(getResources().getStringArray(R.array.category_icons_array)));

      categoryIcon.setItems(this, iconsArray);
      categoryIcon.setOnItemSelectedListener(new IconEditText.OnItemSelectedListener() {
         @Override
         public void onItemSelectedListener(String item, int selectedIndex) {
            int iconId =
                  getResources().getIdentifier(item, "drawable", "com.gruppe1.pem.challengeme");
            categoryIconIndicator.setImageResource(iconId);
            System.out.println("set Selection: " + item + " - " + selectedIndex);
         }
      });
      categoryIconIndicator.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            categoryIcon.callOnClick();
         }
      });

      if (extras != null) {
         if (extras.getInt(Constants.EXTRA_PARENT_CATEGORY_ID) != 0) {
            parentCategoryId = extras.getInt(Constants.EXTRA_PARENT_CATEGORY_ID);
            DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
            dbHelper.init();
            parentCategory = new Category(this, parentCategoryId, dbHelper);
            categoryColorHex = Integer.parseInt(parentCategory.getColor(), 16) + 0xFF000000;
            dbHelper.close();
         }
         if (extras.getInt(Constants.EXTRA_CATEGORY_ID) != 0) {
            //edit category
            categoryId = extras.getInt(Constants.EXTRA_CATEGORY_ID);
            DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
            dbHelper.init();

            Category editCategory = new Category(getApplicationContext(), categoryId, dbHelper);

            getSupportActionBar().setTitle(
                  getString(R.string.title_edit_category_activity) + " " + editCategory.getName());
            newCategory_name.setText(editCategory.getName());
            categoryColorHex = Integer.parseInt(editCategory.getColor(), 16) + 0xFF000000;
            categoryIcon.setSelection(iconsArray.indexOf(editCategory.getIcon()));

            String defaultSizeTypeString =
                  getDefaultSizeTypeString(editCategory.getDefaultSizeType());
            int indexOfDefaultSize = defaultSizes.indexOf(new DefaultSize(defaultSizeTypeString,
                  sharedPreferences.getString(defaultSizeTypeString, "")));
            categoryDefaultSize.setSelection(indexOfDefaultSize);

            dbHelper.close();
         }
      }

      categoryColor.setOnColorSelectedListener(new ExactColorEditText.OnColorSelectedListener() {
         @Override
         public void onColorSelectedListener(int color) {
            if (color != -1) {
               categoryColorHex = color;
               categoryColorIndicator.setBackgroundColor(color);
            }
         }
      });
      ((TextInputLayout) findViewById(R.id.textInputLayout)).setHint(
            getString(R.string.new_category_label_color));
      categoryColor.initialize(this, categoryColorHex);
      categoryColorIndicator.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            categoryColor.callOnClick();
         }
      });

      ArrayList<Category> allCategories = Category.getAllCategories(this);
      DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
      dbHelper.init();
      categoryParent.setItems(this, dbHelper, allCategories);
      dbHelper.close();
      categoryParent.setOnItemSelectedListener(new CategoryEditText.OnItemSelectedListener() {
         @Override
         public void onItemSelectedListener(Category item, int selectedIndex) {
            if (item != null && item.getId() > 0) {
               parentCategory = item;
               parentCategoryId = parentCategory.getId();
               categoryDefaultSize.setSelection(item.getDefaultSizeType() + 1);
               categoryColorHex = Integer.parseInt(item.getColor(), 16) + 0xFF000000;
               categoryColor.setExactColorId(categoryColorHex);
               categoryIcon.setSelection(iconsArray.indexOf(parentCategory.getIcon()));
            } else {
               parentCategory = null;
               parentCategoryId = -1;
            }
         }
      });

      if (parentCategory != null) {
         categoryParent.setSelection(allCategories.indexOf(parentCategory));
         String defaultSizeTypeString =
               getDefaultSizeTypeString(parentCategory.getDefaultSizeType());
         int indexOfDefaultSize = defaultSizes.indexOf(new DefaultSize(defaultSizeTypeString,
               sharedPreferences.getString(defaultSizeTypeString, "")));
         categoryDefaultSize.setSelection(indexOfDefaultSize);
         categoryIcon.setSelection(iconsArray.indexOf(parentCategory.getIcon()));
      }
      if (parentCategory == null && categoryId < 0) {
         categoryIcon.setSelection(iconsArray.indexOf("kleiderbuegel"));
      }
   }

   private String getDefaultSizeTypeString(int defaultSizeType) {
      String defaultSizeTypeString;
      switch (defaultSizeType) {
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
      return defaultSizeTypeString;
   }

   private void setupToolbar() {
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
      editCategory.setNameEn(newCategory_name.getText()
            .toString());
      editCategory.setNameDe(newCategory_name.getText()
            .toString());
      editCategory.setDefaultSizeType(categoryDefaultSize.getSelectedItemPosition() - 1);
      editCategory.setColor(String.format("%06X", (0xFFFFFF & categoryColor.getExactColor())));
      if (parentCategory == null) {
         editCategory.setParentCategoryId(-1);
      } else {
         editCategory.setParentCategoryId(parentCategory.getId());
      }
      editCategory.setIcon(categoryIcon.getSelectedItem());
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
