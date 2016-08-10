package com.gruppe1.pem.challengeme.views;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Color;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.DefaultRecyclerListAdapter;
import com.gruppe1.pem.challengeme.helpers.CategoryDataSource;
import com.gruppe1.pem.challengeme.helpers.ColorDataSource;
import com.gruppe1.pem.challengeme.helpers.ColorHelper;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.ItemDataSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bianka on 13.10.2015.
 */
public class SearchResultsActivity extends AppCompatActivity {

   private ArrayList<ListItemIconName> mDataset;

   @Bind (R.id.noItemLayout)
   RelativeLayout noItemLayout;
   @Bind (R.id.noItemText)
   TextView noItemText;
   @Bind (R.id.noItemArrow)
   ImageView noItemArrow;
   @Bind (R.id.listView)
   RecyclerView listView;
   @Bind (R.id.gridView)
   RecyclerView gridView;
   @Bind (R.id.filterColorLayout)
   LinearLayout filterColorLayout;
   @Bind (R.id.filterColorLayoutFirstLine)
   LinearLayout filterColorLayoutFirstLine;
   @Bind (R.id.filterColorLayoutSecondLine)
   LinearLayout filterColorLayoutSecondLine;
   @Bind (R.id.filterCategoryLayout)
   LinearLayout filterCategoryLayout;
   @Bind (R.id.filterCategoryLayoutFirstLine)
   LinearLayout filterCategoryLayoutFirstLine;
   @Bind (R.id.filterCategoryLayoutSecondLine)
   LinearLayout filterCategoryLayoutSecondLine;
   @Bind (R.id.filterRatingLayout)
   LinearLayout filterRatingLayout;
   @Bind (R.id.filterWishlist)
   ImageButton filterWishlist;
   @Bind (R.id.filterRating)
   ImageButton filterRating;
   @Bind (R.id.filterColor)
   ImageButton filterColor;
   @Bind (R.id.filterCategory)
   ImageButton filterCategory;
   @Bind (R.id.toolbar)
   Toolbar toolbar;

   private DefaultRecyclerListAdapter defaultRecyclerListAdapter;
   private String query;

   private boolean filterWishlistActivated;
   private boolean filterRatingActivated;
   private boolean filterColorActivated;
   private boolean filterCategoryActivated;

   private ArrayList<Integer> filterColorIds;
   private HashSet<Integer> filterCategoryIds;
   private int filterRatingCount;

   private ItemDataSource itemDataSource;
   private CategoryDataSource categoryDataSource;
   private ColorDataSource colorDataSource;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_search_results);
      ButterKnife.bind(this);

      itemDataSource = new ItemDataSource(this);
      categoryDataSource = new CategoryDataSource(this);
      colorDataSource = new ColorDataSource(this);

      setupToolbar();

      mDataset = new ArrayList<>();
      query = "";
      filterWishlistActivated = false;
      filterRatingCount = -1;
      filterColorIds = new ArrayList<>();
      filterCategoryIds = new HashSet<>();

      defaultRecyclerListAdapter = new DefaultRecyclerListAdapter(this, R.layout.list_item_default,
            R.layout.list_item_category, mDataset, false, false);
      defaultRecyclerListAdapter.setOnItemClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            searchResultsActivityOnItemClick(v, listView.getChildPosition(v));
         }
      });
      defaultRecyclerListAdapter.setOnIcMoreClickListener(
            new DefaultRecyclerListAdapter.OnIcMoreClickListener() {

               @Override
               public void onClick(View view, ListItemIconName item) {
                  searchResultsActivityOnIcMoreClick(listView, view, mDataset.indexOf(item));
               }
            });

      LinearLayoutManager linearLayoutManagerList = new LinearLayoutManager(getBaseContext());
      listView.setLayoutManager(linearLayoutManagerList);
      listView.setHasFixedSize(true);
      listView.setAdapter(defaultRecyclerListAdapter);
      gridView.setVisibility(View.GONE);

      initDataset();
      setupFilterViews();
   }

   private void setRatingFilter(int rating) {
      for (int i = 0; i < 5; i++) {
         ImageButton tmpRatingButton = (ImageButton) filterRatingLayout.getChildAt(i);
         if (i < rating) {
            tmpRatingButton.setBackground(getDrawable(R.drawable.ic_star_white));
         } else {
            tmpRatingButton.setBackground(getDrawable(R.drawable.ic_star_border_white));
         }
      }
   }

   private void deselectAllColorFilter() {
      for (int i = 0; i < filterColorLayoutFirstLine.getChildCount(); i++) {
         ImageButton tmpColorButton = (ImageButton) filterColorLayoutFirstLine.getChildAt(i);
         String tmpColor = (String) tmpColorButton.getTag();
         tmpColorButton.setBackground(getDrawable(R.drawable.circle_color));
         tmpColorButton.getBackground()
               .setColorFilter(
                     new PorterDuffColorFilter(android.graphics.Color.parseColor(tmpColor),
                           PorterDuff.Mode.MULTIPLY));
      }
      for (int i = 0; i < filterColorLayoutSecondLine.getChildCount(); i++) {
         ImageButton tmpColorButton = (ImageButton) filterColorLayoutSecondLine.getChildAt(i);
         String tmpColor = (String) tmpColorButton.getTag();
         tmpColorButton.setBackground(getDrawable(R.drawable.circle_color));
         tmpColorButton.getBackground()
               .setColorFilter(
                     new PorterDuffColorFilter(android.graphics.Color.parseColor(tmpColor),
                           PorterDuff.Mode.MULTIPLY));
      }
   }

   private void deselectAllCategoryFilter() {
      for (int i = 0; i < filterCategoryLayoutFirstLine.getChildCount(); i++) {
         ImageButton tmpCategoryButton = (ImageButton) filterCategoryLayoutFirstLine.getChildAt(i);
         String tmpIcon = (String) tmpCategoryButton.getTag();
         int iconId =
               getResources().getIdentifier(tmpIcon, "drawable", "com.gruppe1.pem.challengeme");
         LayerDrawable newDrawableIcon =
               (LayerDrawable) getDrawable(R.drawable.icon_category_circle);
         newDrawableIcon.setDrawableByLayerId(R.id.circle_icon, getDrawable(iconId));
         tmpCategoryButton.setBackground(newDrawableIcon);
      }
      for (int i = 0; i < filterCategoryLayoutSecondLine.getChildCount(); i++) {
         ImageButton tmpCategoryButton = (ImageButton) filterCategoryLayoutSecondLine.getChildAt(i);
         String tmpIcon = (String) tmpCategoryButton.getTag();
         int iconId =
               getResources().getIdentifier(tmpIcon, "drawable", "com.gruppe1.pem.challengeme");
         LayerDrawable newDrawableIcon =
               (LayerDrawable) getDrawable(R.drawable.icon_category_circle);
         newDrawableIcon.setDrawableByLayerId(R.id.circle_icon, getDrawable(iconId));
         tmpCategoryButton.setBackground(newDrawableIcon);
      }
   }

   private void setupCategoryFilterViews() {
      int colorIconWidthHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
            getResources().getDisplayMetrics());
      int colorIconMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
            getResources().getDisplayMetrics());
      List<Category> allCategories = categoryDataSource.getAllBaseCategories();
      int positionCategory = 0;
      for (final Category tmpCat : allCategories) {
         ImageButton tmpCategoryButton = new ImageButton(this);
         LinearLayout.LayoutParams attibuteNameLayoutParams =
               new LinearLayout.LayoutParams(colorIconWidthHeight, colorIconWidthHeight);
         attibuteNameLayoutParams.setMargins(0, 0, colorIconMargin, 0);
         tmpCategoryButton.setLayoutParams(attibuteNameLayoutParams);

         int iconId = getResources().getIdentifier(tmpCat.getIcon(), "drawable",
               "com.gruppe1.pem.challengeme");
         LayerDrawable newDrawableIcon =
               (LayerDrawable) getDrawable(R.drawable.icon_category_circle);
         newDrawableIcon.setDrawableByLayerId(R.id.circle_icon, getDrawable(iconId));
         tmpCategoryButton.setBackground(newDrawableIcon);
         tmpCategoryButton.setTag(tmpCat.getIcon());

         tmpCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (filterCategoryIds.contains(tmpCat.getId())) {
                  filterCategoryIds.remove(tmpCat.getId());
                  for (Integer id : categoryDataSource.getAllUnderCategoryIds(tmpCat.getId())) {
                     filterCategoryIds.remove(id);
                  }
                  int iconId = getResources().getIdentifier(tmpCat.getIcon(), "drawable",
                        "com.gruppe1.pem.challengeme");
                  LayerDrawable newDrawableIcon =
                        (LayerDrawable) getDrawable(R.drawable.icon_category_circle);
                  newDrawableIcon.setDrawableByLayerId(R.id.circle_icon, getDrawable(iconId));
                  v.setBackground(newDrawableIcon);
               } else {
                  filterCategoryIds.add(tmpCat.getId());
                  LayerDrawable newDrawableIcon =
                        (LayerDrawable) getDrawable(R.drawable.icon_category_circle_filled);
                  int colorHex = getResources().getColor(R.color.primary);
                  newDrawableIcon.setDrawableByLayerId(R.id.circle_icon,
                        ColorHelper.filterIconColor(SearchResultsActivity.this, tmpCat.getIcon(),
                              colorHex));
                  v.setBackground(newDrawableIcon);
               }
               initDataset();
            }
         });
         if (allCategories.size() > 9) {
            if (positionCategory < allCategories.size() / 2) {
               filterCategoryLayoutFirstLine.addView(tmpCategoryButton);
            } else {
               filterCategoryLayoutSecondLine.addView(tmpCategoryButton);
            }
         } else {
            filterCategoryLayoutFirstLine.addView(tmpCategoryButton);
         }
         positionCategory++;
      }
      filterCategory.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (filterRatingActivated) {
               if (filterRatingCount == -1) {
                  filterRating.callOnClick();
               } else {
                  filterRatingLayout.setVisibility(View.GONE);
                  filterRatingActivated = false;
               }
            }
            if (filterColorActivated) {
               if (filterColorIds.size() == 0) {
                  filterColor.callOnClick();
               } else {
                  filterColorLayout.setVisibility(View.GONE);
                  filterColorActivated = false;
               }
            }
            if (filterCategoryActivated) {
               filterCategoryIds.clear();
               deselectAllCategoryFilter();
               filterCategory.setBackground(getDrawable(R.drawable.icon_category_circle));
               filterCategoryLayout.setVisibility(View.GONE);
               filterCategoryActivated = false;
               initDataset();
            } else {
               LayerDrawable newDrawableIcon =
                     (LayerDrawable) getDrawable(R.drawable.icon_category_circle_filled);
               int colorHex = getResources().getColor(R.color.primary);
               newDrawableIcon.setDrawableByLayerId(R.id.circle_icon,
                     ColorHelper.filterIconColor(SearchResultsActivity.this, "kleiderbuegel",
                           colorHex));
               filterCategory.setBackground(newDrawableIcon);
               filterCategoryLayout.setVisibility(View.VISIBLE);
               filterCategoryActivated = true;
            }
         }
      });
   }

   private void setupColorFilterViews() {
      int colorIconWidthHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
            getResources().getDisplayMetrics());
      int colorIconMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
            getResources().getDisplayMetrics());
      List<Color> allColors = colorDataSource.getAllColors();
      int positionColor = 0;
      for (final Color tmpColor : allColors) {
         ImageButton tmpColorButton = new ImageButton(this);
         LinearLayout.LayoutParams attibuteNameLayoutParams =
               new LinearLayout.LayoutParams(colorIconWidthHeight, colorIconWidthHeight);
         attibuteNameLayoutParams.setMargins(0, 0, colorIconMargin, 0);
         tmpColorButton.setLayoutParams(attibuteNameLayoutParams);
         tmpColorButton.setBackground(getDrawable(R.drawable.circle_color));
         tmpColorButton.getBackground()
               .setColorFilter(new PorterDuffColorFilter(
                     android.graphics.Color.parseColor((tmpColor).getHexColor()),
                     PorterDuff.Mode.MULTIPLY));
         tmpColorButton.setTag(tmpColor.getHexColor());
         tmpColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (filterColorIds.contains(tmpColor.getId())) {
                  filterColorIds.remove(filterColorIds.indexOf(tmpColor.getId()));
                  v.setBackground(getDrawable(R.drawable.circle_color));
                  v.getBackground()
                        .setColorFilter(new PorterDuffColorFilter(
                              android.graphics.Color.parseColor((tmpColor).getHexColor()),
                              PorterDuff.Mode.MULTIPLY));
               } else {
                  filterColorIds.add(tmpColor.getId());
                  v.setBackground(getDrawable(R.drawable.circle_color_filled));
                  ((LayerDrawable) v.getBackground()).getDrawable(0)
                        .setColorFilter(new PorterDuffColorFilter(
                              android.graphics.Color.parseColor((tmpColor).getHexColor()),
                              PorterDuff.Mode.MULTIPLY));
               }
               initDataset();
            }
         });
         if (positionColor < allColors.size() / 2) {
            filterColorLayoutFirstLine.addView(tmpColorButton);
         } else {
            filterColorLayoutSecondLine.addView(tmpColorButton);
         }
         positionColor++;
      }
      filterColor.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (filterRatingActivated) {
               if (filterRatingCount == -1) {
                  filterRating.callOnClick();
               } else {
                  filterRatingLayout.setVisibility(View.GONE);
                  filterRatingActivated = false;
               }
            }
            if (filterCategoryActivated) {
               if (filterCategoryIds.size() == 0) {
                  filterCategory.callOnClick();
               } else {
                  filterCategoryLayout.setVisibility(View.GONE);
                  filterCategoryActivated = false;
               }
            }
            if (filterColorActivated) {
               filterColorIds.clear();
               deselectAllColorFilter();
               filterColor.setBackground(getDrawable(R.drawable.icon_color_circle));
               filterColorLayout.setVisibility(View.GONE);
               filterColorActivated = false;
               initDataset();
            } else {
               LayerDrawable newDrawableIcon =
                     (LayerDrawable) getDrawable(R.drawable.icon_category_circle_filled);
               int colorHex = getResources().getColor(R.color.primary);
               newDrawableIcon.setDrawableByLayerId(R.id.circle_icon,
                     ColorHelper.filterIconColor(SearchResultsActivity.this, "ic_color_lens_white",
                           colorHex));
               filterColor.setBackground(newDrawableIcon);
               filterColorLayout.setVisibility(View.VISIBLE);
               filterColorActivated = true;
            }
         }
      });
   }

   private void setupWishlistFilterViews() {
      filterWishlist.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (filterWishlistActivated) {
               filterWishlist.setBackground(getDrawable(R.drawable.icon_wishlist_circle));
            } else {
               LayerDrawable newDrawableIcon =
                     (LayerDrawable) getDrawable(R.drawable.icon_wishlist_circle_filled);
               int colorHex = getResources().getColor(R.color.primary);
               newDrawableIcon.setDrawableByLayerId(R.id.circle_icon,
                     ColorHelper.filterIconColor(SearchResultsActivity.this, "ic_wishlist_white",
                           colorHex));
               filterWishlist.setBackground(newDrawableIcon);
            }
            filterWishlistActivated = !filterWishlistActivated;
            initDataset();
         }
      });
   }

   private void setupRatingFilterViews() {
      int colorIconWidthHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
            getResources().getDisplayMetrics());
      int colorIconMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
            getResources().getDisplayMetrics());
      for (int i = 1; i <= 5; i++) {
         ImageButton tmpRatingButton = new ImageButton(this);
         LinearLayout.LayoutParams attibuteNameLayoutParams =
               new LinearLayout.LayoutParams(colorIconWidthHeight, colorIconWidthHeight);
         attibuteNameLayoutParams.setMargins(0, 0, colorIconMargin, 0);
         tmpRatingButton.setLayoutParams(attibuteNameLayoutParams);
         tmpRatingButton.setBackground(getDrawable(R.drawable.ic_star_border_white));
         tmpRatingButton.setTag(i);
         final int ratingWhenClicked = i;
         tmpRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               filterRatingCount = ratingWhenClicked;
               setRatingFilter(ratingWhenClicked);
               initDataset();
            }
         });
         filterRatingLayout.addView(tmpRatingButton);
      }
      filterRating.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (filterColorActivated) {
               if (filterColorIds.size() == 0) {
                  filterColor.callOnClick();
               } else {
                  filterColorLayout.setVisibility(View.GONE);
                  filterColorActivated = false;
               }
            }
            if (filterCategoryActivated) {
               if (filterCategoryIds.size() == 0) {
                  filterCategory.callOnClick();
               } else {
                  filterCategoryLayout.setVisibility(View.GONE);
                  filterCategoryActivated = false;
               }
            }
            if (filterRatingActivated) {
               filterRatingCount = -1;
               setRatingFilter(0);
               filterRating.setBackground(getDrawable(R.drawable.icon_rating_circle));
               filterRatingLayout.setVisibility(View.GONE);
               filterRatingActivated = false;
               initDataset();
            } else {
               LayerDrawable newDrawableIcon =
                     (LayerDrawable) getDrawable(R.drawable.icon_category_circle_filled);
               int colorHex = getResources().getColor(R.color.primary);
               newDrawableIcon.setDrawableByLayerId(R.id.circle_icon,
                     ColorHelper.filterIconColor(SearchResultsActivity.this, "ic_star_border_white",
                           colorHex));
               filterRating.setBackground(newDrawableIcon);
               filterRatingLayout.setVisibility(View.VISIBLE);
               filterRatingActivated = true;
            }
         }
      });
   }

   private void setupFilterViews() {
      setupColorFilterViews();
      setupRatingFilterViews();
      setupWishlistFilterViews();
      setupCategoryFilterViews();
   }

   private void setupToolbar() {
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
   }

   @Override
   public boolean onPrepareOptionsMenu(Menu menu) {
      LayoutInflater inflator =
            (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View v = inflator.inflate(R.layout.action_bar_search, null);
      getSupportActionBar().setCustomView(v);
      return true;
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_search_results_activity, menu);

      // Associate searchable configuration with the SearchView
      SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
      final SearchView searchView = (SearchView) menu.findItem(R.id.search)
            .getActionView();

      searchView.setIconified(false);

      searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
         @Override
         public void onFocusChange(View view, boolean queryTextFocused) {
         }
      });
      searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
         @Override
         public boolean onQueryTextSubmit(String newQuery) {
            query = newQuery;
            initDataset();
            return true;
         }

         @Override
         public boolean onQueryTextChange(String newText) {
            query = newText;
            initDataset();
            return true;
         }
      });
      searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

      return true;
   }

   @Override
   public void onActivityResult(int p_requestCode, int p_resultCode, Intent p_data) {
      super.onActivityResult(p_requestCode, p_resultCode, p_data);

      if (p_requestCode == 1) {
         // item was updated
         initDataset();
         defaultRecyclerListAdapter.notifyDataSetChanged();
      }
   }

   /**
    * shows/hides the noCompareLayout
    *
    * @param show boolean if the noCompareLayout should be shown
    */
   private void showNoItemLayout(boolean show) {
      if (show) {
         noItemLayout.setVisibility(View.VISIBLE);
         noItemArrow.setVisibility(View.GONE);
         String strNoResultsFormat = getResources().getString(R.string.no_search_results);
         String strNoResultsMsg = String.format(strNoResultsFormat, query);
         noItemText.setText(strNoResultsMsg);
      } else {
         noItemLayout.setVisibility(View.INVISIBLE);
      }
   }

   /**
    * initializes the dataset of compares
    */
   private void initDataset() {
      mDataset.clear();
      ArrayList<Item> searchResultItems =
            itemDataSource.getSearchResults(query, filterCategoryIds, filterWishlistActivated,
                  filterRatingCount, filterColorIds);

      for (Item tmpItem : searchResultItems) {
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
      defaultRecyclerListAdapter.notifyDataSetChanged();
   }

   /**
    * Starts the CollectionItemsActivity to show detail informations of an item
    *
    * @param itemid the id of the selected item
    */
   private void selectItem(int itemid, int position) {
      ArrayList<Item> searchResultItems =
            itemDataSource.getSearchResults(query, filterCategoryIds, filterWishlistActivated,
                  filterRatingCount, filterColorIds);

      Intent intent = new Intent();
      intent.setClassName(getPackageName(), getPackageName() + ".views.CollectionItemsActivity");

      intent.putExtra(Constants.EXTRA_CLICKED_ITEM_POSITION, position);
      Bundle b = new Bundle();
      b.putInt(Constants.EXTRA_ITEM_ID, itemid);
      b.putParcelableArrayList(Constants.EXTRA_ITEM_COLLECTION, searchResultItems);
      intent.putExtras(b);
      startActivityForResult(intent, 1);
   }

   private void searchResultsActivityOnItemClick(View view, int position) {
      int itemid = (int) defaultRecyclerListAdapter.getItemId(position);
      selectItem(itemid, position);
   }

   private boolean searchResultsActivityOnIcMoreClick(RecyclerView parent, View view,
         final int position) {

      final AlertDialog.Builder builder = new AlertDialog.Builder(this);
      LayoutInflater inflater = getLayoutInflater();
      View dialogView = inflater.inflate(R.layout.dialog_edit, parent, false);
      TextView headline = (TextView) dialogView.findViewById(R.id.dialog_headline);
      headline.setText(mDataset.get(position)
            .getName());

      builder.setView(dialogView)
            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                  int itemId = (int) defaultRecyclerListAdapter.getItemId(position);

                  itemDataSource.deleteItem(itemId);

                  mDataset.remove(position);
                  mDataset.trimToSize();
                  defaultRecyclerListAdapter.notifyItemRemoved(position);
                  defaultRecyclerListAdapter.notifyItemRangeChanged(position, mDataset.size());
               }
            })
            .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {

               }
            });

      builder.create()
            .show();

      return true;
   }
}
