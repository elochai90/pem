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

import com.gruppe1.pem.challengeme.Color;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.ColorsGridOverlayAdapter;

import java.util.ArrayList;

public class ColorEditText extends EditText {

   ArrayList<Color> colors;
   CharSequence mHint;
   int selectedItemPosition = 0;
   private Activity activity;
   private ColorsGridOverlayAdapter gridColorsAdapter;
   private DataBaseHelper dbHelper;
   private AlertDialog colorsAlertDialog;

   OnItemSelectedListener onItemSelectedListener;

   public ColorEditText(Context context) {
      super(context);

      mHint = getHint();
   }

   public ColorEditText(Context context, AttributeSet attrs) {
      super(context, attrs);

      mHint = getHint();
   }

   public ColorEditText(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);

      mHint = getHint();
   }

   @TargetApi (Build.VERSION_CODES.LOLLIPOP)
   public ColorEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);

      mHint = getHint();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      setFocusable(false);
      setClickable(true);
   }

   public void setItems(Activity activity, DataBaseHelper dbHelper, ArrayList<Color> items) {
      this.activity = activity;
      this.dbHelper = dbHelper;
      this.colors = items;

      configureOnClickListener();
   }

   private void configureOnClickListener() {
      gridColorsAdapter =
            new ColorsGridOverlayAdapter(activity, R.layout.grid_item_overlay_color, colors);

      setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View view) {

            colorsAlertDialog = setupCategoryOverlay(new OnClickListener() {
               @Override
               public void onClick(View view) {
                  int selectedIndex = (Integer) view.getTag();

                  setSelection(selectedIndex);
                  colorsAlertDialog.dismiss();
               }
            });
            colorsAlertDialog.show();
         }
      });
   }
   public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
      this.onItemSelectedListener = onItemSelectedListener;
   }

   public Color getSelectedItem() {
      if (selectedItemPosition < 0) {
         return null;
      }
      return colors.get(selectedItemPosition);
   }

   public interface OnItemSelectedListener {
      void onItemSelectedListener(Color item, int selectedIndex);
   }

   @Override
   public void setSelection(int index) {
      selectedItemPosition = index;
      setText(colors.get(index)
            .getName());

      if (onItemSelectedListener != null) {
         onItemSelectedListener.onItemSelectedListener(colors.get(index), index);
      }
   }

   private AlertDialog setupCategoryOverlay(OnClickListener onItemClickListener) {
      final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      LayoutInflater inflater = activity.getLayoutInflater();

      final View dialogView = inflater.inflate(R.layout.dialog_recycler_view, null);
      TextView headline = (TextView) dialogView.findViewById(R.id.dialog_headline);
      headline.setText(getResources().getString(R.string.item_select_color_overlay_title));
      RecyclerView gridView = (RecyclerView) dialogView.findViewById(R.id.gridView);

      StaggeredGridLayoutManager gridLayoutManager =
            new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
      gridView.setLayoutManager(gridLayoutManager);
      gridView.setHasFixedSize(true);
      int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
      gridView.addItemDecoration(new GridSpacingItemDecoration(3, spacingInPixels, false, 0));

      builder.setView(dialogView);
      final AlertDialog alert = builder.create();
      gridColorsAdapter.setOnItemClickListener(onItemClickListener);
      gridView.setAdapter(gridColorsAdapter);
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
