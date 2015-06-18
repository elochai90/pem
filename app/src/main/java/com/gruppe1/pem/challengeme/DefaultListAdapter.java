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

/**
 * Created by bianka on 18.06.2015.
 */
public class DefaultListAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<ListItemIconName> data = new ArrayList();

    public DefaultListAdapter(Context context, int layoutResourceId, List<ListItemIconName> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.firstLine = (TextView) row.findViewById(R.id.firstLine);
            holder.secondLine = (TextView) row.findViewById(R.id.secondLine);
            holder.image = (ImageView) row.findViewById(R.id.imageView);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ListItemIconName item = data.get(position);
        holder.firstLine.setText(item.name);
        holder.secondLine.setText("z.B. Item-Attribute oder Anzahl Kategorie Items");
        holder.image.setImageResource(item.icon);
        return row;
    }

    static class ViewHolder {
        TextView firstLine;
        TextView secondLine;
        ImageView image;
    }
}
