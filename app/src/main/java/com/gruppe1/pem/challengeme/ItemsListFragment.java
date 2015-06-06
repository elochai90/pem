package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ItemsListFragment extends Fragment implements AbsListView.OnItemClickListener {
/*

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
*/

    private OnFragmentInteractionListener mListener;

    private AbsListView mListView;

    private ListAdapter mAdapter;


    // TODO: make only one Instance in another file, to be able to access it from everywhere
    public static final String MY_PREFERENCES = "Preferences_File";
    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;


    /*// TODO: Rename and change types of parameters
    public static ItemsListFragment newInstance(String param1, String param2) {
        ItemsListFragment fragment = new ItemsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    public ItemsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/

        sharedPreferences = getActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // TODO: replace by database categories
        ListItemIconName[] dummyItemItems = {
                new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt rose"),
                new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt rose"),
                new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt rose"),
                new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt rose"),
                new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt rose")
        };

        if(sharedPreferences.getBoolean("gridView", true))
            mAdapter = new ItemItemAdapter(getActivity(), R.layout.grid_item_item, dummyItemItems);
        else
            mAdapter = new ItemItemAdapter(getActivity(), R.layout.list_item_categories, dummyItemItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if(sharedPreferences.getBoolean("gridView", true))
            view = inflater.inflate(R.layout.fragment_categories_item_grid, container, false);
        else
            view = inflater.inflate(R.layout.fragment_categories_item_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            mListener.selectItem(position);
        }
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public interface OnFragmentInteractionListener {
        public void selectItem(int id);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_categories_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // TODO: openSettings();
                return true;
            case R.id.action_add:
                // TODO: not add, but change list/grid view
                System.out.println("Change grid view ");
                CategoriesAvtivity.changeListGridView(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
