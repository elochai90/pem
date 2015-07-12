package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class SavedComparesDetailActivity extends Activity {

    private Compare compareItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_compares_detail);

        ImageView detail1 = (ImageView) findViewById(R.id.detail1oben);
        ImageView detail2 = (ImageView) findViewById(R.id.detail2unten);
        TextView nameitem1 = (TextView) findViewById(R.id.nameItem1);
        TextView nameitem2 = (TextView) findViewById(R.id.nameItem2);
        CardView card_view_item1 = (CardView) findViewById(R.id.card_view_item1);
        CardView card_view_item2 = (CardView) findViewById(R.id.card_view_item2);
        TextView timeStampSavedCompare = (TextView) findViewById(R.id.timeStampSavedCompare);

        DataBaseHelper dbHelper = new DataBaseHelper(this);
        dbHelper.init();

        compareItem = (Compare)getIntent().getSerializableExtra("item");
        ArrayList<Integer> itemIds = compareItem.getItemIds();
        final Item item1 = new Item(this, itemIds.get(0), dbHelper);
        final Item item2 = new Item(this, itemIds.get(1), dbHelper);

        if(getActionBar() != null) {
            getActionBar().setTitle(compareItem.getName());
        }

        detail1.setImageBitmap(ImageLoader.getPicFromFile(item1.getImageFile(), 500, 500));
        detail2.setImageBitmap(ImageLoader.getPicFromFile(item2.getImageFile(), 500, 500));
        nameitem1.setText(item1.getName());
        nameitem2.setText(item2.getName());

        card_view_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(item1.getId());
            }
        });
        card_view_item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(item2.getId());
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH);
        String compareCreatedTimestamp = sdf.format(new Date(Long.parseLong(compareItem.getTimestamp())));
        timeStampSavedCompare.setText(getResources().getString(R.string.compare_saved) + ":  " + compareCreatedTimestamp);

        dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_saved_compares_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:

                new AlertDialog.Builder(this)
                        .setTitle("Do you really want to delete '" + compareItem.getName() + "'?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {

                                DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
                                dbHelper.init();
                                Compare deleteItem = new Compare(SavedComparesDetailActivity.this, compareItem.getId(), dbHelper);
                                deleteItem.delete();
                                dbHelper.close();

                                setResult(RESULT_OK);
                                SavedComparesDetailActivity.this.finish();
                            }
                        }).create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Starts the NewItemActivity to show detail information of an item
     * @param itemid the id of the selected item
     */
    private void selectItem(int itemid) {
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), getPackageName() + ".views.NewItemActivity");
        Bundle b = new Bundle();
        b.putInt(Constants.EXTRA_ITEM_ID, itemid);
        intent.putExtras(b);
        startActivityForResult(intent, 1);
    }


    @Override
    public void onActivityResult(int p_requestCode, int p_resultCode, Intent p_data) {
        super.onActivityResult(p_requestCode, p_resultCode, p_data);

        if(p_requestCode == 1) {
            // TODO: update item
        }
    }

}
