package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ItemsListActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener  {

    public static final int REQUEST_CODE = 1;

    private ArrayList<ListItemIconName> mDataset;

    private GridView gridView;
    private DefaultGridAdapter gridAdapter;
    private ListView listView;
    private DefaultListAdapter listAdapter;
    private Boolean list;

    private RelativeLayout noItemLayout;

    private int categoryId;

    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;

    private Object[] selectedItem;
    ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.default_list_grid_view);


        noItemLayout = (RelativeLayout) findViewById(R.id.noItemLayout);
        TextView noItemText = (TextView) findViewById(R.id.noItemText);
        noItemText.setText(R.string.no_items);

        sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        if (savedInstanceState != null) {
            list = savedInstanceState.getBoolean(Constants.KEY_VIEW_ITEMS_AS_LIST, true);
        } else {
            list = true;
        }
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
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
        setTitle(category.getName());
        mDataset = new ArrayList<ListItemIconName>();

        initDataset();

        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new DefaultListAdapter(this, R.layout.list_item_default, mDataset, false, false);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new DefaultGridAdapter(this, R.layout.grid_item_default, mDataset, false);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        gridView.setVisibility(View.INVISIBLE);

        com.github.clans.fab.FloatingActionButton fab_add_compare = (FloatingActionButton) findViewById(R.id.add_compare);
        com.github.clans.fab.FloatingActionButton fab_add_wishlist_item = (FloatingActionButton) findViewById(R.id.add_wishlist_item);
        com.github.clans.fab.FloatingActionButton fab_add_category = (FloatingActionButton) findViewById(R.id.add_category);
        com.github.clans.fab.FloatingActionButton fab_add_item = (FloatingActionButton) findViewById(R.id.add_item);
        fab_add_compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".NewCompareActivity");
                startActivity(intent);

            }
        });

        fab_add_wishlist_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".NewItemActivity");
                intent.putExtra("is_wishlist", true);
                startActivity(intent);

            }
        });
        fab_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".NewCategoryActivity");
                startActivity(intent);

            }
        });

        fab_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".NewItemActivity");
                intent.putExtra("category_id", categoryId);
                startActivityForResult(intent, 1);
            }
        });


    }

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
            listAdapter.notifyDataSetChanged();
            gridAdapter.notifyDataSetChanged();
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(Constants.KEY_VIEW_ITEMS_AS_LIST, list);
        super.onSaveInstanceState(savedInstanceState);
    }


    private void initDataset() {
        mDataset.clear();

        DataBaseHelper db_helper = new DataBaseHelper(this);
        db_helper.init();

        System.out.println("Category Id before init: " + categoryId);
        ArrayList<Item> allCategoryItems = Item.getItemsByCategoryId(this, categoryId);

        Iterator catIt = allCategoryItems.iterator();
        System.out.println("cat items: " + allCategoryItems.size());

        while (catIt.hasNext()) {
            Item tmpItem = (Item)catIt.next();
            int iconId = getResources().getIdentifier("kleiderbuegel", "drawable", "com.gruppe1.pem.challengeme");
            mDataset.add(new ListItemIconName(tmpItem.getId(), iconId , tmpItem.getName(), getPicFromFile(tmpItem.getImageFile())));
        }
        if(mDataset.size() > 0) {
            showNoItemLayout(false);
        } else {
            showNoItemLayout(true);
        }
    }

   // @Override
    public void selectItem(int itemid) {
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), getPackageName() + ".NewItemActivity");
        Bundle b = new Bundle();
        b.putInt(Constants.EXTRA_ITEM_ID, itemid);
        intent.putExtras(b);
        startActivityForResult(intent, 1);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

      /*  if(position == 0) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), getPackageName() + ".NewItemActivity");
            startActivity(intent);
        } else {*/
            int itemid = list ? listAdapter.getItem(position).elementId : gridAdapter.getItem(position).elementId;
            selectItem(itemid);
        //}
    }


    private Bitmap getPicFromFile(String imageFile) {
        int targetW = 500;
        int targetH = 500;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile, bmOptions);

        Bitmap cropImg = bitmap;
        if(bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > height) {
                int crop = (width - height) / 2;
                cropImg = Bitmap.createBitmap(bitmap, crop, 0, height, height);
            } else {
                int crop = (height - width) / 2;
                cropImg = Bitmap.createBitmap(bitmap, 0, crop, width, width);
            }
        }

        return cropImg;
    }



    private ActionMode.Callback modeCallBack = new ActionMode.Callback() {

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {
            if(selectedItem != null) {
                int position = (int) selectedItem[0];
                View view = (View) selectedItem[1];
                view.setSelected(false);
                selectedItem = null;
            }
            mode = null;
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Options");
            mode.getMenuInflater().inflate(R.menu.menu_items_list_action_mode, menu);
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.delete: {
                    new AlertDialog.Builder(ItemsListActivity.this)
                            .setTitle("Do you really want to delete '" + listAdapter.getItem((int) selectedItem[0]).name + "'?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0, int arg1) {

                                    int itemId = (int)listAdapter.getItemId((int)selectedItem[0]);

                                    DataBaseHelper db_helper = new DataBaseHelper(getApplicationContext());
                                    db_helper.init();

                                    Item deleteItem = new Item(getApplicationContext(), itemId, db_helper );
                                    deleteItem.delete();

                                    initDataset();
                                    listAdapter.notifyDataSetChanged();
                                    gridAdapter.notifyDataSetChanged();
                                    actionMode.finish();
                                }
                            }).create().show();
                    break;
                }
                default:
                    return false;
            }
            return true;
        }
    };

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("Long click");
        if(actionMode != null)
            actionMode.finish();
        actionMode = startActionMode(modeCallBack);
        view.setSelected(true);

        selectedItem = new Object[2];
        selectedItem[0] = position;
        selectedItem[1] = view;

        return true;
    }
}
