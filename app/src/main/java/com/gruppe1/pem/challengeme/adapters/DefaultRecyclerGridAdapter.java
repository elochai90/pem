package com.gruppe1.pem.challengeme.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Array adapter to fill a default list view
 */
public class DefaultRecyclerGridAdapter
      extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
   private Context context;
   private int layoutResourceId;
   private int layoutResourceCategoryId;
   private ArrayList<ListItemIconName> data = new ArrayList<>();
   private boolean isCategory;
   private PicassoSingleton picassoSingleton;
   private DataBaseHelper dbHelper;

   private View.OnClickListener onItemClickListener;
   private View.OnClickListener onIcMoreClickListener;

   private final int TYPE_CATEGORY = 0;
   private final int TYPE_ITEM = 1;

   public DefaultRecyclerGridAdapter(Context context, int layoutResourceId, int layoutResourceCategoryId,
         ArrayList<ListItemIconName> data, boolean isCategory) {
      super();
      this.layoutResourceId = layoutResourceId;
      this.layoutResourceCategoryId = layoutResourceCategoryId;
      this.context = context;
      this.data = data;
      this.isCategory = isCategory;
      this.picassoSingleton = PicassoSingleton.getInstance(context);
      this.dbHelper = new DataBaseHelper(context);
      this.dbHelper.init();
   }

   public void setOnItemClickListener(View.OnClickListener onClickListener) {
      this.onItemClickListener = onClickListener;
   }

   public void setOnIcMoreClickListener(View.OnClickListener onClickListener) {
      this.onIcMoreClickListener = onClickListener;
   }

   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      RecyclerView.ViewHolder vh;
      if(viewType == TYPE_CATEGORY) {
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
   public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
      final ListItemIconName item = data.get(position);
      int viewType = viewHolder.getItemViewType();

      if(viewType == TYPE_CATEGORY) {
         ViewHolderCategory holder = (ViewHolderCategory) viewHolder;
         holder.imageTitle.setText(item.getName());
         Category category = new Category(this.context, item.getElementId(), this.dbHelper);

         int colorHex = ColorHelper.calculateMinDarkColor(category.getColor());
         holder.image.setImageDrawable(ColorHelper.filterIconColor(context, category.getIcon(), colorHex));
         holder.textContainer.setBackgroundColor(colorHex);
         holder.itemView.setBackgroundColor(colorHex);
         holder.rightTextView.setText(
               String.valueOf(Item.getItemsCountByCategoryId(context, item.getElementId(), false)));
         holder.moreButton.setTag(position);
         holder.itemView.setOnClickListener(onItemClickListener);
         holder.moreButton.setOnClickListener(onIcMoreClickListener);
      } else if(viewType == TYPE_ITEM) {
         ViewHolder holder = (ViewHolder) viewHolder;
         holder.imageTitle.setText(item.getName());
         dbHelper.setTable(Constants.ITEMS_DB_TABLE);
         Item listItem = new Item(context, item.getElementId(), dbHelper);
         dbHelper.setTable(Constants.COLORS_DB_TABLE);
         Color attributeColor = new Color(context, listItem.getPrimaryColorId(), dbHelper);
         Category parentCategory = new Category(context, listItem.getCategoryId(), dbHelper);
         ArrayList<Attribute> allAttributes =
               Attribute.getAttributesByItemId(context, item.getElementId());
         if(item.getItemFile() == null) {
            int colorHex = ColorHelper.calculateMinDarkColor(parentCategory.getColor());
            holder.image.setImageDrawable(ColorHelper.filterIconColor(context, parentCategory.getIcon(), colorHex));
         } else {
            int colorHex = ColorHelper.calculateMinDarkColor(parentCategory.getColor());
            Drawable icon = ColorHelper.filterIconColor(context, parentCategory.getIcon(), colorHex);
            picassoSingleton.setImageFit(item.getItemFile(), holder.image, icon, icon);
         }
         String secondLineText = "";
         int attributesToShowCount = 0;
         int colorHex = -1;
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
            if(allAttributes.get(i).getAttributeType().getValueType() == 3) {
               colorHex = Integer.parseInt(allAttributes.get(i).getValue().toString());
            }
         }
         holder.secondLineTextView.setText(secondLineText);
         holder.rightTextView.setVisibility(View.GONE);

         if(listItem.getRating() > 0.0f) {
            holder.listItemRatingBar.setRating(listItem.getRating());
         } else {
            holder.listItemRatingBar.setVisibility(View.GONE);
         }
         if(colorHex == -1) {
            Color color = new Color(context, listItem.getPrimaryColorId(), dbHelper);
            colorHex = android.graphics.Color.parseColor(color.getHexColor());
         }
         colorHex = ColorHelper.calculateMinDarkColor(colorHex);
         holder.textContainer.setBackgroundColor(colorHex);
         holder.moreButton.setTag(position);
         holder.itemView.setOnClickListener(onItemClickListener);
         holder.moreButton.setOnClickListener(onIcMoreClickListener);

      }
   }

   @Override
   public int getItemViewType(int position) {
      if(data.get(position).isCategoryElement() || isCategory) {
         return TYPE_CATEGORY;
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
      CardView itemView;
      @Bind(R.id.textView)
      TextView imageTitle;
      @Bind(R.id.rightTextView)
      TextView rightTextView;
      @Bind(R.id.secondLineTextView)
      TextView secondLineTextView;
      @Bind(R.id.second_line)
      LinearLayout secondLine;
      @Bind(R.id.imageView)
      ImageView image;
      @Bind(R.id.ic_more)
      ImageView moreButton;
      @Bind(R.id.listItemRatingBar)
      RatingBar listItemRatingBar;
      @Bind(R.id.text_container)
      RelativeLayout textContainer;

      public ViewHolder(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
         this.itemView = (CardView) itemView;
      }
   }

   public static class ViewHolderCategory extends RecyclerView.ViewHolder {
      CardView itemView;
      @Bind(R.id.textView)
      TextView imageTitle;
      @Bind(R.id.rightTextView)
      TextView rightTextView;
      @Bind(R.id.imageView)
      ImageView image;
      @Bind(R.id.ic_more)
      ImageView moreButton;
      @Bind(R.id.text_container)
      RelativeLayout textContainer;

      public ViewHolderCategory(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
         this.itemView = (CardView) itemView;
      }
   }
}
