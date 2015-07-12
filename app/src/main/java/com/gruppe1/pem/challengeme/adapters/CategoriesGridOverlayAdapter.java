package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;

import java.util.ArrayList;

/**
 * Created by bianka on 18.06.2015.
 * the Category Adapter for a Category Select Overlay
 */
public class CategoriesGridOverlayAdapter extends ArrayAdapter<ListItemIconName> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<ListItemIconName> data = new ArrayList<>();

    /**
     * Constructor of the CategoriesGridOverlayAdapter
     * @param context the context
     * @param layoutResourceId Layout resource for a row
     * @param data the data to fill the overlay grid with
     */
    public CategoriesGridOverlayAdapter(Context context, int layoutResourceId, ArrayList<ListItemIconName> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        int iconId = data.get(position).getIcon();
        String categoryName = data.get(position).getName();
        if (row == null || row.getTag() == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) row.findViewById(R.id.textView);
            holder.textView.setText(categoryName);
            holder.imageView = (ImageView) row.findViewById(R.id.imageView);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        holder.imageView.setImageResource(iconId);
        return row;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
