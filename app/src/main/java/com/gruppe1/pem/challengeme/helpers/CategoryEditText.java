package com.gruppe1.pem.challengeme.helpers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.CategoriesGridOverlayAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryEditText extends EditText {

   List<Category> categories;
   CharSequence mHint;
   int selectedItemPosition = 0;
   private Activity activity;
   private CategoriesGridOverlayAdapter gridCateoriesAdapter;
   private DataBaseHelper dbHelper;
   private AlertDialog categoriesAlertDialog;

   OnItemSelectedListener onItemSelectedListener;

   public CategoryEditText(Context context) {
      super(context);

      mHint = getHint();
   }

   public CategoryEditText(Context context, AttributeSet attrs) {
      super(context, attrs);

      mHint = getHint();
   }

   public CategoryEditText(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);

      mHint = getHint();
   }

   @TargetApi (Build.VERSION_CODES.LOLLIPOP)
   public CategoryEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);

      mHint = getHint();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      setFocusable(false);
      setClickable(true);
   }

   public void setItems(Activity activity, DataBaseHelper dbHelper, ArrayList<Category> items) {
      this.activity = activity;
      this.dbHelper = dbHelper;
      this.categories = items;

      configureOnClickListener();
   }

   private void configureOnClickListener() {
      ArrayList<ListItemIconName> catArray = new ArrayList<>();
      for (Category tmpCat : categories) {
         int iconId = getResources().getIdentifier(tmpCat.getIcon(), "drawable",
               "com.gruppe1.pem.challengeme");
         catArray.add(
               new ListItemIconName("category", tmpCat.getId(), iconId, tmpCat.getName(), null));
      }
      gridCateoriesAdapter =
            new CategoriesGridOverlayAdapter(activity, R.layout.grid_item_overlay_category,
                  catArray);

      setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View view) {

            categoriesAlertDialog = setupCategoryOverlay(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                  int selectedIndex = (Integer) view.getTag();
                  setSelection(selectedIndex);
                  categoriesAlertDialog.dismiss();
               }
            });
            categoriesAlertDialog.show();
         }
      });
   }

   public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
      this.onItemSelectedListener = onItemSelectedListener;
   }

   public Category getSelectedItem() {
      if (selectedItemPosition < 0) {
         return null;
      }
      return categories.get(selectedItemPosition);
   }

   public interface OnItemSelectedListener {
      void onItemSelectedListener(com.gruppe1.pem.challengeme.Category item, int selectedIndex);
   }

   @Override
   public void setSelection(int index) {
      selectedItemPosition = index;
      setText(categories.get(index)
            .getName());

      if (onItemSelectedListener != null) {
         onItemSelectedListener.onItemSelectedListener(categories.get(index), index);
      }
   }

   private AlertDialog setupCategoryOverlay(View.OnClickListener onItemClickListener) {
      final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
      LayoutInflater inflater = activity.getLayoutInflater();

      final View dialogView = inflater.inflate(R.layout.dialog_recycler_view, null);
      TextView headline = (TextView) dialogView.findViewById(R.id.dialog_headline);
      headline.setText(getResources().getString(R.string.item_select_category_overlay_title));
      RecyclerView gridView = (RecyclerView) dialogView.findViewById(R.id.gridView);

      StaggeredGridLayoutManager gridLayoutManager =
            new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
      gridView.setLayoutManager(gridLayoutManager);
      gridView.setHasFixedSize(true);
      int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
      gridView.addItemDecoration(new GridSpacingItemDecoration(3, spacingInPixels, false, 0));
      builder.setView(dialogView);
      final AlertDialog alert = builder.create();
      gridCateoriesAdapter.setOnItemClickListener(onItemClickListener);
      gridView.setAdapter(gridCateoriesAdapter);
      alert.setButton(DialogInterface.BUTTON_NEGATIVE,
            getResources().getString(android.R.string.cancel),
            new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                  alert.dismiss();
               }
            });
      return alert;
   }
}
