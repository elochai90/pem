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

    MyLocationListener(Context context, LocationManager locationManager) {
        this.context = context;
        this.locationManager = locationManager;

        getLastGPS();
    }

    private void getLastGPS() {


        List<String> providers = locationManager.getProviders(true);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;
        for (int i=providers.size()-1; i>=0; i--) {
            l = locationManager.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        double[] gps = new double[2];
        if (l != null) {
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
        System.out.println("GPS: " + cityName + " - " + countryCode);
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public void onLocationChanged(Location loc) {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1);
            System.out.println(addresses);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
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