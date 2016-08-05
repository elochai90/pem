package com.gruppe1.pem.challengeme.views;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.ColorHelper;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SavedComparesDetailActivity extends AppCompatActivity {

   private Compare compareItem;

   @Bind (R.id.detail1obenSelectableLayer)
   ImageView detail1Layer;
   @Bind (R.id.detail2untenSelectableLayer)
   ImageView detail2Layer;
   @Bind (R.id.detail1oben)
   ImageView detail1;
   @Bind (R.id.detail2unten)
   ImageView detail2;
   @Bind (R.id.card_view_item1)
   CardView card_view_item1;
   @Bind (R.id.card_view_item2)
   CardView card_view_item2;
   @Bind (R.id.timeStampSavedCompare)
   TextView timeStampSavedCompare;
   @Bind (R.id.toolbar)
   Toolbar toolbar;

   PicassoSingleton picassoSingleton;

   private ArrayList<Item> compareItems = new ArrayList<>();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_saved_compares_detail);
      ButterKnife.bind(this);

      picassoSingleton = PicassoSingleton.getInstance(this);

      setupToolbar();

      DataBaseHelper dbHelper = new DataBaseHelper(this);
      dbHelper.init();

      compareItem = (Compare) getIntent().getSerializableExtra("item");
      ArrayList<Integer> itemIds = compareItem.getItemIds();
      compareItems.add(new Item(this, itemIds.get(0), dbHelper));
      compareItems.add(new Item(this, itemIds.get(1), dbHelper));

      if (getSupportActionBar() != null) {
         getSupportActionBar().setTitle(compareItem.getName());
      }

      Category category1 = new Category(this, compareItems.get(0)
            .getCategoryId(), dbHelper);
      Category category2 = new Category(this, compareItems.get(1)
            .getCategoryId(), dbHelper);

      int colorHex1 = ColorHelper.calculateMinDarkColor(category1.getColor());
      int colorHex2 = ColorHelper.calculateMinDarkColor(category2.getColor());
      Drawable icon1 = ColorHelper.filterIconColor(this, category1.getIcon(), colorHex1);
      Drawable icon2 = ColorHelper.filterIconColor(this, category2.getIcon(), colorHex2);
      picassoSingleton.setImageFit(compareItems.get(0)
            .getImageFile(), detail1, icon1, icon1);
      picassoSingleton.setImageFit(compareItems.get(1)
            .getImageFile(), detail2, icon2, icon2);

      detail1Layer.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            selectItem(0, 0);
         }
      });
      detail2Layer.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            selectItem(1, 1);
         }
      });
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH);
      String compareCreatedTimestamp =
            sdf.format(new Date(Long.parseLong(compareItem.getTimestamp())));
      timeStampSavedCompare.setText(
            getResources().getString(R.string.outfit_saved) + " " + compareCreatedTimestamp);

      dbHelper.close();
   }

   private void setupToolbar() {
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
      super.onActivityResult(p_requestCode, p_resultCode, p_data);
      if (p_resultCode != RESULT_OK) {
         return;
      }
      if (p_requestCode == 0) {
         int itemId = p_data.getIntExtra(Constants.EXTRA_ITEM_ID, -1);
         DataBaseHelper dbHelper = new DataBaseHelper(this);
         dbHelper.init();
         compareItems.remove(0);
         compareItems.add(0, new Item(this, itemId, dbHelper));
         Category category1 = new Category(this, compareItems.get(0)
               .getCategoryId(), dbHelper);
         dbHelper.close();
         int colorHex1 = ColorHelper.calculateMinDarkColor(category1.getColor());
         Drawable icon1 = ColorHelper.filterIconColor(this, category1.getIcon(), colorHex1);
         picassoSingleton.setImageFit(compareItems.get(0)
               .getImageFile(), detail1, icon1, icon1);
      } else if (p_requestCode == 1) {
         int itemId = p_data.getIntExtra(Constants.EXTRA_ITEM_ID, -1);
         DataBaseHelper dbHelper = new DataBaseHelper(this);
         dbHelper.init();
         compareItems.remove(1);
         compareItems.add(1, new Item(this, itemId, dbHelper));
         Category category2 = new Category(this, compareItems.get(1)
               .getCategoryId(), dbHelper);
         dbHelper.close();
         int colorHex2 = ColorHelper.calculateMinDarkColor(category2.getColor());
         Drawable icon2 = ColorHelper.filterIconColor(this, category2.getIcon(), colorHex2);
         picassoSingleton.setImageFit(compareItems.get(1)
               .getImageFile(), detail2, icon2, icon2);
      } else if (p_requestCode == 2) {
         int compareId = p_data.getIntExtra("compareId", -1);
         int compareItemId1 = p_data.getIntExtra("compareItemId1", -1);
         int compareItemId2 = p_data.getIntExtra("compareItemId2", -1);
         String compareTimestamp = p_data.getStringExtra("compareTimestamp");
         String compareName = p_data.getStringExtra("compareName");

         SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH);
         String compareCreatedTimestamp = sdf.format(new Date(Long.parseLong(compareTimestamp)));
         timeStampSavedCompare.setText(getResources().getString(R.string.outfit_saved) + " " +
               compareCreatedTimestamp);
         getSupportActionBar().setTitle(compareName);

         DataBaseHelper dbHelper = new DataBaseHelper(this);
         dbHelper.init();
         compareItems.clear();
         compareItems.add(new Item(this, compareItemId1, dbHelper));
         compareItems.add(new Item(this, compareItemId2, dbHelper));
         compareItem = new Compare(this, compareId, dbHelper);

         Category category1 = new Category(this, compareItems.get(0)
               .getCategoryId(), dbHelper);
         Category category2 = new Category(this, compareItems.get(1)
               .getCategoryId(), dbHelper);

         int colorHex1 = ColorHelper.calculateMinDarkColor(category1.getColor());
         int colorHex2 = ColorHelper.calculateMinDarkColor(category2.getColor());
         Drawable icon1 = ColorHelper.filterIconColor(this, category1.getIcon(), colorHex1);
         Drawable icon2 = ColorHelper.filterIconColor(this, category2.getIcon(), colorHex2);
         picassoSingleton.setImageFit(compareItems.get(0)
               .getImageFile(), detail1, icon1, icon1);
         picassoSingleton.setImageFit(compareItems.get(1)
               .getImageFile(), detail2, icon2, icon2);
         dbHelper.close();
      }
   }
}
