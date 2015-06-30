package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class SavedComparesDetailActivity extends Activity {

    private ImageView detail1;
    private ImageView detail2;
    private TextView nameitem1;
    private TextView nameitem2;
    private TextView compareTitle;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_compares_detail);

        detail1 = (ImageView) findViewById(R.id.detail1oben);
        detail2 = (ImageView) findViewById(R.id.detail2unten);
        nameitem1 = (TextView) findViewById(R.id.nameItem1);
        nameitem2 = (TextView) findViewById(R.id.nameItem2);
        compareTitle = (TextView) findViewById(R.id.comparetitle);

        this.dbHelper = new DataBaseHelper(this);
        this.dbHelper.init();

        Compare item = (Compare)getIntent().getSerializableExtra("item");
        ArrayList<Integer> itemIds = item.getItemIds();
        Item item1 = new Item(this, itemIds.get(0), dbHelper);
        Item item2 = new Item(this, itemIds.get(1), this.dbHelper);

        detail1.setImageBitmap(ImageLoader.getPicFromFile(item1.getImageFile(), 100, 100));
        detail2.setImageBitmap(ImageLoader.getPicFromFile(item2.getImageFile(), 100, 100));
        nameitem1.setText(item1.getName());
        nameitem2.setText(item2.getName());
        compareTitle.setText(item.getName());
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
