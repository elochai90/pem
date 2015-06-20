package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by bianka on 11.06.2015.
 */
public class MyLocationListener implements LocationListener {

    Context context;
    LocationManager locationManager;

    String cityName;
    String countryCode;



    // TODO: implement Methods to check if GPS is turned on


    MyLocationListener(Context context, LocationManager locationManager) {
        this.context = context;
        this.locationManager = locationManager;

        if(!getLastGPS()){
            System.out.println("no GPS found");
            this.cityName = "Muenchen"; // TODO: get default location from sharedPreferences
            this.countryCode = "DE"; // TODO: get default location from sharedPreferences
        }
    }

    private boolean getLastGPS() {


        List<String> providers = locationManager.getProviders(true);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;

        for (int i=providers.size()-1; i>=0; i--) {
            l = locationManager.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        double[] gps = new double[2];
        if(l == null) {
            return false;
        }
        else {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        try {
            cityName = gcd.getFromLocation(l.getLatitude(),  l.getLongitude(), 1).get(0).getLocality();
            countryCode = gcd.getFromLocation(l.getLatitude(), l.getLongitude(), 1).get(0).getCountryCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("GPS found: " + cityName + " - " + countryCode);
        return true;
    }

    private String replaceUmlauteFromString(String string) {
        String result = "";
        if(string != null) {
            result = string.toLowerCase();
            result = string.replace("ä","ae");
            result = string.replace("ö","oe");
            result = string.replace("ü","ue");
            result = string.replace("ß","ss");
        }
        return result;
    }

    public String getCityName() {

        return replaceUmlauteFromString(cityName);
    }

    public String getCountryCode() {
        return replaceUmlauteFromString(countryCode);
    }

    @Override
    public void onLocationChanged(Location loc) {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1);
            System.out.println(addresses);
            if (addresses.size() > 0) {
                cityName = addresses.get(0).getLocality();
                countryCode = addresses.get(0).getCountryCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("GPS updated: " + cityName + " - " + countryCode);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

}