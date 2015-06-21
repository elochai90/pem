package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class NewCategoryActivity extends Activity {

    private Button saveButton;
    private EditText newCategory_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        newCategory_name = (EditText) findViewById(R.id.editNameCategory);
        saveButton = (Button) findViewById(R.id.save_new_category);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                DataBaseHelper db_helper = new DataBaseHelper(getApplicationContext());
                db_helper.init();
                Category testCategory = new Category(0, db_helper);
                testCategory.setName(newCategory_name.getText().toString());
                testCategory.save();
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
