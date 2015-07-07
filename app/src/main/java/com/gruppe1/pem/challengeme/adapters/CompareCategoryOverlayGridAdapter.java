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
import java.util.List;

/**
 * Created by bianka on 18.06.2015.
 */
public class CompareCategoryOverlayGridAdapter extends ArrayAdapter<ListItemIconName> {
    private Context context;
    private int layoutResourceId;
    private List<ListItemIconName> data = new ArrayList();

    public CompareCategoryOverlayGridAdapter(Context context, int layoutResourceId, List<ListItemIconName> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        int iconId = data.get(position).icon;
        String categoryName = data.get(position).name;
        if (row == null || row.getTag() == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.rightTextView = (TextView) row.findViewById(R.id.rightTextView);
            holder.textView = (TextView) row.findViewById(R.id.textView);
            holder.rightTextView.setVisibility(View.INVISIBLE);
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
        TextView rightTextView;
        TextView textView;
        ImageView imageView;
    }
}
