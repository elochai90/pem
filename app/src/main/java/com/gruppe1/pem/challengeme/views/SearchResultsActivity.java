package com.gruppe1.pem.challengeme.views;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Color;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.DefaultRecyclerListAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;


/**
 * Created by bianka on 13.10.2015.
 */
public class SearchResultsActivity extends AppCompatActivity {

    private ArrayList<ListItemIconName> mDataset;

    private RecyclerView gridView;
    private RecyclerView listView;
    private DefaultRecyclerListAdapter defaultRecyclerListAdapter;
    private ArrayList<Item> searchResultItems = new ArrayList<>();
    private String query;

    private RelativeLayout noItemLayout;
    private TextView noItemText;
    private ImageView noItemArrow;

    private LinearLayout filterColorLayout;
    private LinearLayout filterColorLayoutFirstLine;
    private LinearLayout filterColorLayoutSecondLine;
    private LinearLayout filterRatingLayout;
    private ImageButton filterWishlist;
    private ImageButton filterRating;
    private ImageButton filterColor;
    private boolean filterWishlistActivated;
    private boolean filterRatingActivated;
    private boolean filterColorActivated;

    private ArrayList<Integer> filterColorIds;
    private int filterRatingCount;



    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        noItemLayout = (RelativeLayout) findViewById(R.id.noItemLayout);
        noItemText = (TextView) findViewById(R.id.noItemText);
        noItemArrow = (ImageView) findViewById(R.id.noItemArrow);
        listView = (RecyclerView) findViewById(R.id.listView);
        gridView = (RecyclerView) findViewById(R.id.gridView);
        filterColorLayout = (LinearLayout) findViewById(R.id.filterColorLayout);
        filterColorLayoutFirstLine = (LinearLayout) findViewById(R.id.filterColorLayoutFirstLine);
        filterColorLayoutSecondLine = (LinearLayout) findViewById(R.id.filterColorLayoutSecondLine);
        filterRatingLayout = (LinearLayout) findViewById(R.id.filterRatingLayout);
        filterWishlist = (ImageButton) findViewById(R.id.filterWishlist);
        filterRating = (ImageButton) findViewById(R.id.filterRating);
        filterColor = (ImageButton) findViewById(R.id.filterColor);

        sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setupToolbar();

        DataBaseHelper db_helper = new DataBaseHelper(this);
        db_helper.init();
        db_helper.close();
        mDataset = new ArrayList<>();
        query = "";
        filterWishlistActivated = false;
        filterRatingCount = -1;
        filterColorIds = new ArrayList<>();


        defaultRecyclerListAdapter = new DefaultRecyclerListAdapter(this, R.layout.list_item_default, mDataset, false, false);
        defaultRecyclerListAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchResultsActivityOnItemClick(v,listView.getChildPosition(v));
            }
        });
        defaultRecyclerListAdapter.setOnItemLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                searchResultsActivityOnItemLongClick(listView,v,listView.getChildPosition(v));
                return true;
            }
        });

        LinearLayoutManager linearLayoutManagerList = new LinearLayoutManager(getBaseContext());
        listView.setLayoutManager(linearLayoutManagerList);
        listView.setHasFixedSize(true);
        listView.setAdapter(defaultRecyclerListAdapter);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridView.setLayoutManager(gridLayoutManager);
        gridView.setHasFixedSize(true);
        gridView.setVisibility(View.INVISIBLE);

        initDataset();
        setupFilterViews();

    }

    private void setRatingFilter(int rating) {
        for(int i = 0; i < 5; i++) {
            ImageButton tmpRatingButton = (ImageButton) filterRatingLayout.getChildAt(i);
            if(i < rating) {
                tmpRatingButton.setBackground(getDrawable(R.drawable.ic_star_white));
            } else {
                tmpRatingButton.setBackground(getDrawable(R.drawable.ic_star_border_white));
            }
        }
    }
    private void deselectAllColorFilter() {
        for(int i = 0; i < filterColorLayoutFirstLine.getChildCount(); i++) {
            ImageButton tmpColorButton = (ImageButton) filterColorLayoutFirstLine.getChildAt(i);
            String tmpColor = (String)tmpColorButton.getTag();
            tmpColorButton.setBackground(getDrawable(R.drawable.circle_color));
            tmpColorButton.getBackground().setColorFilter(new
                    PorterDuffColorFilter(android.graphics.Color.parseColor(tmpColor), PorterDuff.Mode.MULTIPLY));
        }
        for(int i = 0; i < filterColorLayoutSecondLine.getChildCount(); i++) {
            ImageButton tmpColorButton = (ImageButton) filterColorLayoutSecondLine.getChildAt(i);
            String tmpColor = (String)tmpColorButton.getTag();
            tmpColorButton.setBackground(getDrawable(R.drawable.circle_color));
            tmpColorButton.getBackground().setColorFilter(new
                    PorterDuffColorFilter(android.graphics.Color.parseColor(tmpColor), PorterDuff.Mode.MULTIPLY));
        }
    }

    private void setupFilterViews() {

        int colorIconWidthHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        int colorIconMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        ArrayList<Color> allColors = Color.getAllColors(getApplicationContext());
        int positionColor = 0;
        for (final Color tmpColor : allColors) {
            ImageButton tmpColorButton = new ImageButton(this);
            LinearLayout.LayoutParams attibuteNameLayoutParams = new LinearLayout.LayoutParams(colorIconWidthHeight, colorIconWidthHeight);
            attibuteNameLayoutParams.setMargins(0, 0, colorIconMargin, 0);
            tmpColorButton.setLayoutParams(attibuteNameLayoutParams);
            tmpColorButton.setBackground(getDrawable(R.drawable.circle_color));
            tmpColorButton.getBackground().setColorFilter(new
                    PorterDuffColorFilter(android.graphics.Color.parseColor((tmpColor).getHexColor()), PorterDuff.Mode.MULTIPLY));
            tmpColorButton.setTag(tmpColor.getHexColor());
            tmpColorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(filterColorIds.contains(tmpColor.getId())) {
                        filterColorIds.remove(filterColorIds.indexOf(tmpColor.getId()));
                        v.setBackground(getDrawable(R.drawable.circle_color));
                        v.getBackground().setColorFilter(new
                                PorterDuffColorFilter(android.graphics.Color.parseColor((tmpColor).getHexColor()), PorterDuff.Mode.MULTIPLY));
                    } else {
                        filterColorIds.add(tmpColor.getId());
                        v.setBackground(getDrawable(R.drawable.circle_color_filled));
                        ((LayerDrawable)v.getBackground()).getDrawable(0).setColorFilter(new
                                PorterDuffColorFilter(android.graphics.Color.parseColor((tmpColor).getHexColor()), PorterDuff.Mode.MULTIPLY));
                    }
                    initDataset();
                }
            });
            if(positionColor < allColors.size()/2) {
                filterColorLayoutFirstLine.addView(tmpColorButton);
            } else {
                filterColorLayoutSecondLine.addView(tmpColorButton);
            }
            positionColor++;
        }
        for(int i = 1; i <= 5; i++) {
            ImageButton tmpRatingButton = new ImageButton(this);
            LinearLayout.LayoutParams attibuteNameLayoutParams = new LinearLayout.LayoutParams(colorIconWidthHeight, colorIconWidthHeight);
            attibuteNameLayoutParams.setMargins(0, 0, colorIconMargin, 0);
            tmpRatingButton.setLayoutParams(attibuteNameLayoutParams);
            tmpRatingButton.setBackground(getDrawable(R.drawable.ic_star_border_white));
            tmpRatingButton.setTag(i);
            final int ratingWhenClicked = i;
            tmpRatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterRatingCount = ratingWhenClicked;
                    setRatingFilter(ratingWhenClicked);
                    initDataset();
                }
            });
            filterRatingLayout.addView(tmpRatingButton);
        }
        filterWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filterWishlistActivated) {
                    filterWishlist.setBackground(getDrawable(R.drawable.icon_wishlist_circle));
                } else {
                    filterWishlist.setBackground(getDrawable(R.drawable.icon_wishlist_circle_filled));
                }
                filterWishlistActivated = !filterWishlistActivated;
                initDataset();
            }
        });
        filterRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filterColorActivated) {
                    if(filterColorIds.size() == 0) {
                        filterColor.callOnClick();
                    } else {
                        filterColorLayout.setVisibility(View.GONE);
                        filterColorActivated = false;
                    }
                }
                if(filterRatingActivated) {
                    filterRatingCount = -1;
                    setRatingFilter(0);
                    filterRating.setBackground(getDrawable(R.drawable.icon_rating_circle));
                    filterRatingLayout.setVisibility(View.GONE);
                    filterRatingActivated = false;
                    initDataset();
                } else {
                    filterRating.setBackground(getDrawable(R.drawable.icon_rating_circle_filled));
                    filterRatingLayout.setVisibility(View.VISIBLE);
                    filterRatingActivated = true;
                }
            }
        });
        filterColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filterRatingActivated) {
                    if(filterRatingCount == -1) {
                        filterRating.callOnClick();
                    } else {
                        filterRatingLayout.setVisibility(View.GONE);
                        filterRatingActivated = false;
                    }
                }
                if(filterColorActivated) {
                    filterColorIds.clear();
                    deselectAllColorFilter();
                    filterColor.setBackground(getDrawable(R.drawable.icon_color_circle));
                    filterColorLayout.setVisibility(View.GONE);
                    filterColorActivated = false;
                    initDataset();
                } else {
                    filterColor.setBackground(getDrawable(R.drawable.icon_color_circle_filled));
                    filterColorLayout.setVisibility(View.VISIBLE);
                    filterColorActivated = true;
                }
            }
        });
    }


    private void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_search, null);
        getSupportActionBar().setCustomView(v);
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
            public boolean onQueryTextSubmit(String newQuery) {
                query = newQuery;
                initDataset();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText;
                initDataset();
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
            initDataset();
            defaultRecyclerListAdapter.notifyDataSetChanged();
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
    private void initDataset() {
        mDataset.clear();

        DataBaseHelper db_helper = new DataBaseHelper(this);
        db_helper.init();

        searchResultItems = Item.getSearchResults(this, query, filterWishlistActivated, filterRatingCount, filterColorIds);

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
        defaultRecyclerListAdapter.notifyDataSetChanged();
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

    private void searchResultsActivityOnItemClick(View view, int position) {
        int itemid = (int) defaultRecyclerListAdapter.getItemId(position);
        selectItem(itemid, position);
    }

    private boolean searchResultsActivityOnItemLongClick(RecyclerView parent, View view, final int position) {

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
                int itemId = (int)defaultRecyclerListAdapter.getItemId(position);

                DataBaseHelper db_helper = new DataBaseHelper(getApplicationContext());
                db_helper.init();

                Item deleteItem = new Item(getApplicationContext(), itemId, db_helper );
                deleteItem.delete();
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
