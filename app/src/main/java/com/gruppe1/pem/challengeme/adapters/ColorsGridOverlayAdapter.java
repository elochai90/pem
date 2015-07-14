package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Color;
import com.gruppe1.pem.challengeme.R;

import java.util.ArrayList;

/**
 * the Color Adapter for a Color Overlay
 */
public class ColorsGridOverlayAdapter extends ArrayAdapter<Color> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Color> data;

    /**
     * Constructor of the ColorsGridOverlayAdapter
     * @param context the context
     * @param layoutResourceId Layout resource for a item
     * @param data the list of colors to fill the overlay grid with
     */
    public ColorsGridOverlayAdapter(Context context, int layoutResourceId, ArrayList<Color> data) {
        super(context, layoutResourceId, data);

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        String hexColor = data.get(position).getHexColor();
        String colorName = data.get(position).getName();
        if (row == null || row.getTag() == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) row.findViewById(R.id.textView);
            holder.textView.setText(colorName);
            holder.imageView = (ImageView) row.findViewById(R.id.imageView);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        holder.imageView.setBackgroundColor(android.graphics.Color.parseColor(hexColor));
        return row;
    }
    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }

    @Override
    public int getCount() {
        return data.size();
    }


    /**
     *
     * @param color the color to check
     * @return if the color has brightness over 60%
     */
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
