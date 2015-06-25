package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CompareListAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<CompareItem> data = new ArrayList();

    public CompareListAdapter(Context context, int layoutResourceId, List<CompareItem> data) {
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


       /* if(position == 0) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.list_item_add, parent, false);
            ((TextView) row.findViewById(R.id.addText)).setText(item.name);
        } else {*/
            if (row == null || row.getTag() == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.itemName1 = (TextView) row.findViewById(R.id.item1Name);
                holder.itemName2 = (TextView) row.findViewById(R.id.item2Name);
                holder.compareName = (TextView) row.findViewById(R.id.compareName);
                holder.imageItem1 = (ImageView) row.findViewById(R.id.imageItem1);
                holder.imageItem2 = (ImageView) row.findViewById(R.id.imageItem2);
                holder.rightTextView = (TextView) row.findViewById(R.id.rightTextView);
                holder.itemActionButton = (ImageView) row.findViewById(R.id.itemActionButton);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            holder.compareName.setText(item.name);
            holder.itemName1.setText(item.nameItem1);
            holder.itemName2.setText(item.nameItem2);
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            holder.rightTextView.setText(dateFormat.format(item.createdAt));
            holder.imageItem1.setImageResource(item.iconItem1);
            holder.imageItem2.setImageResource(item.iconItem2);
       // }

        return row;
    }

    static class ViewHolder {
        TextView itemName1;
        TextView itemName2;
        TextView compareName;
        ImageView imageItem1;
        ImageView imageItem2;
        TextView rightTextView;
        ImageView itemActionButton;
    }
}
