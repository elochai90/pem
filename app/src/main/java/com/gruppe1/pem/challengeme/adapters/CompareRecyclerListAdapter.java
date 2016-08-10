package com.gruppe1.pem.challengeme.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Compare;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.CategoryDataSource;
import com.gruppe1.pem.challengeme.helpers.ColorHelper;
import com.gruppe1.pem.challengeme.helpers.ItemDataSource;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Array adapter to fill a default list view
 */
public class CompareRecyclerListAdapter
      extends RecyclerView.Adapter<CompareRecyclerListAdapter.ViewHolder> {
   private Context context;
   private int layoutResourceId;
   private ArrayList<Compare> data = new ArrayList<>();
   private PicassoSingleton picassoSingleton;

   private View.OnClickListener onItemClickListener;
   private View.OnClickListener onIcMoreClickListener;

   /**
    * Constructor of the CompareRecyclerListAdapter
    *
    * @param context          the context
    * @param layoutResourceId Layout resource for a compare
    * @param data             the list of Compare to fill the list with
    */
   public CompareRecyclerListAdapter(Context context, int layoutResourceId,
         ArrayList<Compare> data) {
      super();
      this.layoutResourceId = layoutResourceId;
      this.context = context;
      this.data = data;
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
      CategoryDataSource categoryDataSource = new CategoryDataSource(context);
      ItemDataSource itemDataSource = new ItemDataSource(context);

      Compare compare = data.get(position);

      holder.compareName.setText(compare.getName());

      ArrayList<Integer> itemIds = compare.getItemIds();
      Item item1 = itemDataSource.getItem(itemIds.get(0));
      Item item2 = itemDataSource.getItem(itemIds.get(1));
      Category category1 = categoryDataSource.getCategory(item1.getCategoryId());
      Category category2 = categoryDataSource.getCategory(item2.getCategoryId());

      holder.itemName1.setText(item1.getName());
      holder.itemName2.setText(item2.getName());
      SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
      Date date = null;
      try {
         date = iso8601Format.parse(compare.getTimestamp());
      } catch (ParseException e) {
         e.printStackTrace();
      }
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH);

      holder.rightTextView.setText(sdf.format(date));
      int colorHex1 = ColorHelper.calculateMinDarkColor(category1.getColor());
      int colorHex2 = ColorHelper.calculateMinDarkColor(category2.getColor());
      Drawable icon1 = ColorHelper.filterIconColor(context, category1.getIcon(), colorHex1);
      Drawable icon2 = ColorHelper.filterIconColor(context, category2.getIcon(), colorHex2);
      picassoSingleton.setImageFit(item1.getImageFile(), holder.imageItem1, icon1, icon1);
      picassoSingleton.setImageFit(item2.getImageFile(), holder.imageItem2, icon2, icon2);

      holder.itemView.setOnClickListener(onItemClickListener);
      holder.moreButton.setTag(position);
      holder.moreButton.setOnClickListener(onIcMoreClickListener);
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

      @Bind (R.id.item1Name)
      TextView itemName1;
      @Bind (R.id.item2Name)
      TextView itemName2;
      @Bind (R.id.compareName)
      TextView compareName;
      @Bind (R.id.rightTextView)
      TextView rightTextView;
      @Bind (R.id.imageItem1)
      ImageView imageItem1;
      @Bind (R.id.imageItem2)
      ImageView imageItem2;
      @Bind (R.id.ic_more)
      ImageView moreButton;

      RelativeLayout itemView;

      public ViewHolder(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
         this.itemView = (RelativeLayout) itemView;
      }
   }
}
