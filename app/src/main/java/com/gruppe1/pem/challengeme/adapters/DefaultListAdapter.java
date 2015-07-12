package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
 * Created by bianka on 18.06.2015.
 * Array adapter to fill a default list view
 */
public class DefaultListAdapter extends ArrayAdapter<ListItemIconName> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<ListItemIconName> data = new ArrayList<>();
    private boolean wishlist;
    private boolean isCategory;
    private DataBaseHelper db_helper;
    private PicassoSingleton picassoSingleton;

    /**
     * Constructor of the DefaultListAdapter
     * @param context the context
     * @param layoutResourceId  Layout resource for a item
     * @param data the list of ListItemIconNames to fill the list with
     * @param isCategory boolean if the adapter should fill the category list view
     * @param wishlist boolean if the adapter should fill the wishlist list view
     */
    public DefaultListAdapter(Context context, int layoutResourceId, ArrayList<ListItemIconName> data, boolean isCategory, boolean wishlist) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.wishlist = wishlist;
        this.isCategory = isCategory;

        db_helper = new DataBaseHelper(context);
        db_helper.init();

        this.picassoSingleton = PicassoSingleton.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        final ListItemIconName item = data.get(position);
        if (row == null || row.getTag() == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
           row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.firstLine = (TextView) row.findViewById(R.id.firstLine);
            holder.secondLine = (TextView) row.findViewById(R.id.secondLine);
            holder.rightTextView = (TextView) row.findViewById(R.id.rightTextView);
            holder.image = (ImageView) row.findViewById(R.id.imageView);
            holder.itemActionButton = (Button) row.findViewById(R.id.itemActionButton);
            holder.listItemRatingBar = (RatingBar) row.findViewById(R.id.listItemRatingBar);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.firstLine.setText(item.getName());

        if(isCategory || item.getItemFile() == null) {
            holder.image.setImageResource(item.getIcon());
        } else {
            picassoSingleton.setImage(item.getItemFile(), Constants.LIST_VIEW_IMAGE_WIDTH, Constants.LIST_VIEW_IMAGE_HEIGHT, holder.image );
        }

        if(isCategory) {
            holder.rightTextView.setText(Item.getItemsCountByCategoryId(context, item.getElementId(), false) + "");
            holder.listItemRatingBar.setVisibility(View.INVISIBLE);
        } else {
            getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
            Item listItem = new Item(context, item.getElementId(), getDb_helper());
            getDb_helper().setTable(Constants.COLORS_DB_TABLE);
            Color attributeColor = new Color(context, listItem.getPrimaryColorId(), getDb_helper());
            ArrayList<Attribute> allAttributes = Attribute.getAttributesByItemId(context, item.getElementId());
            String secondLineText = "";
            int attributesToShowCount = 0;
            if(!attributeColor.getName().equals("")) {
                secondLineText += "Color: " + attributeColor.getName() + "   ";
                attributesToShowCount++;
            }
            for(int i = 0; i < allAttributes.size(); i++) {
                // show only three, not empty attributes of type String
                if(attributesToShowCount < 3
                        && !allAttributes.get(i).getValue().toString().equals("")
                        && allAttributes.get(i).getAttributeType().getValueType() == 1) {
                    secondLineText += allAttributes.get(i).getAttributeType().getName() + ": " + allAttributes.get(i).getValue().toString() + "   ";
                    attributesToShowCount++;
                }
            }
            holder.secondLine.setText(secondLineText);

            holder.listItemRatingBar.setRating(listItem.getRating());
        }


        if(wishlist) {
            holder.itemActionButton.setVisibility(View.VISIBLE);
            holder.rightTextView.setVisibility(View.INVISIBLE);
            holder.listItemRatingBar.setVisibility(View.INVISIBLE);

            getDb_helper().setTable(Constants.ITEMS_DB_TABLE);

            Item wishlistItem = new Item(context, item.getElementId(), getDb_helper());

            if(wishlistItem.getIsWish() == 1) {
                holder.itemActionButton.setSelected(false);
                holder.itemActionButton.setTextColor(context.getResources().getColor(R.color.accent));
                holder.itemActionButton.setText(R.string.wishlist_button_not_selected);
            } else {
                holder.itemActionButton.setSelected(true);
                holder.itemActionButton.setTextColor(context.getResources().getColor(R.color.primary_dark));
                holder.itemActionButton.setText(R.string.wishlist_button_selected);
            }

            holder.itemActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getDb_helper().setTable(Constants.ITEMS_DB_TABLE);

                    Item wishlistItem = new Item(context, item.getElementId(), getDb_helper());

                    HashMap<String, String> itemAttributes = new HashMap<>();
                    itemAttributes.put("name", wishlistItem.getName());
                    itemAttributes.put("image_file", wishlistItem.getImageFile());
                    itemAttributes.put("category_id", wishlistItem.getCategoryId() + "");
                    itemAttributes.put("primary_color", wishlistItem.getPrimaryColorId() + "");
                    itemAttributes.put("rating", wishlistItem.getRating() + "");


                    if(v.isSelected()) {
                        v.setSelected(false);
                        ((Button) v).setTextColor(context.getResources().getColor(R.color.accent));
                        ((Button) v).setText(R.string.wishlist_button_not_selected);
                        itemAttributes.put("is_wish", "1");
                    } else {
                        v.setSelected(true);
                        ((Button) v).setTextColor(context.getResources().getColor(R.color.primary_dark));
                        ((Button) v).setText(R.string.wishlist_button_selected);
                        itemAttributes.put("is_wish", "0");
                    }
                    wishlistItem.edit(itemAttributes);
                    wishlistItem.save();
                }
            });
        }

        return row;
    }

    @Override
    public long getItemId(int p_position){
        return this.data.get(p_position).getElementId();
    }

    static class ViewHolder {
        TextView firstLine;
        TextView secondLine;
        TextView rightTextView;
        ImageView image;
        Button itemActionButton;
        RatingBar listItemRatingBar;
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
