package com.gruppe1.pem.challengeme.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
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
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;

import java.util.ArrayList;

/**
 * Array adapter to fill a default list view
 */
public class DefaultRecyclerGridAdapter
      extends RecyclerView.Adapter<DefaultRecyclerGridAdapter.ViewHolder> {
   private Context context;
   private int layoutResourceId;
   private ArrayList<ListItemIconName> data = new ArrayList<>();
   private boolean isCategory;
   private PicassoSingleton picassoSingleton;
   private DataBaseHelper dbHelper;

   private View.OnClickListener onItemClickListener;
   private View.OnClickListener onIcMoreClickListener;

   public DefaultRecyclerGridAdapter(Context context, int layoutResourceId,
         ArrayList<ListItemIconName> data, boolean isCategory) {
      super();
      this.layoutResourceId = layoutResourceId;
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
   public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext())
            .inflate(layoutResourceId, parent, false);
      ViewHolder vh = new ViewHolder(v);
      return vh;
   }

   @Override
   public void onBindViewHolder(ViewHolder holder, int position) {
      final ListItemIconName item = data.get(position);

      holder.imageTitle.setText(item.getName());

      if (isCategory || item.getItemFile() == null || item.isCategoryElement()) {
         Category category = new Category(this.context, item.getElementId(), this.dbHelper);
         holder.textContainer.setBackgroundColor(
               Integer.parseInt(category.getColor(), 16) + 0xFF000000);
         holder.image.setImageResource(item.getIcon());
      } else {
         picassoSingleton.setImage(item.getItemFile(), Constants.LIST_VIEW_IMAGE_WIDTH,
               Constants.LIST_VIEW_IMAGE_HEIGHT, holder.image);
      }

      if (isCategory || item.isCategoryElement()) {
         holder.rightTextView.setText(
               Item.getItemsCountByCategoryId(context, item.getElementId(), false) + "");
         holder.listItemRatingBar.setVisibility(View.GONE);
      } else {
         dbHelper.setTable(Constants.ITEMS_DB_TABLE);
         Item listItem = new Item(context, item.getElementId(), dbHelper);
         dbHelper.setTable(Constants.COLORS_DB_TABLE);
         Color attributeColor = new Color(context, listItem.getPrimaryColorId(), dbHelper);
         ArrayList<Attribute> allAttributes =
               Attribute.getAttributesByItemId(context, item.getElementId());
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
         holder.rightTextView.setText(secondLineText);

         holder.listItemRatingBar.setRating(listItem.getRating());
         if(colorHex == -1) {
            Color color = new Color(context, listItem.getPrimaryColorId(), dbHelper);
            colorHex = android.graphics.Color.parseColor(color.getHexColor());
         }
         holder.textContainer.setBackgroundColor(colorHex);
      }

      holder.moreButton.setTag(position);
      holder.itemView.setOnClickListener(onItemClickListener);
      holder.moreButton.setOnClickListener(onIcMoreClickListener);
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
      TextView imageTitle;
      TextView rightTextView;
      ImageView image;
      ImageView moreButton;
      RatingBar listItemRatingBar;
      RelativeLayout textContainer;

      public ViewHolder(View itemView) {
         super(itemView);
         this.itemView = (CardView) itemView;
         imageTitle = (TextView) itemView.findViewById(R.id.textView);
         rightTextView = (TextView) itemView.findViewById(R.id.rightTextView);
         image = (ImageView) itemView.findViewById(R.id.imageView);
         moreButton = (ImageView) itemView.findViewById(R.id.ic_more);
         listItemRatingBar = (RatingBar) itemView.findViewById(R.id.listItemRatingBar);
         textContainer = (RelativeLayout) itemView.findViewById(R.id.text_container);
      }
   }
}
