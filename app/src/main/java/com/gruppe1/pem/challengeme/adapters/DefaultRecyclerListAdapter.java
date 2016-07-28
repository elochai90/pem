package com.gruppe1.pem.challengeme.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.gruppe1.pem.challengeme.helpers.ColorHelper;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.util.ArrayList;
import java.util.HashMap;

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
   private DataBaseHelper db_helper;
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

      db_helper = new DataBaseHelper(context);
      db_helper.init();

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
      int viewType = holder.getItemViewType();
      final ListItemIconName item = data.get(position);
      if (viewType == TYPE_CATEGORY) {
         Category category = new Category(this.context, item.getElementId(), getDb_helper());
         ViewHolderCategory viewHolder = (ViewHolderCategory) holder;
         int colorHex = ColorHelper.calculateMinDarkColor(category.getColor());
         ((ViewHolderCategory) holder).image.setImageDrawable(
               ColorHelper.filterIconColor(context, category.getIcon(), colorHex));
         viewHolder.firstLine.setText(item.getName());
         viewHolder.rightTextView.setText(
               String.valueOf(Item.getItemsCountByCategoryId(context, item.getElementId(), false)));
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
         getDb_helper().setTable(Constants.ITEMS_DB_TABLE);
         Item listItem = new Item(context, item.getElementId(), getDb_helper());
         getDb_helper().setTable(Constants.COLORS_DB_TABLE);
         Color attributeColor = new Color(context, listItem.getPrimaryColorId(), getDb_helper());
         Category parentCategory = new Category(context, listItem.getCategoryId(), getDb_helper());
         ArrayList<Attribute> allAttributes =
               Attribute.getAttributesByItemId(context, item.getElementId());
         if (item.getItemFile() == null) {
            int colorHex = ColorHelper.calculateMinDarkColor(parentCategory.getColor());
            viewHolder.image.setImageDrawable(
                  ColorHelper.filterIconColor(context, parentCategory.getIcon(), colorHex));
         } else {
            int padding = (int) context.getResources().getDimension(R.dimen.margin_small);
            viewHolder.image.setPadding(padding, padding, padding, padding);
            int colorHex = ColorHelper.calculateMinDarkColor(parentCategory.getColor());
            Drawable icon =
                  ColorHelper.filterIconColor(context, parentCategory.getIcon(), colorHex);
            picassoSingleton.setImageFit(item.getItemFile(), viewHolder.image, icon, icon);
         }
         viewHolder.firstLine.setText(item.getName());
         String secondLineText = "";
         int attributesToShowCount = 0;
         if (!attributeColor.getName()
               .equals("")) {
            secondLineText += attributeColor.getName() + "   ";
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
      @Bind(R.id.firstLine)
      TextView firstLine;
      @Bind(R.id.secondLine)
      TextView secondLine;
      @Bind(R.id.rightTextView)
      TextView rightTextView;
      @Bind(R.id.imageView)
      ImageView image;
      @Bind(R.id.listItemRatingBar)
      RatingBar listItemRatingBar;
      @Bind(R.id.ic_more)
      ImageView moreButton;

      public ViewHolder(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
         this.itemView = (RelativeLayout) itemView;
      }
   }

   public static class ViewHolderCategory extends RecyclerView.ViewHolder {
      RelativeLayout itemView;
      @Bind(R.id.firstLine)
      TextView firstLine;
      @Bind(R.id.rightTextView)
      TextView rightTextView;
      @Bind(R.id.imageView)
      ImageView image;
      @Bind(R.id.ic_more)
      ImageView moreButton;

      public ViewHolderCategory(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
         this.itemView = (RelativeLayout) itemView;
      }
   }

   /**
    * Get the db_helper instance for this class
    *
    * @return DataBaseHelper instance
    */
   private DataBaseHelper getDb_helper() {
      if (!db_helper.isOpen()) {
         System.out.println("db helper was closed");
         db_helper = new DataBaseHelper(context);
         db_helper.init();
      }
      return db_helper;
   }
}
