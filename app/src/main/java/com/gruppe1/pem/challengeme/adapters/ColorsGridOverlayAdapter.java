package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Color;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;

import java.util.ArrayList;

/**
 * the Color Adapter for a Color Overlay
 */
public class ColorsGridOverlayAdapter extends RecyclerView.Adapter<ColorsGridOverlayAdapter.ViewHolder>  {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Color> data;
    private View.OnClickListener onClickListener;

    /**
     * Constructor of the ColorsGridOverlayAdapter
     * @param context the context
     * @param layoutResourceId Layout resource for a item
     * @param data the list of colors to fill the overlay grid with
     */
    public ColorsGridOverlayAdapter(Context context, int layoutResourceId, ArrayList<Color> data) {
        super();

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View row = convertView;
//        ViewHolder holder;
//
//        String hexColor = data.get(position).getHexColor();
//        String colorName = data.get(position).getName();
//        if (row == null || row.getTag() == null) {
//            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//            row = inflater.inflate(layoutResourceId, parent, false);
//            holder = new ViewHolder();
//            holder.textView.setText(colorName);
//            row.setTag(holder);
//        } else {
//            holder = (ViewHolder) row.getTag();
//        }
//        holder.imageView.setBackgroundColor(android.graphics.Color.parseColor(hexColor));
//        return row;
//    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
              .inflate(layoutResourceId, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Color color = data.get(position);

        holder.textView.setText(color.getName());
        holder.itemView.setBackgroundColor(android.graphics.Color.parseColor(color.getHexColor()));

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(onClickListener);
    }

    public Color getItem(int position) {
        return data.get(position);
    }
    @Override
    public long getItemId(int position) {
        return data.get(position)
              .getId();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    /**
     *
     * @param color the color to check
     * @return if the color has brightness over 60%
     */
    public boolean isColorLight(int color) {
        boolean isColorLight = false;
        float[] pixelHSV = new float[3];
         android.graphics.Color.colorToHSV(color, pixelHSV);

        float brightness = pixelHSV[2];

        if (brightness > 0.6) {
            isColorLight = true;
        }
        return isColorLight;
    }

}
