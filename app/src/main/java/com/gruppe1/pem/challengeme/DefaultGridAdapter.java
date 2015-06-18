package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianka on 18.06.2015.
 */
public class DefaultGridAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<ListItemIconName> data = new ArrayList();

    public DefaultGridAdapter(Context context, int layoutResourceId, List<ListItemIconName> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        ListItemIconName item = data.get(position);


        if(position == 0) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.grid_item_add, parent, false);
            ((TextView) row.findViewById(R.id.addText)).setText(item.name);
        } else {
            if (row == null || row.getTag() == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.imageTitle = (TextView) row.findViewById(R.id.textView);
                holder.rightTextView = (TextView) row.findViewById(R.id.rightTextView);
                holder.image = (ImageView) row.findViewById(R.id.imageView);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            holder.imageTitle.setText(item.name);
            holder.rightTextView.setText("9");
            holder.image.setImageResource(item.icon);
        }
        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        TextView rightTextView;
        ImageView image;
    }
}
