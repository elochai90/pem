package com.gruppe1.pem.challengeme.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;

/**
 * the Category Adapter for a Category Select Overlay
 */
public class CategoriesGridOverlayAdapter
      extends RecyclerView.Adapter<CategoriesGridOverlayAdapter.ViewHolder> {
   private Context context;
   private int layoutResourceId;
   private ArrayList<ListItemIconName> data = new ArrayList<>();
   private DataBaseHelper dbHelper;
   private View.OnClickListener onClickListener;

   /**
    * Constructor of the CategoriesGridOverlayAdapter
    *
    * @param context          the context
    * @param layoutResourceId Layout resource for a row
    * @param data             the data to fill the overlay grid with
    */
   public CategoriesGridOverlayAdapter(Context context, int layoutResourceId,
         ArrayList<ListItemIconName> data) {
      super();
      this.layoutResourceId = layoutResourceId;
      this.context = context;
      this.data = data;
      this.dbHelper = new DataBaseHelper(context);
      this.dbHelper.init();
   }

   public void setOnItemClickListener(View.OnClickListener onClickListener) {
      this.onClickListener = onClickListener;
   }

   @Override
   public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext())
            .inflate(layoutResourceId, parent, false);
      ViewHolder vh = new ViewHolder(v);
      return vh;
   }

   @Override
   public void onBindViewHolder(ViewHolder holder, int position) {
      Category category = new Category(this.context, data.get(position)
            .getElementId(), this.dbHelper);
      int icon = this.context.getResources()
            .getIdentifier(category.getIcon(), "drawable", "com.gruppe1.pem.challengeme");

      String categoryName = data.get(position)
            .getName();
      holder.textView.setText(categoryName);
      int colorHex = Integer.parseInt(category.getColor(), 16) + 0xFF000000;
      float[] hsv = new float[3];
      android.graphics.Color.colorToHSV(colorHex, hsv);
      if (hsv[2] > 0.75f) {
         hsv[2] -= 0.25f;
      }
      colorHex = android.graphics.Color.HSVToColor(hsv);
      holder.textView.setBackgroundColor(colorHex);
      holder.imageView.setImageResource(icon);
      holder.itemView.setTag(position);
      holder.itemView.setOnClickListener(onClickListener);
   }

   @Override
   public int getItemCount() {
      return data.size();
   }

   @Override
   public long getItemId(int position) {
      return data.get(position)
            .getElementId();
   }

   public ListItemIconName getItem(int position) {
      return data.get(position);
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
}
