package com.geekbrains.myweatherv3;

import java.io.Serializable;
import java.util.ArrayList;

public class Parcel implements Serializable {
    private String cityName;
    private boolean visibleWind;
    private boolean visiblePressure;
    private int countHoursBetweenForecasts;
    private boolean darkTheme;
    private ArrayList<String> data;

    public String getCityName() {
        return cityName;
    }

    public boolean isVisibleWind() {
        return visibleWind;
    }

    public boolean isVisiblePressure() {
        return visiblePressure;
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public int getCountHoursBetweenForecasts() {
        return countHoursBetweenForecasts;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setVisibleWind(boolean visibleWind) {
        this.visibleWind = visibleWind;
    }

    public void setVisiblePressure(boolean visiblePressure) {
        this.visiblePressure = visiblePressure;
    }

    public void setCountHoursBetweenForecasts(int countHoursBetweenForecasts) {
        this.countHoursBetweenForecasts = countHoursBetweenForecasts;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }

    public Parcel(String cityName, boolean visibleWind, boolean visiblePressure, int countHoursBetweenForecasts, boolean darkTheme, ArrayList<String> data) {
        this.cityName = cityName;
        this.visibleWind = visibleWind;
        this.visiblePressure = visiblePressure;
        this.countHoursBetweenForecasts = countHoursBetweenForecasts;
        this.darkTheme = darkTheme;
        this.data = data;
    }
}
