package com.gruppe1.pem.challengeme.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Attribute;
import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Color;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.AttributeDataSource;
import com.gruppe1.pem.challengeme.helpers.CategoryDataSource;
import com.gruppe1.pem.challengeme.helpers.ColorDataSource;
import com.gruppe1.pem.challengeme.helpers.ColorHelper;
import com.gruppe1.pem.challengeme.helpers.ItemDataSource;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Array adapter to fill a default list view
 */
public class DefaultRecyclerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
   private Context context;
   private int layoutResourceId;
   private int layoutResourceCategoryId;
   private ArrayList<ListItemIconName> data = new ArrayList<>();
   private boolean wishlist;
   private boolean isCategory;
   private PicassoSingleton picassoSingleton;

   private View.OnClickListener onItemClickListener;
   private OnIcMoreClickListener onIcMoreClickListener;

   private final int TYPE_CATEGORY = 0;
   private final int TYPE_ITEM = 1;
   private final int TYPE_WISHLIST = 2;

   public DefaultRecyclerListAdapter(Context context, int layoutResourceId,
         int layoutResourceCategoryId, ArrayList<ListItemIconName> data, boolean isCategory,
         boolean wishlist) {
      super();

      this.layoutResourceId = layoutResourceId;
      this.layoutResourceCategoryId = layoutResourceCategoryId;
      this.context = context;
      this.data = data;
      this.wishlist = wishlist;
      this.isCategory = isCategory;
      this.picassoSingleton = PicassoSingleton.getInstance(context);
   }

   public interface OnIcMoreClickListener {
      void onClick(View view, ListItemIconName item);
   }

   public void setOnItemClickListener(View.OnClickListener onClickListener) {
      this.onItemClickListener = onClickListener;
   }

   public void setOnIcMoreClickListener(OnIcMoreClickListener onClickListener) {
      this.onIcMoreClickListener = onClickListener;
   }

   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      RecyclerView.ViewHolder vh;
      if (viewType == TYPE_CATEGORY) {
         View v = LayoutInflater.from(parent.getContext())
               .inflate(layoutResourceCategoryId, parent, false);
         vh = new ViewHolderCategory(v);
      } else {
         View v = LayoutInflater.from(parent.getContext())
               .inflate(layoutResourceId, parent, false);
         vh = new ViewHolder(v);
      }
      return vh;
   }

   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      CategoryDataSource categoryDataSource = new CategoryDataSource(context);
      ItemDataSource itemDataSource = new ItemDataSource(context);
      ColorDataSource colorDataSource = new ColorDataSource(context);
      AttributeDataSource attributeDataSource = new AttributeDataSource(context);

      int viewType = holder.getItemViewType();
      final ListItemIconName item = data.get(position);
      if (viewType == TYPE_CATEGORY) {
         Category category = categoryDataSource.getCategory(item.getElementId());
         ViewHolderCategory viewHolder = (ViewHolderCategory) holder;
         int colorHex = ColorHelper.calculateMinDarkColor(category.getColor());
         ((ViewHolderCategory) holder).image.setImageDrawable(
               ColorHelper.filterIconColor(context, category.getIcon(), colorHex));
         viewHolder.firstLine.setText(item.getName());
         viewHolder.rightTextView.setText(String.valueOf(
               itemDataSource.getItemsCountByCategoryId(item.getElementId(), false)));
         viewHolder.itemView.setOnClickListener(onItemClickListener);
         viewHolder.moreButton.setTag(position);
         viewHolder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onIcMoreClickListener.onClick(view, item);
            }
         });
      } else if (viewType == TYPE_ITEM || viewType == TYPE_WISHLIST) {
         ViewHolder viewHolder = (ViewHolder) holder;
         Item listItem = itemDataSource.getItem(item.getElementId());
         Color attributeColor = colorDataSource.getColor(listItem.getPrimaryColorId());
         Category parentCategory = categoryDataSource.getCategory(listItem.getCategoryId());
         List<Attribute> allAttributes =
               attributeDataSource.getAttributesByItemId(item.getElementId());
         if (item.getItemFile() == null || item.getItemFile()
               .isEmpty()) {
            int exactColor = Integer.parseInt(
                  attributeDataSource.getAttributeExactColorByItemId(listItem.getId())
                        .getValue()
                        .toString());
            int colorHex = ColorHelper.calculateMinDarkColor(exactColor);
            viewHolder.image.setImageDrawable(
                  ColorHelper.filterIconColor(context, parentCategory.getIcon(), colorHex));
         } else {
            int padding = (int) context.getResources()
                  .getDimension(R.dimen.margin_small);
            viewHolder.image.setPadding(padding, padding, padding, padding);
            int colorHex = ColorHelper.calculateMinDarkColor(parentCategory.getColor());
            Drawable icon =
                  ColorHelper.filterIconColor(context, parentCategory.getIcon(), colorHex);
            picassoSingleton.setImageFit(item.getItemFile(), viewHolder.image, icon, icon);
         }
         viewHolder.firstLine.setText(item.getName());
         String secondLineText = "";
         int attributesToShowCount = 0;
         if (attributeColor != null || !attributeColor.getName(context)
               .equals("")) {
            secondLineText += attributeColor.getName(context) + "   ";
            attributesToShowCount++;
         }
         for (int i = 0; i < allAttributes.size(); i++) {
            // show only three, not empty attributes of type String
            if (attributesToShowCount < 3 && !allAttributes.get(i)
                  .getValue()
                  .toString()
                  .equals("") && allAttributes.get(i)
                  .getAttributeType()
                  .getValueType() == 1) {
               secondLineText += allAttributes.get(i)
                     .getValue()
                     .toString() + "   ";
               attributesToShowCount++;
            }
         }
         viewHolder.secondLine.setText(secondLineText);
         viewHolder.listItemRatingBar.setRating(listItem.getRating());
         viewHolder.itemView.setOnClickListener(onItemClickListener);
         viewHolder.moreButton.setTag(position);
         viewHolder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onIcMoreClickListener.onClick(view, item);
            }
         });

         if (viewType == TYPE_WISHLIST) {
            viewHolder.rightTextView.setVisibility(View.GONE);
            viewHolder.listItemRatingBar.setVisibility(View.GONE);
         }
      }
   }

   @Override
   public int getItemViewType(int position) {
      if (data.get(position)
            .isCategoryElement() || isCategory) {
         return TYPE_CATEGORY;
      } else if (data.get(position)
            .isWishlistElement() || wishlist) {
         return TYPE_WISHLIST;
      } else {
         return TYPE_ITEM;
      }
   }

   @Override
   public long getItemId(int p_position) {
      return this.data.get(p_position)
            .getElementId();
   }

   public ListItemIconName getItem(int p_position) {
      return this.data.get(p_position);
   }

   @Override
   public int getItemCount() {
      return data.size();
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {
      RelativeLayout itemView;
      @Bind (R.id.firstLine)
      TextView firstLine;
      @Bind (R.id.secondLine)
      TextView secondLine;
      @Bind (R.id.rightTextView)
      TextView rightTextView;
      @Bind (R.id.imageView)
      ImageView image;
      @Bind (R.id.listItemRatingBar)
      RatingBar listItemRatingBar;
      @Bind (R.id.ic_more)
      ImageView moreButton;

      public ViewHolder(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
         this.itemView = (RelativeLayout) itemView;
      }
   }

   public static class ViewHolderCategory extends RecyclerView.ViewHolder {
      RelativeLayout itemView;
      @Bind (R.id.firstLine)
      TextView firstLine;
      @Bind (R.id.rightTextView)
      TextView rightTextView;
      @Bind (R.id.imageView)
      ImageView image;
      @Bind (R.id.ic_more)
      ImageView moreButton;

      public ViewHolderCategory(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
         this.itemView = (RelativeLayout) itemView;
      }
   }
}
