package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Attribute;
import com.gruppe1.pem.challengeme.Color;
import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Array adapter to fill a default list view
 */
public class CompareRecyclerListAdapter extends RecyclerView.Adapter<CompareRecyclerListAdapter.ViewHolder>  {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Compare> data = new ArrayList<>();
    private DataBaseHelper db_helper;
    private PicassoSingleton picassoSingleton;


    /**
     * Constructor of the CompareRecyclerListAdapter
     * @param context the context
     * @param layoutResourceId  Layout resource for a compare
     * @param data the list of Compare to fill the list with
     */
    public CompareRecyclerListAdapter(Context context, int layoutResourceId, ArrayList<Compare> data) {
        super();
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.db_helper = new DataBaseHelper(context);
        this.db_helper.init();
        this.picassoSingleton = PicassoSingleton.getInstance(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutResourceId, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Compare item = data.get(position);

        holder.compareName.setText(item.getName());

        ArrayList<Integer> itemIds = item.getItemIds();
        Item item1 = new Item(this.context, itemIds.get(0), this.db_helper);
        Item item2 = new Item(this.context, itemIds.get(1), this.db_helper);

        holder.itemName1.setText(item1.getName());
        holder.itemName2.setText(item2.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH);
        String currentDateandTime = sdf.format(new Date(Long.parseLong(item.getTimestamp())));

        holder.rightTextView.setText(currentDateandTime);
        picassoSingleton.setImage(item1.getImageFile(), Constants.LIST_VIEW_IMAGE_WIDTH, Constants.LIST_VIEW_IMAGE_HEIGHT, holder.imageItem1);
        picassoSingleton.setImage(item2.getImageFile(), Constants.LIST_VIEW_IMAGE_WIDTH, Constants.LIST_VIEW_IMAGE_HEIGHT, holder.imageItem2);


    }

    @Override
    public long getItemId(int p_position){
        return this.data.get(p_position).getId();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName1;
        TextView itemName2;
        TextView compareName;
        ImageView imageItem1;
        ImageView imageItem2;
        TextView rightTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName1 = (TextView) itemView.findViewById(R.id.item1Name);
            itemName2 = (TextView)itemView.findViewById(R.id.item2Name);
            compareName = (TextView)itemView.findViewById(R.id.compareName);
            rightTextView = (TextView)itemView.findViewById(R.id.rightTextView);
            imageItem1 = (ImageView)itemView.findViewById(R.id.imageItem1);
            imageItem2 = (ImageView)itemView.findViewById(R.id.imageItem2);
        }
    }

    /**
     * Get the db_helper instance for this class
     * @return DataBaseHelper instance
     */
    private DataBaseHelper getDb_helper() {
        if(!db_helper.isOpen()) {
            System.out.println("db helper was closed");
            db_helper = new DataBaseHelper(context);
            db_helper.init();
        }
        return db_helper;
    }
}
