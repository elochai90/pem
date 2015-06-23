package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;


public class NewCategoryActivity extends Activity {

    private com.github.clans.fab.FloatingActionButton saveFAB;
    private EditText newCategory_name;
    private Spinner editUpperCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        final Activity thisActivity = this;

        newCategory_name = (EditText) findViewById(R.id.editNameCategory);
        editUpperCategory = (Spinner) findViewById(R.id.editUpperCategory);
        saveFAB = (FloatingActionButton) findViewById(R.id.save_new_category);
        saveFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseHelper db_helper = new DataBaseHelper(getApplicationContext());
                db_helper.init();
                Category testCategory = new Category(thisActivity.getApplicationContext(), 0, db_helper);
                testCategory.setName(newCategory_name.getText().toString());
                testCategory.setParentCategoryId(0); // TODO: real ParentCatId
                testCategory.save();

                // sending new Item back to CategoriesFragment for actualizing list view
                Intent i = new Intent();
                i.putExtra("categoryName", testCategory.getName()); // TODO: pass all data back to catFragment to show list item
                i.putExtra("categoryId", testCategory.getId()); // TODO: pass real Id back
                i.putExtra("categoryParentId", 0); // TODO: pass real parentId back

                setResult(Activity.RESULT_OK, i);
                thisActivity.finish();
            }
        });

        ArrayList<CharSequence> upperCategoriesList = new ArrayList<CharSequence>();
        ArrayList<Category> allCategories = Category.getAllCategories(this);
        System.out.println(allCategories.toString());
        upperCategoriesList.add("None");

        for(Category cat : allCategories) {
            upperCategoriesList.add(cat.getName());
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getBaseContext(), android.R.layout.simple_spinner_item,
                upperCategoriesList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        editUpperCategory.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_category, menu);
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
