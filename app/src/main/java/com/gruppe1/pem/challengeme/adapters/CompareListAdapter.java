package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.ImageLoader;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CompareListAdapter extends ArrayAdapter<Compare> {
    private Context context;
    private int layoutResourceId;
    private List<Compare> data = new ArrayList<>();
    private DataBaseHelper dbHelper;
    private PicassoSingleton picassoSingleton;

    public CompareListAdapter(Context context, int layoutResourceId, List<Compare> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.dbHelper = new DataBaseHelper(context);
        this.dbHelper.init();
        this.picassoSingleton = PicassoSingleton.getInstance();
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
        picassoSingleton.setImage(item1.getImageFile(), Constants.LIST_VIEW_IMAGE_WIDTH, Constants.LIST_VIEW_IMAGE_HEIGHT, holder.imageItem1);
        picassoSingleton.setImage(item2.getImageFile(), Constants.LIST_VIEW_IMAGE_WIDTH, Constants.LIST_VIEW_IMAGE_HEIGHT, holder.imageItem2);

        return row;
    }

    @Override
    public long getItemId(int p_position){
        return this.data.get(p_position).getId();
    }

    static class ViewHolder {
        TextView itemName1;
        TextView itemName2;
        TextView compareName;
        ImageView imageItem1;
        ImageView imageItem2;
        TextView rightTextView;
    }
}
