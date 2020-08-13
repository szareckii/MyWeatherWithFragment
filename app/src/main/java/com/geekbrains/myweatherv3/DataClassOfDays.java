package com.geekbrains.myweatherv3;

import android.graphics.drawable.Drawable;

public class DataClassOfDays {
    String textDay;
    String texTemptDay;
    Drawable drawableDay;
    String texTemptNight;
    Drawable drawableNight;

    public DataClassOfDays(String textDay, String texTemptDay, Drawable drawableDay, String texTemptNight, Drawable drawableNight) {
        this.textDay = textDay;
        this.texTemptDay = texTemptDay;
        this.drawableDay = drawableDay;
        this.texTemptNight = texTemptNight;
        this.drawableNight = drawableNight;
    }
}
