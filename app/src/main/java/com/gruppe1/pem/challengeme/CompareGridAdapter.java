package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianka on 18.06.2015.
 */
public class CompareGridAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<CompareItem> data = new ArrayList();

    public CompareGridAdapter(Context context, int layoutResourceId, List<CompareItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        CompareItem item = data.get(position);
        if (row == null || row.getTag() == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.compareName = (TextView) row.findViewById(R.id.compareName);
            holder.imageItem1 = (ImageView) row.findViewById(R.id.imageItem1);
            holder.imageItem2 = (ImageView) row.findViewById(R.id.imageItem2);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.compareName.setText(item.name);
        holder.imageItem1.setImageResource(item.iconItem1);
        holder.imageItem2.setImageResource(item.iconItem2);
        return row;
    }

    static class ViewHolder {
        TextView compareName;
        ImageView imageItem1;
        ImageView imageItem2;
    }
}
