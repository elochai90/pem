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
import com.gruppe1.pem.challengeme.helpers.ColorHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Array adapter to fill the icon overlay view
 */
public class IconsGridOverlayAdapter extends ArrayAdapter<String> {
    private Context context;
    private int layoutResourceId;
    private List<String> data = new ArrayList<>();

    /**
     * Constructor of the IconsGridOverlayAdapter
     * @param context the context
     * @param layoutResourceId Layout resource for a item
     * @param data the list of drawable names of the icons to fill the grid with
     */
    public IconsGridOverlayAdapter(Context context, int layoutResourceId, List<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        String iconName = data.get(position);
        if (row == null || row.getTag() == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) row.findViewById(R.id.textView);
            holder.textView.setVisibility(View.GONE);
            holder.imageView = (ImageView) row.findViewById(R.id.imageView);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        int colorHex = context.getResources().getColor(R.color.primary_dark);
        holder.imageView.setImageDrawable(ColorHelper.filterIconColor(context, iconName, colorHex));
        return row;
    }
    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
