package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.DefaultRecyclerGridAdapter;
import com.gruppe1.pem.challengeme.adapters.DefaultRecyclerListAdapter;
import com.gruppe1.pem.challengeme.helpers.CategoryDataSource;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.GridSpacingItemDecoration;
import com.gruppe1.pem.challengeme.helpers.ItemDataSource;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemsListActivity extends AppCompatActivity {
   private ArrayList<ListItemIconName> mDataset;

   private Boolean list;

   private int categoryId;

   private SharedPreferences.Editor editor;
   private SharedPreferences sharedPreferences;

   private Object[] selectedItem;

   private DefaultRecyclerListAdapter defaultRecyclerListAdapter;
   private DefaultRecyclerGridAdapter defaultRecyclerGridAdapter;

   private ItemDataSource itemDataSource;
   private CategoryDataSource categoryDataSource;

   private int categoriesCount = 0;
   @Bind (R.id.menu)
   FloatingActionMenu menu;
   @Bind (R.id.add_item)
   FloatingActionButton fab_add_item;
   @Bind (R.id.add_category)
   FloatingActionButton fab_add_category;
   @Bind (R.id.add_wishlist_item)
   FloatingActionButton fab_add_wishlist_item;
   @Bind (R.id.add_compare)
   FloatingActionButton fab_add_compare;
   @Bind (R.id.noItemLayout)
   RelativeLayout noItemLayout;
   @Bind (R.id.noItemText)
   TextView noItemText;
   @Bind (R.id.listView)
   RecyclerView listView;
   @Bind (R.id.gridView)
   RecyclerView gridView;
   @Bind (R.id.toolbar)
   Toolbar toolbar;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_items_list);
      ButterKnife.bind(this);

      itemDataSource = new ItemDataSource(this);
      categoryDataSource = new CategoryDataSource(this);

      setupToolbar();
      noItemText.setText(R.string.no_items);

      sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
      editor = sharedPreferences.edit();

      list = savedInstanceState == null ||
            savedInstanceState.getBoolean(Constants.KEY_VIEW_ITEMS_AS_LIST, true);

      if (savedInstanceState == null) {
         Bundle extras = getIntent().getExtras();
         if (extras == null) {
            categoryId = -1;
         } else {
            categoryId = extras.getInt(Constants.EXTRA_CATEGORY_ID);
         }
      } else {
         categoryId = -1;
      }

      Category category = categoryDataSource.getCategory(categoryId);
      if (getSupportActionBar() != null) {
         getSupportActionBar().setTitle(category.getName(this));
      }
      //        setTitle(category.getName());
      mDataset = new ArrayList<>();

      initDataset();

      defaultRecyclerListAdapter = new DefaultRecyclerListAdapter(this, R.layout.list_item_default,
            R.layout.list_item_category, mDataset, false, false);
      defaultRecyclerListAdapter.setOnItemClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            itemListActivityOnItemClick(v, listView.getChildPosition(v));
         }
      });
      defaultRecyclerListAdapter.setOnIcMoreClickListener(
            new DefaultRecyclerListAdapter.OnIcMoreClickListener() {

               @Override
               public void onClick(View view, ListItemIconName item) {
                  itemListActivityOnIcMoreClick(listView, view, mDataset.indexOf(item));
               }
            });

      LinearLayoutManager linearLayoutManagerList = new LinearLayoutManager(this.getBaseContext());
      listView.setLayoutManager(linearLayoutManagerList);
      listView.setHasFixedSize(true);
      listView.setAdapter(defaultRecyclerListAdapter);

      defaultRecyclerGridAdapter = new DefaultRecyclerGridAdapter(this, R.layout.grid_item_default,
            R.layout.grid_item_category, mDataset, false);

      defaultRecyclerGridAdapter.setOnItemClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            itemListActivityOnItemClick(v, listView.getChildPosition(v));
         }
      });
      defaultRecyclerGridAdapter.setOnIcMoreClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            itemListActivityOnIcMoreClick(listView, v, (Integer) v.getTag());
         }
      });
      StaggeredGridLayoutManager gridLayoutManager =
            new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
      gridView.setLayoutManager(gridLayoutManager);
      gridView.setHasFixedSize(true);
      int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
      gridView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, false, 0));
      gridView.setAdapter(defaultRecyclerGridAdapter);
      gridView.setVisibility(View.INVISIBLE);

      menu.setVisibility(View.VISIBLE);
      menu.setClosedOnTouchOutside(true);
      fab_add_compare.setLabelText(getString(R.string.title_activity_saved_outfits));
      fab_add_wishlist_item.setLabelText(getString(R.string.title_activity_new_Wishlistitem));
      fab_add_category.setLabelText(getString(R.string.title_activity_new_category));
      fab_add_item.setLabelText(getString(R.string.title_activity_new_item));
      fab_add_compare.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), getPackageName() + ".views.NewCompareActivity");
            startActivity(intent);
            menu.close(false);
         }
      });

      fab_add_wishlist_item.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(),
                  getPackageName() + ".views.CollectionItemsActivity");
            intent.putExtra(Constants.EXTRA_ITEM_IS_WISHLIST, true);
            intent.putExtra(Constants.EXTRA_CATEGORY_ID, categoryId);
            startActivityForResult(intent, 1);
            menu.close(false);
         }
      });
      fab_add_category.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), getPackageName() + ".views.NewCategoryActivity");
            intent.putExtra(Constants.EXTRA_PARENT_CATEGORY_ID, categoryId);
            startActivityForResult(intent, 1);
            menu.close(false);
         }
      });

      fab_add_item.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(),
                  getPackageName() + ".views.CollectionItemsActivity");
            intent.putExtra(Constants.EXTRA_CATEGORY_ID, categoryId);
            startActivityForResult(intent, 1);
            menu.close(false);
         }
      });

      MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_app_id));
      AdView mAdView = (AdView) findViewById(R.id.adView);
      AdRequest adRequest = new AdRequest.Builder().build();
      mAdView.loadAd(adRequest);
   }

   private void setupToolbar() {
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
   }

   /**
    * shows/hides the noCompareLayout
    *
    * @param show boolean if the noCompareLayout should be shown
    */
   private void showNoItemLayout(boolean show) {
      if (show) {
         noItemLayout.setVisibility(View.VISIBLE);
      } else {
         noItemLayout.setVisibility(View.INVISIBLE);
      }
   }

   @Override
   public void onResume() {
      super.onResume();
      list = sharedPreferences.getBoolean(Constants.KEY_VIEW_ITEMS_AS_LIST, true);
      switchListGridView(list);
   }

   @Override
   public void onActivityResult(int p_requestCode, int p_resultCode, Intent p_data) {
      super.onActivityResult(p_requestCode, p_resultCode, p_data);

      System.out.println("onactivityresult");
      if (p_requestCode == 1) {
         // item was updated
         // TODO: get position from data; notifyitemsetchanged
         initDataset();
         defaultRecyclerListAdapter.notifyDataSetChanged();
         defaultRecyclerGridAdapter.notifyDataSetChanged();
      }
   }

   @Override
   public boolean onPrepareOptionsMenu(Menu menu) {
      super.onPrepareOptionsMenu(menu);
      list = sharedPreferences.getBoolean(Constants.KEY_VIEW_ITEMS_AS_LIST, true);
      menu.getItem(0)
            .setIcon(list ? R.drawable.ic_view_grid : R.drawable.ic_view_list);
      return true;
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_items_list, menu);
      return true;
   }

   /**
    * Switches the list/grid view of the items
    *
    * @param shouldBeListView boolean if the items should be shown as list view
    */
   private void switchListGridView(boolean shouldBeListView) {
      if (shouldBeListView) {
         gridView.setVisibility(View.INVISIBLE);
         listView.setVisibility(View.VISIBLE);
      } else {
         listView.setVisibility(View.INVISIBLE);
         gridView.setVisibility(View.VISIBLE);
      }

      list = shouldBeListView;
      editor.putBoolean(Constants.KEY_VIEW_ITEMS_AS_LIST, list);
      editor.commit();
   }

   @Override
   public void onBackPressed() {
      Intent i = new Intent();
      setResult(Activity.RESULT_OK, i);
      this.finish();
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();

      if (id == android.R.id.home) {
         onBackPressed();
         return true;
      } else if (id == R.id.action_switchView) {
         switchListGridView(!list);
         if (list) {
            item.setIcon(R.drawable.ic_view_grid);
         } else {
            item.setIcon(R.drawable.ic_view_list);
         }
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
      // Save currently selected layout manager.
      savedInstanceState.putSerializable(Constants.KEY_VIEW_ITEMS_AS_LIST, list);
      super.onSaveInstanceState(savedInstanceState);
   }

   /**
    * initializes the dataset of compares
    */
   private void initDataset() {
      mDataset.clear();

      List<Category> allBaseCategories =
            categoryDataSource.getCategoriesWithParentCategory(categoryId);

      for (Category tmpCat : allBaseCategories) {
         int iconId = getResources().getIdentifier(tmpCat.getIcon(), "drawable",
               "com.gruppe1.pem.challengeme");
         mDataset.add(
               new ListItemIconName(this, "category", tmpCat.getId(), iconId, tmpCat.getName(this),
                     null));
      }

      categoriesCount = mDataset.size();

      ArrayList<Item> allCategoryItems = itemDataSource.getItemsByCategoryId(categoryId, false);

      for (Item tmpItem : allCategoryItems) {
         int iconId = getResources().getIdentifier("kleiderbuegel", "drawable",
               "com.gruppe1.pem.challengeme");
         mDataset.add(new ListItemIconName(this, "item", tmpItem.getId(), iconId, tmpItem.getName(),
               tmpItem.getImageFile()));
      }
      if (mDataset.size() > 0) {
         showNoItemLayout(false);
      } else {
         showNoItemLayout(true);
      }
   }

   /**
    * Starts the CollectionItemsActivity to show detail informations of an item
    *
    * @param itemid the id of the selected item
    */
   private void selectItem(int itemid, int position) {
      Intent intent = new Intent();
      intent.setClassName(getPackageName(), getPackageName() + ".views.CollectionItemsActivity");

      ArrayList<Item> allCategoryItems = itemDataSource.getItemsByCategoryId(categoryId, false);
      intent.putExtra(Constants.EXTRA_CLICKED_ITEM_POSITION, position);
      Bundle b = new Bundle();
      b.putInt(Constants.EXTRA_ITEM_ID, itemid);
      b.putParcelableArrayList(Constants.EXTRA_ITEM_COLLECTION, allCategoryItems);
      intent.putExtras(b);
      startActivityForResult(intent, 1);
   }

   /**
    * Starts the ItemsListActivity of the category
    *
    * @param categoryId the id of the selected category
    */
   private void selectCategory(int categoryId) {
      Intent intent = new Intent();
      intent.setClassName(getPackageName(), getPackageName() + ".views.ItemsListActivity");
      Bundle b = new Bundle();
      b.putInt(Constants.EXTRA_CATEGORY_ID, categoryId);
      intent.putExtras(b);
      startActivityForResult(intent, 1);
   }

   private void itemListActivityOnItemClick(View view, int position) {
      boolean isCategory = list ? defaultRecyclerListAdapter.getItem(position)
            .isCategoryElement() : defaultRecyclerGridAdapter.getItem(position)
            .isCategoryElement();
      boolean isItem = list ? defaultRecyclerListAdapter.getItem(position)
            .isItemElement() : defaultRecyclerGridAdapter.getItem(position)
            .isItemElement();
      if (isCategory) {
         int catId = list ? (int) defaultRecyclerListAdapter.getItemId(position) :
               (int) defaultRecyclerGridAdapter.getItemId(position);
         selectCategory(catId);
      } else if (isItem) {
         int itemId = list ? (int) defaultRecyclerListAdapter.getItemId(position) :
               (int) defaultRecyclerGridAdapter.getItemId(position);
         selectItem(itemId, position - categoriesCount);
      }
   }

   private boolean itemListActivityOnIcMoreClick(RecyclerView parent, View view,
         final int position) {
      selectedItem = new Object[2];
      selectedItem[0] = position;
      selectedItem[1] = view;

      final AlertDialog.Builder builder = new AlertDialog.Builder(this);
      // Get the layout inflater
      LayoutInflater inflater = getLayoutInflater();

      // Inflate and set the layout for the dialog
      // Pass null as the parent view because its going in the dialog layout

      View dialogView = inflater.inflate(R.layout.dialog_edit, parent, false);
      TextView headline = (TextView) dialogView.findViewById(R.id.dialog_headline);
      headline.setText(mDataset.get(position)
            .getName());

      if (defaultRecyclerListAdapter.getItem(position)
            .isItemElement()) {
         builder.setView(dialogView)
               .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                     int itemId = (int) defaultRecyclerListAdapter.getItemId((int) selectedItem[0]);

                     itemDataSource.deleteItem(itemId);

                     mDataset.remove(position);
                     mDataset.trimToSize();
                     defaultRecyclerListAdapter.notifyItemRemoved(position);
                     defaultRecyclerGridAdapter.notifyItemRemoved(position);
                     defaultRecyclerListAdapter.notifyItemRangeChanged(position, mDataset.size());
                     defaultRecyclerGridAdapter.notifyItemRangeChanged(position, mDataset.size());
                  }
               })
               .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {

                  }
               });

         builder.create()
               .show();
      } else if (defaultRecyclerListAdapter.getItem(position)
            .isCategoryElement()) {
         builder.setView(dialogView)
               .setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                     Intent intent = new Intent();
                     intent.setClassName(getPackageName(),
                           getPackageName() + ".views.NewCategoryActivity");
                     int categoryId =
                           (int) defaultRecyclerListAdapter.getItemId((int) selectedItem[0]);

                     Category category = categoryDataSource.getCategory(categoryId);

                     intent.putExtra(Constants.EXTRA_CATEGORY_ID, category.getId());
                     intent.putExtra(Constants.EXTRA_PARENT_CATEGORY_ID,
                           category.getParentCategoryId());
                     startActivityForResult(intent, 1);
                  }
               })
               .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                     int categoryId =
                           (int) defaultRecyclerListAdapter.getItemId((int) selectedItem[0]);

                     ArrayList<Item> items = itemDataSource.getItemsByCategoryId(categoryId, true);
                     for (Item c : items) {
                        itemDataSource.deleteItem(c.getId());
                     }

                     categoryDataSource.deleteCategory(categoryId);

                     mDataset.remove(position);
                     mDataset.trimToSize();
                     defaultRecyclerListAdapter.notifyItemRemoved(position);
                     defaultRecyclerGridAdapter.notifyItemRemoved(position);
                     defaultRecyclerListAdapter.notifyItemRangeChanged(position, mDataset.size());
                     defaultRecyclerGridAdapter.notifyItemRangeChanged(position, mDataset.size());
                  }
               })
               .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {

                  }
               });

         builder.create()
               .show();
      }

      return true;
   }
}
