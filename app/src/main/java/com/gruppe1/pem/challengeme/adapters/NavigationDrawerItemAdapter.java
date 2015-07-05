package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.WeatherRequest;

/**
 * Created by bianka on 05.06.2015.
 */
public class NavigationDrawerItemAdapter extends ArrayAdapter<ListItemIconName>
{
    private final Context context;
    private final int layoutResourceId;
    private ListItemIconName data[] = null;

    public NavigationDrawerItemAdapter(Context context, int layoutResourceId, ListItemIconName[] data)
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
        View v;
        if(position == 0) {

            v = inflater.inflate(R.layout.list_item_navigation_drawer_weather, parent, false);

            ImageView imageView = (ImageView) v.findViewById(R.id.navDrawerWeatherImage);
            TextView descTextView = (TextView) v.findViewById(R.id.navDrawerWeatherDescription);
            TextView tempTextView = (TextView) v.findViewById(R.id.navDrawerWeatherTemp);

            new WeatherRequest(context, imageView, descTextView, tempTextView);

        } else {

            v = inflater.inflate(layoutResourceId, parent, false);

            ImageView imageView = (ImageView) v.findViewById(R.id.navDrawerImageView);
            TextView textView = (TextView) v.findViewById(R.id.navDrawerTextView);

            ListItemIconName choice = data[position-1];

            imageView.setImageResource(choice.icon);
            textView.setText(choice.name);
        }
        return v;
    }

    @Override
    public int getCount()
    {
        return data.length+1;
    }
}
