package com.gruppe1.pem.challengeme.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.View;

import com.gruppe1.pem.challengeme.HSVColorPickerDialog;

public class ExactColorEditText extends TextInputEditText {

   CharSequence mHint;
   int exactColorId = -1;
   private Activity activity;
   private HSVColorPickerDialog colorsAlertDialog;

   OnColorSelectedListener onColorSelectedListener;

   public ExactColorEditText(Context context) {
      super(context);

      mHint = getHint();
   }

   public ExactColorEditText(Context context, AttributeSet attrs) {
      super(context, attrs);

      mHint = getHint();
   }

   public ExactColorEditText(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);

      mHint = getHint();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      setFocusable(false);
      setClickable(true);
   }

   public void initialize(Activity activity, int exactColorId) {
      this.activity = activity;
      setExactColorId(exactColorId);
      configureOnClickListener();
   }

   private void configureOnClickListener() {
      setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View view) {

            colorsAlertDialog = new HSVColorPickerDialog(activity, exactColorId,
                  new HSVColorPickerDialog.OnColorSelectedListener() {
                     @Override
                     public void colorSelected(Integer color) {
                        setExactColorId(color);
                     }
                  });
            colorsAlertDialog.show();
         }
      });
   }

   public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
      this.onColorSelectedListener = onColorSelectedListener;
   }

   public int getExactColor() {
      return exactColorId;
   }

   public void setExactColorId(int exactColorId) {
      this.exactColorId = exactColorId;
      if (exactColorId != -1) {
         String hexColor = String.format("#%06X", (0xFFFFFF & exactColorId));
         setText(hexColor);
      }
      if (onColorSelectedListener != null) {
         onColorSelectedListener.onColorSelectedListener(exactColorId);
      }
   }

   public interface OnColorSelectedListener {
      void onColorSelectedListener(int color);
   }
}
