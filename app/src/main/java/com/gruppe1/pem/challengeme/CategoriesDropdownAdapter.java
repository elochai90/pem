package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bianka on 26.06.2015.
 */
public class CategoriesDropdownAdapter extends ArrayAdapter implements SpinnerAdapter {

    Context context;
    int textViewResourceId;
    ArrayList<Category> arrayList;

    public CategoriesDropdownAdapter(Context context, int textViewResourceId,  ArrayList<Category> arrayList) {
        super(context, textViewResourceId, arrayList);

        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.arrayList = arrayList;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        return getAdapterView(position, convertView, parent);
    }

    private View getAdapterView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);

        if (position == getCount()) {
            ((TextView)v.findViewById(android.R.id.text1)).setText("");
            ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
        } else {
            //        if (convertView == null)
            //        {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //convertView = vi.inflate(android.R.layout.simple_spinner_dropdown_item, null);
            v = vi.inflate(android.R.layout.simple_spinner_dropdown_item, null);
            //        }

            TextView textView = (TextView) v.findViewById(android.R.id.text1);
            textView.setText(arrayList.get(position).getName());//after changing from ArrayList<String> to ArrayList<Object>
            //        textView.setId(arrayList.get(position).getId());
            textView.setTag(arrayList.get(position).getId());
            v.setTag(arrayList.get(position).getId());

            //        if (position  == 0) {
            //            textView.setHeight(0);
            //        }
            //        else{
            textView.setHeight(100);
//        }
        }

        return v;

    }

    @Override
    public long getItemId(int position) {
        return arrayList.get(position).getId();
    }

    @Override
    public String getItem(int position) {
        return arrayList.get(position).getName();
    }
    @Override
    public int getCount() {
        return arrayList.size()-1;
    }

    public int findPositionOfCategoryId(int catId) {
        DataBaseHelper db_helper = new DataBaseHelper(context);
        db_helper.init();
        Category category = new Category(context, catId, db_helper);
        System.out.println(category.getName());
        System.out.println(arrayList.indexOf(category));
        return arrayList.indexOf(category);
    }

}
