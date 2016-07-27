package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import android.widget.Toast;

import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.CompareRecyclerGridAdapter;
import com.gruppe1.pem.challengeme.adapters.CompareRecyclerListAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.GridSpacingItemDecoration;

import java.util.ArrayList;

public class CompareFragment extends Fragment {

   private ArrayList<Compare> mDataset;

   private View rootView;

   private RecyclerView gridView;
   private RecyclerView listView;
   private Boolean list;
   private FrameLayout frameLayout;

   private RelativeLayout noComparesLayout;

   private SharedPreferences.Editor editor;
   private SharedPreferences sharedPreferences;

   private Object[] selectedItem;
   private CompareRecyclerListAdapter compareRecyclerListAdapter;
   private CompareRecyclerGridAdapter compareRecyclerGridAdapter;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);

      rootView = inflater.inflate(R.layout.default_recycler_view, container, false);

      noComparesLayout = (RelativeLayout) rootView.findViewById(R.id.noItemLayout);
      listView = (RecyclerView) rootView.findViewById(R.id.listView);
      gridView = (RecyclerView) rootView.findViewById(R.id.gridView);
      frameLayout = (FrameLayout) rootView.findViewById(R.id.frameLayout);

      TextView noComparesText = (TextView) rootView.findViewById(R.id.noItemText);
      noComparesText.setText(R.string.no_compares);

      LinearLayoutManager linearLayoutManagerList =
            new LinearLayoutManager(getActivity().getBaseContext());
      listView.setLayoutManager(linearLayoutManagerList);
      listView.setHasFixedSize(true);
      StaggeredGridLayoutManager gridLayoutManager =
            new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
      gridView.setLayoutManager(gridLayoutManager);
//      gridView.setHasFixedSize(true);
      int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
      gridView.addItemDecoration(
            new GridSpacingItemDecoration(2, spacingInPixels, false, 0));

      mDataset = new ArrayList<>();
      initDataset();

      compareRecyclerListAdapter =
            new CompareRecyclerListAdapter(getActivity(), R.layout.list_item_compare, mDataset);
      compareRecyclerListAdapter.setOnItemClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            compareFragmentOnItemClick(v, listView.getChildPosition(v));
         }
      });
      compareRecyclerListAdapter.setOnIcMoreClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            compareFragmentOnIcMoreClick(listView, v, (Integer) v.getTag());
         }
      });
      listView.setAdapter(compareRecyclerListAdapter);

      compareRecyclerGridAdapter =
            new CompareRecyclerGridAdapter(getActivity(), R.layout.grid_item_compare, mDataset);
      compareRecyclerGridAdapter.setOnItemClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            compareFragmentOnItemClick(v, listView.getChildPosition(v));
         }
      });
      compareRecyclerGridAdapter.setOnIcMoreClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            compareFragmentOnIcMoreClick(listView, v, (Integer) v.getTag());
         }
      });
      gridView.setAdapter(compareRecyclerGridAdapter);
      gridView.setVisibility(View.GONE);

      return rootView;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      list = savedInstanceState == null ||
            savedInstanceState.getBoolean(Constants.KEY_VIEW_COMPARE_AS_LIST, true);

      setHasOptionsMenu(true);
      sharedPreferences =
            getActivity().getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
      editor = sharedPreferences.edit();
   }

   /**
    * shows/hides the noCompareLayout
    *
    * @param show boolean if the noCompareLayout should be shown
    */
   private void showNoComparesLayout(boolean show) {
      if (show) {
         gridView.setVisibility(View.GONE);
         listView.setVisibility(View.GONE);
         noComparesLayout.setVisibility(View.VISIBLE);
      } else {
         noComparesLayout.setVisibility(View.GONE);
         if (list) {
            listView.setVisibility(View.VISIBLE);
         } else {
            gridView.setVisibility(View.VISIBLE);
         }
      }
   }

   @Override
   public void onResume() {
      super.onResume();
      list = sharedPreferences.getBoolean(Constants.KEY_VIEW_COMPARE_AS_LIST, true);
      switchListGridView(list);
   }

   @Override
   public void onPrepareOptionsMenu(Menu menu) {
      super.onPrepareOptionsMenu(menu);
      list = sharedPreferences.getBoolean(Constants.KEY_VIEW_COMPARE_AS_LIST, true);
      menu.getItem(0)
            .setIcon(list ? R.drawable.ic_view_grid : R.drawable.ic_view_list);
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      // Inflate the menu; this adds items to the action bar if it is present.
      menu.clear();
      inflater.inflate(R.menu.menu_compare_fragment, menu);
   }

   /**
    * Switches the list/grid view of the compares
    *
    * @param shouldBeListView boolean if the compares should be shown as list view
    */
   private void switchListGridView(boolean shouldBeListView) {
      if (shouldBeListView) {
         gridView.setVisibility(View.GONE);
         listView.setVisibility(View.VISIBLE);
      } else {
         listView.setVisibility(View.GONE);
         gridView.setVisibility(View.VISIBLE);
      }

      list = shouldBeListView;
      editor.putBoolean(Constants.KEY_VIEW_COMPARE_AS_LIST, list);
      editor.commit();
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
            intentSettings.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".views.SettingsActivity");
            startActivityForResult(intentSettings, 0);
            return true;
      }

      return super.onOptionsItemSelected(item);
   }

   /**
    * initializes the dataset of compares
    */
   private void initDataset() {
      mDataset.clear();
      mDataset.addAll(Compare.geAllCompares(getActivity().getApplicationContext()));
      if (mDataset.size() > 0) {
         showNoComparesLayout(false);
      } else {
         showNoComparesLayout(true);
      }
      final RecyclerView mRecyclerView = list ? listView : gridView;
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
            System.out.println(mRecyclerView.getLayoutManager().getClass());
            if (mRecyclerView.getAdapter()
                  .getItemCount() == 0) {
               gridView.setNestedScrollingEnabled(false);
               listView.setNestedScrollingEnabled(false);
               frameLayout.setPadding(0, 0, 0, actionBarHeight);
            } else if (list && ((LinearLayoutManager)listView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0 &&
                  ((LinearLayoutManager)listView.getLayoutManager()).findLastCompletelyVisibleItemPosition() ==
                        listView.getAdapter()
                                 .getItemCount() - 1) {
                  listView.setNestedScrollingEnabled(false);
                  gridView.setNestedScrollingEnabled(false);
                  frameLayout.setPadding(0, 0, 0, actionBarHeight);
            } else if (!list && ((StaggeredGridLayoutManager)gridView.getLayoutManager()).findFirstCompletelyVisibleItemPositions(null).length > 0 &&
                  ((StaggeredGridLayoutManager)gridView.getLayoutManager()).findLastCompletelyVisibleItemPositions(null).length > 0 &&
                  ((StaggeredGridLayoutManager)gridView.getLayoutManager()).findFirstCompletelyVisibleItemPositions(null)[0] == 0 &&
                  ((StaggeredGridLayoutManager)gridView.getLayoutManager()).findLastCompletelyVisibleItemPositions(null)[0] ==
                        gridView.getAdapter()
                                 .getItemCount() - 1) {
                  gridView.setNestedScrollingEnabled(false);
                  listView.setNestedScrollingEnabled(false);
                  frameLayout.setPadding(0, 0, 0, actionBarHeight);
            } else {
               gridView.setNestedScrollingEnabled(true);
               listView.setNestedScrollingEnabled(true);
            }
         }
      }, 5);
   }

   @Override
   public void onSaveInstanceState(Bundle savedInstanceState) {
      // Save currently selected layout manager.
      savedInstanceState.putSerializable(Constants.KEY_VIEW_COMPARE_AS_LIST, list);
      super.onSaveInstanceState(savedInstanceState);
   }

   private void compareFragmentOnItemClick(View view, int position) {
      Intent intent = new Intent();
      intent.setClassName(getActivity().getPackageName(),
            getActivity().getPackageName() + ".views.SavedComparesDetailActivity");
      intent.putExtra("item", mDataset.get(position));
      startActivityForResult(intent, 0);
   }

   // for actualizing the categories list on coming back from new category
   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      try {
         super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == 1 || requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
               initDataset();
               compareRecyclerListAdapter.notifyDataSetChanged();
               compareRecyclerGridAdapter.notifyDataSetChanged();
            }
         }
      } catch (Exception ex) {
         Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT)
               .show();
      }
   }

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
   }

   private boolean compareFragmentOnIcMoreClick(RecyclerView parent, View view, final int position) {
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
                  int compareId = (int) compareRecyclerListAdapter.getItemId((int) selectedItem[0]);

                  DataBaseHelper db_helper =
                        new DataBaseHelper(getActivity().getApplicationContext());
                  db_helper.init();

                  Compare deleteCompare =
                        new Compare(getActivity().getApplicationContext(), compareId, db_helper);
                  deleteCompare.delete();
                  db_helper.close();

                  mDataset.remove(position);
                  compareRecyclerListAdapter.notifyItemRemoved(position);
                  compareRecyclerGridAdapter.notifyItemRemoved(position);
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
