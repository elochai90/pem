package com.gruppe1.pem.challengeme.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.DefaultSize;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;

/**
 * Created by bianka on 06.07.2015.
 */
public class DefaultSizesAdapter extends ArrayAdapter<DefaultSize> implements SpinnerAdapter {

    Context context;
    int textViewResourceId;
    ArrayList<DefaultSize> arrayList;

    public DefaultSizesAdapter(Context context, int textViewResourceId, ArrayList<DefaultSize> arrayList) {
        super(context, textViewResourceId, arrayList);

        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.arrayList = arrayList;

    }


    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(android.R.layout.simple_spinner_dropdown_item, null);

        TextView textView = (TextView) v.findViewById(android.R.id.text1);
        if(position == 0) {
            textView.setText(arrayList.get(position).getDefaultSizeName());
        } else {
            textView.setText(arrayList.get(position).getDefaultSizeName() + ": " + arrayList.get(position).getDefaultSizeValue());
        }
        textView.setTag(arrayList.get(position).getDefaultSizeName());
        v.setTag(arrayList.get(position).getDefaultSizeName());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        textView.setPadding(padding,padding,padding,padding);
        return v;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(android.R.layout.simple_spinner_dropdown_item, null);

        TextView textView = (TextView) v.findViewById(android.R.id.text1);
        if(position == 0) {
            textView.setText(arrayList.get(position).getDefaultSizeName());
        } else {
            textView.setText(arrayList.get(position).getDefaultSizeName() + ": " + arrayList.get(position).getDefaultSizeValue());
        }
        textView.setTag(arrayList.get(position).getDefaultSizeName());
        v.setTag(arrayList.get(position).getDefaultSizeName());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);

        return v;

    }

    @Override
    public DefaultSize getItem(int position) {
        return arrayList.get(position);
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
}
