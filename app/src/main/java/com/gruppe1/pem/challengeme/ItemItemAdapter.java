package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by bianka on 05.06.2015.
 */
public class ItemItemAdapter extends ArrayAdapter<ListItemIconName>
{
    private final Context context;
    private final int layoutResourceId;
    private ListItemIconName data[] = null;

    public ItemItemAdapter(Context context, int layoutResourceId, ListItemIconName[] data)
    {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        View v = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageView = (ImageView) v.findViewById(R.id.itemImageView);

        ListItemIconName choice = data[position];

        imageView.setImageResource(choice.icon);

        return v;
    }
}
