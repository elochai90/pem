package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.DefaultRecyclerGridAdapter;
import com.gruppe1.pem.challengeme.adapters.DefaultRecyclerListAdapter;
import com.gruppe1.pem.challengeme.helpers.CategoryDataSource;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CategoriesFragment extends Fragment {
   private static final int REQUEST_CODE = 1;

   private SharedPreferences.Editor editor;
   private SharedPreferences sharedPreferences;

   private ArrayList<ListItemIconName> mDataset;

   private CategoryDataSource categoryDataSource;

   @Bind (R.id.listView)
   RecyclerView listView;
   @Bind (R.id.gridView)
   RecyclerView gridView;

   private Boolean list;

   private Object[] selectedItem;

   private DefaultRecyclerListAdapter defaultRecyclerListAdapter;
   private DefaultRecyclerGridAdapter defaultRecyclerGridAdapter;

   public static CategoriesFragment newInstance() {
      return new CategoriesFragment();
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);
      View rootView = getActivity().getLayoutInflater()
            .inflate(R.layout.default_recycler_view, container, false);
      ButterKnife.bind(this, rootView);

      categoryDataSource = new CategoryDataSource(getActivity());

      mDataset = new ArrayList<>();
      sharedPreferences =
            getActivity().getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
      editor = sharedPreferences.edit();

      list = savedInstanceState == null ||
            savedInstanceState.getBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, true);

      initDataset();
      defaultRecyclerListAdapter =
            new DefaultRecyclerListAdapter(getActivity(), R.layout.list_item_default,
                  R.layout.list_item_category, mDataset, true, false);
      defaultRecyclerListAdapter.setOnItemClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            categoryFragmentOnItemClick(v, listView.getChildPosition(v));
         }
      });
      defaultRecyclerListAdapter.setOnIcMoreClickListener(
            new DefaultRecyclerListAdapter.OnIcMoreClickListener() {

               @Override
               public void onClick(View view, ListItemIconName item) {
                  categoryFragmentOnIcMoreClick(listView, view, mDataset.indexOf(item));
               }
            });

      LinearLayoutManager linearLayoutManagerList =
            new LinearLayoutManager(getActivity().getBaseContext());
      listView.setLayoutManager(linearLayoutManagerList);
      listView.setHasFixedSize(true);
      listView.setAdapter(defaultRecyclerListAdapter);

      defaultRecyclerGridAdapter =
            new DefaultRecyclerGridAdapter(getActivity(), R.layout.grid_item_default,
                  R.layout.grid_item_category, mDataset, true);
      defaultRecyclerGridAdapter.setOnItemClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            categoryFragmentOnItemClick(v, listView.getChildPosition(v));
         }
      });
      defaultRecyclerGridAdapter.setOnIcMoreClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            categoryFragmentOnIcMoreClick(listView, v, (Integer) v.getTag());
         }
      });
      StaggeredGridLayoutManager gridLayoutManager =
            new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
      gridView.setLayoutManager(gridLayoutManager);
      gridView.setHasFixedSize(true);
      int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
      gridView.addItemDecoration(new GridSpacingItemDecoration(3, spacingInPixels, false, 0));
      gridView.setAdapter(defaultRecyclerGridAdapter);
      gridView.setVisibility(View.INVISIBLE);

      return rootView;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      list = savedInstanceState == null ||
            savedInstanceState.getBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, true);
      setHasOptionsMenu(true);
   }

   /**
    * Starts the ItemsListActivity of the category
    *
    * @param categoryId the id of the selected category
    */
   private void selectCategory(int categoryId) {
      Intent intent = new Intent();
      intent.setClassName(getActivity().getPackageName(),
            getActivity().getPackageName() + ".views.ItemsListActivity");
      Bundle b = new Bundle();
      b.putInt(Constants.EXTRA_CATEGORY_ID, categoryId);
      intent.putExtras(b);
      startActivityForResult(intent, 1);
   }

   @Override
   public void onResume() {
      super.onResume();
      list = sharedPreferences.getBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, true);
      switchListGridView(list);
   }

   @Override
   public void onPrepareOptionsMenu(Menu menu) {
      super.onPrepareOptionsMenu(menu);
      list = sharedPreferences.getBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, true);
      menu.getItem(0)
            .setIcon(list ? R.drawable.ic_view_grid : R.drawable.ic_view_list);
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      // Inflate the menu; this adds items to the action bar if it is present.
      menu.clear();
      inflater.inflate(R.menu.menu_categories_fragment, menu);
   }

   /**
    * Switches the list/grid view of the categories
    *
    * @param shouldBeListView boolean if the categories should be shown as list view
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
      editor.putBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, list);
      editor.apply();
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_switchView:
            switchListGridView(!list);
            if (list) {
               item.setIcon(R.drawable.ic_view_grid);
            } else {
               item.setIcon(R.drawable.ic_view_list);
            }
            return true;
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
         case R.id.about:
            Intent intentAbout = new Intent();
            intentAbout.setClassName(getActivity().getPackageName(),
                  getActivity().getPackageName() + ".views.AboutActivity");
            startActivity(intentAbout);
            return true;
      }

      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onSaveInstanceState(Bundle savedInstanceState) {
      // Save currently selected layout manager.
      savedInstanceState.putSerializable(Constants.KEY_VIEW_CATEGORIES_AS_LIST, list);
      super.onSaveInstanceState(savedInstanceState);
   }

   /**
    * initializes the dataset of compares
    */
   private void initDataset() {
      mDataset.clear();

      List<Category> allBaseCategories = categoryDataSource.getCategoriesWithParentCategory(-1);

      for (Category tmpCat : allBaseCategories) {
         int iconId = getResources().getIdentifier(tmpCat.getIcon(), "drawable",
               "com.gruppe1.pem.challengeme");
         mDataset.add(new ListItemIconName(getActivity(), "category", tmpCat.getId(), iconId,
               tmpCat.getName(getActivity()), null));
      }
   }

   private void categoryFragmentOnItemClick(View view, int position) {
      selectCategory(mDataset.get(position)
            .getElementId());
   }

   @Override
   public void onActivityResult(int p_requestCode, int p_resultCode, Intent p_data) {
      super.onActivityResult(p_requestCode, p_resultCode, p_data);
      onResume();

      if ((p_requestCode == 1 || p_requestCode == 0) && p_resultCode == Activity.RESULT_OK) {
         // item was updated
         initDataset();
         defaultRecyclerListAdapter.notifyDataSetChanged();
         defaultRecyclerGridAdapter.notifyDataSetChanged();
      }
   }

   private boolean categoryFragmentOnIcMoreClick(RecyclerView parent, View view,
         final int position) {
      selectedItem = new Object[2];
      selectedItem[0] = position;
      selectedItem[1] = view;

      final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      LayoutInflater inflater = getActivity().getLayoutInflater();
      View dialogView = inflater.inflate(R.layout.dialog_edit, parent, false);
      TextView headline = (TextView) dialogView.findViewById(R.id.dialog_headline);
      headline.setText(mDataset.get(position)
            .getName());

      builder.setView(dialogView)
            .setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                  Intent intent = new Intent();
                  intent.setClassName(getActivity().getPackageName(),
                        getActivity().getPackageName() + ".views.NewCategoryActivity");
                  int categoryId =
                        (int) defaultRecyclerListAdapter.getItemId((int) selectedItem[0]);

                  intent.putExtra(Constants.EXTRA_CATEGORY_ID, categoryId);

                  startActivityForResult(intent, REQUEST_CODE);
               }
            })
            .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                  int categoryId =
                        (int) defaultRecyclerListAdapter.getItemId((int) selectedItem[0]);
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

      return true;
   }

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
   }
}
