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
 * Array adapter to fill the navigation drawer
 */
public class NavigationDrawerAdapter extends ArrayAdapter<ListItemIconName>
{
    private final Context context;
    private final int layoutResourceId;
    private ListItemIconName data[] = null;

    /**
     * Constructor of the NavigationDrawerAdapter
     * @param context the context
     * @param layoutResourceId Layout resource for a navigation drawer list item
     * @param data the array of ListItemIconName to fill the navigation drawer with
     */
    public NavigationDrawerAdapter(Context context, int layoutResourceId, ListItemIconName[] data)
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
            ImageView img1 = (ImageView) v.findViewById(R.id.imgView1);
            ImageView img2 = (ImageView) v.findViewById(R.id.imgView2);
            ImageView img3 = (ImageView) v.findViewById(R.id.imgView3);
            TextView descTextView = (TextView) v.findViewById(R.id.navDrawerWeatherDescription);
            TextView tempTextView = (TextView) v.findViewById(R.id.navDrawerWeatherTemp);
            TextView day1 = (TextView) v.findViewById(R.id.day1);
            TextView day2 = (TextView) v.findViewById(R.id.day2);
            TextView day3 = (TextView) v.findViewById(R.id.day3);
            TextView temp1 = (TextView) v.findViewById(R.id.temp1);
            TextView temp2 = (TextView) v.findViewById(R.id.temp2);
            TextView temp3 = (TextView) v.findViewById(R.id.temp3);

            new WeatherRequest(context, imageView, descTextView, tempTextView, img1, img2, img3, day1, day2, day3, temp1, temp2, temp3);

        } else {

            v = inflater.inflate(layoutResourceId, parent, false);

            ImageView imageView = (ImageView) v.findViewById(R.id.navDrawerImageView);
            TextView textView = (TextView) v.findViewById(R.id.navDrawerTextView);

            ListItemIconName choice = data[position-1];

            imageView.setImageResource(choice.getIcon());
            textView.setText(choice.getName());
        }
        return v;
    }

    @Override
    public int getCount()
    {
        return data.length+1;
    }
}
