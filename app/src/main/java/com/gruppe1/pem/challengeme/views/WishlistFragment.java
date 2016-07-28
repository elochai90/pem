package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.DefaultRecyclerListAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WishlistFragment extends Fragment {

   private ArrayList<ListItemIconName> mDataset;
   private Object[] selectedItem;

   @Bind (R.id.noItemLayout)
   RelativeLayout noWishlistItemLayout;
   @Bind (R.id.listView)
   RecyclerView listView;
   @Bind (R.id.gridView)
   RecyclerView gridView;
   @Bind (R.id.frameLayout)
   FrameLayout frameLayout;
   @Bind (R.id.noItemText)
   TextView noWishlistItemText;

   DefaultRecyclerListAdapter defaultRecyclerListAdapter;

   private DataBaseHelper db_helper;

   public static WishlistFragment newInstance(int page, String title) {
      WishlistFragment wishlistFragment = new WishlistFragment();
      return wishlistFragment;
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);

      View rootView = inflater.inflate(R.layout.default_recycler_view, container, false);
      ButterKnife.bind(this, rootView);

      db_helper = new DataBaseHelper(getActivity());
      db_helper.init();

      mDataset = new ArrayList<>();

      noWishlistItemText.setText(R.string.no_wishlist_items);

      gridView.setVisibility(View.GONE);

      LinearLayoutManager linearLayoutManagerList =
            new LinearLayoutManager(getActivity().getBaseContext());
      listView.setLayoutManager(linearLayoutManagerList);
      listView.setHasFixedSize(true);

      initDataset();

      defaultRecyclerListAdapter =
            new DefaultRecyclerListAdapter(getActivity(), R.layout.list_item_default,
                  R.layout.list_item_category, mDataset, false, true);
      defaultRecyclerListAdapter.setOnItemClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            wishlistFragmentOnItemClick(v, listView.getChildPosition(v));
         }
      });
      defaultRecyclerListAdapter.setOnIcMoreClickListener(
            new DefaultRecyclerListAdapter.OnIcMoreClickListener() {

               @Override
               public void onClick(View v, ListItemIconName item) {
                  wishlistFragmentOnIcMoreClick(listView, v, mDataset.indexOf(item));
               }
            });
      listView.setAdapter(defaultRecyclerListAdapter);

      return rootView;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setHasOptionsMenu(true);
   }

   /**
    * shows/hides the noCompareLayout
    *
    * @param show boolean if the noCompareLayout should be shown
    */
   private void showNoWishlistItemLayout(boolean show) {
      if (show) {
         gridView.setVisibility(View.INVISIBLE);
         listView.setVisibility(View.INVISIBLE);
         noWishlistItemLayout.setVisibility(View.VISIBLE);
      } else {
         noWishlistItemLayout.setVisibility(View.INVISIBLE);
         listView.setVisibility(View.VISIBLE);
      }
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      // Inflate the menu; this adds items to the action bar if it is present.
      menu.clear();
      inflater.inflate(R.menu.menu_wishlist_fragment, menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.search:
            Intent intent = new Intent();
            intent.setClassName(getActivity().getPackageName(),
                  getActivity().getPackageName() + ".views.SearchResultsActivity");
            startActivity(intent);
            return true;
         case R.id.settings:
            Intent intentSettings = new Intent();
            intentSettings.setClassName(getActivity().getPackageName(),
                  getActivity().getPackageName() + ".views.SettingsActivity");
            startActivityForResult(intentSettings, 0);
            return true;
      }
      return super.onOptionsItemSelected(item);
   }

   /**
    * initializes the dataset
    */
   private void initDataset() {
      //      mDataset = new ArrayList<>();
      mDataset.clear();

      ArrayList<Item> allWishlistItems =
            Item.getAllItems(getActivity().getApplicationContext(), true);

      for (Item tmpItem : allWishlistItems) {
         int iconId = getResources().getIdentifier("kleiderbuegel", "drawable",
               "com.gruppe1.pem.challengeme");
         mDataset.add(new ListItemIconName(getActivity(), "wishlist", tmpItem.getId(), iconId,
               tmpItem.getName(), tmpItem.getImageFile()));
      }
      if (mDataset.size() > 0) {
         showNoWishlistItemLayout(false);
      } else {
         showNoWishlistItemLayout(true);
      }
      final LinearLayoutManager layoutManager = (LinearLayoutManager) listView.getLayoutManager();
      new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (getActivity().getTheme()
                  .resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
               actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                     getResources().getDisplayMetrics());
            }
            //no items in the RecyclerView
            if (listView.getAdapter()
                  .getItemCount() == 0 ||
                  (layoutManager.findFirstCompletelyVisibleItemPosition() == 0 &&
                        layoutManager.findLastCompletelyVisibleItemPosition() ==
                              listView.getAdapter()
                                    .getItemCount() - 1)) {
               listView.setNestedScrollingEnabled(false);
               frameLayout.setPadding(0, 0, 0, actionBarHeight);
            } else {
               listView.setNestedScrollingEnabled(true);
            }
         }
      }, 5);
   }

   /**
    * Starts the CollectionItemsActivity to show detail information of an item
    *
    * @param itemid the id of the selected item
    */
   private void selectItem(int itemid, int position) {
      Intent intent = new Intent();
      intent.setClassName(getActivity().getPackageName(),
            getActivity().getPackageName() + ".views.CollectionItemsActivity");
      ArrayList<Item> allWishlistItems =
            Item.getAllItems(getActivity().getApplicationContext(), true);
      intent.putExtra(Constants.EXTRA_CLICKED_ITEM_POSITION, position);
      Bundle b = new Bundle();
      b.putInt(Constants.EXTRA_ITEM_ID, itemid);
      b.putParcelableArrayList(Constants.EXTRA_ITEM_COLLECTION, allWishlistItems);
      intent.putExtras(b);
      startActivityForResult(intent, 1);
   }

   private void wishlistFragmentOnItemClick(View view, int position) {
      int itemid = (int) defaultRecyclerListAdapter.getItemId(position);
      selectItem(itemid, position);
   }

   // for actualizing the categories list on coming back from new category
   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (resultCode != Activity.RESULT_OK || data == null || !isAdded()) {
         return;
      }
      if (requestCode == 1 || requestCode == 0) {
         int itemId = data.getIntExtra(Constants.EXTRA_ITEM_ID, -1);
         if (itemId != -1) {
            DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
            dbHelper.init();
            Item item = new Item(getActivity(), itemId, dbHelper);
            int iconId = getResources().getIdentifier("kleiderbuegel", "drawable",
                  "com.gruppe1.pem.challengeme");
            mDataset.add(new ListItemIconName(getActivity(), "wishlist", item.getId(), iconId,
                  item.getName(), item.getImageFile()));
            defaultRecyclerListAdapter.notifyItemInserted(mDataset.size() - 1);
            dbHelper.close();
         } else {

            initDataset();
            defaultRecyclerListAdapter.notifyDataSetChanged();
            defaultRecyclerListAdapter.notifyItemRangeChanged(0, mDataset.size());
         }
      }
   }

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
   }

   private boolean wishlistFragmentOnIcMoreClick(RecyclerView parent, View view,
         final int position) {
      selectedItem = new Object[2];
      selectedItem[0] = position;
      selectedItem[1] = view;

      final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      // Get the layout inflater
      LayoutInflater inflater = getActivity().getLayoutInflater();

      // Inflate and set the layout for the dialog
      // Pass null as the parent view because its going in the dialog layout

      View dialogView = inflater.inflate(R.layout.dialog_edit, parent, false);
      TextView headline = (TextView) dialogView.findViewById(R.id.dialog_headline);
      headline.setText(mDataset.get(position)
            .getName());

      builder.setView(dialogView)
            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                  int wishlistItemId =
                        (int) defaultRecyclerListAdapter.getItemId((int) selectedItem[0]);

                  DataBaseHelper db_helper =
                        new DataBaseHelper(getActivity().getApplicationContext());
                  db_helper.init();

                  Item deleteWishlistItem =
                        new Item(getActivity().getApplicationContext(), wishlistItemId, db_helper);
                  deleteWishlistItem.delete();
                  db_helper.close();

                  mDataset.remove(position);
                  mDataset.trimToSize();
                  defaultRecyclerListAdapter.notifyItemRemoved(position);
               }
            })
            .setNegativeButton(R.string.wishlist_button_not_selected,
                  new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                        Item wishlistItem = new Item(getActivity(), mDataset.get(position)
                              .getElementId(), getDb_helper());
                        mDataset.remove(position);
                        mDataset.trimToSize();
                        defaultRecyclerListAdapter.notifyItemRemoved(position);
                        HashMap<String, String> itemAttributes = new HashMap<>();
                        itemAttributes.put("name", wishlistItem.getName());
                        itemAttributes.put("image_file", wishlistItem.getImageFile());
                        itemAttributes.put("category_id", wishlistItem.getCategoryId() + "");
                        itemAttributes.put("primary_color", wishlistItem.getPrimaryColorId() + "");
                        itemAttributes.put("rating", wishlistItem.getRating() + "");
                        if (wishlistItem.getIsWish() == 1) {
                           itemAttributes.put("is_wish", "0");
                        } else {
                           itemAttributes.put("is_wish", "1");
                        }
                        wishlistItem.edit(itemAttributes);
                        wishlistItem.save();
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

   /**
    * Get the db_helper instance for this class
    *
    * @return DataBaseHelper instance
    */
   private DataBaseHelper getDb_helper() {
      if (!db_helper.isOpen()) {
         System.out.println("db helper was closed");
         db_helper = new DataBaseHelper(getActivity());
         db_helper.init();
      }
      return db_helper;
   }
}
