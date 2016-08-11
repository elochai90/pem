package com.gruppe1.pem.challengeme.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.R;

public class CustomListPreferenceDialog extends ListPreference {

   private TextView headline;

   public CustomListPreferenceDialog(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public void setDialogTitle(CharSequence dialogTitle) {
      headline.setText(dialogTitle);
   }

   @Override
   public void setDialogTitle(int dialogTitleResId) {
      headline.setText(dialogTitleResId);
   }

   @Override
   protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
      super.onPrepareDialogBuilder(builder);
      headline = (TextView) LayoutInflater.from(getContext())
            .inflate(R.layout.dialog_headline, null);
      builder.setCustomTitle(headline);
      setDialogTitle(getDialogTitle());
   }
}
