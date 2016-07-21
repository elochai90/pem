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
import com.gruppe1.pem.challengeme.R;

import java.util.ArrayList;
import java.util.List;

public class ClickToSelectEditText extends EditText {

   List<Category> mItems;
   String[] mListableItems;
   CharSequence mHint;
   int selectedItemPosition = 0;

   OnItemSelectedListener<Category> onItemSelectedListener;

   public ClickToSelectEditText(Context context) {
      super(context);

      mHint = getHint();
   }

   public ClickToSelectEditText(Context context, AttributeSet attrs) {
      super(context, attrs);

      mHint = getHint();
   }

   public ClickToSelectEditText(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);

      mHint = getHint();
   }

   @TargetApi (Build.VERSION_CODES.LOLLIPOP)
   public ClickToSelectEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);

      mHint = getHint();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      setFocusable(false);
      setClickable(true);
   }

   public void setItems(ArrayList<Category> items) {
      this.mItems = items;

      this.mListableItems = new String[items.size()+1];

      this.mListableItems[0] = getResources().getString(R.string.new_category_parent_none);
      int i = 1;


      for (Category item : mItems) {
         mListableItems[i++] = item.getName();
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
                     if (selectedIndex <= 0) {
                        onItemSelectedListener.onItemSelectedListener(null, selectedIndex);
                     } else {
                        onItemSelectedListener.onItemSelectedListener(mItems.get(selectedIndex-1), selectedIndex);
                     }
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

   public int getPosition(Category category) {
      for(int i = 0; i < mListableItems.length; i++) {
         if(category.getId() == (mItems.get(i).getId())) {
            return i+1;
         }
      }
      return 0;
   }

   public Category getSelectedItem() {
      if(selectedItemPosition < 0) {
         return null;
      }
      return mItems.get(selectedItemPosition);
   }

   public void setOnItemSelectedListener(OnItemSelectedListener<Category> onItemSelectedListener) {
      this.onItemSelectedListener = onItemSelectedListener;
   }

   public interface OnItemSelectedListener<Category> {
      void onItemSelectedListener(com.gruppe1.pem.challengeme.Category item, int selectedIndex);
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
