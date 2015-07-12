package com.gruppe1.pem.challengeme.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.gruppe1.pem.challengeme.Color;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.helpers.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            if(getActionBar() != null) {
                getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        addPreferencesFromResource(R.xml.pref_general);

        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle("Standard Sizes:");
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_default_sizes);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle("Favourite Colors:");
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_fav_colors);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle("Show Wishlist Items in Compares:");
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_view);

        ArrayList<Color> allColors = com.gruppe1.pem.challengeme.Color.getAllColors(this);
        CharSequence[] allColorsChars = new CharSequence[allColors.size()];
        for(int i = 0; i < allColors.size(); i++) {
            allColorsChars[i] = allColors.get(i).getName();
        }
        MultiSelectListPreference favColorPreferences = (MultiSelectListPreference) findPreference("favorite_colors");
        favColorPreferences.setEntries(allColorsChars);
        favColorPreferences.setEntryValues(allColorsChars);


        boolean show_wishlist_item_in_compare;
        if (sharedPreferences.contains(Constants.KEY_WISHLIST_IN_COMPARE)) {
            show_wishlist_item_in_compare = sharedPreferences.getBoolean(Constants.KEY_WISHLIST_IN_COMPARE, false);
        } else {
            show_wishlist_item_in_compare = PreferenceManager
                            .getDefaultSharedPreferences(findPreference("show_wishlist_item_in_compare").getContext())
                            .getBoolean(findPreference("show_wishlist_item_in_compare").getKey(), false);
        }
        bindPreferenceSummaryToValue(findPreference("show_wishlist_item_in_compare"), Boolean.toString(show_wishlist_item_in_compare));

        String tops_default;
        if (sharedPreferences.contains(Constants.KEY_DS_1_NAME)) {
            tops_default = sharedPreferences.getString(Constants.KEY_DS_1_NAME, "");
        } else {
            tops_default = PreferenceManager
                    .getDefaultSharedPreferences(findPreference("Tops").getContext())
                    .getString(findPreference("Tops").getKey(), "");
        }
        bindPreferenceSummaryToValue(findPreference("Tops"), tops_default);

        String bottoms_default;
        if (sharedPreferences.contains(Constants.KEY_DS_2_NAME)) {
            bottoms_default = sharedPreferences.getString(Constants.KEY_DS_2_NAME, "");
        } else {
            bottoms_default = PreferenceManager
                    .getDefaultSharedPreferences(findPreference("Bottoms").getContext())
                    .getString(findPreference("Bottoms").getKey(), "");
        }
        bindPreferenceSummaryToValue(findPreference("Bottoms"), bottoms_default);

        String shoes_default;
        if (sharedPreferences.contains(Constants.KEY_DS_3_NAME)) {
            shoes_default = sharedPreferences.getString(Constants.KEY_DS_3_NAME, "");
        } else {
            shoes_default = PreferenceManager
                    .getDefaultSharedPreferences(findPreference("Shoes").getContext())
                    .getString(findPreference("Shoes").getKey(), "");
        }
        bindPreferenceSummaryToValue(findPreference("Shoes"), shoes_default);

        Set<String> favorite_colors;
        if (sharedPreferences.contains(Constants.KEY_FAVORITE_COLORS)) {
            favorite_colors = sharedPreferences.getStringSet(Constants.KEY_FAVORITE_COLORS, new HashSet<String>());
        } else {
            favorite_colors = PreferenceManager
                    .getDefaultSharedPreferences(findPreference("favorite_colors").getContext())
                    .getStringSet(findPreference("favorite_colors").getKey(), new HashSet<String>());
        }
        bindPreferenceSummaryToValue(findPreference("favorite_colors"), favorite_colors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via ALWAYS_SIMPLE_PREFS, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }


    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

                if(preference.getKey().equals("Tops")) {
                    editor.putString(Constants.KEY_DS_1_NAME, stringValue);
                    editor.apply();
                } else if(preference.getKey().equals("Bottoms")) {
                    editor.putString(Constants.KEY_DS_2_NAME, stringValue);
                    editor.apply();
                } else if(preference.getKey().equals("Shoes")) {
                    editor.putString(Constants.KEY_DS_3_NAME, stringValue);
                    editor.apply();
                }
            } else if (preference instanceof CheckBoxPreference) {
                if(preference.getKey().equals("show_wishlist_item_in_compare")) {
                    editor.putBoolean(Constants.KEY_WISHLIST_IN_COMPARE, Boolean.parseBoolean(stringValue));
                    editor.apply();
                }
            } else if (preference instanceof MultiSelectListPreference) {
                if(preference.getKey().equals("favorite_colors")) {
                    HashSet<String> values = (HashSet<String>) value;
                    editor.putStringSet(Constants.KEY_FAVORITE_COLORS,  values);
                    editor.apply();
                    preference.setSummary(stringValue.substring(1, stringValue.length() - 1));
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
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference, Object initialValue) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, initialValue);
    }
}
