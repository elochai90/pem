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
    private List<Compare> data = new ArrayList();
    private DataBaseHelper dbHelper;

    public CompareGridAdapter(Context context, int layoutResourceId, List<Compare> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.dbHelper = new DataBaseHelper(context);
        this.dbHelper.init();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        Compare item = data.get(position);
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

        holder.compareName.setText(item.getName());

        ArrayList<Integer> itemIds = item.getItemIds();
        Item item1 = new Item(this.context, itemIds.get(0), this.dbHelper);
        Item item2 = new Item(this.context, itemIds.get(1), this.dbHelper);

        holder.imageItem1.setImageBitmap(ImageLoader.getPicFromFile(item1.getImageFile(), 1000, 1000));
        holder.imageItem2.setImageBitmap(ImageLoader.getPicFromFile(item2.getImageFile(), 1000, 1000));
        return row;
    }

    static class ViewHolder {
        TextView compareName;
        ImageView imageItem1;
        ImageView imageItem2;
    }
}
