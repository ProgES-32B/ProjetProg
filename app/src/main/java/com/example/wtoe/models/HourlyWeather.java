package com.example.wtoe.models;

public class HourlyWeather {

    private String mHour;
    private Integer mImage;
    private String mTemp;

    public HourlyWeather(String hour, Integer image, String temp) {
        this.mHour = hour;
        this.mImage = image;
        this.mTemp = temp;
    }

    public String getmHour() { return this.mHour; }
    public Integer getmImage() { return this.mImage; }
    public String getmTemp() { return this.mTemp; }

}
