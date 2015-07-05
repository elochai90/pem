package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.CompareImageAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class NewCompareActivity extends Activity {

    ViewPager viewPager1;
    ViewPager viewPager2;
    //Spinner view_pager1_spinner;
    //Button newItemCompare;
    ImageView img1;
    ImageView img2;
    //Spinner view_pager2_spinner;
    ArrayList<Item> firstCatItems;
    ArrayList<Item> secontCatItems;
    FloatingActionButton saveCompareFAB;
    ArrayList<Category> allCategories;
    Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_compare);

        viewPager1 = (ViewPager) findViewById(R.id.view_pager1);
        viewPager2 = (ViewPager) findViewById(R.id.view_pager2);
        viewPager1.setVisibility(View.INVISIBLE);
        viewPager2.setVisibility(View.INVISIBLE);
        //view_pager1_spinner = (Spinner) findViewById(R.id.view_pager1_spinner);
        //view_pager2_spinner = (Spinner) findViewById(R.id.view_pager2_spinner);
        saveCompareFAB = (FloatingActionButton) findViewById(R.id.save_compare);
        allCategories = Category.getAllCategories(getApplicationContext());
        //newItemCompare = (Button) findViewById(R.id.new_item_compare);
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

        ArrayList<CharSequence> upperCategoriesList = new ArrayList<CharSequence>();
        upperCategoriesList.add("None");

        for(Category cat : allCategories) {
            upperCategoriesList.add(cat.getName());
        }
        String tmpString = upperCategoriesList.toString();
        final String[] upperCategoriesList2 = stringToArray(tmpString);

        final ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<CharSequence>(
                this,
                android.R.layout.simple_list_item_1,
                upperCategoriesList );

        img1.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder1 = new AlertDialog.Builder(NewCompareActivity.this);

                builder1.setTitle("Choose Category");
                builder1.setItems(upperCategoriesList2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item != 0) {
                            firstCatItems = Item.getItemsByCategoryId(getApplicationContext(), allCategories.get(item - 1).getId());
                            if (firstCatItems.size() > 0) {
                                CompareImageAdapter adapter = new CompareImageAdapter(getApplicationContext(), item); // TODO: richtige Id uebergeben
                                viewPager1.setAdapter(adapter);
                                img1.setVisibility(View.INVISIBLE);
                                viewPager1.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.no_child_items), Toast.LENGTH_SHORT).show();
                            }
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

                final AlertDialog.Builder builder2 = new AlertDialog.Builder(NewCompareActivity.this);
                builder2.setTitle("Choose Category");
                builder2.setItems(upperCategoriesList2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item != 0) {
                            secontCatItems = Item.getItemsByCategoryId(getApplicationContext(), allCategories.get(item - 1).getId());

                            if (secontCatItems.size() > 0) {
                                CompareImageAdapter adapter = new CompareImageAdapter(getApplicationContext(), item); // TODO: richtige Id uebergeben
                                viewPager2.setAdapter(adapter);
                                img2.setVisibility(View.INVISIBLE);
                                viewPager2.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.no_child_items), Toast.LENGTH_SHORT).show();
                            }
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
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());
                        name = "Compare " + currentDateandTime;
                    }

                    Compare newCompare = new Compare(getApplicationContext(), -1);
                    newCompare.setName(name);
                    newCompare.addItemId(firstItemID);
                    newCompare.addItemId(secondtItemID);
                    newCompare.insert();
                    newCompare.closeDBConnection();

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

}
