package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by bianka on 05.06.2015.
 */
public class WishlistItemAdapter extends RecyclerView.Adapter<WishlistItemAdapter.ViewHolder> implements View.OnClickListener,
        View.OnLongClickListener{

    private List<ListItemIconName> mDataset;
    private Context context;
    private boolean isViewAsList;

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
            mTextView = (TextView) v.findViewById(R.id.wishlistTextView);
            mImageView = (ImageView) v.findViewById(R.id.wishlistImageView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public WishlistItemAdapter(Context context, List<ListItemIconName> myDataset, boolean isViewAsList) {
        this.context = context;
        this.isViewAsList = isViewAsList;
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
    public WishlistItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        // create a new view
        if(isViewAsList) {
            v = LayoutInflater.from(context).inflate(R.layout.list_item_wishlist, parent, false);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.grid_item_wishlist, parent, false);
        }
        // TODO: set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        vh.mView.setOnClickListener(WishlistItemAdapter.this);
        vh.mView.setOnLongClickListener(WishlistItemAdapter.this);
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

    // Implement OnClick listener. The clicked item text is displayed in a Toast message.
    @Override
    public void onClick(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (view.getId() == holder.mView.getId()) {
            Toast.makeText(context, "Item: " + holder.getPosition(), Toast.LENGTH_SHORT).show();
        }
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
    }


}