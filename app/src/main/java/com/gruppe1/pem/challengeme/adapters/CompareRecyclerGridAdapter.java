package com.gruppe1.pem.challengeme.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Attribute;
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.ColorHelper;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Array adapter to fill a default list view
 */
public class CompareRecyclerGridAdapter
      extends RecyclerView.Adapter<CompareRecyclerGridAdapter.ViewHolder> {
   private Context context;
   private int layoutResourceId;
   private List<Compare> data = new ArrayList<>();
   private DataBaseHelper dbHelper;
   private PicassoSingleton picassoSingleton;

   private View.OnClickListener onItemClickListener;
   private View.OnClickListener onIcMoreClickListener;

   public CompareRecyclerGridAdapter(Context context, int layoutResourceId,
         ArrayList<Compare> data) {
      super();
      this.layoutResourceId = layoutResourceId;
      this.context = context;
      this.data = data;
      this.dbHelper = new DataBaseHelper(context);
      this.dbHelper.init();
      this.picassoSingleton = PicassoSingleton.getInstance(context);
   }

   public void setOnItemClickListener(View.OnClickListener onClickListener) {
      this.onItemClickListener = onClickListener;
   }

   public void setOnIcMoreClickListener(View.OnClickListener onClickListener) {
      this.onIcMoreClickListener = onClickListener;
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
      Compare item = data.get(position);

      holder.compareName.setText(item.getName());

      ArrayList<Integer> itemIds = item.getItemIds();
      Item item1 = new Item(this.context, itemIds.get(0), this.dbHelper);
      Item item2 = new Item(this.context, itemIds.get(1), this.dbHelper);
      Category category1 = new Category(this.context, item1.getCategoryId(), this.dbHelper);
      Category category2 = new Category(this.context, item2.getCategoryId(), this.dbHelper);
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH);
      String currentDateandTime = sdf.format(new Date(Long.parseLong(item.getTimestamp())));

      holder.rightTextView.setText(currentDateandTime);

      int colorHex1 = ColorHelper.calculateMinDarkColor(category1.getColor());
      int colorHex2 = ColorHelper.calculateMinDarkColor(category2.getColor());
      Drawable icon1 = ColorHelper.filterIconColor(context, category1.getIcon(), colorHex1);
      Drawable icon2 = ColorHelper.filterIconColor(context, category2.getIcon(), colorHex2);
      picassoSingleton.setImageFit(item1.getImageFile(), holder.imageItem1, icon1, icon1);
      picassoSingleton.setImageFit(item2.getImageFile(), holder.imageItem2, icon2, icon2);
      holder.itemView.setOnClickListener(onItemClickListener);
      holder.moreButton.setTag(position);
      holder.moreButton.setOnClickListener(onIcMoreClickListener);

      //        int iconId = context.getResources().getIdentifier(category1.getIcon(),
       // "drawable", "com.gruppe1.pem.challengeme");

      Attribute attribute1 = Attribute.getAttributeExactColorByItemId(context, item1.getId());
      int color1 = item1.getPrimaryColorId();
      if (attribute1.getValue() != null) {
         color1 = Integer.parseInt(attribute1.getValue()
               .toString());
      }
      Attribute attribute2 = Attribute.getAttributeExactColorByItemId(context, item2.getId());
      int color2 = item2.getPrimaryColorId();
      if (attribute1.getValue() != null) {
         color2 = Integer.parseInt(attribute2.getValue()
               .toString());
      }
      GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
            new int[] { color1, color2 });
      holder.itemView.setBackground(gd);
   }

   @Override
   public long getItemId(int p_position) {
      return this.data.get(p_position)
            .getId();
   }

   @Override
   public int getItemCount() {
      return data.size();
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {
      CardView itemView;
      @Bind(R.id.compareName)
      TextView compareName;
      @Bind(R.id.imageItem1)
      ImageView imageItem1;
      @Bind(R.id.imageItem2)
      ImageView imageItem2;
      @Bind(R.id.rightTextView)
      TextView rightTextView;
      @Bind(R.id.ic_more)
      ImageView moreButton;

      public ViewHolder(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
         this.itemView = (CardView) itemView;
      }
   }
}
