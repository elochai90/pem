package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class SavedComparesDetailActivity extends Activity {

    private ImageView detail1;
    private ImageView detail2;
    private TextView item1;
    private TextView item2;
    private TextView compareTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_compares_detail);

        detail1 = (ImageView) findViewById(R.id.detail1oben);
        detail2 = (ImageView) findViewById(R.id.detail2unten);
        item1 = (TextView) findViewById(R.id.nameItem1);
        item2 = (TextView) findViewById(R.id.nameItem2);
        compareTitle = (TextView) findViewById(R.id.comparetitle);

        CompareItem clickedCompare = (CompareItem)getIntent().getSerializableExtra("item");
        detail1.setImageResource(clickedCompare.iconItem1);
        detail2.setImageResource(clickedCompare.iconItem2);
        item1.setText(clickedCompare.nameItem1);
        item2.setText(clickedCompare.nameItem2);
        compareTitle.setText(clickedCompare.name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_saved_compares_detail, menu);
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
