package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
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

public class SavedComparesDetailActivity extends AppCompatActivity {

   private Compare compareItem;

   private ImageView detail1;
   private ImageView detail2;
   private TextView nameitem1;
   private TextView nameitem2;
   private CardView card_view_item1;
   private CardView card_view_item2;
   private TextView timeStampSavedCompare;

   private ArrayList<Item> compareItems = new ArrayList<>();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_saved_compares_detail);

      setupToolbar();

      detail1 = (ImageView) findViewById(R.id.detail1oben);
      detail2 = (ImageView) findViewById(R.id.detail2unten);
      nameitem1 = (TextView) findViewById(R.id.nameItem1);
      nameitem2 = (TextView) findViewById(R.id.nameItem2);
      card_view_item1 = (CardView) findViewById(R.id.card_view_item1);
      card_view_item2 = (CardView) findViewById(R.id.card_view_item2);
      timeStampSavedCompare = (TextView) findViewById(R.id.timeStampSavedCompare);

      DataBaseHelper dbHelper = new DataBaseHelper(this);
      dbHelper.init();

      compareItem = (Compare) getIntent().getSerializableExtra("item");
      ArrayList<Integer> itemIds = compareItem.getItemIds();
      compareItems.add(new Item(this, itemIds.get(0), dbHelper));
      compareItems.add(new Item(this, itemIds.get(1), dbHelper));

      if (getSupportActionBar() != null) {
         getSupportActionBar().setTitle(compareItem.getName());
      }

      detail1.setImageBitmap(ImageLoader.getPicFromFile(compareItems.get(0)
            .getImageFile(), 500, 500));
      detail2.setImageBitmap(ImageLoader.getPicFromFile(compareItems.get(1)
            .getImageFile(), 500, 500));
      nameitem1.setText(compareItems.get(0)
            .getName());
      nameitem2.setText(compareItems.get(1)
            .getName());

      card_view_item1.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            selectItem(0, 0);
         }
      });
      card_view_item2.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            selectItem(1, 1);
         }
      });
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH);
      String compareCreatedTimestamp =
            sdf.format(new Date(Long.parseLong(compareItem.getTimestamp())));
      timeStampSavedCompare.setText(
            getResources().getString(R.string.compare_saved) + ":  " + compareCreatedTimestamp);

      dbHelper.close();
   }

   private void setupToolbar() {
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
         case android.R.id.home:
            setResult(RESULT_OK);
            finish();
            return true;
         case R.id.delete:

            String strDelCompareTitleFormat =
                  getResources().getString(R.string.compare_delete_overlay);
            String strDelCompareTitle =
                  String.format(strDelCompareTitleFormat, compareItem.getName());

            new AlertDialog.Builder(this).setTitle(strDelCompareTitle)
                  .setNegativeButton(android.R.string.no, null)
                  .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                     public void onClick(DialogInterface arg0, int arg1) {

                        DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
                        dbHelper.init();
                        Compare deleteItem =
                              new Compare(SavedComparesDetailActivity.this, compareItem.getId(),
                                    dbHelper);
                        deleteItem.delete();
                        dbHelper.close();

                        setResult(RESULT_OK);
                        SavedComparesDetailActivity.this.finish();
                     }
                  })
                  .create()
                  .show();
            return true;
         case R.id.edit:
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), getPackageName() + ".views.NewCompareActivity");
            Bundle b = new Bundle();
            b.putInt(Constants.EXTRA_COMPARE_ID, compareItem.getId());
            intent.putExtras(b);
            startActivityForResult(intent, 2);
            return true;
      }
      return super.onOptionsItemSelected(item);
   }

   /**
    * Starts the CollectionItemsActivity to show detail information of an item
    *
    * @param position the position of the item
    */
   private void selectItem(int position, int detailViewIndex) {
      Intent intent = new Intent();
      intent.setClassName(getPackageName(), getPackageName() + ".views.CollectionItemsActivity");

      intent.putExtra(Constants.EXTRA_CLICKED_ITEM_POSITION, position);
      Bundle b = new Bundle();
      b.putInt(Constants.EXTRA_ITEM_ID, compareItems.get(position)
            .getId());
      b.putParcelableArrayList(Constants.EXTRA_ITEM_COLLECTION, compareItems);
      intent.putExtras(b);
      startActivityForResult(intent, detailViewIndex);
   }

   @Override
   public void onActivityResult(int p_requestCode, int p_resultCode, Intent p_data) {
      try {
         super.onActivityResult(p_requestCode, p_resultCode, p_data);

         if (p_resultCode == Activity.RESULT_OK) {
            if (p_requestCode == 0) {
               int itemId = p_data.getIntExtra("itemId", -1);
               String itemImageFile = p_data.getStringExtra("itemImage");
               String itemName = p_data.getStringExtra("itemName");
               detail1.setImageBitmap(ImageLoader.getPicFromFile(itemImageFile, 500, 500));
               nameitem1.setText(itemName);
            } else if (p_requestCode == 1) {
               int itemId = p_data.getIntExtra("itemId", -1);
               String itemImageFile = p_data.getStringExtra("itemImage");
               String itemName = p_data.getStringExtra("itemName");
               detail2.setImageBitmap(ImageLoader.getPicFromFile(itemImageFile, 500, 500));
               nameitem2.setText(itemName);
            } else if (p_requestCode == 2) {
               int compareId = p_data.getIntExtra("compareId", -1);
               int compareItemId1 = p_data.getIntExtra("compareItemId1", -1);
               int compareItemId2 = p_data.getIntExtra("compareItemId2", -1);
               String compareTimestamp = p_data.getStringExtra("compareTimestamp");
               String compareName = p_data.getStringExtra("compareName");

               SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH);
               String compareCreatedTimestamp =
                     sdf.format(new Date(Long.parseLong(compareTimestamp)));
               timeStampSavedCompare.setText(
                     getResources().getString(R.string.compare_saved) + ":  " +
                           compareCreatedTimestamp);
               getSupportActionBar().setTitle(compareName);

               DataBaseHelper dbHelper = new DataBaseHelper(this);
               dbHelper.init();
               compareItems.clear();
               compareItems.add(new Item(this, compareItemId1, dbHelper));
               compareItems.add(new Item(this, compareItemId2, dbHelper));
               compareItem = new Compare(this, compareId, dbHelper);
               dbHelper.close();
               detail1.setImageBitmap(ImageLoader.getPicFromFile(compareItems.get(0)
                     .getImageFile(), 500, 500));
               nameitem1.setText(compareItems.get(0)
                     .getName());
               detail2.setImageBitmap(ImageLoader.getPicFromFile(compareItems.get(1)
                     .getImageFile(), 500, 500));
               nameitem2.setText(compareItems.get(1)
                     .getName());
            }
         }
      } catch (Exception ex) {
      }
   }
}
