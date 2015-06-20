package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DefaultListAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<ListItemIconName> data = new ArrayList();
    private boolean wishlist;
    private boolean compare;

    public DefaultListAdapter(Context context, int layoutResourceId, List<ListItemIconName> data, boolean wishlist, boolean compare) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.wishlist = wishlist;
        this.compare = compare;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        ListItemIconName item = data.get(position);


        if(position == 0) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.list_item_add, parent, false);
            ((TextView) row.findViewById(R.id.addText)).setText(item.name);
        } else {
            if (row == null || row.getTag() == null) {
                System.out.println("row is null");
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.firstLine = (TextView) row.findViewById(R.id.firstLine);
                holder.secondLine = (TextView) row.findViewById(R.id.secondLine);
                holder.rightTextView = (TextView) row.findViewById(R.id.rightTextView);
                holder.image = (ImageView) row.findViewById(R.id.imageView);
                holder.itemActionButton = (ImageView) row.findViewById(R.id.itemActionButton);
                row.setTag(holder);
                System.out.println("holder: " + holder);
            } else {
                System.out.println("row is not null");
                holder = (ViewHolder) row.getTag();
                System.out.println("holder: " + holder);
            }

            holder.firstLine.setText(item.name);
            holder.secondLine.setText("z.B. Item-Attribute");
            holder.rightTextView.setText("9");
            holder.image.setImageResource(item.icon);
            if(wishlist) {
                holder.itemActionButton.setVisibility(View.VISIBLE);
                holder.rightTextView.setVisibility(View.INVISIBLE);
                holder.itemActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ImageView) v).setImageResource(R.drawable.abc_btn_check_to_on_mtrl_015);
                        // TODO: save and put to categories; if click again change icon back
                    }
                });
            }else if(compare){
                holder.rightTextView.setVisibility(View.INVISIBLE);
            }
        }

        return row;
    }

    static class ViewHolder {
        TextView firstLine;
        TextView secondLine;
        TextView rightTextView;
        ImageView image;
        ImageView itemActionButton;
    }
}
