package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class NewCompareActivity extends Activity {

    ViewPager viewPager1;
    ViewPager viewPager2;
    Spinner view_pager1_spinner;
    Spinner view_pager2_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_compare);

        viewPager1 = (ViewPager) findViewById(R.id.view_pager1);
        viewPager2 = (ViewPager) findViewById(R.id.view_pager2);
        viewPager1.setVisibility(View.INVISIBLE);
        viewPager2.setVisibility(View.INVISIBLE);
        view_pager1_spinner = (Spinner) findViewById(R.id.view_pager1_spinner);
        view_pager2_spinner = (Spinner) findViewById(R.id.view_pager2_spinner);


        ArrayList<CharSequence> upperCategoriesList = new ArrayList<CharSequence>();
        final ArrayList<Category> allCategories = Category.getAllCategories(this);

        upperCategoriesList.add("None");
        for(Category cat : allCategories) {
            upperCategoriesList.add(cat.getName());
        }
        ArrayAdapter<CharSequence> spinnerAdapter1 = new ArrayAdapter<CharSequence>(getBaseContext(), android.R.layout.simple_spinner_item, upperCategoriesList);
        ArrayAdapter<CharSequence> spinnerAdapter2 = new ArrayAdapter<CharSequence>(getBaseContext(), android.R.layout.simple_spinner_item, upperCategoriesList);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        view_pager1_spinner.setAdapter(spinnerAdapter1);
        view_pager2_spinner.setAdapter(spinnerAdapter2);

        view_pager1_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0){
                    ArrayList<Item> childItems = Item.getItemsByCategoryId(getApplicationContext(),allCategories.get(position - 1).getId());

                    if( childItems.size() > 0) {
                        CompareImageAdapter adapter = new CompareImageAdapter(getApplicationContext(), position); // TODO: richtige Id uebergeben
                        viewPager1.setAdapter(adapter);
                        view_pager1_spinner.setVisibility(View.INVISIBLE);
                        viewPager1.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_child_items), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        view_pager2_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
                    ArrayList<Item> childItems = Item.getItemsByCategoryId(getApplicationContext(), allCategories.get(position - 1).getId());

                    if( childItems.size() > 0) {
                        CompareImageAdapter adapter = new CompareImageAdapter(getApplicationContext(), position); // TODO: richtige Id uebergeben
                        viewPager2.setAdapter(adapter);
                        view_pager2_spinner.setVisibility(View.INVISIBLE);
                        viewPager2.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_child_items), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
}
