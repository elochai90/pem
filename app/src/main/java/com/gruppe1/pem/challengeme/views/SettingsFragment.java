package com.gruppe1.pem.challengeme.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.gruppe1.pem.challengeme.Color;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.ColorDataSource;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.CustomEditTextPreferenceDialog;
import com.gruppe1.pem.challengeme.helpers.CustomListPreferenceDialog;
import com.gruppe1.pem.challengeme.helpers.CustomMultipleSelectListPreferenceDialog;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SettingsFragment extends PreferenceFragment {

   private static SharedPreferences sharedPreferences;
   private static SharedPreferences.Editor editor;
   private Locale myLocale;

   private ColorDataSource colorDataSource;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      sharedPreferences =
            getActivity().getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
      editor = sharedPreferences.edit();
      colorDataSource = new ColorDataSource(getActivity());
      setupSimplePreferencesScreen();
   }

   /**
    * Shows the simplified settings UI if the device configuration if the device configuration
    * dictates that a simplified, single-pane UI should be shown.
    */
   private void setupSimplePreferencesScreen() {
      if (!isSimplePreferences(getActivity())) {
         return;
      }

      addPreferencesFromResource(R.xml.pref_general);

      List<Color> allColors = colorDataSource.getAllColors();
      CharSequence[] allColorsChars = new CharSequence[allColors.size()];
      for (int i = 0; i < allColors.size(); i++) {
         allColorsChars[i] = allColors.get(i)
               .getName(getActivity());
      }
      MultiSelectListPreference favColorPreferences =
            (MultiSelectListPreference) findPreference("favorite_colors");
      favColorPreferences.setEntries(allColorsChars);
      favColorPreferences.setEntryValues(allColorsChars);

      boolean show_wishlist_item_in_compare;
      if (sharedPreferences.contains(Constants.KEY_WISHLIST_IN_COMPARE)) {
         show_wishlist_item_in_compare =
               sharedPreferences.getBoolean(Constants.KEY_WISHLIST_IN_COMPARE, false);
      } else {
         show_wishlist_item_in_compare = PreferenceManager.getDefaultSharedPreferences(
               findPreference("show_wishlist_item_in_compare").getContext())
               .getBoolean(findPreference("show_wishlist_item_in_compare").getKey(), false);
      }
      bindPreferenceSummaryToValue(findPreference("show_wishlist_item_in_compare"),
            Boolean.toString(show_wishlist_item_in_compare));

      String tops_default;
      if (sharedPreferences.contains(Constants.KEY_DS_1_NAME)) {
         tops_default = sharedPreferences.getString(Constants.KEY_DS_1_NAME, "");
      } else {
         tops_default =
               PreferenceManager.getDefaultSharedPreferences(findPreference("Tops").getContext())
                     .getString(findPreference("Tops").getKey(), "");
      }
      bindPreferenceSummaryToValue(findPreference("Tops"), tops_default);

      String bottoms_default;
      if (sharedPreferences.contains(Constants.KEY_DS_2_NAME)) {
         bottoms_default = sharedPreferences.getString(Constants.KEY_DS_2_NAME, "");
      } else {
         bottoms_default =
               PreferenceManager.getDefaultSharedPreferences(findPreference("Bottoms").getContext())
                     .getString(findPreference("Bottoms").getKey(), "");
      }
      bindPreferenceSummaryToValue(findPreference("Bottoms"), bottoms_default);

      String shoes_default;
      if (sharedPreferences.contains(Constants.KEY_DS_3_NAME)) {
         shoes_default = sharedPreferences.getString(Constants.KEY_DS_3_NAME, "");
      } else {
         shoes_default =
               PreferenceManager.getDefaultSharedPreferences(findPreference("Shoes").getContext())
                     .getString(findPreference("Shoes").getKey(), "");
      }
      bindPreferenceSummaryToValue(findPreference("Shoes"), shoes_default);

      String language_def;
      if (sharedPreferences.contains(Constants.KEY_LANGUAGE)) {
         language_def = sharedPreferences.getString(Constants.KEY_LANGUAGE, "");
      } else {
         language_def = PreferenceManager.getDefaultSharedPreferences(
               findPreference("Language").getContext())
               .getString(findPreference("Language").getKey(), "");
      }
      bindPreferenceSummaryToValue(findPreference("Language"), language_def);

      Set<String> favorite_colors;
      if (sharedPreferences.contains(Constants.KEY_FAVORITE_COLORS)) {
         favorite_colors =
               sharedPreferences.getStringSet(Constants.KEY_FAVORITE_COLORS, new HashSet<String>());
      } else {
         favorite_colors = PreferenceManager.getDefaultSharedPreferences(
               findPreference("favorite_colors").getContext())
               .getStringSet(findPreference("favorite_colors").getKey(), new HashSet<String>());
      }
      bindPreferenceSummaryToValue(findPreference("favorite_colors"), favorite_colors);
   }

   /**
    * Helper method to determine if the device has an extra-large screen. For example, 10" tablets
    * are extra-large.
    */
   private static boolean isXLargeTablet(Context context) {
      return (context.getResources()
            .getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
            Configuration.SCREENLAYOUT_SIZE_XLARGE;
   }

   /**
    * Determines whether the simplified settings UI should be shown. This is true if this is forced
    * via ALWAYS_SIMPLE_PREFS, or the device doesn't have newer APIs like {@link
    * PreferenceFragment}, or the device doesn't have an extra-large screen. In these cases, a
    * single-pane "simplified" settings UI should be shown.
    */
   private static boolean isSimplePreferences(Context context) {
      return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet(context);
   }

   /**
    * A preference value change listener that updates the preference's summary to reflect its new
    * value.
    */
   private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
         new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
               String stringValue = value.toString();

               if (preference instanceof CustomListPreferenceDialog) {
                  CustomListPreferenceDialog listPreference =
                        (CustomListPreferenceDialog) preference;
                  int index = listPreference.findIndexOfValue(stringValue);

                  // Set the summary to reflect the new value.
                  preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

                  if (preference.getKey()
                        .equals("Language")) {
                     String lang_in_correct_lang;
                     String lang;
                     int index_lang;
                     switch (stringValue) {
                        case "German":
                        case "Deutsch":
                        case "de":
                           lang = "de";
                           index_lang = 0;
                           lang_in_correct_lang = getString(R.string.settings_lang_de);
                           break;
                        case "English":
                        case "Englisch":
                        case "en":
                           lang = "en";
                           index_lang = 1;
                           lang_in_correct_lang = getString(R.string.settings_lang_en);
                           break;
                        default:
                           lang = "en";
                           index_lang = 1;
                           lang_in_correct_lang = getString(R.string.settings_lang_en);
                           break;
                     }
                     //                     Set the summary to reflect the new value.
                     listPreference.setValueIndex(index_lang);
                     preference.setSummary(
                           index_lang >= 0 ? listPreference.getEntries()[index_lang] : null);
                     preference.setSummary(lang_in_correct_lang);
                     editor.putString(Constants.KEY_LANGUAGE, lang);
                     editor.apply();

                     changeLang(lang);
                  }
               } else if (preference instanceof CheckBoxPreference) {
                  if (preference.getKey()
                        .equals("show_wishlist_item_in_compare")) {
                     editor.putBoolean(Constants.KEY_WISHLIST_IN_COMPARE,
                           Boolean.parseBoolean(stringValue));
                     editor.apply();
                  }
               } else if (preference instanceof CustomMultipleSelectListPreferenceDialog) {
                  if (preference.getKey()
                        .equals("favorite_colors")) {
                     HashSet<String> values = (HashSet<String>) value;
                     editor.putStringSet(Constants.KEY_FAVORITE_COLORS, values);
                     editor.apply();
                     preference.setSummary(stringValue.substring(1, stringValue.length() - 1));
                  }
               } else if (preference instanceof CustomEditTextPreferenceDialog) {
                  if (preference.getKey()
                        .equals("Tops")) {
                     editor.putString(Constants.KEY_DS_1_NAME, stringValue);
                     editor.apply();
                     preference.setSummary(stringValue);
                  } else if (preference.getKey()
                        .equals("Bottoms")) {
                     editor.putString(Constants.KEY_DS_2_NAME, stringValue);
                     editor.apply();
                     preference.setSummary(stringValue);
                  } else if (preference.getKey()
                        .equals("Shoes")) {
                     editor.putString(Constants.KEY_DS_3_NAME, stringValue);
                     editor.apply();
                     preference.setSummary(stringValue);
                  }
               } else {
                  // For all other preferences, set the summary to the value's
                  // simple string representation.
                  preference.setSummary(stringValue);
               }
               return true;
            }
         };

   /**
    * Binds a preference's summary to its value. More specifically, when the preference's value is
    * changed, its summary (line of text below the preference title) is updated to reflect the
    * value. The summary is also immediately updated upon calling this method. The exact display
    * format is dependent on the type of preference.
    *
    * @see #sBindPreferenceSummaryToValueListener
    */
   private void bindPreferenceSummaryToValue(Preference preference, Object initialValue) {
      // Set the listener to watch for value changes.
      preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

      // Trigger the listener immediately with the preference's
      // current value.
      sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, initialValue);
   }

   public void changeLang(String lang) {
      if (lang.equalsIgnoreCase("")) {
         return;
      }
      myLocale = new Locale(lang);

      Locale.setDefault(myLocale);
      android.content.res.Configuration config = getActivity().getBaseContext()
            .getResources()
            .getConfiguration();
      config.locale = myLocale;
      getActivity().getBaseContext()
            .getResources()
            .updateConfiguration(config, getActivity().getBaseContext()
                  .getResources()
                  .getDisplayMetrics());
   }

   @Override
   public void onConfigurationChanged(android.content.res.Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      //        getDelegate().onConfigurationChanged(newConfig);
      if (myLocale != null) {
         newConfig.locale = myLocale;
         Locale.setDefault(myLocale);
         getActivity().getBaseContext()
               .getResources()
               .updateConfiguration(newConfig, getActivity().getBaseContext()
                     .getResources()
                     .getDisplayMetrics());
      }
   }
}
