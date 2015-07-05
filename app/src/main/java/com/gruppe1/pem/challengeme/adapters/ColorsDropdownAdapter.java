package com.gruppe1.pem.challengeme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Color;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;

/**
 * Created by bianka on 26.06.2015.
 */
public class ColorsDropdownAdapter extends ArrayAdapter implements SpinnerAdapter {

    Context context;
    int textViewResourceId;
    ArrayList<Color> arrayList;

    public ColorsDropdownAdapter(Context context, int textViewResourceId, ArrayList<Color> arrayList) {
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
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(android.R.layout.simple_spinner_dropdown_item, null);

            TextView textView = (TextView) v.findViewById(android.R.id.text1);
            textView.setBackgroundColor(android.graphics.Color.parseColor(arrayList.get(position).getHexColor()));
            if(! isColorLight(android.graphics.Color.parseColor(arrayList.get(position).getHexColor()))) {
                textView.setTextColor(context.getResources().getColor(android.R.color.white));
            }
            textView.setText(arrayList.get(position).getName());
            textView.setTag(arrayList.get(position).getId());
            v.setTag(arrayList.get(position).getId());

            textView.setHeight(130);
            textView.setPadding(5,5,5,5);
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

    public int findPositionOfColorId(int colorId) {
        DataBaseHelper db_helper = new DataBaseHelper(context);
        db_helper.init();
        db_helper.setTable(Constants.COLORS_DB_TABLE);
        Color color = new Color(context, colorId, db_helper);
        db_helper.close();
        return arrayList.indexOf(color);
    }

    public Color getColorAtPosition(int position) {
        return arrayList.get(position);
    }

    public boolean isColorLight(int color) {
        boolean isColorLight = false;
        float[] pixelHSV = new float[3];
         android.graphics.Color.colorToHSV(color, pixelHSV);

        float brightness = pixelHSV[2];

        if (brightness > 0.6) {
            isColorLight = true;
        }
        return isColorLight;
    }

}
