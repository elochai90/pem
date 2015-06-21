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
        ArrayList<Category> allCategories = Category.getAllCategories(this);
        System.out.println(allCategories.toString());
        upperCategoriesList.add("None");
        for(Category cat : allCategories) {
            upperCategoriesList.add(cat.getName());
        }
        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(getBaseContext(), android.R.layout.simple_spinner_item,
                upperCategoriesList);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        view_pager1_spinner.setAdapter(spinnerAdapter);
        view_pager2_spinner.setAdapter(spinnerAdapter);

        view_pager1_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    CompareImageAdapter adapter = new CompareImageAdapter(getApplicationContext(), 0); // TODO: richtige Id übergeben
                    viewPager1.setAdapter(adapter);
                    view_pager1_spinner.setVisibility(View.INVISIBLE);
                    viewPager1.setVisibility(View.VISIBLE);
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
                    CompareImageAdapter adapter = new CompareImageAdapter(getApplicationContext(), 0); // TODO: richtige Id übergeben
                    viewPager2.setAdapter(adapter);
                    view_pager2_spinner.setVisibility(View.INVISIBLE);
                    viewPager2.setVisibility(View.VISIBLE);
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
        getMenuInflater().inflate(R.menu.menu_saved_compares, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
