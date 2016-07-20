package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
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
import com.gruppe1.pem.challengeme.helpers.ImageLoader;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Array adapter to fill a default list view
 */
public class CompareRecyclerGridAdapter extends RecyclerView.Adapter<CompareRecyclerGridAdapter.ViewHolder>  {
    private Context context;
    private int layoutResourceId;
    private List<Compare> data = new ArrayList<>();
    private DataBaseHelper dbHelper;
    private PicassoSingleton picassoSingleton;

    private View.OnClickListener onItemClickListener;
    private View.OnLongClickListener onItemLongClickListener;

    public CompareRecyclerGridAdapter(Context context, int layoutResourceId, ArrayList<Compare> data) {
        super();
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.dbHelper = new DataBaseHelper(context);
        this.dbHelper.init();
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
        Compare item = data.get(position);

        holder.compareName.setText(item.getName());

        ArrayList<Integer> itemIds = item.getItemIds();
        Item item1 = new Item(this.context, itemIds.get(0), this.dbHelper);
        Item item2 = new Item(this.context, itemIds.get(1), this.dbHelper);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH);
        String currentDateandTime = sdf.format(new Date(Long.parseLong(item.getTimestamp())));

        holder.rightTextView.setText(currentDateandTime);

        picassoSingleton.setImage(item1.getImageFile(), Constants.LIST_VIEW_IMAGE_WIDTH_BIG, Constants.LIST_VIEW_IMAGE_HEIGHT_BIG, holder.imageItem1);
        picassoSingleton.setImage(item2.getImageFile(), Constants.LIST_VIEW_IMAGE_WIDTH_BIG, Constants.LIST_VIEW_IMAGE_HEIGHT_BIG, holder.imageItem2);

        holder.itemView.setOnClickListener(onItemClickListener);
        holder.itemView.setOnLongClickListener(onItemLongClickListener);
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
        CardView itemView;
        TextView compareName;
        ImageView imageItem1;
        ImageView imageItem2;
        TextView rightTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = (CardView) itemView;
            compareName = (TextView) itemView.findViewById(R.id.compareName);
            imageItem1 = (ImageView)itemView.findViewById(R.id.imageItem1);
            imageItem2 = (ImageView)itemView.findViewById(R.id.imageItem2);
            rightTextView = (TextView)itemView.findViewById(R.id.rightTextView);
        }
    }

}
