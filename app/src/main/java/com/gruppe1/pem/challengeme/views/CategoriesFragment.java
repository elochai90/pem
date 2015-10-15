package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.DefaultGridAdapter;
import com.gruppe1.pem.challengeme.adapters.DefaultListAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.DefaultSetup;

import java.util.ArrayList;


public class CategoriesFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final int REQUEST_CODE = 1;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private ArrayList<ListItemIconName> mDataset;
    private View rootView;
    private GridView gridView;
    private DefaultGridAdapter gridAdapter;
    private ListView listView;
    private DefaultListAdapter listAdapter;
    private Boolean list;

    private Object[] selectedItem;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        mDataset = new ArrayList<>();
        sharedPreferences = getActivity().getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initDataset();

        list = savedInstanceState == null || savedInstanceState.getBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, true);

        rootView = getActivity().getLayoutInflater().inflate(R.layout.default_list_grid_view, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new DefaultListAdapter(getActivity(), R.layout.list_item_default, mDataset, true, false);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridAdapter = new DefaultGridAdapter(getActivity(), R.layout.grid_item_default, mDataset, true);
        gridView.setAdapter(gridAdapter);
        gridView.setVisibility(View.INVISIBLE);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = savedInstanceState == null || savedInstanceState.getBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, true);
        setHasOptionsMenu(true);
    }

    /**
     * Starts the ItemsListActivity of the category
     * @param categoryId the id of the selected category
     */
    private void selectCategory(int categoryId) {
        Intent intent = new Intent();
        intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".views.ItemsListActivity");
        Bundle b = new Bundle();
        b.putInt(Constants.EXTRA_CATEGORY_ID, categoryId);
        intent.putExtras(b);
        startActivity(intent);
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
        menu.getItem(0).setIcon(list ? R.drawable.ic_view_grid : R.drawable.ic_view_list);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_categories_fragment, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        final Menu optionMenu =  menu;
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused) {
                    searchView.clearFocus();
                    optionMenu.findItem(R.id.search).collapseActionView();
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                optionMenu.findItem(R.id.search).collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

    }

    /**
     * Switches the list/grid view of the categories
     * @param shouldBeListView boolean if the categories should be shown as list view
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
        editor.putBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, list);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        DataBaseHelper db_helper = new DataBaseHelper(getActivity().getApplicationContext());
        db_helper.init();

        mDataset.clear();

        boolean firstDbInit = sharedPreferences.getBoolean(Constants.KEY_FIRST_DB_INIT, true);

        if(firstDbInit) {
            new DefaultSetup(getActivity().getApplicationContext());
            editor.putBoolean(Constants.KEY_FIRST_DB_INIT, false);
            editor.commit();
        }

        ArrayList<Category> allCategories = Category.getAllCategories(getActivity().getApplicationContext());

        for (Category tmpCat : allCategories) {
            int iconId = getResources().getIdentifier(tmpCat.getIcon(), "drawable", "com.gruppe1.pem.challengeme");
            mDataset.add(new ListItemIconName(tmpCat.getId(), iconId, tmpCat.getName(), null));
        }
        db_helper.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectCategory(mDataset.get(position).getElementId());
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

        View dialogView = inflater.inflate(R.layout.dialog_edit, parent, false);
        TextView headline = (TextView)dialogView.findViewById(R.id.dialog_headline);
        headline.setText(mDataset.get(position).getName());

        builder.setView(dialogView).setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".views.NewCategoryActivity");
                int categoryId = (int)listAdapter.getItemId((int)selectedItem[0]);
                intent.putExtra("category_id", categoryId);

                startActivityForResult(intent, REQUEST_CODE);
            }
        }).setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int categoryId = (int)listAdapter.getItemId((int)selectedItem[0]);

                ArrayList<Item> items = Item.getItemsByCategoryId(getActivity().getApplicationContext(), categoryId, true);
                for (Item c : items) {
                    c.delete();
                }
                DataBaseHelper db_helper = new DataBaseHelper(getActivity().getApplicationContext());
                db_helper.init();

                Category deleteCategory = new Category(getActivity().getApplicationContext(), categoryId, db_helper );
                deleteCategory.delete();

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
