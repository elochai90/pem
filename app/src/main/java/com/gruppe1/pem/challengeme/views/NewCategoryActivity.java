package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.DefaultSize;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.CategoryEditText;
import com.gruppe1.pem.challengeme.helpers.ColorEditText;
import com.gruppe1.pem.challengeme.helpers.ColorHelper;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.DefaultSizesEditText;
import com.gruppe1.pem.challengeme.helpers.ExactColorEditText;
import com.gruppe1.pem.challengeme.helpers.IconEditText;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewCategoryActivity extends AppCompatActivity {

   @Bind (R.id.categoryName)
   TextInputEditText newCategory_name;
   @Bind (R.id.categoryNameLayout)
   TextInputLayout categoryNameLayout;
   @Bind (R.id.categoryDefaultSizeLayout)
   TextInputLayout categoryDefaultSizeLayout;
   @Bind (R.id.attrExactColorLayout)
   TextInputLayout attrExactColorLayout;
   @Bind (R.id.categoryDefaultSize)
   DefaultSizesEditText categoryDefaultSize;
   @Bind (R.id.categoryParent)
   CategoryEditText categoryParent;
   @Bind (R.id.categoryIconIndicator)
   ImageView categoryIconIndicator;
   @Bind (R.id.categoryIcon)
   IconEditText categoryIcon;
   @Bind (R.id.attrExactColorIndicator)
   View categoryColorIndicator;
   @Bind (R.id.attrExactColorValue)
   ExactColorEditText categoryColor;
   @Bind (R.id.toolbar)
   Toolbar toolbar;

   private Bundle extras;

   private SharedPreferences sharedPreferences;

   private int categoryId;
   private int categoryColorHex;
   private Category parentCategory = null;

   private ArrayList<DefaultSize> defaultSizesArray;
   private ArrayList<String> iconsArray;
   private ArrayList<Category> categoryArray;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_new_category);
      ButterKnife.bind(this);
      setupToolbar();
      getSupportActionBar().setTitle(R.string.title_activity_new_category);
      extras = getIntent().getExtras();

      sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);

      categoryId = -1;
      categoryColorHex = -1;

      setupCategoryDefaultSizesTextField();
      setupCategoryIconTextField();

      setupCategoryColorTextField();
      setupCategoryParentTextField();

      newCategory_name.addTextChangedListener(new MyTextWatcher(newCategory_name));
      categoryDefaultSize.addTextChangedListener(new MyTextWatcher(categoryDefaultSize));
      categoryColor.addTextChangedListener(new MyTextWatcher(categoryColor));

      if (extras != null) {
         if (extras.getInt(Constants.EXTRA_CATEGORY_ID) != 0) {
            //edit category
            categoryId = extras.getInt(Constants.EXTRA_CATEGORY_ID);
         } else if (extras.getInt(Constants.EXTRA_PARENT_CATEGORY_ID) != 0) {
            int parentCategoryId = extras.getInt(Constants.EXTRA_PARENT_CATEGORY_ID);
            DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
            dbHelper.init();
            parentCategory = new Category(this, parentCategoryId, dbHelper);
            dbHelper.close();
         }
      }

      if (categoryId >= 0) {
         DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
         dbHelper.init();
         Category editCategory = new Category(getApplicationContext(), categoryId, dbHelper);
         Category parentCategory =
               new Category(getApplicationContext(), editCategory.getParentCategoryId(), dbHelper);
         getSupportActionBar().setTitle(
               getString(R.string.title_edit_category_activity) + " " + editCategory.getName());
         newCategory_name.setText(editCategory.getName());
         categoryColorHex = Integer.parseInt(editCategory.getColor(), 16) + 0xFF000000;
         categoryColor.setExactColorId(categoryColorHex);
         categoryIcon.setSelection(getIconsArray().indexOf(editCategory.getIcon()));

         String defaultSizeTypeString = getDefaultSizeTypeString(editCategory.getDefaultSizeType());
         int indexOfDefaultSize = getDefaultSizesArray().indexOf(
               new DefaultSize(defaultSizeTypeString,
                     sharedPreferences.getString(defaultSizeTypeString, "")));
         categoryDefaultSize.setSelection(indexOfDefaultSize);
         categoryParent.setSelection(getCategoryArray().indexOf(parentCategory));
         dbHelper.close();
         getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
      } else if (parentCategory != null) {
         categoryParent.setSelection(getCategoryArray().indexOf(parentCategory));
         categoryColorHex = Integer.parseInt(parentCategory.getColor(), 16) + 0xFF000000;
         categoryColor.setExactColorId(categoryColorHex);
         String defaultSizeTypeString =
               getDefaultSizeTypeString(parentCategory.getDefaultSizeType());
         int indexOfDefaultSize = getDefaultSizesArray().indexOf(
               new DefaultSize(defaultSizeTypeString,
                     sharedPreferences.getString(defaultSizeTypeString, "")));
         categoryDefaultSize.setSelection(indexOfDefaultSize);
         categoryIcon.setSelection(getIconsArray().indexOf(parentCategory.getIcon()));
      } else {
         categoryIcon.setSelection(getIconsArray().indexOf("kleiderbuegel"));
      }
   }

   private void setupCategoryParentTextField() {
      ArrayList<Category> allCategories = getCategoryArray();
      DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
      dbHelper.init();
      categoryParent.setItems(this, dbHelper, allCategories);
      dbHelper.close();
      categoryParent.setOnItemSelectedListener(new CategoryEditText.OnItemSelectedListener() {
         @Override
         public void onItemSelectedListener(Category item, int selectedIndex) {
            if (item != null && item.getId() > 0) {
               parentCategory = item;
               if (categoryId <= 0) {
                  categoryDefaultSize.setSelection(item.getDefaultSizeType() + 1);
                  categoryColorHex = Integer.parseInt(item.getColor(), 16) + 0xFF000000;
                  categoryColor.setExactColorId(categoryColorHex);
                  categoryIcon.setSelection(getIconsArray().indexOf(parentCategory.getIcon()));
               }
            } else {
               parentCategory = null;
            }
         }
      });
   }

   private ArrayList<Category> getCategoryArray() {
      categoryArray = Category.getAllCategories(this);
      return categoryArray;
   }

   private ArrayList<String> getIconsArray() {
      if (iconsArray == null) {
         iconsArray = new ArrayList<>(
               Arrays.asList(getResources().getStringArray(R.array.category_icons_array)));
      }
      return iconsArray;
   }

   private ArrayList<DefaultSize> getDefaultSizesArray() {
      if (defaultSizesArray == null) {
         String defaultSize1Value = sharedPreferences.getString(Constants.KEY_DS_1_NAME, "");
         String defaultSize2Value = sharedPreferences.getString(Constants.KEY_DS_2_NAME, "");
         String defaultSize3Value = sharedPreferences.getString(Constants.KEY_DS_3_NAME, "");

         defaultSizesArray = new ArrayList<>();
         defaultSizesArray.add(new DefaultSize(getString(R.string.default_size_none), ""));
         defaultSizesArray.add(
               new DefaultSize(getString(R.string.default_size_tops), defaultSize1Value));
         defaultSizesArray.add(
               new DefaultSize(getString(R.string.default_size_bottoms), defaultSize2Value));
         defaultSizesArray.add(
               new DefaultSize(getString(R.string.default_size_shoes), defaultSize3Value));
      }
      return defaultSizesArray;
   }

   private void setupCategoryColorTextField() {

      categoryColor.setOnColorSelectedListener(new ExactColorEditText.OnColorSelectedListener() {
         @Override
         public void onColorSelectedListener(int color) {
            if (color != -1) {
               categoryColorHex = color;
               categoryColorIndicator.setBackgroundColor(color);
            }
         }
      });
      ((TextInputLayout) findViewById(R.id.attrExactColorLayout)).setHint(
            getString(R.string.new_category_label_color));
      categoryColor.initialize(this, categoryColorHex);
      categoryColorIndicator.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            categoryColor.callOnClick();
         }
      });
   }

   private void setupCategoryDefaultSizesTextField() {
      categoryDefaultSize.setItems(this, getDefaultSizesArray());
   }

   private void setupCategoryIconTextField() {
      final ArrayList<String> iconsArray = getIconsArray();

      categoryIcon.setItems(this, iconsArray);
      categoryIcon.setOnItemSelectedListener(new IconEditText.OnItemSelectedListener() {
         @Override
         public void onItemSelectedListener(String item, int selectedIndex) {
            int colorHex = getResources().getColor(R.color.primary_dark);
            categoryIconIndicator.setImageDrawable(
                  ColorHelper.filterIconColor(NewCategoryActivity.this, item, colorHex));
         }
      });
      categoryIconIndicator.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            categoryIcon.callOnClick();
         }
      });
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
    * Validating form
    */
   private void submitForm() {
      if (!validateName() || !validateDefaultSize() || !validateExactColor()) {
         return;
      }

      saveNewCategory();
   }

   private boolean validateName() {
      if (newCategory_name.getText()
            .toString()
            .trim()
            .isEmpty()) {
         // TODO: extract strings
         categoryNameLayout.setError("Geben Sie einen Namen für die Kategorie an");
         newCategory_name.requestFocus();
         return false;
      } else {
         categoryNameLayout.setErrorEnabled(false);
      }
      return true;
   }

   private boolean validateDefaultSize() {
      if (categoryDefaultSize.getSelectedItem() == null) {
         // TODO: extract strings
         categoryDefaultSizeLayout.setError("Geben Sie eine Standardgröße für die Kategorie an");
         categoryDefaultSize.requestFocus();
         return false;
      } else {
         categoryDefaultSizeLayout.setErrorEnabled(false);
      }
      return true;
   }


   private boolean validateExactColor() {
      if (categoryColor.getExactColor() == -1) {
         // TODO: extract strings
         attrExactColorLayout.setError("Geben Sie eine Farbe für die Kategorie an");
         categoryColor.requestFocus();
         return false;
      } else {
         attrExactColorLayout.setErrorEnabled(false);
      }
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
      //      Intent i = new Intent();
      //      i.putExtra("categoryName", editCategory.getName());
      //      i.putExtra("defaultSize", editCategory.getDefaultSizeType());
      //      i.putExtra("icon", editCategory.getIcon());
      //      i.putExtra("categoryId", editCategory.getId());
      //      i.putExtra("categoryParentId", editCategory.getParentCategoryId());
      //      i.putExtra("color", editCategory.getColor());

      setResult(Activity.RESULT_OK);
      this.finish();
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.save:
            submitForm();
            return true;
      }
      return super.onOptionsItemSelected(item);
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
            case R.id.categoryName:
               validateName();
               break;
            case R.id.categoryDefaultSize:
               validateDefaultSize();
               break;
            case R.id.attrExactColorValue:
               validateExactColor();
               break;
         }
      }
   }
}
