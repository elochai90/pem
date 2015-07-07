package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.adapters.CompareGridAdapter;
import com.gruppe1.pem.challengeme.adapters.CompareListAdapter;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;
import java.util.List;


public class CompareFragment extends Fragment  implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private List<Compare> mDataset;

    private View rootView;

    private GridView gridView;
    private CompareGridAdapter gridAdapter;
    private ListView listView;
    private CompareListAdapter listAdapter;
    private Boolean list;

    private RelativeLayout noComparesLayout;

    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;

    private Object[] selectedItem;
    ActionMode actionMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.default_list_grid_view, container, false);

        noComparesLayout = (RelativeLayout) rootView.findViewById(R.id.noItemLayout);
        TextView noComparesText = (TextView) rootView.findViewById(R.id.noItemText);
        noComparesText.setText(R.string.no_compares);

        mDataset = new ArrayList<>();
        initDataset();

        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new CompareListAdapter(getActivity(), R.layout.list_item_compare, mDataset);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setNumColumns(3);
        gridAdapter = new CompareGridAdapter(getActivity(), R.layout.grid_item_compare, mDataset);
        gridView.setAdapter(gridAdapter);
        gridView.setVisibility(View.INVISIBLE);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);

        FloatingActionMenu menu = (FloatingActionMenu) rootView.findViewById(R.id.menu);
        menu.setClosedOnTouchOutside(true);
        com.github.clans.fab.FloatingActionButton fab_add_compare = (FloatingActionButton) rootView.findViewById(R.id.add_compare);
        com.github.clans.fab.FloatingActionButton fab_add_wishlist_item = (FloatingActionButton) rootView.findViewById(R.id.add_wishlist_item);
        com.github.clans.fab.FloatingActionButton fab_add_category = (FloatingActionButton) rootView.findViewById(R.id.add_category);
        com.github.clans.fab.FloatingActionButton fab_add_item = (FloatingActionButton) rootView.findViewById(R.id.add_item);
        fab_add_compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".views.NewCompareActivity");
                startActivityForResult(intent, 0);

            }
        });

        fab_add_wishlist_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".views.NewItemActivity");
                intent.putExtra("is_wishlist", true);
                startActivityForResult(intent, 0);

            }
        });
        fab_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".views.NewCategoryActivity");
                startActivityForResult(intent, 0);

            }
        });

        fab_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".views.NewItemActivity");
                startActivity(intent);

            }
        });



        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            list = savedInstanceState.getBoolean(Constants.KEY_VIEW_COMPARE_AS_LIST, true);
        } else {
            list = true;
        }

        setHasOptionsMenu(true);

        sharedPreferences = getActivity().getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


    }

    private void showNoComparesLayout(boolean show) {
        if(show) {
            noComparesLayout.setVisibility(View.VISIBLE);
        } else {
            noComparesLayout.setVisibility(View.INVISIBLE);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_switchView) {
            switchListGridView(!list);
            if(list) {
                item.setIcon(R.drawable.ic_view_grid);
            } else {
                item.setIcon(R.drawable.ic_view_list);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                    listAdapter.notifyDataSetChanged();
                    gridAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        selectedItem = new Object[2];
        selectedItem[0] = position;
        selectedItem[1] = view;

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        View dialogView = inflater.inflate(R.layout.dialog_edit, null);
        TextView headline = (TextView)dialogView.findViewById(R.id.dialog_headline);
        headline.setText(mDataset.get(position).getName());

        builder.setView(dialogView).setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int compareId = (int) listAdapter.getItemId((int) selectedItem[0]);

                DataBaseHelper db_helper = new DataBaseHelper(getActivity().getApplicationContext());
                db_helper.init();

                Compare deleteCompare = new Compare(getActivity().getApplicationContext(), compareId, db_helper);
                deleteCompare.delete();
                db_helper.close();

                initDataset();
                listAdapter.notifyDataSetChanged();
                gridAdapter.notifyDataSetChanged();
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
