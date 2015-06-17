package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by bianka on 05.06.2015.
 */
public class DefaultItemAdapter extends  RecyclerView.Adapter<DefaultItemAdapter.ViewHolder>{ // implements View.OnClickListener,View.OnLongClickListener


    private List<ListItemIconName> mDataset;
    private Context context;
    private boolean isViewAsList;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public TextView mTextView;
        public ImageView mImageView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
            mTextView = (TextView) v.findViewById(R.id.textView);
            mImageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }

    public DefaultItemAdapter(Context context, List<ListItemIconName> myDataset, boolean isViewAsList, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener)
    {
        this.context = context;
        this.isViewAsList = isViewAsList;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
        mDataset = myDataset;
    }


    public void updateList(List<ListItemIconName> data) {
        mDataset = data;
        notifyDataSetChanged();
    }
    public void updateView(boolean shouldBeViewAsList) {
        isViewAsList = shouldBeViewAsList;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DefaultItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        // create a new view
        if(isViewAsList) {
            v = LayoutInflater.from(context).inflate(R.layout.list_item_default, parent, false);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.grid_item_default, parent, false);
        }
        // TODO: set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        vh.mView.setOnClickListener(onClickListener);
        vh.mView.setOnLongClickListener(onLongClickListener);
        vh.mView.setTag(vh);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).name);
        holder.mImageView.setImageResource(mDataset.get(position).icon);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addItem(int position, ListItemIconName data) {
        mDataset.add(position, data);
        notifyItemInserted(position);
        // Call this method to refresh the list and display the "updated" list
    }

    public void removeItem(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
        // Call this method to refresh the list and display the "updated" list
    }
/*
    // Implement OnClick listener. The clicked item text is displayed in a Toast message.
    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setClassName(context.getPackageName(), context.getPackageName() + ".ItemsListActivity");
        context.startActivity(intent);
    }

    // Implement OnLongClick listener. Long Clicked items is removed from list.
    @Override
    public boolean onLongClick(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (view.getId() == holder.mView.getId()) {
            removeItem(holder.getPosition());


            Toast.makeText(context, "Item " + holder.getPosition() + " has been removed from list", Toast.LENGTH_SHORT).show();
        }
        return false;
    }*/
}
