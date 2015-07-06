package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.CompareImageAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class NewCompareActivity extends Activity {

    ViewPager viewPager1;
    ViewPager viewPager2;
    ImageView img1;
    ImageView img2;
    ArrayList<Item> firstCatItems;
    ArrayList<Item> secontCatItems;
    FloatingActionButton saveCompareFAB;
    ArrayList<Category> allCategories;
    Activity thisActivity;
    String[] upperCategoriesList2;
    ArrayList<CharSequence> upperCategoriesList;
    AlertDialog.Builder builder1;
    AlertDialog.Builder builder2;
    private  ArrayList<Item> items;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_compare);


        sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);

        viewPager1 = (ViewPager) findViewById(R.id.view_pager1);
        viewPager2 = (ViewPager) findViewById(R.id.view_pager2);
        viewPager1.setVisibility(View.INVISIBLE);
        viewPager2.setVisibility(View.INVISIBLE);
        saveCompareFAB = (FloatingActionButton) findViewById(R.id.save_compare);
        allCategories = Category.getAllCategories(getApplicationContext());
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);

        thisActivity = this;
        saveCompareFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCompare();
                // TODO: save item
            }
        });

        upperCategoriesList = new ArrayList<CharSequence>();
        upperCategoriesList.add("None");

        for(Category cat : allCategories) {
            int catsize = Item.getItemsCountByCategoryId(getApplicationContext(), cat.getId(), sharedPreferences.getBoolean(Constants.KEY_WISHLIST_IN_COMPARE, false));
            if(catsize > 0) {
                items = Item.getItemsByCategoryId(getApplicationContext(), cat.getId(), sharedPreferences.getBoolean(Constants.KEY_WISHLIST_IN_COMPARE, false));
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getImageFile() != null) {
                        upperCategoriesList.add(cat.getName());
                        break;
                    }
                }
            }

        }
        String tmpString = upperCategoriesList.toString();
        upperCategoriesList2 = stringToArray(tmpString);

        final ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<CharSequence>(
                this,
                android.R.layout.simple_list_item_1,
                upperCategoriesList );

        builder1 = new AlertDialog.Builder(NewCompareActivity.this);
        builder2 = new AlertDialog.Builder(NewCompareActivity.this);

        img1.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder1.setTitle("Choose Category");
                builder1.setItems(upperCategoriesList2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item != 0) {
                            firstCatItems = Item.getItemsByCategoryId(getApplicationContext(), allCategories.get(item - 1).getId(), sharedPreferences.getBoolean(Constants.KEY_WISHLIST_IN_COMPARE, false));
                            for(int i =  0; i  < allCategories.size(); i++){
                                if(allCategories.get(i).getName().equals(upperCategoriesList.get(item))){
                                    CompareImageAdapter adapter = new CompareImageAdapter(thisActivity, i, 1);
                                    viewPager1.setAdapter(adapter);
                                    break;
                                }
                            }
                            img1.setVisibility(View.INVISIBLE);
                            viewPager1.setVisibility(View.VISIBLE);
                                                }
                        dialog.dismiss();
                    }
                });

                builder1.show();


            }
        });

        img2.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {


                builder2.setTitle("Choose Category");
                builder2.setItems(upperCategoriesList2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item != 0) {
                            secontCatItems = Item.getItemsByCategoryId(getApplicationContext(), allCategories.get(item - 1).getId(), sharedPreferences.getBoolean(Constants.KEY_WISHLIST_IN_COMPARE, false));
                            for(int i =  0; i  < allCategories.size(); i++){
                                if(allCategories.get(i).getName().equals(upperCategoriesList.get(item))){
                                    CompareImageAdapter adapter = new CompareImageAdapter(thisActivity, i, 2);
                                    viewPager2.setAdapter(adapter);
                                    break;
                                }
                            }
                            img2.setVisibility(View.INVISIBLE);
                            viewPager2.setVisibility(View.VISIBLE);
                        }
                        dialog.dismiss();
                    }
                });
                builder2.show();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_compare, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void saveCompare() {

        final int firstElementPosition = viewPager1.getCurrentItem();
        final int secondElementPosition = viewPager2.getCurrentItem();

        if(firstCatItems != null && secontCatItems != null) {

            final int firstItemID = firstCatItems.get(firstElementPosition).getId();
            final int secondtItemID = secontCatItems.get(secondElementPosition).getId();


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Get the layout inflater
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
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());
                        name = "Compare " + currentDateandTime;
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

                    setResult(RESULT_OK);
                    thisActivity.finish();
                }
            })
            .setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {


                }
            });

            builder.create().show();
        }
    }
    private String[] stringToArray(String str){
        str = str.substring(1, str.length()-1);
        String[] str2array = str.split(", ");

        return str2array;
    }

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
