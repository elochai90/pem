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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.CompareRecyclerGridAdapter;
import com.gruppe1.pem.challengeme.adapters.CompareRecyclerListAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.RecyclerItemClickListener;

import java.util.ArrayList;


public class CompareFragment extends Fragment {

    private ArrayList<Compare> mDataset;

    private View rootView;

    private RecyclerView gridView;
    private RecyclerView listView;
    private Boolean list;

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

        TextView noComparesText = (TextView) rootView.findViewById(R.id.noItemText);
        noComparesText.setText(R.string.no_compares);

        mDataset = new ArrayList<>();
        initDataset();

        compareRecyclerListAdapter = new CompareRecyclerListAdapter(getActivity(), R.layout.list_item_compare, mDataset);

        LinearLayoutManager linearLayoutManagerList = new LinearLayoutManager(getActivity().getBaseContext());
        listView.setLayoutManager(linearLayoutManagerList);
        listView.setHasFixedSize(true);
        listView.setAdapter(compareRecyclerListAdapter);
        listView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                compareFragmentOnItemClick(view, position);
            }

            @Override
            public void onItemLongClick(RecyclerView parent, View view, int position) {
                compareFragmentOnItemLongClick(parent, view, position);
            }
        }));
        compareRecyclerGridAdapter = new CompareRecyclerGridAdapter(getActivity(), R.layout.grid_item_compare, mDataset);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        gridView.setLayoutManager(gridLayoutManager);
        gridView.setHasFixedSize(true);
        gridView.setAdapter(compareRecyclerGridAdapter);
        gridView.setVisibility(View.INVISIBLE);
        gridView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                compareFragmentOnItemClick(view, position);
            }

            @Override
            public void onItemLongClick(RecyclerView parent, View view, int position) {
                compareFragmentOnItemLongClick(parent, view, position);
            }
        }));

        return rootView;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list = savedInstanceState == null || savedInstanceState.getBoolean(Constants.KEY_VIEW_COMPARE_AS_LIST, true);

        setHasOptionsMenu(true);

        sharedPreferences = getActivity().getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * shows/hides the noCompareLayout
     * @param show boolean if the noCompareLayout should be shown
     */
    private void showNoComparesLayout(boolean show) {
        if(show) {
            gridView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.INVISIBLE);
            noComparesLayout.setVisibility(View.VISIBLE);
        } else {
            noComparesLayout.setVisibility(View.INVISIBLE);
            if(list) {
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
        menu.getItem(0).setIcon(list ? R.drawable.ic_view_grid : R.drawable.ic_view_list);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_compare_fragment, menu);

    }

    /**
     * Switches the list/grid view of the compares
     * @param shouldBeListView boolean if the compares should be shown as list view
     */
    private void switchListGridView(boolean shouldBeListView) {
        if(shouldBeListView) {
            gridView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.INVISIBLE);
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
                if(list) {
                    item.setIcon(R.drawable.ic_view_grid);
                } else {
                    item.setIcon(R.drawable.ic_view_list);
                }
                return true;
            case R.id.search:
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".views.SearchResultsActivity");
                startActivity(intent);
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
        if(mDataset.size() > 0) {
            showNoComparesLayout(false);
        } else {
            showNoComparesLayout(true);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(Constants.KEY_VIEW_COMPARE_AS_LIST, list);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void compareFragmentOnItemClick(View view, int position) {
            Intent intent = new Intent();
            intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".views.SavedComparesDetailActivity");
            intent.putExtra("item", mDataset.get(position));
            startActivityForResult(intent, 0);
    }

    // for actualizing the categories list on coming back from new category
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 0) {
                if(resultCode == Activity.RESULT_OK) {
                    initDataset();
                    compareRecyclerListAdapter.notifyDataSetChanged();
                    compareRecyclerGridAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean compareFragmentOnItemLongClick(RecyclerView parent, View view, int position) {
        selectedItem = new Object[2];
        selectedItem[0] = position;
        selectedItem[1] = view;

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        View dialogView = inflater.inflate(R.layout.dialog_edit, parent, false);
        TextView headline = (TextView)dialogView.findViewById(R.id.dialog_headline);
        headline.setText(mDataset.get(position).getName());

        builder.setView(dialogView).setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int compareId = (int) compareRecyclerListAdapter.getItemId((int) selectedItem[0]);

                DataBaseHelper db_helper = new DataBaseHelper(getActivity().getApplicationContext());
                db_helper.init();

                Compare deleteCompare = new Compare(getActivity().getApplicationContext(), compareId, db_helper);
                deleteCompare.delete();
                db_helper.close();

                initDataset();
                compareRecyclerListAdapter.notifyDataSetChanged();
                compareRecyclerGridAdapter.notifyDataSetChanged();
            }
        }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();

        return true;
    }
}
