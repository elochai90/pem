package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class CategoriesFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    public static final int REQUEST_CODE = 1;

    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;

    private List<ListItemIconName> mDataset;

    private View rootView;

    private GridView gridView;
    private DefaultGridAdapter gridAdapter;
    private ListView listView;
    private DefaultListAdapter listAdapter;
    private Boolean list;

    private Object[] selectedItem;
    ActionMode actionMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);


        initDataset();

        if (savedInstanceState != null) {
            list = savedInstanceState.getBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, true);
        } else {
            list = true;
        }

        rootView = getActivity().getLayoutInflater().inflate(R.layout.default_list_grid_view, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new DefaultListAdapter(getActivity(), R.layout.list_item_default, mDataset, false);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridAdapter = new DefaultGridAdapter(getActivity(), R.layout.grid_item_default, mDataset);
        gridView.setAdapter(gridAdapter);
        gridView.setVisibility(View.INVISIBLE);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);

        com.github.clans.fab.FloatingActionButton fab_add_wishlist_item = (FloatingActionButton) rootView.findViewById(R.id.add_wishlist_item);
        com.github.clans.fab.FloatingActionButton fab_add_category = (FloatingActionButton) rootView.findViewById(R.id.add_category);
        com.github.clans.fab.FloatingActionButton fab_add_item = (FloatingActionButton) rootView.findViewById(R.id.add_item);
        fab_add_wishlist_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".NewItemActivity");
                startActivity(intent);

            }
        });
        fab_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".NewCategoryActivity");
                startActivity(intent);

            }
        });

        fab_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".NewItemActivity");
                startActivity(intent);

            }
        });


        sharedPreferences = getActivity().getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initDataset();
        if (savedInstanceState != null) {
            list = savedInstanceState.getBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, true);
        } else {
            list = true;
        }


        setHasOptionsMenu(true);


    }

    public void selectCategory(int categoryId) {
        Intent intent = new Intent();
        intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".ItemsListActivity");
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
        if(actionMode != null) {
            actionMode.finish();
            if(selectedItem != null) {
                ((View)selectedItem[1]).setSelected(false);
                selectedItem = null;
            }
        }
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
        editor.putBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, list);
        editor.commit();
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

    private void initDataset() {
        // TODO: replace by database data
        // TEST
        DataBaseHelper db_helper = new DataBaseHelper(getActivity().getApplicationContext());
        db_helper.init();

        mDataset = new ArrayList<ListItemIconName>();
        mDataset.add(new ListItemIconName(0, 0, "add new category"));

        DefaultSetup defaultSetup = new DefaultSetup(getActivity().getApplicationContext());
        defaultSetup.setup("setup_values.xml");
        ArrayList<Category> allCategories = Category.getAllCategories(getActivity().getApplicationContext());

        Iterator catIt = allCategories.iterator();

        while (catIt.hasNext()) {
            Category tmpCat = (Category)catIt.next();
            int iconId = getResources().getIdentifier(tmpCat.getIcon(), "drawable", "com.gruppe1.pem.challengeme");
            Log.e("###ICON###", tmpCat.getName() + " - iconId: " + iconId);
            mDataset.add(new ListItemIconName(tmpCat.getId(), iconId , tmpCat.getName()));
        }
        ArrayList<AttributeType> attributeTypes = AttributeType.getAttributeTypes(getActivity().getApplicationContext());
    }

    private void addNewCategory(ListItemIconName newCat) {
        mDataset.add(newCat);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        actionMode.finish();
        if(position == 0) {
            Intent intent = new Intent();
            intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".NewCategoryActivity");
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            selectCategory(mDataset.get(position).elementId);
        }
    }

    // for actualizing the categories list on coming back from new category
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE) {
                if(resultCode == Activity.RESULT_OK) {
                    System.out.println("Result ok");
                    String categoryName = data.getStringExtra("categoryName");
                    int categoryId = data.getIntExtra("categoryId", -1);
                    int categoryParentId = data.getIntExtra("categoryParentId", -1);
//                    if (categoryParentId == this.category) TODO: check if category is the same like parentCategory of new Category
                    mDataset.add(new ListItemIconName(categoryId, R.drawable.kleiderbuegel, categoryName));
                    listAdapter = new DefaultListAdapter(getActivity(), R.layout.list_item_default, mDataset, false);
                    listView.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();
                    gridAdapter = new DefaultGridAdapter(getActivity(), R.layout.grid_item_default, mDataset);
                    gridView.setAdapter(gridAdapter);
                    gridAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

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
            mode.getMenuInflater().inflate(R.menu.menu_categories_list_action_mode, menu);
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.delete: {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Do you really want to delete '" + listAdapter.getItem((int)selectedItem[0]).name + "' and all items in it?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0, int arg1) {
                                    // TODO: delete persistent in db
                                    listAdapter.remove(listAdapter.getItem((int)selectedItem[0] -1));
//                    gridAdapter.remove(gridAdapter.getItem((int)selectedItem[0] -1));
                                    listAdapter.notifyDataSetChanged();
                                    gridAdapter.notifyDataSetChanged();
                                    System.out.println("selectedItemDelete: " + selectedItem[0].toString());
                                    actionMode.finish();
                                }
                            }).create().show();
                    break;
                }
                case R.id.edit: {
                    // TODO: overgive category data
                    Intent intent = new Intent();
                    intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".NewCategoryActivity");
                    startActivityForResult(intent, REQUEST_CODE);
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
        if(position != 0) {
            actionMode = getActivity().startActionMode(modeCallBack);
            view.setSelected(true);

            selectedItem = new Object[2];
            selectedItem[0] = position;
            selectedItem[1] = view;
        }
        return true;
    }
}
