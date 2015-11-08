package com.gruppe1.pem.challengeme.views;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;


/**
 * Created by bianka on 13.10.2015.
 */
public class SearchResultsActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ArrayList<ListItemIconName> mDataset;

    private ListView listView;
//    private DefaultListAdapter listAdapter;
    private ArrayList<Item> searchResultItems = new ArrayList<>();
    private String query;

    private RelativeLayout noItemLayout;
    private TextView noItemText;
    private ImageView noItemArrow;


    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);




        noItemLayout = (RelativeLayout) findViewById(R.id.noItemLayout);
        noItemText = (TextView) findViewById(R.id.noItemText);
        noItemArrow = (ImageView) findViewById(R.id.noItemArrow);

        sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        DataBaseHelper db_helper = new DataBaseHelper(this);
        db_helper.init();
        db_helper.close();
        mDataset = new ArrayList<>();


        listView = (ListView) findViewById(R.id.listView);
//        listAdapter = new DefaultListAdapter(this, R.layout.list_item_default, mDataset, false, false);
//        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        initDataset("");

//        handleIntent(getIntent());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        ActionBar actionBar = getActionBar();

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_search, null);
        actionBar.setCustomView(v);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results_activity, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setIconified(false);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                initDataset(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                initDataset(newText);
                return true;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }


    @Override
    public void onActivityResult(int p_requestCode, int p_resultCode, Intent p_data) {
        super.onActivityResult(p_requestCode, p_resultCode, p_data);

        if(p_requestCode == 1) {
            // item was updated
            initDataset(query);
//            listAdapter.notifyDataSetChanged();
        }
    }


    /**
     * shows/hides the noCompareLayout
     * @param show boolean if the noCompareLayout should be shown
     */
    private void showNoItemLayout(boolean show) {
        if(show) {
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
    private void initDataset(String query) {
        this.query = query;
        mDataset.clear();

        DataBaseHelper db_helper = new DataBaseHelper(this);
        db_helper.init();

        searchResultItems = Item.getSearchResults(this, query);

        for (Item tmpItem : searchResultItems) {
            int iconId = getResources().getIdentifier("kleiderbuegel", "drawable", "com.gruppe1.pem.challengeme");
            mDataset.add(new ListItemIconName(tmpItem.getId(), iconId, tmpItem.getName(), tmpItem.getImageFile()));
        }
        if(mDataset.size() > 0) {
            showNoItemLayout(false);
        } else {
            showNoItemLayout(true);
        }
        db_helper.close();
//        listAdapter.notifyDataSetChanged();
    }


    /**
     * Starts the NewItemActivity to show detail informations of an item
     * @param itemid the id of the selected item
     */
    private void selectItem(int itemid, int position) {
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), getPackageName() + ".views.CollectionItemsActivity");

        intent.putExtra(Constants.EXTRA_CLICKED_ITEM_POSITION, position);
        Bundle b = new Bundle();
        b.putInt(Constants.EXTRA_ITEM_ID, itemid);
        b.putParcelableArrayList(Constants.EXTRA_ITEM_COLLECTION, searchResultItems);
        intent.putExtras(b);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        int itemid = listAdapter.getItem(position).getElementId();
//        selectItem(itemid, position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        View dialogView = inflater.inflate(R.layout.dialog_edit, parent, false);
        TextView headline = (TextView)dialogView.findViewById(R.id.dialog_headline);
        headline.setText(mDataset.get(position).getName());

        builder.setView(dialogView).setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                int itemId = (int)listAdapter.getItemId(position);

                DataBaseHelper db_helper = new DataBaseHelper(getApplicationContext());
                db_helper.init();

//                Item deleteItem = new Item(getApplicationContext(), itemId, db_helper );
//                deleteItem.delete();
                db_helper.close();

                initDataset(query);
//                listAdapter.notifyDataSetChanged();
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
