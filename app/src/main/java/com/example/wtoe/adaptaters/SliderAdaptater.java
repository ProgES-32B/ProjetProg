package com.example.wtoe.adaptaters;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wtoe.R;
import com.example.wtoe.adaptaters.RecyclerViewWeatherAdaptater;
import com.example.wtoe.http.ClientHTTP;
import com.example.wtoe.models.HourlyWeather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SliderAdaptater extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;


    ArrayList<String> citiesName = new ArrayList<String>();
    ArrayList<Integer> citiesTemp = new ArrayList<Integer>();
    ArrayList<String> citiesTMax = new ArrayList<String>();
    ArrayList<String> citiesTmin = new ArrayList<String>();
    ArrayList<String> citiesTSta = new ArrayList<String>();
    ArrayList<String> lCities = new ArrayList<String>();

    String todayDay = null;

    public SliderAdaptater(Context context) {

        this.context = context;
        getDay();
        //updateTableau();

        FileInputStream fis = null;
        try {
            fis = context.openFileInput("cities.txt");
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
    }

    public int[] slide_images = {
            R.drawable.cielbleusansnuagesoleil2,
            R.drawable.cielnuagebleucielsanssoleil
    };


    @Override
    public int getCount() {
        return lCities.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (ConstraintLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        /*{"coord":{"lon":2.3488,"lat":48.8534},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01d"}],"base":"stations","main":{"temp":282.36,"feels_like":279.51,"temp_min":281.15,"temp_max":283.15,"pressure":1028,"humidity":42},"visibility":10000,"wind":{"speed":5.66,"deg":60,"gust":12.35},"clouds":{"all":0},"dt":1618490008,"sys":{"type":1,"id":6550,"country":"FR","sunrise":1618462762,"sunset":1618512085},"timezone":7200,"id":2988507,"name":"Paris","cod":200}
*/
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_weather_city_layout, container, false);

        TextView cityDeg = (TextView) view.findViewById(R.id.textWDeg);
        TextView cityTempMin = (TextView) view.findViewById(R.id.textWMin);
        TextView cityTempMax = (TextView) view.findViewById(R.id.textWMax);
        TextView cityName = (TextView) view.findViewById(R.id.textWCity);
        TextView cityStatus = (TextView) view.findViewById(R.id.textWStatus);
        TextView cityDay = (TextView) view.findViewById(R.id.textWDay);



        cityName.setText(lCities.get(position).toString());

        JSONObject objWeather = new JSONObject();
        objWeather = getInfoWeather(lCities.get(position).toString());

        Double lon = null;
        Double lat = null;

        List<HourlyWeather> hWeather = new ArrayList<>();
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        System.out.println("Current time => " + ts);
        System.out.println("Latitude: " + lat);
        System.out.println("Longitude : " + lon);

        try {
            Integer temp = convertKelvinToCelcius(objWeather.getJSONObject("main").getDouble("temp"));
            Integer temp_max =   convertKelvinToCelcius(objWeather.getJSONObject("main").getDouble("temp_max"));
            Integer temp_min =   convertKelvinToCelcius(objWeather.getJSONObject("main").getDouble("temp_min"));
            String weat_stat =   objWeather.getJSONArray("weather").getJSONObject(0).getString("main");
            lon = objWeather.getJSONObject("coord").getDouble("lon");
            lat = objWeather.getJSONObject("coord").getDouble("lat");
            cityDeg.setText(temp.toString() + "°");
            cityTempMax.setText(temp_max.toString());
            cityTempMin.setText(temp_min.toString());
            cityStatus.setText(weat_stat);

            ConstraintLayout constraintLayout = (ConstraintLayout) view.findViewById(R.id.constRecycCity);

            switch(weat_stat) {
                case "Clouds":
                    constraintLayout.setBackgroundResource(R.drawable.nuageux3);
                    break;
                case "Clear":
                    constraintLayout.setBackgroundResource(R.drawable.ciel);
                    break;
            }

            Long sunset =   objWeather.getJSONObject("sys").getLong("sunset");
            Long sunrise =   objWeather.getJSONObject("sys").getLong("sunrise");

            TextView textView7 = (TextView) view.findViewById(R.id.textView7);
            TextView textView9 = (TextView) view.findViewById(R.id.textView9);

            textView7.setText(convertDateForSunriseSunset(sunrise));
            textView9.setText(convertDateForSunriseSunset(sunset));

            if(tsLong >=  sunset) {
                constraintLayout.setBackgroundResource(R.drawable.night_stars);
            }

            cityDay.setText(todayDay);
            hWeather.add(new HourlyWeather("Maint.",25,temp.toString() + "°"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        // ADD HOURLY WEATHER---------------------------


        ClientHTTP socket = new ClientHTTP(context);
        socket.requestHourlyWeather(lon,lat);

        try {
            JSONObject jsonObject = getHourlyWeather(lat.toString(),lon.toString());
            Long dt = jsonObject.getJSONArray("hourly").getJSONObject(0).getLong("dt");
            int pos = 0;
            for(int i = 0; dt < tsLong; i++) {
                pos = i;
                dt = jsonObject.getJSONArray("hourly").getJSONObject(i).getLong("dt");
            }

            Integer temp = 0;
            for(int i = pos; i < pos + 20; i++) {
                Long tsdt = jsonObject.getJSONArray("hourly").getJSONObject(i).getLong("dt");
                temp =   convertKelvinToCelcius(jsonObject.getJSONArray("hourly").getJSONObject(i).getDouble("temp"));
                Integer hours = convertDate(tsdt);

                if(hours.toString().length() < 2) {
                    String hour = "0" + hours.toString();
                    hWeather.add(new HourlyWeather(hour,25,temp.toString() + "°"));
                }
                else {
                    hWeather.add(new HourlyWeather(hours.toString(),25,temp.toString() + "°"));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ---------------------------------------------------------------------------------


        RecyclerView myrecyclerview;
        myrecyclerview = (androidx.recyclerview.widget.RecyclerView) view.findViewById(R.id.hourlyWeather);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        myrecyclerview.setAdapter(new RecyclerViewWeatherAdaptater((context),hWeather));
        container.addView(view);

        return view;
    }


    private String convertDateForSunriseSunset(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        Integer hours = cal.get(Calendar.HOUR_OF_DAY);
        Integer minutes = cal.get(Calendar.MINUTE);
        Integer seconds = cal.get(Calendar.SECOND);
        String hour = "";
        String minute = "";
        if(hours.toString().length() < 2 ) {
            hour = "0" + hours.toString();
        }
        else {
            hour = hours.toString();
        }

        if(minutes.toString().length() < 2 ) {
            minute = "0" + minutes.toString();
        }
        else {
            minute = minutes.toString();
        }

        return hour + ":" + minute;
    }

    private int convertDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);

        return hours;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {

        View view = (View) object;
        container.removeView(view);
        //updateTableau();
    }

    public int convertKelvinToCelcius(Double value) {
        return (int) (value - 273.15F);
    }

    private JSONObject getHourlyWeather(String lat, String lon) {
        FileInputStream fis = null;
        ArrayList<String> finalString = new ArrayList<String>();
        JSONObject objres1 = new JSONObject();
        JSONObject objres;
        try {
            fis = context.openFileInput("hourly"+lat+lon+".txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }
            try {
                objres = new JSONObject(sb.toString());
                return objres;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return objres1;
    }

    private JSONObject getInfoWeather(String city) {
        FileInputStream fis = null;
        JSONObject objres1 = new JSONObject();
        JSONObject objres;
        try {
            fis = context.openFileInput(city + ".txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }
            try {
                objres = new JSONObject(sb.toString());
                return objres;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(sb.toString());
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return objres1;
    }


    private void getDay() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                todayDay = "Dimanche";
                break;
            case Calendar.MONDAY:
                todayDay = "Lundi";
                break;
            case Calendar.TUESDAY:
                todayDay = "Mardi";
                break;
            case Calendar.WEDNESDAY:
                todayDay = "Mercredi";
                break;
            case Calendar.THURSDAY:
                todayDay = "Jeudi";
                break;
            case Calendar.FRIDAY:
                todayDay = "Vendredi";
                break;
            case Calendar.SATURDAY:
                todayDay = "Samedi";
        }
    }

}



