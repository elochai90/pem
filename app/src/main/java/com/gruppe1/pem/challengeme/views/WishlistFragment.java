package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.SearchView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.DefaultListAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;


public class WishlistFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private ArrayList<ListItemIconName> mDataset;
    private DefaultListAdapter listAdapter;
    private RelativeLayout noWishlistItemLayout;
    private Object[] selectedItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.default_list_grid_view, container, false);

        noWishlistItemLayout = (RelativeLayout) rootView.findViewById(R.id.noItemLayout);
        TextView noWishlistItemText = (TextView) rootView.findViewById(R.id.noItemText);
        noWishlistItemText.setText(R.string.no_wishlist_items);

        mDataset = new ArrayList<>();
        initDataset();

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new DefaultListAdapter(getActivity(), R.layout.list_item_default, mDataset, false, true);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setVisibility(View.INVISIBLE);

        return rootView;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }


    /**
     * shows/hides the noCompareLayout
     * @param show boolean if the noCompareLayout should be shown
     */
    private void showNoWishlistItemLayout(boolean show) {
        if(show) {
            noWishlistItemLayout.setVisibility(View.VISIBLE);
        } else {
            noWishlistItemLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_wishlist_fragment, menu);

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_switchView) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * initializes the dataset
     */
    private void initDataset() {
        mDataset.clear();

        ArrayList<Item> allWishlistItems = Item.getAllItems(getActivity().getApplicationContext(), true);

        for (Item tmpItem : allWishlistItems) {
            int iconId = getResources().getIdentifier("kleiderbuegel", "drawable", "com.gruppe1.pem.challengeme");
            mDataset.add(new ListItemIconName(tmpItem.getId(), iconId, tmpItem.getName(), tmpItem.getImageFile()));
        }
        if(mDataset.size() > 0) {
            showNoWishlistItemLayout(false);
        } else {
            showNoWishlistItemLayout(true);
        }
    }

    /**
     * Starts the NewItemActivity to show detail information of an item
     * @param itemid the id of the selected item
     */
    private void selectItem(int itemid) {
        Intent intent = new Intent();
        intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".views.NewItemActivity");
        Bundle b = new Bundle();
        b.putInt(Constants.EXTRA_ITEM_ID, itemid);
        intent.putExtras(b);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int itemid = listAdapter.getItem(position).getElementId();
        selectItem(itemid);
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
                }
            }
        } catch(Exception e){
            e.printStackTrace();
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

        builder.setView(dialogView).setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int wishlistItemId = (int) listAdapter.getItemId((int) selectedItem[0]);

                DataBaseHelper db_helper = new DataBaseHelper(getActivity().getApplicationContext());
                db_helper.init();

                Item deleteWishlistItem = new Item(getActivity().getApplicationContext(), wishlistItemId, db_helper);
                deleteWishlistItem.delete();
                db_helper.close();

                initDataset();
                listAdapter.notifyDataSetChanged();
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
