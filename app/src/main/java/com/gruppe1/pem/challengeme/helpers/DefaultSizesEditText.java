package com.gruppe1.pem.challengeme.helpers;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.DefaultSize;
import com.gruppe1.pem.challengeme.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultSizesEditText extends EditText {

   ArrayList<DefaultSize> mItems;
   String[] mListableItems;
   CharSequence mHint;
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

   @TargetApi (Build.VERSION_CODES.LOLLIPOP)
   public DefaultSizesEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);

      mHint = getHint();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      setFocusable(false);
      setClickable(true);
   }

   public void setItems(ArrayList<DefaultSize> items) {
      this.mItems = items;

      this.mListableItems = new String[items.size()];

      int i = 0;


      for (DefaultSize item : mItems) {
         if(i == 0) {
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
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(mHint);
            builder.setItems(mListableItems, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                  selectedItemPosition = selectedIndex;
                  setText(mListableItems[selectedIndex]);

                  if (onItemSelectedListener != null) {
                        onItemSelectedListener.onItemSelectedListener(mItems.get(selectedIndex), selectedIndex);
                  }
               }
            });
//            builder.setPositiveButton(R.string.save, null);
            builder.create().show();
         }
      });
   }

   public int getSelectedItemPosition() {
      return selectedItemPosition;
   }

   public int getPosition(DefaultSize size) {
      for(int i = 0; i < mListableItems.length; i++) {
         if(Objects.equals(size.getDefaultSizeName(), mItems.get(i)
               .getDefaultSizeName())) {
            return i;
         }
      }
      return 0;
   }

   public DefaultSize getSelectedItem() {
      if(selectedItemPosition < 0) {
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
}
