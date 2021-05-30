package com.example.wtoe.http;

import android.app.Dialog;
import android.content.Context;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ClientHTTP {

    private String mApiAddress;
    private String mApiToken;
    private Context mContext;
    private int mStatusCode;
    private RequestQueue requestQueue;

    public ClientHTTP(Context context) {
        this.mApiAddress = "https://api.openweathermap.org/data/2.5/";
        this.mContext = context;
        //this.mApiToken = "72c6152562dd37421baec212294b42c1";
        //this.mApiToken = "10df8650a40d7e31e0c6e61698ee5938";
        //this.mApiToken = "86249cc023461d583034e18c6a7582a2";
        this.mApiToken = "2a77d3ae95f96209003f9d4151e43b7f";
        this.requestQueue = Volley.newRequestQueue(mContext);

        //Hourly https://api.openweathermap.org/data/2.5/onecall?lat=33.441792&lon=-94.037689&exclude=daily&appid=72c6152562dd37421baec212294b42c1
        //https://openweathermap.org/api/one-call-api
    }

    public void insertWeather(JSONObject jsonData) throws IOException {
        FileOutputStream fos = mContext.openFileOutput("weather.txt", mContext.MODE_PRIVATE);
        fos.write(jsonData.toString().getBytes(), 0 ,jsonData.toString().length() );
        fos.close();
        System.out.println("File writed...");
    }

    public JSONObject getWeather(final String[] cities) throws IOException, JSONException {

        JSONObject objres = null;
        JSONObject citiesJson = new JSONObject();
        for(int i = 0; i < cities.length; i++) {
            requestCityWeather(cities[i]);
        }
        return citiesJson;
    }

    public void getWeatherDemo(ArrayList<String> lCities) throws IOException, JSONException {
        for(int i = 0; i < lCities.size(); i++) {
            requestCityWeather(lCities.get(i).toString());
        }
    }

    public void isCityValid(final String city, final Dialog dialog, final Fragment fragment) {
        String URL = mApiAddress + "weather?q=" + city + "&appid=" + mApiToken;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                    // Reading
                    FileInputStream fis = null;
                    ArrayList<String> lCities = new ArrayList<String>();
                    try {
                        fis = mContext.openFileInput("cities.txt");
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader br = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        String text;
                        while ((text = br.readLine()) != null) {
                            lCities.add(text);
                        }
                        fis.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Boolean cont = false;
                    ArrayList<String> testCities = new ArrayList<String>();
                    for(int i =0; i <lCities.size();i++) {
                         testCities.add(lCities.get(i).toString().toUpperCase());
                    }

                    if (!testCities.contains(city.toString().toUpperCase())) {
                        // Writing
                        String lstCities = "";
                        for(int i = 0; i < lCities.size(); i++) {
                            lstCities += lCities.get(i).toString() + "\n";
                        }
                        lstCities += city.toString();

                        FileOutputStream fos = null;
                        try {
                            fos = mContext.openFileOutput("cities.txt", mContext.MODE_PRIVATE);
                            fos.write(lstCities.toString().getBytes(), 0 ,lstCities.toString().length());
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        /*fragment.getFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                        dialog.cancel();*/

                    }
                    else {
                        //Toasty.info(mContext, "La ville est déjà dans les favoris.", Toasty.LENGTH_SHORT).show();
                    }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Temps:" + error.getNetworkTimeMs());
                Toasty.warning(mContext, "Nom de ville introuvable.", Toasty.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);

    }


    public void requestCityWeather(final String city) {
        String URL = mApiAddress + "weather?q=" + city + "&appid=" + mApiToken;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonData = new JSONObject(response);
                    FileOutputStream fos = mContext.openFileOutput(city.toString() + ".txt", mContext.MODE_PRIVATE);
                    fos.write(jsonData.toString().getBytes(), 0 ,jsonData.toString().length() );
                    fos.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }

        });
        requestQueue.add(stringRequest);

    }


    public void requestHourlyWeather(final Double longitude, final Double latitude) {
        String URL = mApiAddress + "onecall?lat=" + latitude + "&lon=" + longitude +"&exclude=current,daily,minutely,alerts&appid=" + mApiToken;
        //https://api.openweathermap.org/data/2.5/onecall?lat=33.44&lon=-94.04&exclude=current,daily,minutely,alerts&appid=72c6152562dd37421baec212294b42c1
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonData = new JSONObject(response);
                    FileOutputStream fos = mContext.openFileOutput("hourly" + latitude.toString() + longitude.toString() + ".txt", mContext.MODE_PRIVATE);
                    fos.write(jsonData.toString().getBytes(), 0 ,jsonData.toString().length() );
                    fos.close();
                } catch (Exception e) {
                    System.out.println("test");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }

        });
        requestQueue.add(stringRequest);

    }

}
