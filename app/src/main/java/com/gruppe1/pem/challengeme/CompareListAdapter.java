package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CompareListAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<Compare> data = new ArrayList();
    private DataBaseHelper dbHelper;

    public CompareListAdapter(Context context, int layoutResourceId, List<Compare> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.dbHelper = new DataBaseHelper(context);
        this.dbHelper.init();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Long time = System.currentTimeMillis();
        View row = convertView;
        ViewHolder holder = null;

        Compare item = data.get(position);

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
            holder.itemActionButton = (Button) row.findViewById(R.id.itemActionButton);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.compareName.setText(item.getName());

        ArrayList<Integer> itemIds = item.getItemIds();
        Item item1 = new Item(this.context, itemIds.get(0), this.dbHelper);
        Item item2 = new Item(this.context, itemIds.get(1), this.dbHelper);

        holder.itemName1.setText(item1.getName());
        holder.itemName2.setText(item2.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        String currentDateandTime = sdf.format(new Date(Long.parseLong(item.getTimestamp())));


        holder.rightTextView.setText(currentDateandTime);
        holder.imageItem1.setImageBitmap(ImageLoader.getPicFromFile(item1.getImageFile(), 100, 100));
        holder.imageItem2.setImageBitmap(ImageLoader.getPicFromFile(item2.getImageFile(), 100, 100));

        return row;
    }

    static class ViewHolder {
        TextView itemName1;
        TextView itemName2;
        TextView compareName;
        ImageView imageItem1;
        ImageView imageItem2;
        TextView rightTextView;
        Button itemActionButton;
    }
}
