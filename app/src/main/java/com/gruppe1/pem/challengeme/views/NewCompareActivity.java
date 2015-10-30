package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.CategoriesGridOverlayAdapter;
import com.gruppe1.pem.challengeme.adapters.CompareImageAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class NewCompareActivity extends Activity {

    private ViewPager viewPager1;
    private ViewPager viewPager2;
    private ImageView img1;
    private ImageView img2;
    private ArrayList<Item> firstCatItems;
    private ArrayList<Item> secontCatItems;

    private Activity thisActivity;
    private ArrayList<Category> upperCategoriesList;
    private AlertDialog builder1;
    private AlertDialog builder2;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_compare);
        getActionBar().setTitle(R.string.title_activity_compare);

        sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);

        viewPager1 = (ViewPager) findViewById(R.id.view_pager1);
        viewPager2 = (ViewPager) findViewById(R.id.view_pager2);
        viewPager1.setVisibility(View.INVISIBLE);
        viewPager2.setVisibility(View.INVISIBLE);

        ArrayList<Category> allCategories = Category.getAllCategories(getApplicationContext());
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);

        thisActivity = this;

        upperCategoriesList = new ArrayList<>();

        for(Category cat : allCategories) {
            int catsize = Item.getItemsCountByCategoryId(getApplicationContext(), cat.getId(), sharedPreferences.getBoolean(Constants.KEY_WISHLIST_IN_COMPARE, false));
            if(catsize > 0) {
                ArrayList<Item> items = Item.getItemsByCategoryId(getApplicationContext(), cat.getId(), sharedPreferences.getBoolean(Constants.KEY_WISHLIST_IN_COMPARE, false));
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getImageFile() != null) {
                        upperCategoriesList.add(cat);
                        break;
                    }
                }
            }
        }
        img1.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder1 = createCategoryOverlay(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        firstCatItems = Item.getItemsByCategoryId(getApplicationContext(), upperCategoriesList.get(position).getId(), sharedPreferences.getBoolean(Constants.KEY_WISHLIST_IN_COMPARE, false));

                        for (int i = 0; i < firstCatItems.size(); i++) {
                            String imageFile = firstCatItems.get(i).getImageFile();
                            if (imageFile == null) {
                                firstCatItems.remove(i);
                            }
                        }
                        CompareImageAdapter adapter = new CompareImageAdapter(thisActivity, firstCatItems, 1);
                        viewPager1.setAdapter(adapter);
                        img1.setVisibility(View.INVISIBLE);
                        viewPager1.setVisibility(View.VISIBLE);
                        builder1.dismiss();
                    }
                });
                builder1.show();
            }
        });

        img2.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder2 = createCategoryOverlay(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        secontCatItems = Item.getItemsByCategoryId(getApplicationContext(), upperCategoriesList.get(position).getId(), sharedPreferences.getBoolean(Constants.KEY_WISHLIST_IN_COMPARE, false));

                        for (int i = 0; i < secontCatItems.size(); i++) {
                            String imageFile = secontCatItems.get(i).getImageFile();
                            if (imageFile == null) {
                                secontCatItems.remove(i);
                            }
                        }
                        CompareImageAdapter adapter = new CompareImageAdapter(thisActivity, secontCatItems, 2);
                        viewPager2.setAdapter(adapter);
                        img2.setVisibility(View.INVISIBLE);
                        viewPager2.setVisibility(View.VISIBLE);
                        builder2.dismiss();
                    }
                });
                builder2.show();
            }
        });
    }

    /**
     * creates a overlay with categories as grid view
     * @param onItemClickListener the onItemClickListener for the items in the overlay
     * @return the created AlertDialog
     */
    private AlertDialog createCategoryOverlay(AdapterView.OnItemClickListener onItemClickListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_grid, null);
        TextView headline = (TextView)dialogView.findViewById(R.id.dialog_headline);
        headline.setText(getString(R.string.compare_choose_cat_overlay_title));
        GridView gridView = (GridView) dialogView.findViewById(R.id.gridView);

        ArrayList<ListItemIconName> catArray = new ArrayList<>();

        for (Category tmpCat : upperCategoriesList) {
            int iconId = getResources().getIdentifier(tmpCat.getIcon(), "drawable", "com.gruppe1.pem.challengeme");
            catArray.add(new ListItemIconName(tmpCat.getId(), iconId, tmpCat.getName(), null));
        }

        builder.setView(dialogView);
        final AlertDialog alert = builder.create();
        final CategoriesGridOverlayAdapter gridAdapter = new CategoriesGridOverlayAdapter(this, R.layout.grid_item_overlay, catArray);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(onItemClickListener);
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });

        return alert;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_compare, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_item_save:
                saveCompare();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * creates and saves the new category
     */
    private void saveCompare() {

        final int firstElementPosition = viewPager1.getCurrentItem();
        final int secondElementPosition = viewPager2.getCurrentItem();

        // FIXME lists sometimes empty
        if(firstCatItems.size() > 0 && secontCatItems.size() > 0) {

            final int firstItemID = firstCatItems.get(firstElementPosition).getId();
            final int secondtItemID = secontCatItems.get(secondElementPosition).getId();


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout

            View dialogView = inflater.inflate(R.layout.dialog_textfield, null);
            TextView headline = (TextView)dialogView.findViewById(R.id.dialog_headline);
            headline.setText(R.string.save_compare);

            final TextView inputField = (TextView)dialogView.findViewById(R.id.dialog_text);
            inputField.setHint(R.string.compare_name);


            builder.setView(dialogView).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    String name = inputField.getText().toString();

                    if(name.equals("")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                        String currentDateandTime = sdf.format(new Date());
                        name = getString(R.string.title_activity_compare) + " " + currentDateandTime;
                    }

                    DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
                    dbHelper.init();


                    Compare newCompare = new Compare(getApplicationContext(), -1, dbHelper);
                    newCompare.setName(name);
                    newCompare.addItemId(firstItemID);
                    newCompare.addItemId(secondtItemID);
                    newCompare.insert();
                    newCompare.closeDBConnection();

                    dbHelper.close();

                    thisActivity.setResult(RESULT_OK);
                    thisActivity.finish();
                }
            })
            .setNegativeButton(R.string.abort,null);
            builder.create().show();
        }
    }

    /**
     * shows the overlay to choose a category
     * @param p_builder int the overlay buider to show
     */
    public void showCategoryChooser(int p_builder) {
       switch (p_builder){
           case 1:
               builder1.show();
                break;
           case 2:
               builder2.show();
       }
    }
}
