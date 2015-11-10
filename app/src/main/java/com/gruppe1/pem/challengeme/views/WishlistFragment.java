package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.DefaultRecyclerListAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.RecyclerItemClickListener;

import java.util.ArrayList;


public class WishlistFragment extends Fragment {

    private ArrayList<ListItemIconName> mDataset;
    private RelativeLayout noWishlistItemLayout;
    private Object[] selectedItem;
    private RecyclerView listView;
    private RecyclerView gridView;
    private FrameLayout frameLayout;
    private DefaultRecyclerListAdapter defaultRecyclerListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.default_recycler_view, container, false);

        noWishlistItemLayout = (RelativeLayout) rootView.findViewById(R.id.noItemLayout);
        listView = (RecyclerView) rootView.findViewById(R.id.listView);
        gridView = (RecyclerView) rootView.findViewById(R.id.gridView);
        frameLayout = (FrameLayout) rootView.findViewById(R.id.frameLayout);

        TextView noWishlistItemText = (TextView) rootView.findViewById(R.id.noItemText);
        noWishlistItemText.setText(R.string.no_wishlist_items);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridView.setLayoutManager(gridLayoutManager);
        gridView.setHasFixedSize(true);
        gridView.setVisibility(View.INVISIBLE);

        LinearLayoutManager linearLayoutManagerList = new LinearLayoutManager(getActivity().getBaseContext());
        listView.setLayoutManager(linearLayoutManagerList);
        listView.setHasFixedSize(true);

        mDataset = new ArrayList<>();
        initDataset();


        defaultRecyclerListAdapter = new DefaultRecyclerListAdapter(getActivity(), R.layout.list_item_default, mDataset, false, true);
        listView.setAdapter(defaultRecyclerListAdapter);
        listView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                wishlistFragmentOnItemClick(view, position);
            }

            @Override
            public void onItemLongClick(RecyclerView parent, View view, int position) {
                wishlistFragmentOnItemLongClick(parent, view, position);
            }
        }));

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
            gridView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.INVISIBLE);
            noWishlistItemLayout.setVisibility(View.VISIBLE);
        } else {
            noWishlistItemLayout.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_wishlist_fragment, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".views.SearchResultsActivity");
                startActivity(intent);
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
        final LinearLayoutManager layoutManager = (LinearLayoutManager) listView.getLayoutManager();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TypedValue tv = new TypedValue();
                int actionBarHeight = 0;
                if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                {
                    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
                }
                //no items in the RecyclerView
                if (listView.getAdapter().getItemCount() == 0) {
                    listView.setNestedScrollingEnabled(false);
                    frameLayout.setPadding(0, 0, 0, actionBarHeight);
                    //if the first and the last item is visible
                } else if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0
                        && layoutManager.findLastCompletelyVisibleItemPosition() == listView.getAdapter().getItemCount() - 1) {
                    listView.setNestedScrollingEnabled(false);
                    frameLayout.setPadding(0,0,0, actionBarHeight);
                } else {
                    listView.setNestedScrollingEnabled(true);
                }
            }
        }, 5);
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

    private void wishlistFragmentOnItemClick(View view, int position) {
        int itemid = (int) defaultRecyclerListAdapter.getItemId(position);
//        getItem(position).getElementId();
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
                    defaultRecyclerListAdapter.notifyDataSetChanged();
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean wishlistFragmentOnItemLongClick(RecyclerView parent, View view, int position) {
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
                int wishlistItemId = (int) defaultRecyclerListAdapter.getItemId((int) selectedItem[0]);

                DataBaseHelper db_helper = new DataBaseHelper(getActivity().getApplicationContext());
                db_helper.init();

                Item deleteWishlistItem = new Item(getActivity().getApplicationContext(), wishlistItemId, db_helper);
                deleteWishlistItem.delete();
                db_helper.close();

                initDataset();
                defaultRecyclerListAdapter.notifyDataSetChanged();
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
