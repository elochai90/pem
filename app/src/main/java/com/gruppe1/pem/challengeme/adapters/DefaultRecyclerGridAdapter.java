package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
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
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Array adapter to fill a default list view
 */
public class DefaultRecyclerGridAdapter extends RecyclerView.Adapter<DefaultRecyclerGridAdapter.ViewHolder>  {
    private Context context;
    private int layoutResourceId;
    private ArrayList<ListItemIconName> data = new ArrayList<>();
    private boolean isCategory;
    private PicassoSingleton picassoSingleton;

    private View.OnClickListener onItemClickListener;
    private View.OnLongClickListener onItemLongClickListener;

    public DefaultRecyclerGridAdapter(Context context, int layoutResourceId, ArrayList<ListItemIconName> data, boolean isCategory) {
        super();
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.isCategory = isCategory;
        this.picassoSingleton = PicassoSingleton.getInstance(context);
    }


    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onItemClickListener  = onClickListener;
    }
    public void setOnItemLongClickListener(View.OnLongClickListener onClickListener) {
        this.onItemLongClickListener = onClickListener;
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
        final ListItemIconName item = data.get(position);

        holder.imageTitle.setText(item.getName());


        if(isCategory || item.getItemFile() ==  null || item.isCategoryElement()) {
            holder.image.setImageResource(item.getIcon());
        } else {
            picassoSingleton.setImage(item.getItemFile(), Constants.LIST_VIEW_IMAGE_WIDTH, Constants.LIST_VIEW_IMAGE_HEIGHT, holder.image);
        }


        if(isCategory || item.isCategoryElement()) {
            holder.rightTextView.setText(Item.getItemsCountByCategoryId(context, item.getElementId(), false) + "");
        }

        holder.itemView.setOnClickListener(onItemClickListener);
        holder.itemView.setOnLongClickListener(onItemLongClickListener);
    }

    @Override
    public long getItemId(int p_position){
        return this.data.get(p_position).getElementId();
    }

    public ListItemIconName getItem(int p_position){
        return this.data.get(p_position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView itemView;
        TextView imageTitle;
        TextView rightTextView;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = (CardView) itemView;
            imageTitle = (TextView) itemView.findViewById(R.id.textView);
            rightTextView = (TextView)itemView.findViewById(R.id.rightTextView);
            image = (ImageView)itemView.findViewById(R.id.imageView);
        }
    }

}
