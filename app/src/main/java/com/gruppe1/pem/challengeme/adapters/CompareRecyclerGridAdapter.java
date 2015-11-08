package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Array adapter to fill a default list view
 */
public class CompareRecyclerGridAdapter extends RecyclerView.Adapter<CompareRecyclerGridAdapter.ViewHolder>  {
    private Context context;
    private int layoutResourceId;
    private List<Compare> data = new ArrayList<>();
    private DataBaseHelper dbHelper;
    private PicassoSingleton picassoSingleton;


    public CompareRecyclerGridAdapter(Context context, int layoutResourceId, ArrayList<Compare> data) {
        super();
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.dbHelper = new DataBaseHelper(context);
        this.dbHelper.init();
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
        Item item1 = new Item(this.context, itemIds.get(0), this.dbHelper);
        Item item2 = new Item(this.context, itemIds.get(1), this.dbHelper);

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
        TextView compareName;
        ImageView imageItem1;
        ImageView imageItem2;

        public ViewHolder(View itemView) {
            super(itemView);
            compareName = (TextView) itemView.findViewById(R.id.compareName);
            imageItem1 = (ImageView)itemView.findViewById(R.id.imageItem1);
            imageItem2 = (ImageView)itemView.findViewById(R.id.imageItem2);
        }
    }

}
