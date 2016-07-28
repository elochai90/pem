package com.gruppe1.pem.challengeme.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.IconsGridOverlayAdapter;

import java.util.ArrayList;

public class IconEditText extends TextInputEditText {

   ArrayList<String> icons;
   CharSequence mHint;
   int selectedItemPosition = 0;
   private Activity activity;
   private IconsGridOverlayAdapter gridAdapter;
   private AlertDialog iconsAlertDialog;

   OnItemSelectedListener onItemSelectedListener;

   public IconEditText(Context context) {
      super(context);

      mHint = getHint();
   }

   public IconEditText(Context context, AttributeSet attrs) {
      super(context, attrs);

      mHint = getHint();
   }

   public IconEditText(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);

      mHint = getHint();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      setFocusable(false);
      setClickable(true);
   }

   public void setItems(Activity activity, ArrayList<String> items) {
      this.activity = activity;
      this.icons = items;

      configureOnClickListener();
   }

   private void configureOnClickListener() {
      //      ArrayList<String> iconsArray = new ArrayList<>(
      //            Arrays.asList(getResources().getStringArray(R.array.category_icons_array)));
      gridAdapter =
            new IconsGridOverlayAdapter(activity, R.layout.grid_item_overlay_category, icons);

      setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View view) {

            iconsAlertDialog = setupIconOverlay(new AdapterView.OnItemClickListener() {
                                                   @Override
                                                   public void onItemClick(AdapterView<?> parent,
                                                         View view, int position, long id) {
                                                      setSelection(position);
                                                      iconsAlertDialog.dismiss();
                                                   }
                                                }
                  //            new View.OnClickListener() {
                  //               @Override
                  //               public void onClick(View view) {
                  //                  int selectedIndex = (Integer) view.getTag();
                  //                  setSelection(selectedIndex);
                  //                  categoriesAlertDialog.dismiss();
                  //               }
                  //            }
            );
            iconsAlertDialog.show();
         }
      });
   }

   public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
      this.onItemSelectedListener = onItemSelectedListener;
   }

   public String getItem(int index) {
      if (index < 0) {
         return null;
      }
      return icons.get(index);
   }

   public String getSelectedItem() {
      if (selectedItemPosition < 0) {
         return null;
      }
      return icons.get(selectedItemPosition);
   }

   public interface OnItemSelectedListener {
      void onItemSelectedListener(String item, int selectedIndex);
   }

   @Override
   public void setSelection(int index) {
      selectedItemPosition = index;
      SharedPreferences prefs =
            activity.getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
      String language = prefs.getString(Constants.KEY_LANGUAGE, "en");
      String icon_title = "cat_" + icons.get(index) + "_" + language;
      int icon_title_id =
            getResources().getIdentifier(icon_title, "string", "com.gruppe1.pem.challengeme");
      setText(getResources().getString(icon_title_id));

      if (onItemSelectedListener != null) {
         onItemSelectedListener.onItemSelectedListener(icons.get(index), index);
      }
   }

   private AlertDialog setupIconOverlay(AdapterView.OnItemClickListener onItemClickListener) {

      final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      LayoutInflater inflater = activity.getLayoutInflater();

      View dialogView = inflater.inflate(R.layout.dialog_grid_view, null);
      TextView headline = (TextView) dialogView.findViewById(R.id.dialog_headline);
      headline.setText(R.string.new_category_overlay_title);
      GridView gridView = (GridView) dialogView.findViewById(R.id.gridView);

      builder.setView(dialogView);
      final AlertDialog alert = builder.create();
      gridView.setAdapter(gridAdapter);
      gridView.setOnItemClickListener(onItemClickListener);
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
