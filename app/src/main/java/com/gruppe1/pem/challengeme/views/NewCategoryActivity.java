package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.DefaultSizesAdapter;
import com.gruppe1.pem.challengeme.adapters.IconsGridOverlayAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;
import java.util.Arrays;


public class NewCategoryActivity extends Activity {

    private EditText newCategory_name;
    private Spinner categoryDefaultSize;
    private ImageView categoryIcon;
    private Bundle extras;

    private String iconName;
    private SharedPreferences sharedPreferences;

    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);
        getActionBar().setTitle(R.string.title_activity_new_category);
        extras = getIntent().getExtras();

        sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);

        categoryId = 0;
        iconName = "kleiderbuegel";

        newCategory_name = (EditText) findViewById(R.id.categoryName);
        categoryDefaultSize = (Spinner) findViewById(R.id.categoryDefaultSize);
        categoryIcon = (ImageView) findViewById(R.id.categoryIcon);

        String defaultSize1Value = sharedPreferences.getString(Constants.KEY_DS_1_NAME, "");
        String defaultSize2Value = sharedPreferences.getString(Constants.KEY_DS_2_NAME, "");
        String defaultSize3Value = sharedPreferences.getString(Constants.KEY_DS_3_NAME, "");

        ArrayList<DefaultSize> defaultSizes = new ArrayList<>();
        defaultSizes.add(new DefaultSize(getString(R.string.default_size_none), ""));
        defaultSizes.add(new DefaultSize(getString(R.string.default_size_tops), defaultSize1Value));
        defaultSizes.add(new DefaultSize(getString(R.string.default_size_bottoms), defaultSize2Value));
        defaultSizes.add(new DefaultSize(getString(R.string.default_size_shoes), defaultSize3Value));


        ArrayAdapter<DefaultSize> adapter = new DefaultSizesAdapter(getBaseContext(), android.R.layout.simple_spinner_item,
                defaultSizes);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        categoryDefaultSize.setAdapter(adapter);

        categoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIconOverlay();
            }
        });



        if(extras != null) {
            //edit category
            categoryId = extras.getInt("category_id");
            DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
            dbHelper.init();

            Category editCategory = new Category(getApplicationContext(), categoryId, dbHelper);

            setTitle("Edit " + editCategory.getName());
            newCategory_name.setText(editCategory.getName());

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
    }

    /**
     * creates the overlay to choose an Icon for the new category
     */
    private void createIconOverlay() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_grid, null);
        TextView headline = (TextView)dialogView.findViewById(R.id.dialog_headline);
        headline.setText(R.string.new_category_overlay_title);
        GridView gridView = (GridView) dialogView.findViewById(R.id.gridView);

        ArrayList<String> iconsArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.category_icons_array)));
        builder.setView(dialogView);
        final AlertDialog alert = builder.create();
        final IconsGridOverlayAdapter gridAdapter = new IconsGridOverlayAdapter(this, R.layout.grid_item_overlay, iconsArray);
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
        editCategory.setName(newCategory_name.getText().toString());
        editCategory.setDefaultSizeType(categoryDefaultSize.getSelectedItemPosition() - 1);
        editCategory.setIcon(iconName);
        editCategory.save();

        db_helper.close();

        // sending new Item back to CategoriesFragment for actualizing list view
        Intent i = new Intent();
        i.putExtra("categoryName", editCategory.getName());
        i.putExtra("defaultSize", editCategory.getDefaultSizeType());
        i.putExtra("icon", editCategory.getIcon());
        i.putExtra("categoryId", editCategory.getId());

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
