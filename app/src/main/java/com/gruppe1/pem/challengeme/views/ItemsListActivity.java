package com.gruppe1.pem.challengeme.views;

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
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.DefaultRecyclerGridAdapter;
import com.gruppe1.pem.challengeme.adapters.DefaultRecyclerListAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;


public class ItemsListActivity extends AppCompatActivity {
    private ArrayList<ListItemIconName> mDataset;

    private RecyclerView gridView;
    private RecyclerView listView;
    private Boolean list;

    private FloatingActionMenu menu;

    private RelativeLayout noItemLayout;

    private int categoryId;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private Object[] selectedItem;

    private DefaultRecyclerListAdapter defaultRecyclerListAdapter;
    private DefaultRecyclerGridAdapter defaultRecyclerGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_items_list);

        setupToolbar();
        noItemLayout = (RelativeLayout) findViewById(R.id.noItemLayout);
        TextView noItemText = (TextView) findViewById(R.id.noItemText);
        noItemText.setText(R.string.no_items);

        sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        list = savedInstanceState == null || savedInstanceState.getBoolean(Constants.KEY_VIEW_ITEMS_AS_LIST, true);

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

        DataBaseHelper db_helper = new DataBaseHelper(this);
        db_helper.init();
        Category category = new Category(this, categoryId, db_helper);
        db_helper.close();
        getSupportActionBar().setTitle(category.getName());
//        setTitle(category.getName());
        mDataset = new ArrayList<>();

        initDataset();

        listView = (RecyclerView) findViewById(R.id.listView);
        defaultRecyclerListAdapter = new DefaultRecyclerListAdapter(this, R.layout.list_item_default, mDataset, false, false);
        defaultRecyclerListAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListActivityOnItemClick(v, listView.getChildPosition(v));
            }
        });
        defaultRecyclerListAdapter.setOnItemLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemListActivityOnItemLongClick(listView, v, listView.getChildPosition(v));
                return true;
            }
        });

        LinearLayoutManager linearLayoutManagerList = new LinearLayoutManager(this.getBaseContext());
        listView.setLayoutManager(linearLayoutManagerList);
        listView.setHasFixedSize(true);
        listView.setAdapter(defaultRecyclerListAdapter);

        gridView = (RecyclerView) findViewById(R.id.gridView);
        defaultRecyclerGridAdapter = new DefaultRecyclerGridAdapter(this, R.layout.grid_item_default, mDataset, false);

        defaultRecyclerGridAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListActivityOnItemClick(v,listView.getChildPosition(v));
            }
        });
        defaultRecyclerGridAdapter.setOnItemLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemListActivityOnItemLongClick(listView,v,listView.getChildPosition(v));
                return true;
            }
        });
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridView.setLayoutManager(gridLayoutManager);
        gridView.setHasFixedSize(true);
        gridView.setAdapter(defaultRecyclerGridAdapter);
        gridView.setVisibility(View.INVISIBLE);


        menu = (FloatingActionMenu) findViewById(R.id.menu);
        menu.setVisibility(View.VISIBLE);
        menu.setClosedOnTouchOutside(true);
        com.github.clans.fab.FloatingActionButton fab_add_compare = (FloatingActionButton) findViewById(R.id.add_compare);
        fab_add_compare.setLabelText(getString(R.string.title_activity_saved_compares));
        com.github.clans.fab.FloatingActionButton fab_add_wishlist_item = (FloatingActionButton) findViewById(R.id.add_wishlist_item);
        fab_add_wishlist_item.setLabelText(getString(R.string.title_activity_new_Wishlistitem));
        com.github.clans.fab.FloatingActionButton fab_add_category = (FloatingActionButton) findViewById(R.id.add_category);
        fab_add_category.setLabelText(getString(R.string.title_activity_new_category));
        com.github.clans.fab.FloatingActionButton fab_add_item = (FloatingActionButton) findViewById(R.id.add_item);
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
                intent.setClassName(getPackageName(), getPackageName() + ".views.NewItemActivity");
                intent.putExtra("is_wishlist", true);
                startActivity(intent);
                menu.close(false);

            }
        });
        fab_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".views.NewCategoryActivity");
                startActivity(intent);
                menu.close(false);

            }
        });

        fab_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".views.NewItemActivity");
                intent.putExtra("category_id", categoryId);
                startActivityForResult(intent, 1);
                menu.close(false);
            }
        });
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    /**
     * shows/hides the noCompareLayout
     * @param show boolean if the noCompareLayout should be shown
     */
    private void showNoItemLayout(boolean show) {
        if(show) {
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

        if(p_requestCode == 1) {
            // item was updated
            initDataset();
            defaultRecyclerListAdapter.notifyDataSetChanged();
            defaultRecyclerGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        list = sharedPreferences.getBoolean(Constants.KEY_VIEW_ITEMS_AS_LIST, true);
        menu.getItem(0).setIcon(list ? R.drawable.ic_view_grid : R.drawable.ic_view_list);
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
     * @param shouldBeListView boolean if the items should be shown as list view
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
        editor.putBoolean(Constants.KEY_VIEW_ITEMS_AS_LIST, list);
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

        DataBaseHelper db_helper = new DataBaseHelper(this);
        db_helper.init();

        ArrayList<Item> allCategoryItems = Item.getItemsByCategoryId(this, categoryId, false);

        for (Item tmpItem : allCategoryItems) {
            int iconId = getResources().getIdentifier("kleiderbuegel", "drawable", "com.gruppe1.pem.challengeme");
            mDataset.add(new ListItemIconName(tmpItem.getId(), iconId, tmpItem.getName(), tmpItem.getImageFile()));
        }
        if(mDataset.size() > 0) {
            showNoItemLayout(false);
        } else {
            showNoItemLayout(true);
        }
        db_helper.close();
    }


    /**
     * Starts the NewItemActivity to show detail informations of an item
     * @param itemid the id of the selected item
     */
    private void selectItem(int itemid, int position) {
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), getPackageName() + ".views.CollectionItemsActivity");

        ArrayList<Item> allCategoryItems = Item.getItemsByCategoryId(this, categoryId, false);
        intent.putExtra(Constants.EXTRA_CLICKED_ITEM_POSITION, position);
        Bundle b = new Bundle();
        b.putInt(Constants.EXTRA_ITEM_ID, itemid);
        b.putParcelableArrayList(Constants.EXTRA_ITEM_COLLECTION, allCategoryItems);
        intent.putExtras(b);
        startActivityForResult(intent, 1);
    }



    private void itemListActivityOnItemClick(View view, int position) {
        int itemid = list ? (int) defaultRecyclerListAdapter.getItemId(position) : (int)defaultRecyclerGridAdapter.getItemId(position);
        selectItem(itemid, position);
    }


    private boolean itemListActivityOnItemLongClick(RecyclerView parent, View view, int position) {
        selectedItem = new Object[2];
        selectedItem[0] = position;
        selectedItem[1] = view;

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
                int itemId = (int)defaultRecyclerListAdapter.getItemId((int)selectedItem[0]);

                DataBaseHelper db_helper = new DataBaseHelper(getApplicationContext());
                db_helper.init();

                Item deleteItem = new Item(getApplicationContext(), itemId, db_helper );
                deleteItem.delete();
                db_helper.close();

                initDataset();
                defaultRecyclerListAdapter.notifyDataSetChanged();
                defaultRecyclerGridAdapter.notifyDataSetChanged();
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
