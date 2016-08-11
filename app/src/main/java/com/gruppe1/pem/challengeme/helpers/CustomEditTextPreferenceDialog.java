package com.gruppe1.pem.challengeme.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.R;

public class CustomEditTextPreferenceDialog extends DialogPreference {

   private TextView headline;
   private TextInputLayout textInputLayout;

   public CustomEditTextPreferenceDialog(Context context, AttributeSet attrs) {
      super(context, attrs);
      setPersistent(false);
      setDialogLayoutResource(R.layout.dialog_textfield);
   }

   @Override
   protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
      super.onPrepareDialogBuilder(builder);
      builder.setTitle(null);
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
   protected void onBindDialogView(View view) {
      super.onBindDialogView(view);
      headline = (TextView) view.findViewById(R.id.dialog_headline);
      textInputLayout = (TextInputLayout) view.findViewById(R.id.dialog_text_layout);
      SharedPreferences sharedPreferences = getSharedPreferences();
      textInputLayout.setHint(getDialogTitle());
      textInputLayout.getEditText()
            .setText(sharedPreferences.getString(getKey(), ""));
      setDialogTitle(getTitle());
   }

   @Override
   protected void onDialogClosed(boolean positiveResult) {
      super.onDialogClosed(positiveResult);

      if (positiveResult) {
         callChangeListener(textInputLayout.getEditText()
               .getText()
               .toString());
      }
   }
}
