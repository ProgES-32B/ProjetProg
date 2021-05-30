package com.example.wtoe.models;
import android.graphics.drawable.Drawable;

public class City {
    private String Name;
    private String Deg;
    private Integer draw;

    public City() {

    }

    public City(String name, String deg, Integer draw) {
        Name = name;
        Deg = deg;
        this.draw = draw;
    }

    public String getName() {
        return Name;
    }

    public String getDeg() { return Deg; }

    public Integer getDraw() { return  this.draw;}

}
