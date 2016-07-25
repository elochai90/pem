package com.gruppe1.pem.challengeme.helpers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.gruppe1.pem.challengeme.HSVColorPickerDialog;

import java.util.Calendar;

public class DateEditText extends EditText {

   CharSequence mHint;
   int day = -1;
   int month = -1;
   int year = -1;
   private String date = "";
   private Activity activity;
   private DatePickerDialog datePickerDialog;

   DatePickerDialog.OnDateSetListener onDateSelectedListener;

   public DateEditText(Context context) {
      super(context);

      mHint = getHint();
   }

   public DateEditText(Context context, AttributeSet attrs) {
      super(context, attrs);

      mHint = getHint();
   }

   public DateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);

      mHint = getHint();
   }

   @TargetApi (Build.VERSION_CODES.LOLLIPOP)
   public DateEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);

      mHint = getHint();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      setFocusable(false);
      setClickable(true);
   }

   public void initialize(Activity activity, String date) {
      this.activity = activity;
      setDate(date);
      configureOnClickListener();
   }

   private void configureOnClickListener() {
      setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View view) {
            int displayDay = Calendar.getInstance()
                        .get(Calendar.DAY_OF_MONTH);
            int displayYear = Calendar.getInstance()
                  .get(Calendar.YEAR);
            int displayMonth = Calendar.getInstance()
                  .get(Calendar.MONTH);
            if (date.length() > 0) {
               String[] parts = date.split("\\.");
               displayDay = Integer.parseInt(parts[0]);
               displayMonth = Integer.parseInt(parts[1]) - 1;
               displayYear = Integer.parseInt(parts[2]);
            }
            datePickerDialog = new DatePickerDialog(activity, onDateSelectedListener, displayYear, displayMonth, displayDay);
            datePickerDialog.show();
         }
      });
   }


   public void setOnDateSelectedListener(DatePickerDialog.OnDateSetListener onDateSelectedListener) {
      this.onDateSelectedListener = onDateSelectedListener;
   }

   public void setDate(String date) {
      this.date = date;
      if (date.length() > 0) {
         String[] parts = date.split("\\.");
         day = Integer.parseInt(parts[0]);
         month = Integer.parseInt(parts[1]) - 1;
         year = Integer.parseInt(parts[2]);
         setText(date);
      }
   }

   public String getDate() {
      return date;
   }
}
