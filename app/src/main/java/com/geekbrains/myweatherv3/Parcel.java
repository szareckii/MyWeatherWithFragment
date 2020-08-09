package com.geekbrains.myweatherv3;

import java.io.Serializable;

public class Parcel implements Serializable {
    private String cityName;
    private boolean visibleWind;
    private boolean visiblePressure;
    private int countHoursBetweenForecasts;

    public String getCityName() {
        return cityName;
    }

    public boolean isVisibleWind() {
        return visibleWind;
    }

    public boolean isVisiblePressure() {
        return visiblePressure;
    }

    public int getCountHoursBetweenForecasts() {
        return countHoursBetweenForecasts;
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

    public Parcel(String cityName, boolean visibleWind, boolean visiblePressure, int countHoursBetweenForecasts) {
        this.cityName = cityName;
        this.visibleWind = visibleWind;
        this.visiblePressure = visiblePressure;
        this.countHoursBetweenForecasts = countHoursBetweenForecasts;
    }
}
