package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianka on 18.06.2015.
 */
public class IconsGridAdapter extends ArrayAdapter<String> {
    private Context context;
    private int layoutResourceId;
    private List<String> data = new ArrayList();

    public IconsGridAdapter(Context context, int layoutResourceId, List<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        String iconName = data.get(position);
        if (row == null || row.getTag() == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.rightTextView = (TextView) row.findViewById(R.id.rightTextView);
            holder.textView = (TextView) row.findViewById(R.id.textView);
            holder.rightTextView.setVisibility(View.INVISIBLE);
            holder.textView.setVisibility(View.INVISIBLE);
            holder.imageView = (ImageView) row.findViewById(R.id.imageView);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        int iconId = context.getResources().getIdentifier(iconName, "drawable", "com.gruppe1.pem.challengeme");
        holder.imageView.setImageResource(iconId);
        return row;
    }
    static class ViewHolder {
        TextView rightTextView;
        TextView textView;
        ImageView imageView;
    }
}
