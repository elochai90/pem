package com.gruppe1.pem.challengeme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class WishlistFragment extends Fragment implements AdapterView.OnItemClickListener{



    private ArrayList<ListItemIconName> mDataset;

    private View rootView;

    private GridView gridView;
    private DefaultGridAdapter gridAdapter;
    private ListView listView;
    private DefaultListAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.default_list_grid_view, container, false);

        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new DefaultListAdapter(getActivity(), R.layout.list_item_default, mDataset, false, true);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setVisibility(View.INVISIBLE);

        com.github.clans.fab.FloatingActionButton fab_add_compare = (FloatingActionButton) rootView.findViewById(R.id.add_compare);
        com.github.clans.fab.FloatingActionButton fab_add_wishlist_item = (FloatingActionButton) rootView.findViewById(R.id.add_wishlist_item);
        com.github.clans.fab.FloatingActionButton fab_add_category = (FloatingActionButton) rootView.findViewById(R.id.add_category);
        com.github.clans.fab.FloatingActionButton fab_add_item = (FloatingActionButton) rootView.findViewById(R.id.add_item);
        fab_add_compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".NewCompareActivity");
                startActivity(intent);

            }
        });

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

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDataset();
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_wishlist_fragment, menu);
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }


    private void initDataset() {
        // TODO: replace by database data
        mDataset = new ArrayList<ListItemIconName>();

        DataBaseHelper db_helper = new DataBaseHelper(getActivity().getApplicationContext());
        db_helper.init();

        mDataset.clear();


        ArrayList<Item> allWishlistItems = Item.getAllItems(getActivity().getApplicationContext(), true);

        Iterator wItemIt = allWishlistItems.iterator();

        while (wItemIt.hasNext()) {
            Item tmpItem = (Item)wItemIt.next();
            int iconId = getResources().getIdentifier("kleiderbuegel", "drawable", "com.gruppe1.pem.challengeme");
            mDataset.add(new ListItemIconName(tmpItem.getId(), iconId, tmpItem.getName(), getPicFromFile(tmpItem.getImageFile())));
        }


    }


    private Bitmap getPicFromFile(String imageFile) {
        // Get the dimensions of the View
        TypedValue value = new TypedValue();
        (getActivity()).getTheme().resolveAttribute(android.R.attr.listPreferredItemHeightLarge, value, true);
        int targetW = value.data;
        int targetH = value.data;

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
        return bitmap;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO
    }
}
