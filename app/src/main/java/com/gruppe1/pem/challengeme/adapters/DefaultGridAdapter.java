package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Array adapter to fill a default grid view
 */
public class DefaultGridAdapter extends ArrayAdapter<ListItemIconName> {
    private Context context;
    private int layoutResourceId;
    private List<ListItemIconName> data = new ArrayList<>();
    private boolean isCategory;
    private PicassoSingleton picassoSingleton;

    /**
     * Constructor of the DefaultGridAdapter
     * @param context the context
     * @param layoutResourceId  Layout resource for a item
     * @param data the list of ListItemIconNames to fill the grid list with
     * @param isCategory boolean if the adapter should fill the category grid
     */
    public DefaultGridAdapter(Context context, int layoutResourceId, List<ListItemIconName> data, boolean isCategory) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.isCategory = isCategory;
        this.picassoSingleton = PicassoSingleton.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        ListItemIconName item = data.get(position);


        if (row == null || row.getTag() == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.textView);
            holder.rightTextView = (TextView) row.findViewById(R.id.rightTextView);
            holder.image = (ImageView) row.findViewById(R.id.imageView);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.imageTitle.setText(item.getName());


        if(isCategory || item.getItemFile() ==  null) {
            holder.image.setImageResource(item.getIcon());
        } else {
            picassoSingleton.setImage(item.getItemFile(), Constants.LIST_VIEW_IMAGE_WIDTH, Constants.LIST_VIEW_IMAGE_HEIGHT, holder.image);
        }


        if(isCategory) {
            holder.rightTextView.setText(Item.getItemsCountByCategoryId(context, item.getElementId(), false) + "");
        }
        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        TextView rightTextView;
        ImageView image;
    }
}
