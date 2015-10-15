package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * custom LocationListener
 */
public class MyLocationListener implements LocationListener {

    private Context context;
    private LocationManager locationManager;
    private String cityName;
    private String countryCode;

    // TODO: implement Methods to check if GPS is turned on

    MyLocationListener(Context context, LocationManager locationManager) {
        this.context = context;
        this.locationManager = locationManager;

        getLastGPS();
    }

    private boolean getLastGPS() {
        List<String> providers = locationManager.getProviders(true);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;

        for (int i=providers.size()-1; i>=0; i--) {
            l = locationManager.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        if(l == null) {
            return false;
        }
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        try {
            if(gcd.getFromLocation(l.getLatitude(),  l.getLongitude(), 1).size() > 0) {
                cityName = gcd.getFromLocation(l.getLatitude(),  l.getLongitude(), 1).get(0).getLocality();
                countryCode = gcd.getFromLocation(l.getLatitude(), l.getLongitude(), 1).get(0).getCountryCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * replaces 'umlaute' from a string
     * @param string String with umlaute
     * @return the string without umlaute
     */
    private String replaceUmlauteFromString(String string) {
        String result = "";
        if(string != null) {
            string = string.toLowerCase();
            string = string.replace("ä","ae");
            string = string.replace("ö","oe");
            string = string.replace("ü","ue");
            result = string.replace("ß","ss");
        }
        return result;
    }

    /**
     * get the GPS city name without umlaute
     * @return the GPS city name without umlaute
     */
    public String getCityName() {
        return replaceUmlauteFromString(cityName);
    }

    /**
     * get the GPS country code without umlaute
     * @return the GPS country code without umlaute
     */
    public String getCountryCode() {
        return replaceUmlauteFromString(countryCode);
    }

    @Override
    public void onLocationChanged(Location loc) {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1);
            if (addresses.size() > 0) {
                cityName = addresses.get(0).getLocality();
                countryCode = addresses.get(0).getCountryCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

}