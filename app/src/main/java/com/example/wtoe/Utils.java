package com.example.wtoe;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

    Context context;

    public Utils (Context context) {
        this.context = context;
    }

    public int convertKelvinToCelcius(Double value) {
        return (int) (value - 273.15F);
    }


    public JSONObject getCityInfoWeather(String city) {
        // Init local variables
        FileInputStream fis = null;
        JSONObject objres = null;

        try {
            fis = context.openFileInput(city + ".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String text;

            // Reading line by line
            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            // Converting to JSONObject the String
            objres = new JSONObject(sb.toString());
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return objres;
    }

}
