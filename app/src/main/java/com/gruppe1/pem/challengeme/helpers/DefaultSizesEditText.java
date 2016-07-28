package com.gruppe1.pem.challengeme.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.DefaultSize;
import com.gruppe1.pem.challengeme.R;

import java.util.ArrayList;
import java.util.Objects;

public class DefaultSizesEditText extends TextInputEditText {

   private Activity activity;
   private ArrayList<DefaultSize> mItems;
   private String[] mListableItems;
   private CharSequence mHint;
   int selectedItemPosition = 0;

   OnItemSelectedListener onItemSelectedListener;

   public DefaultSizesEditText(Context context) {
      super(context);

      mHint = getHint();
   }

   public DefaultSizesEditText(Context context, AttributeSet attrs) {
      super(context, attrs);

      mHint = getHint();
   }

   public DefaultSizesEditText(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);

      mHint = getHint();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      setFocusable(false);
      setClickable(true);
   }

   public void setItems(Activity activity, ArrayList<DefaultSize> items) {
      this.activity = activity;
      this.mItems = items;

      this.mListableItems = new String[items.size()];

      int i = 0;

      for (DefaultSize item : mItems) {
         if (i == 0) {
            mListableItems[i++] = item.getDefaultSizeName();
         } else {
            mListableItems[i++] = item.getDefaultSizeName() + ": " + item.getDefaultSizeValue();
         }
      }

      configureOnClickListener();
   }

   private void configureOnClickListener() {
      setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View view) {
            AlertDialog alertDialog = setupDefaultSizesOverlay();
            alertDialog.show();
         }
      });
   }

   public int getSelectedItemPosition() {
      return selectedItemPosition;
   }

   public int getPosition(DefaultSize size) {
      for (int i = 0; i < mListableItems.length; i++) {
         if (Objects.equals(size.getDefaultSizeName(), mItems.get(i)
               .getDefaultSizeName())) {
            return i;
         }
      }
      return 0;
   }

   public DefaultSize getSelectedItem() {
      if (selectedItemPosition < 0) {
         return null;
      }
      return mItems.get(selectedItemPosition);
   }

   public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
      this.onItemSelectedListener = onItemSelectedListener;
   }

   public interface OnItemSelectedListener {
      void onItemSelectedListener(DefaultSize item, int selectedIndex);
   }

   @Override
   public void setSelection(int index) {
      selectedItemPosition = index;
      setText(mListableItems[index]);

      if (onItemSelectedListener != null) {
         onItemSelectedListener.onItemSelectedListener(mItems.get(index), index);
      }
   }

   private AlertDialog setupDefaultSizesOverlay() {
      final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
      LayoutInflater inflater = activity.getLayoutInflater();

      TextView headline = (TextView) inflater.inflate(R.layout.dialog_headline, null);
      headline.setText(getResources().getString(R.string.new_category_label_defaultSize));
      builder.setCustomTitle(headline);
      builder.setItems(mListableItems, new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialogInterface, int selectedIndex) {
            selectedItemPosition = selectedIndex;
            setText(mListableItems[selectedIndex]);

            if (onItemSelectedListener != null) {
               onItemSelectedListener.onItemSelectedListener(mItems.get(selectedIndex),
                     selectedIndex);
            }
         }
      });
      final AlertDialog alert = builder.create();
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
