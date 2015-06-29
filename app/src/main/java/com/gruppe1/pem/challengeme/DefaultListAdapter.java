package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefaultListAdapter extends ArrayAdapter<ListItemIconName> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<ListItemIconName> data = new ArrayList();
    private boolean wishlist;
    private boolean isCategory;

    private DataBaseHelper db_helper;

    public int currentSelection;

    public DefaultListAdapter(Context context, int layoutResourceId, ArrayList<ListItemIconName> data, boolean isCategory, boolean wishlist) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.wishlist = wishlist;
        this.isCategory = isCategory;

        db_helper = new DataBaseHelper(context);
        db_helper.init();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        final ListItemIconName item = data.get(position);


     /*   if(position == 0) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.list_item_add, parent, false);
            ((TextView) row.findViewById(R.id.addText)).setText(item.name);
        } else {*/
            if (row == null || row.getTag() == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
               row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.firstLine = (TextView) row.findViewById(R.id.firstLine);
                holder.secondLine = (TextView) row.findViewById(R.id.secondLine);
                holder.rightTextView = (TextView) row.findViewById(R.id.rightTextView);
                holder.image = (ImageView) row.findViewById(R.id.imageView);
                holder.itemActionButton = (Button) row.findViewById(R.id.itemActionButton);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            holder.firstLine.setText(item.name);

            if(isCategory || item.itemBitmap == null) {
                holder.image.setImageResource(item.icon);
            } else {
                holder.image.setImageBitmap(item.itemBitmap);
            }

            if(isCategory) {
                holder.rightTextView.setText(Item.getItemsCountByCategoryId(context, item.elementId) + "");
            } else {
                holder.secondLine.setText("z.B. Item-Attribute");
            }


            if(wishlist) {
                holder.itemActionButton.setVisibility(View.VISIBLE);
                holder.rightTextView.setVisibility(View.INVISIBLE);

                db_helper.setTable(Constants.ITEMS_DB_TABLE);

                Item wishlistItem = new Item(context, item.elementId, db_helper);

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
                        System.out.println("on wishlist click button: " + ((Button) v).isSelected());

                        db_helper.setTable(Constants.ITEMS_DB_TABLE);

                        Item wishlistItem = new Item(context, item.elementId, db_helper);

                        HashMap<String, String> itemAttributes = new HashMap<String, String>();
                        itemAttributes.put("name", wishlistItem.getName());
                        itemAttributes.put("image_file", wishlistItem.getImageFile());
                        itemAttributes.put("category_id", wishlistItem.getCategoryId() + "");
                        itemAttributes.put("primary_color", wishlistItem.getPrimaryColorId() + "");
                        itemAttributes.put("rating", wishlistItem.getRating() + "");


                        if(((Button)v).isSelected()) {
                            ((Button) v).setSelected(false);
                            ((Button) v).setTextColor(context.getResources().getColor(R.color.accent));
                            ((Button) v).setText(R.string.wishlist_button_not_selected);
                            itemAttributes.put("is_wish", "1");
                        } else {
                            ((Button) v).setSelected(true);
                            ((Button) v).setTextColor(context.getResources().getColor(R.color.primary_dark));
                            ((Button) v).setText(R.string.wishlist_button_selected);
                            itemAttributes.put("is_wish", "0");
                        }
                        wishlistItem.edit(itemAttributes);
                        wishlistItem.save();
                    }
                });
            }
        //}

        return row;
    }

    @Override
    public long getItemId(int p_position){
        return this.data.get(p_position).elementId;
    }

    static class ViewHolder {
        TextView firstLine;
        TextView secondLine;
        TextView rightTextView;
        ImageView image;
        Button itemActionButton;
    }
}
