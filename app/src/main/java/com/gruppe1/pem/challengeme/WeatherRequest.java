package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import android.util.Log;

import com.gruppe1.pem.challengeme.helpers.Constants;

public class WeatherRequest {

    String etResponse;
    String tvIsConnected;
    Context context;

    ImageView weatherImageView;
    ImageView weatherImg1;
    ImageView weatherImg2;
    ImageView weatherImg3;
    TextView weatherDescView;
    TextView weatherTempView;
    TextView day1;
    TextView day2;
    TextView day3;
    TextView temp1;
    TextView temp2;
    TextView temp3;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    public WeatherRequest(Context context, ImageView weatherImageView, TextView weatherDescView, TextView weatherTempView, ImageView weatherImg1, ImageView weatherImg2, ImageView weatherImg3, TextView day1, TextView day2,
                          TextView day3, TextView temp1, TextView temp2,TextView temp3) {
        this.context = context;
        this.weatherImageView = weatherImageView;
        this.weatherDescView = weatherDescView;
        this.weatherTempView = weatherTempView;
        this.weatherImg1 = weatherImg1;
        this.weatherImg2 = weatherImg2;
        this.weatherImg3 = weatherImg3;
        this.day1 = day1;
        this.day2 = day2;
        this.day3 = day3;
        this.temp1 = temp1;
        this.temp2 = temp2;
        this.temp3 = temp3;

        this.sharedPreferences = context.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();

        // check if you are connected or not
        if(isConnected()){
            tvIsConnected = "You are conncted";
        }
        else{
            tvIsConnected = "You are NOT conncted";
        }

        String[] location = getGPS();

        // call AsynTask to perform network operation on separate thread
        new HttpAsyncTask().execute("http://api.openweathermap.org/data/2.5/weather?q=" + location[0] + "," + location[1]);
        new HttpAsyncTask().execute("http://api.openweathermap.org/data/2.5/forecast?q=" + location[0] + "," + location[1]);
    }

    private String[]  getGPS() {
        // Get GPS location
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener(context, locationManager);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600000, 1000, locationListener); // all 1h, more than 1000 meters
        String[] location = {locationListener.getCityName(), locationListener.getCountryCode()};
        return location;
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    // convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    // check network connection
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public int getImage(String code) {

        switch(code){
            case "200":
            case "201":
            case "202":
            case "210":
            case "211":
            case "212":
            case "221":
            case "230":
            case "231":
            case "232":
                return R.mipmap.weather_thunderstorm;
            case "500":
            case "501":
                return R.mipmap.weather_lightsunrain;
            case "502":
            case "503":
            case "504":
                return R.mipmap.weather_heavysunrain;
            case "511":
                return R.mipmap.weather_freezingrain;
            case "520":
            case "521":
                return R.mipmap.weather_lightrain;
            case "522":
                return R.mipmap.weather_heavyrain;
            case "600":
            case "601":
            case "602":
            case "611":
            case "621":
                return R.mipmap.weather_snow;
            case "701":
            case "711":
            case "721":
            case "731":
            case "741":
                return R.mipmap.weather_fog;
            case "800":
                return R.mipmap.weather_sun;
            case "801":
                return R.mipmap.weather_sunclouds;
            case "802":
            case "803":
            case "804":
            default:
                return R.mipmap.weather_clouds;
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            JSONObject json = null; // convert String to JSONObject
            String weather_today_desc = "";
            String weather_today_temp = "";
            int weather_today_image = 0;
            String weather_1_day = "";
            String weather_1_temp = "";
            int weather_1_image = 0;
            String weather_2_day = "";
            String weather_2_temp = "";
            int weather_2_image = 0;
            String weather_3_day = "";
            String weather_3_temp = "";
            int weather_3_image = 0;

            boolean weatherAvailable = false;
            boolean todayWeather = false;

            try {
                json = new JSONObject(result);
                weatherAvailable = true;

                if(json.has("weather")){
                    todayWeather = true;
                    JSONArray weather = json.getJSONArray("weather"); // get articles array
                    weather_today_desc = weather.getJSONObject(0).getString("main");
                    weather_today_temp = Math.round(json.getJSONObject("main").getDouble("temp") - 273.15) + "°C";
                    weather_today_image = getImage(weather.getJSONObject(0).getString("id"));

                }
                else{
                    JSONArray forecast = json.getJSONArray("list"); // get articles array
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE");
                    String currentDateandTime = sdf.format(date);

                    ArrayList<String> days = new ArrayList<String>();
                    ArrayList<String> temps = new ArrayList<String>();
                    ArrayList<String> codes = new ArrayList<String>();

                    for(int i = 1; i < forecast.length()-8; i++){
                        int weather_dt = Integer.parseInt(forecast.getJSONObject(i).getString("dt"));
                        Date converteddate = new Date(weather_dt*1000L);
                        SimpleDateFormat sdf2 = new SimpleDateFormat("EEE-HH", Locale.ENGLISH);
                        String forecastDateandTime = sdf2.format(converteddate);
                        String[] segs = forecastDateandTime.split( Pattern.quote("-") );

                        if(!segs[0].equals(currentDateandTime)){
                            if(segs[1].equals("14")){

                                JSONArray weather = forecast.getJSONObject(i).getJSONArray("weather"); // get articles array
                                String weather_main = weather.getJSONObject(0).getString("main");
                                String temp = Math.round(forecast.getJSONObject(i).getJSONObject("main").getDouble("temp") - 273.15) + "°C";
                                String code = weather.getJSONObject(0).getString("id");

                                days.add(segs[0]);
                                temps.add(temp);
                                codes.add(code);

                            }
                        }

                    }
                    weather_1_day = days.get(0);
                    weather_1_temp = temps.get(0);
                    weather_1_image = getImage(codes.get(0));
                    weather_2_day = days.get(1);
                    weather_2_temp = temps.get(1);
                    weather_2_image = getImage(codes.get(1));
                    weather_3_day = days.get(2);
                    weather_3_temp = temps.get(2);
                    weather_3_image = getImage(codes.get(2));

                }

            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("NOOO WEATHER");
                weather_today_desc = sharedPreferences.getString(Constants.KEY_W_TODAY_DESC, "");
                weather_today_temp = sharedPreferences.getString(Constants.KEY_W_TODAY_TEMP, "");
                weather_today_image = sharedPreferences.getInt(Constants.KEY_W_TODAY_IMAGE, 0);
                weather_1_day = sharedPreferences.getString(Constants.KEY_W_1_DAY, "");
                weather_1_temp = sharedPreferences.getString(Constants.KEY_W_1_TEMP, "");
                weather_1_image = sharedPreferences.getInt(Constants.KEY_W_1_IMAGE, 0);
                weather_2_day = sharedPreferences.getString(Constants.KEY_W_2_DAY, "");
                weather_2_temp = sharedPreferences.getString(Constants.KEY_W_2_TEMP, "");
                weather_2_image = sharedPreferences.getInt(Constants.KEY_W_2_IMAGE, 0);
                weather_3_day = sharedPreferences.getString(Constants.KEY_W_3_DAY, "");
                weather_3_temp = sharedPreferences.getString(Constants.KEY_W_3_TEMP, "");
                weather_3_image = sharedPreferences.getInt(Constants.KEY_W_3_IMAGE, 0);
            }


            if(!weatherAvailable || todayWeather) {
                weatherImageView.setImageResource(weather_today_image);
                weatherDescView.setText(weather_today_desc);
                weatherTempView.setText(weather_today_temp);
                editor.putInt(Constants.KEY_W_TODAY_IMAGE, weather_today_image);
                editor.putString(Constants.KEY_W_TODAY_DESC, weather_today_desc);
                editor.putString(Constants.KEY_W_TODAY_TEMP, weather_today_temp);
            }
            if(!weatherAvailable || !todayWeather){
                weatherImg1.setImageResource(weather_1_image);
                day1.setText(weather_1_day);
                temp1.setText(weather_1_temp);
                editor.putString(Constants.KEY_W_1_DAY, weather_1_day);
                editor.putInt(Constants.KEY_W_1_IMAGE, weather_1_image);
                editor.putString(Constants.KEY_W_1_TEMP, weather_1_temp);

                weatherImg2.setImageResource(weather_2_image);
                day2.setText(weather_2_day);
                temp2.setText(weather_2_temp);
                editor.putString(Constants.KEY_W_2_DAY, weather_2_day);
                editor.putInt(Constants.KEY_W_2_IMAGE, weather_2_image);
                editor.putString(Constants.KEY_W_2_TEMP, weather_2_temp);

                weatherImg3.setImageResource(weather_3_image);
                day3.setText(weather_3_day);
                temp3.setText(weather_3_temp);
                editor.putString(Constants.KEY_W_3_DAY, weather_3_day);
                editor.putInt(Constants.KEY_W_3_IMAGE, weather_3_image);
                editor.putString(Constants.KEY_W_3_TEMP, weather_3_temp);
            }

            editor.apply();

            etResponse = result;
        }
    }

}
