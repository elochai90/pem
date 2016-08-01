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
import com.gruppe1.pem.challengeme.SquareImageView;
import com.gruppe1.pem.challengeme.helpers.ColorHelper;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * the Category Adapter for a Category Select Overlay
 */
public class CategoriesGridOverlayAdapter
      extends RecyclerView.Adapter<CategoriesGridOverlayAdapter.ViewHolder> {
   private Context context;
   private ArrayList<ListItemIconName> data = new ArrayList<>();
   private DataBaseHelper dbHelper;
   private View.OnClickListener onClickListener;

   /**
    * Constructor of the CategoriesGridOverlayAdapter
    *
    * @param context          the context
    * @param data             the data to fill the overlay grid with
    */
   public CategoriesGridOverlayAdapter(Context context,
         ArrayList<ListItemIconName> data) {
      super();
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
            .inflate(R.layout.grid_item_overlay_category, parent, false);
      ViewHolder vh = new ViewHolder(v);
      return vh;
   }

   @Override
   public void onBindViewHolder(ViewHolder holder, int position) {
      Category category = new Category(this.context, data.get(position)
            .getElementId(), this.dbHelper);
      //      int icon = this.context.getResources()
      //            .getIdentifier(category.getIcon(), "drawable", "com.gruppe1.pem.challengeme");

      //      int colorHex = ColorHelper.calculateMinDarkColor(category.getColor());
      //      holder.image.setImageDrawable(ColorHelper.filterIconColor(context, category.getIcon
      // (), colorHex));
      String categoryName = category.getName();
      holder.textView.setText(categoryName);
      int colorHex = ColorHelper.calculateMinDarkColor(category.getColor());
      holder.textView.setBackgroundColor(colorHex);
      holder.itemView.setBackgroundColor(colorHex);
      holder.imageView.setImageDrawable(
            ColorHelper.filterIconColor(context, category.getIcon(), colorHex));
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
      @Bind (R.id.textView)
      TextView textView;
      @Bind (R.id.imageView)
      SquareImageView imageView;

      public ViewHolder(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
      }
   }
}
