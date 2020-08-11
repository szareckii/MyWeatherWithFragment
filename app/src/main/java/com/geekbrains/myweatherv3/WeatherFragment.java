package com.geekbrains.myweatherv3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
//import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WeatherFragment extends Fragment {
    public static final String PARCEL = "parcel";
    private String cityName;
    private TextView textTempCurrent;
    private TextView textPlusOneHour;
    private TextView textPlusTwoHours;
    private TextView textPlusThreeHours;
    private TextView textTempNow;
    private TextView textTempPlusOneHour;
    private TextView textTempPlusTwoHours;
    private TextView textTempPlusThreeHours;
    private ImageView imageTypsWeather;
    private ImageView imageTypsWeatherNow;
    private ImageView imageTypsWeatherPlusOneHour;
    private ImageView imageTypsWeatherPlusTwoHours;
    private ImageView imageTypsWeatherPlusThreeHours;
    private ImageView imageTypeWeatherPlusOneDay;
    private ImageView imageTypeWeatherPlusOneDayNight;
    private ImageView imageTypeWeatherPlusTwoDay;
    private ImageView imageTypeWeatherPlusTwoDayNight;
    private ImageView imageTypeWeatherPlusThreeDay;
    private ImageView imageTypeWeatherPlusThreeDayNight;
    private TextView textCurrentDayPlusOne;
    private TextView textCurrentDayPlusTwo;
    private TextView textCurrentDayPlusThree;
    private TextView textTempCurrentDayPlusOne;
    private TextView textTempCurrentDayPlusOneNight;
    private TextView textTempCurrentDayPlusTwo;
    private TextView textTempCurrentDayPlusTwoNight;
    private TextView textTempCurrentDayPlusThree;
    private TextView textTempCurrentDayPlusThreeNight;
    private TextView textWindNow;
    private TextView textWindPlusOneHour;
    private TextView textWindPlusTwoHours;
    private TextView textWindPlusThreeHours;
    private TextView textPressureNow;
    private TextView textPressurePlusOneHour;
    private TextView textPressurePlusTwoHours;
    private TextView textPressurePlusThreeHours;
    private ImageView imgBtnSettings;
    private static final String TAG = "myLogs";
    private static final  int REQUEST_CODE = 1;
    private TextView textUnitWindNow;
    private TextView textUnitWindPlusOne;
    private TextView textUnitWindPlusTwo;
    private TextView textUnitWindPlusThree;
    private TextView textUnitPressureNow;
    private TextView textUnitPressurePlusOne;
    private TextView textUnitPressurePlusTwo;
    private TextView textUnitPressurePlusThree;
    private boolean windyVisible;
    private boolean pressureVisible;
    private int countHoursBetweenForecasts;
    private ImageView imgBtnWeatherToYandex;
    private TextView cityNameView;

    // Фабричный метод создания фрагмента
    public static WeatherFragment create(Parcel parcel) {
        WeatherFragment f = new WeatherFragment();    // создание

        // Передача параметра
        Bundle args = new Bundle();
        args.putSerializable(PARCEL, parcel);
        f.setArguments(args);
        return f;
    }


    // Получить посылку из параметра
    public Parcel getParcel() {
        assert getArguments() != null;
        return (Parcel) getArguments().getSerializable(PARCEL);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_weather, container, false);

        initViews(layout);

        Parcel parcel = getParcel();

        cityNameView.setText(parcel.getCityName());
        cityName = parcel.getCityName();

        windyVisible = parcel.isVisibleWind();
        pressureVisible = parcel.isVisiblePressure();
        countHoursBetweenForecasts = parcel.getCountHoursBetweenForecasts();
        setVisiblePressure(pressureVisible);
        setVisibleWindy(windyVisible);

        setTemp(cityName);
        findCurrentHour(countHoursBetweenForecasts);
        setSettingsBtnClickBehavior();
        setYandexBtnClickBehavior();

        return layout;
    }

    /*Метод открытия окна с настройками*/
    private void setSettingsBtnClickBehavior() {
        imgBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);

                if (windyVisible) {
                    intent.putExtra("WindyVisible", true);
                } else {
                    intent.putExtra("WindyVisible", false);
                }

                if (pressureVisible) {
                    intent.putExtra("PressureVisible", true);
                } else {
                    intent.putExtra("PressureVisible", false);
                }

                intent.putExtra("СountHoursBetweenForecasts", countHoursBetweenForecasts);

                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (resultCode == Activity.RESULT_OK){
            windyVisible = data.getBooleanExtra("windyVisible", false);
            pressureVisible = data.getBooleanExtra("pressureVisible", false);
            countHoursBetweenForecasts = data.getIntExtra("countHoursBetweenForecasts", 3);
            findCurrentHour(countHoursBetweenForecasts);

            setVisibleWindy(windyVisible);
            setVisiblePressure(pressureVisible);

            Parcel parcel = getParcel();

            parcel.setVisibleWind(windyVisible);
            parcel.setVisiblePressure(pressureVisible);
            parcel.setCountHoursBetweenForecasts(countHoursBetweenForecasts);

            Log.d(TAG, "WeatherFragment. WindyVisible - " + windyVisible);
            Log.d(TAG, "WeatherFragment. PressureVisible - " + pressureVisible);
        }
    }

    /*Метод скрытия/отображения из активити view относящихся к давлению*/
    private void setVisiblePressure(boolean pressureVisible) {
        if (!pressureVisible) {
            textPressureNow.setVisibility(View.GONE);
            textPressurePlusOneHour.setVisibility(View.GONE);
            textPressurePlusTwoHours.setVisibility(View.GONE);
            textPressurePlusThreeHours.setVisibility(View.GONE);
            textUnitPressureNow.setVisibility(View.GONE);
            textUnitPressurePlusOne.setVisibility(View.GONE);
            textUnitPressurePlusTwo.setVisibility(View.GONE);
            textUnitPressurePlusThree.setVisibility(View.GONE);
        }
        else {
            textPressureNow.setVisibility(View.VISIBLE);
            textPressurePlusOneHour.setVisibility(View.VISIBLE);
            textPressurePlusTwoHours.setVisibility(View.VISIBLE);
            textPressurePlusThreeHours.setVisibility(View.VISIBLE);
            textUnitPressureNow.setVisibility(View.VISIBLE);
            textUnitPressurePlusOne.setVisibility(View.VISIBLE);
            textUnitPressurePlusTwo.setVisibility(View.VISIBLE);
            textUnitPressurePlusThree.setVisibility(View.VISIBLE);
        }
    }

    /*Метод скрытия/отображения из активити view относящихся к ветру*/
    private void setVisibleWindy(boolean windyVisible) {
        if (!windyVisible) {
            textWindNow.setVisibility(View.GONE);
            textWindPlusOneHour.setVisibility(View.GONE);
            textWindPlusTwoHours.setVisibility(View.GONE);
            textWindPlusThreeHours.setVisibility(View.GONE);
            textUnitWindNow.setVisibility(View.GONE);
            textUnitWindPlusOne.setVisibility(View.GONE);
            textUnitWindPlusTwo.setVisibility(View.GONE);
            textUnitWindPlusThree.setVisibility(View.GONE);
        }
        else {
            textWindNow.setVisibility(View.VISIBLE);
            textWindPlusOneHour.setVisibility(View.VISIBLE);
            textWindPlusTwoHours.setVisibility(View.VISIBLE);
            textWindPlusThreeHours.setVisibility(View.VISIBLE);
            textUnitWindNow.setVisibility(View.VISIBLE);
            textUnitWindPlusOne.setVisibility(View.VISIBLE);
            textUnitWindPlusTwo.setVisibility(View.VISIBLE);
            textUnitWindPlusThree.setVisibility(View.VISIBLE);
        }
    }

    /*Метод задания погоды*/
    private void setTemp(String cityName) {
        switch (cityName) {
            case "Moscow":
            case "Москва":
                setWeatherForMoscow();
                break;
            case "London":
            case "Лондон":
                setWeatherForLondon();
                break;
            case "New York":
            case "Нью-Йорк":
                setWeatherForNewYork();
                break;
            case "Beijing":
            case "Пекин":
                setWeatherForBeijing();
                break;
            case "Paris":
            case "Париж":
                setWeatherForParis();
                break;
        }
    }

    /*Метод рандомного заполнения погоды в Москве*/
    private void setWeatherForMoscow() {
        String currentTemp;
        String tempPlusOneHour;
        String tempPlusTwoHours;
        String tempPlusThreeHours;
        String tempPlusOneDay;
        String tempPlusOneDayNight;
        String tempPlusTwoDays;
        String tempPlusTwoDaysNight;
        String tempPlusThreeDays;
        String tempPlusThreeDaysNight;
        String windNow;
        String windPlusOneHour;
        String windPlusTwoHours;
        String windPlusThreeHours;
        String pressureNow;
        String pressurePlusOneHour;
        String pressurePlusTwoHours;
        String pressurePlusThreeHours;
        currentTemp = "+28";
        textTempCurrent.setText(currentTemp);

        tempPlusOneHour = "+28";
        tempPlusTwoHours = "+22";
        tempPlusThreeHours = "+21";

        textTempNow.setText(currentTemp);

        textTempPlusOneHour.setText(tempPlusOneHour);
        textTempPlusTwoHours.setText(tempPlusTwoHours);
        textTempPlusThreeHours.setText(tempPlusThreeHours);
        textTempPlusThreeHours.setText(tempPlusThreeHours);

        imageTypsWeather.setImageResource(R.drawable.cloudysun);
        imageTypsWeatherNow.setImageResource(R.drawable.cloudysun);
        imageTypsWeatherPlusOneHour.setImageResource(R.drawable.cloudy);
        imageTypsWeatherPlusTwoHours.setImageResource(R.drawable.storm);
        imageTypsWeatherPlusThreeHours.setImageResource(R.drawable.storm);

        imageTypeWeatherPlusOneDay.setImageResource(R.drawable.cloudysun);
        imageTypeWeatherPlusOneDayNight.setImageResource(R.drawable.moon);
        imageTypeWeatherPlusTwoDay.setImageResource(R.drawable.sun);
        imageTypeWeatherPlusTwoDayNight.setImageResource(R.drawable.moon);
        imageTypeWeatherPlusThreeDay.setImageResource(R.drawable.cloudysunrainy);
        imageTypeWeatherPlusThreeDayNight.setImageResource(R.drawable.moonandcloudy);

        tempPlusOneDay = "+25";
        tempPlusOneDayNight = "+17";
        tempPlusTwoDays = "+26";
        tempPlusTwoDaysNight = "+20";
        tempPlusThreeDays = "+21";
        tempPlusThreeDaysNight = "+19";

        textTempCurrentDayPlusOne.setText(tempPlusOneDay);
        textTempCurrentDayPlusOneNight.setText(tempPlusOneDayNight);
        textTempCurrentDayPlusTwo.setText(tempPlusTwoDays);
        textTempCurrentDayPlusTwoNight.setText(tempPlusTwoDaysNight);
        textTempCurrentDayPlusThree.setText(tempPlusThreeDays);
        textTempCurrentDayPlusThreeNight.setText(tempPlusThreeDaysNight);

        windNow = "4.5";
        windPlusOneHour = "2.5";
        windPlusTwoHours = "15.4";
        windPlusThreeHours = "18.5";
        pressureNow = "765";
        pressurePlusOneHour = "750";
        pressurePlusTwoHours = "740";
        pressurePlusThreeHours = "745";

        textWindNow.setText(windNow);
        textWindPlusOneHour.setText(windPlusOneHour);
        textWindPlusTwoHours.setText(windPlusTwoHours);
        textWindPlusThreeHours.setText(windPlusThreeHours);
        textPressureNow.setText(pressureNow);
        textPressurePlusOneHour.setText(pressurePlusOneHour);
        textPressurePlusTwoHours.setText(pressurePlusTwoHours);
        textPressurePlusThreeHours.setText(pressurePlusThreeHours);
    }

    /*Метод рандомного заполнения погоды в Лондоне*/
    private void setWeatherForLondon() {
        String currentTemp;
        String tempPlusOneHour;
        String tempPlusTwoHours;
        String tempPlusThreeHours;
        String tempPlusOneDay;
        String tempPlusOneDayNight;
        String tempPlusTwoDays;
        String tempPlusTwoDaysNight;
        String tempPlusThreeDays;
        String tempPlusThreeDaysNight;
        String windNow;
        String windPlusOneHour;
        String windPlusTwoHours;
        String windPlusThreeHours;
        String pressureNow;
        String pressurePlusOneHour;
        String pressurePlusTwoHours;
        String pressurePlusThreeHours;
        currentTemp = "+25";
        textTempCurrent.setText(currentTemp);

        tempPlusOneHour = "+25";
        tempPlusTwoHours = "+24";
        tempPlusThreeHours = "+24";

        textTempNow.setText(currentTemp);

        textTempPlusOneHour.setText(tempPlusOneHour);
        textTempPlusTwoHours.setText(tempPlusTwoHours);
        textTempPlusThreeHours.setText(tempPlusThreeHours);
        textTempPlusThreeHours.setText(tempPlusThreeHours);

        imageTypsWeather.setImageResource(R.drawable.cloudy);
        imageTypsWeatherNow.setImageResource(R.drawable.cloudy);
        imageTypsWeatherPlusOneHour.setImageResource(R.drawable.cloudy);
        imageTypsWeatherPlusTwoHours.setImageResource(R.drawable.cloudysun);
        imageTypsWeatherPlusThreeHours.setImageResource(R.drawable.cloudysun);

        imageTypeWeatherPlusOneDay.setImageResource(R.drawable.cloudysun);
        imageTypeWeatherPlusOneDayNight.setImageResource(R.drawable.moonandcloudy);
        imageTypeWeatherPlusTwoDay.setImageResource(R.drawable.sun);
        imageTypeWeatherPlusTwoDayNight.setImageResource(R.drawable.moon);
        imageTypeWeatherPlusThreeDay.setImageResource(R.drawable.cloudysunrainy);
        imageTypeWeatherPlusThreeDayNight.setImageResource(R.drawable.moonandcloudy);

        tempPlusOneDay = "+27";
        tempPlusOneDayNight = "+20";
        tempPlusTwoDays = "+27";
        tempPlusTwoDaysNight = "+22";
        tempPlusThreeDays = "+28";
        tempPlusThreeDaysNight = "+23";

        textTempCurrentDayPlusOne.setText(tempPlusOneDay);
        textTempCurrentDayPlusOneNight.setText(tempPlusOneDayNight);
        textTempCurrentDayPlusTwo.setText(tempPlusTwoDays);
        textTempCurrentDayPlusTwoNight.setText(tempPlusTwoDaysNight);
        textTempCurrentDayPlusThree.setText(tempPlusThreeDays);
        textTempCurrentDayPlusThreeNight.setText(tempPlusThreeDaysNight);

        windNow = "1.0";
        windPlusOneHour = "2.0";
        windPlusTwoHours = "2.5";
        windPlusThreeHours = "2.1";
        pressureNow = "760";
        pressurePlusOneHour = "758";
        pressurePlusTwoHours = "766";
        pressurePlusThreeHours = "777";

        textWindNow.setText(windNow);
        textWindPlusOneHour.setText(windPlusOneHour);
        textWindPlusTwoHours.setText(windPlusTwoHours);
        textWindPlusThreeHours.setText(windPlusThreeHours);
        textPressureNow.setText(pressureNow);
        textPressurePlusOneHour.setText(pressurePlusOneHour);
        textPressurePlusTwoHours.setText(pressurePlusTwoHours);
        textPressurePlusThreeHours.setText(pressurePlusThreeHours);
    }

    /*Метод рандомного заполнения погоды в Нью-Йорке*/
    private void setWeatherForNewYork() {
        String currentTemp;
        String tempPlusOneHour;
        String tempPlusTwoHours;
        String tempPlusThreeHours;
        String tempPlusOneDay;
        String tempPlusOneDayNight;
        String tempPlusTwoDays;
        String tempPlusTwoDaysNight;
        String tempPlusThreeDays;
        String tempPlusThreeDaysNight;
        String windNow;
        String windPlusOneHour;
        String windPlusTwoHours;
        String windPlusThreeHours;
        String pressureNow;
        String pressurePlusOneHour;
        String pressurePlusTwoHours;
        String pressurePlusThreeHours;
        currentTemp = "+31";
        textTempCurrent.setText(currentTemp);

        tempPlusOneHour = "+32";
        tempPlusTwoHours = "+31";
        tempPlusThreeHours = "+29";

        textTempNow.setText(currentTemp);

        textTempPlusOneHour.setText(tempPlusOneHour);
        textTempPlusTwoHours.setText(tempPlusTwoHours);
        textTempPlusThreeHours.setText(tempPlusThreeHours);
        textTempPlusThreeHours.setText(tempPlusThreeHours);

        imageTypsWeather.setImageResource(R.drawable.cloudysun);
        imageTypsWeatherNow.setImageResource(R.drawable.cloudysun);
        imageTypsWeatherPlusOneHour.setImageResource(R.drawable.sun);
        imageTypsWeatherPlusTwoHours.setImageResource(R.drawable.sun);
        imageTypsWeatherPlusThreeHours.setImageResource(R.drawable.sun);

        imageTypeWeatherPlusOneDay.setImageResource(R.drawable.cloudysun);
        imageTypeWeatherPlusOneDayNight.setImageResource(R.drawable.moon);
        imageTypeWeatherPlusTwoDay.setImageResource(R.drawable.sun);
        imageTypeWeatherPlusTwoDayNight.setImageResource(R.drawable.moon);
        imageTypeWeatherPlusThreeDay.setImageResource(R.drawable.cloudysun);
        imageTypeWeatherPlusThreeDayNight.setImageResource(R.drawable.moon);

        tempPlusOneDay = "+29";
        tempPlusOneDayNight = "+25";
        tempPlusTwoDays = "+28";
        tempPlusTwoDaysNight = "+22";
        tempPlusThreeDays = "+25";
        tempPlusThreeDaysNight = "+17";

        textTempCurrentDayPlusOne.setText(tempPlusOneDay);
        textTempCurrentDayPlusOneNight.setText(tempPlusOneDayNight);
        textTempCurrentDayPlusTwo.setText(tempPlusTwoDays);
        textTempCurrentDayPlusTwoNight.setText(tempPlusTwoDaysNight);
        textTempCurrentDayPlusThree.setText(tempPlusThreeDays);
        textTempCurrentDayPlusThreeNight.setText(tempPlusThreeDaysNight);

        windNow = "0.5";
        windPlusOneHour = "0";
        windPlusTwoHours = "2.7";
        windPlusThreeHours = "4.0";
        pressureNow = "754";
        pressurePlusOneHour = "759";
        pressurePlusTwoHours = "764";
        pressurePlusThreeHours = "766";

        textWindNow.setText(windNow);
        textWindPlusOneHour.setText(windPlusOneHour);
        textWindPlusTwoHours.setText(windPlusTwoHours);
        textWindPlusThreeHours.setText(windPlusThreeHours);
        textPressureNow.setText(pressureNow);
        textPressurePlusOneHour.setText(pressurePlusOneHour);
        textPressurePlusTwoHours.setText(pressurePlusTwoHours);
        textPressurePlusThreeHours.setText(pressurePlusThreeHours);
    }

    /*Метод рандомного заполнения погоды в Пекине*/
    private void setWeatherForBeijing() {
        String currentTemp;
        String tempPlusOneHour;
        String tempPlusTwoHours;
        String tempPlusThreeHours;
        String tempPlusOneDay;
        String tempPlusOneDayNight;
        String tempPlusTwoDays;
        String tempPlusTwoDaysNight;
        String tempPlusThreeDays;
        String tempPlusThreeDaysNight;
        String windNow;
        String windPlusOneHour;
        String windPlusTwoHours;
        String windPlusThreeHours;
        String pressureNow;
        String pressurePlusOneHour;
        String pressurePlusTwoHours;
        String pressurePlusThreeHours;
        currentTemp = "+22";
        textTempCurrent.setText(currentTemp);

        tempPlusOneHour = "+23";
        tempPlusTwoHours = "+24";
        tempPlusThreeHours = "+25";

        textTempNow.setText(currentTemp);

        textTempPlusOneHour.setText(tempPlusOneHour);
        textTempPlusTwoHours.setText(tempPlusTwoHours);
        textTempPlusThreeHours.setText(tempPlusThreeHours);
        textTempPlusThreeHours.setText(tempPlusThreeHours);

        imageTypsWeather.setImageResource(R.drawable.sun);
        imageTypsWeatherNow.setImageResource(R.drawable.sun);
        imageTypsWeatherPlusOneHour.setImageResource(R.drawable.cloudysun);
        imageTypsWeatherPlusTwoHours.setImageResource(R.drawable.sun);
        imageTypsWeatherPlusThreeHours.setImageResource(R.drawable.sun);

        imageTypeWeatherPlusOneDay.setImageResource(R.drawable.cloudysun);
        imageTypeWeatherPlusOneDayNight.setImageResource(R.drawable.moon);
        imageTypeWeatherPlusTwoDay.setImageResource(R.drawable.sun);
        imageTypeWeatherPlusTwoDayNight.setImageResource(R.drawable.moonandcloudy);
        imageTypeWeatherPlusThreeDay.setImageResource(R.drawable.cloudysunrainy);
        imageTypeWeatherPlusThreeDayNight.setImageResource(R.drawable.moon);

        tempPlusOneDay = "+25";
        tempPlusOneDayNight = "+20";
        tempPlusTwoDays = "+26";
        tempPlusTwoDaysNight = "+22";
        tempPlusThreeDays = "+28";
        tempPlusThreeDaysNight = "+25";

        textTempCurrentDayPlusOne.setText(tempPlusOneDay);
        textTempCurrentDayPlusOneNight.setText(tempPlusOneDayNight);
        textTempCurrentDayPlusTwo.setText(tempPlusTwoDays);
        textTempCurrentDayPlusTwoNight.setText(tempPlusTwoDaysNight);
        textTempCurrentDayPlusThree.setText(tempPlusThreeDays);
        textTempCurrentDayPlusThreeNight.setText(tempPlusThreeDaysNight);

        windNow = "1.5";
        windPlusOneHour = "5.7";
        windPlusTwoHours = "9.9";
        windPlusThreeHours = "12.7";
        pressureNow = "744";
        pressurePlusOneHour = "755";
        pressurePlusTwoHours = "765";
        pressurePlusThreeHours = "770";

        textWindNow.setText(windNow);
        textWindPlusOneHour.setText(windPlusOneHour);
        textWindPlusTwoHours.setText(windPlusTwoHours);
        textWindPlusThreeHours.setText(windPlusThreeHours);
        textPressureNow.setText(pressureNow);
        textPressurePlusOneHour.setText(pressurePlusOneHour);
        textPressurePlusTwoHours.setText(pressurePlusTwoHours);
        textPressurePlusThreeHours.setText(pressurePlusThreeHours);
    }

    /*Метод рандомного заполнения погоды в Париже*/
    private void setWeatherForParis() {
        String currentTemp;
        String tempPlusOneHour;
        String tempPlusTwoHours;
        String tempPlusThreeHours;
        String tempPlusOneDay;
        String tempPlusOneDayNight;
        String tempPlusTwoDays;
        String tempPlusTwoDaysNight;
        String tempPlusThreeDays;
        String tempPlusThreeDaysNight;
        String windNow;
        String windPlusOneHour;
        String windPlusTwoHours;
        String windPlusThreeHours;
        String pressureNow;
        String pressurePlusOneHour;
        String pressurePlusTwoHours;
        String pressurePlusThreeHours;
        currentTemp = "+33";
        textTempCurrent.setText(currentTemp);

        tempPlusOneHour = "+32";
        tempPlusTwoHours = "+34";
        tempPlusThreeHours = "+33";

        textTempNow.setText(currentTemp);

        textTempPlusOneHour.setText(tempPlusOneHour);
        textTempPlusTwoHours.setText(tempPlusTwoHours);
        textTempPlusThreeHours.setText(tempPlusThreeHours);
        textTempPlusThreeHours.setText(tempPlusThreeHours);

        imageTypsWeather.setImageResource(R.drawable.cloudysun);
        imageTypsWeatherNow.setImageResource(R.drawable.cloudysun);
        imageTypsWeatherPlusOneHour.setImageResource(R.drawable.sun);
        imageTypsWeatherPlusTwoHours.setImageResource(R.drawable.cloudysunrainy);
        imageTypsWeatherPlusThreeHours.setImageResource(R.drawable.cloudysunrainy);

        imageTypeWeatherPlusOneDay.setImageResource(R.drawable.cloudysunrainy);
        imageTypeWeatherPlusOneDayNight.setImageResource(R.drawable.moonandcloudy);
        imageTypeWeatherPlusTwoDay.setImageResource(R.drawable.cloudysun);
        imageTypeWeatherPlusTwoDayNight.setImageResource(R.drawable.moonandcloudy);
        imageTypeWeatherPlusThreeDay.setImageResource(R.drawable.cloudysun);
        imageTypeWeatherPlusThreeDayNight.setImageResource(R.drawable.moon);

        tempPlusOneDay = "+32";
        tempPlusOneDayNight = "+28";
        tempPlusTwoDays = "+31";
        tempPlusTwoDaysNight = "+25";
        tempPlusThreeDays = "+29";
        tempPlusThreeDaysNight = "+24";

        textTempCurrentDayPlusOne.setText(tempPlusOneDay);
        textTempCurrentDayPlusOneNight.setText(tempPlusOneDayNight);
        textTempCurrentDayPlusTwo.setText(tempPlusTwoDays);
        textTempCurrentDayPlusTwoNight.setText(tempPlusTwoDaysNight);
        textTempCurrentDayPlusThree.setText(tempPlusThreeDays);
        textTempCurrentDayPlusThreeNight.setText(tempPlusThreeDaysNight);

        windNow = "1.0";
        windPlusOneHour = "1.5";
        windPlusTwoHours = "1.4";
        windPlusThreeHours = "2.0";
        pressureNow = "758";
        pressurePlusOneHour = "760";
        pressurePlusTwoHours = "764";
        pressurePlusThreeHours = "765";

        textWindNow.setText(windNow);
        textWindPlusOneHour.setText(windPlusOneHour);
        textWindPlusTwoHours.setText(windPlusTwoHours);
        textWindPlusThreeHours.setText(windPlusThreeHours);
        textPressureNow.setText(pressureNow);
        textPressurePlusOneHour.setText(pressurePlusOneHour);
        textPressurePlusTwoHours.setText(pressurePlusTwoHours);
        textPressurePlusThreeHours.setText(pressurePlusThreeHours);
    }

    /*Метод определения текущего дня и часа и вывод на экран информации о ближайших днях и часах*/
    private void findCurrentHour(int countHoursBetweenForecasts) {
        Calendar cDayPlusOne = Calendar.getInstance();
        cDayPlusOne.add(Calendar.DAY_OF_MONTH, 1);
        Calendar cDayPlusTwo = Calendar.getInstance();
        cDayPlusTwo.add(Calendar.DAY_OF_MONTH, 2);
        Calendar cDayPlusThree = Calendar.getInstance();
        cDayPlusThree.add(Calendar.DAY_OF_MONTH, 3);

        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd/MM");

        textCurrentDayPlusOne.setText(df.format(cDayPlusOne.getTime()));
        textCurrentDayPlusTwo.setText(df.format(cDayPlusTwo.getTime()));
        textCurrentDayPlusThree.setText(df.format(cDayPlusThree.getTime()));

        Calendar cPlusOneHour = Calendar.getInstance();
        cPlusOneHour.set(Calendar.MINUTE, 0);
        cPlusOneHour.add(Calendar.HOUR, countHoursBetweenForecasts);

        Calendar cPlusTwoHours = Calendar.getInstance();
        cPlusTwoHours.set(Calendar.MINUTE, 0);
        cPlusTwoHours.add(Calendar.HOUR, 2 * countHoursBetweenForecasts) ;

        Calendar cPlusThreeHours = Calendar.getInstance();
        cPlusThreeHours.set(Calendar.MINUTE, 0);
        cPlusThreeHours.add(Calendar.HOUR, 3 * countHoursBetweenForecasts);

        @SuppressLint("SimpleDateFormat") DateFormat dfHour = new SimpleDateFormat("HH:mm");
        textPlusOneHour.setText(dfHour.format(cPlusOneHour.getTime()));
        textPlusTwoHours.setText(dfHour.format(cPlusTwoHours.getTime()));
        textPlusThreeHours.setText(dfHour.format(cPlusThreeHours.getTime()));
    }

    /*Метод нажатия по кнопке "Погода Яндекса"*/
    private void setYandexBtnClickBehavior() {
        imgBtnWeatherToYandex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(findYandexWeatherHttp(cityName));
                Intent browser = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(browser);
            }
        });
    }

    /*Метод составления http с погодой от яндекса к выбранному городу*/
    private String findYandexWeatherHttp(String cityName) {
        String yandexWeatherHttp = "https://yandex.ru/pogoda/";
        switch (cityName) {
            case "Moscow":
            case "Москва":
                yandexWeatherHttp += "moscow";
                break;
            case "London":
            case "Лондон":
                yandexWeatherHttp += "10393";
                break;
            case "New York":
            case "Нью-Йорк":
                yandexWeatherHttp += "202";
                break;
            case "Beijing":
            case "Пекин":
                yandexWeatherHttp += "10590";
                break;
            case "Paris":
            case "Париж":
                yandexWeatherHttp += "10502";
                break;
            default:
                yandexWeatherHttp += cityName;
                break;
        }
        return yandexWeatherHttp;
    }

    private void initViews(View layout) {
        cityNameView = layout.findViewById(R.id.textCity);
        textTempCurrent = layout.findViewById(R.id.textTempCurrent);
        textPlusOneHour = layout.findViewById(R.id.textPlusOneHour);
        textPlusTwoHours = layout.findViewById(R.id.textPlusTwoHours);
        textPlusThreeHours = layout.findViewById(R.id.textPlusThreeHours);
        textTempNow = layout.findViewById(R.id.textTempNow);
        textTempPlusOneHour = layout.findViewById(R.id.textTempPlusOneHour);
        textTempPlusTwoHours = layout.findViewById(R.id.textTempPlusTwoHours);
        textTempPlusThreeHours = layout.findViewById(R.id.textTempPlusThreeHours);
        imageTypsWeather = layout.findViewById(R.id.imageTypsWeather);
        imageTypsWeatherNow = layout.findViewById(R.id.imageTypsWeatherNow);
        imageTypsWeatherPlusOneHour = layout.findViewById(R.id.imageTypsWeatherPlusOneHour);
        imageTypsWeatherPlusTwoHours = layout.findViewById(R.id.imageTypsWeatherPlusTwoHours);
        imageTypsWeatherPlusThreeHours = layout.findViewById(R.id.imageTypsWeatherPlusThreeHours);
        imageTypeWeatherPlusOneDay = layout.findViewById(R.id.imageTypeWeatherPlusOneDay);
        imageTypeWeatherPlusOneDayNight = layout.findViewById(R.id.imageTypeWeatherPlusOneDayNight);
        imageTypeWeatherPlusTwoDay = layout.findViewById(R.id.imageTypeWeatherPlusTwoDay);
        imageTypeWeatherPlusTwoDayNight = layout.findViewById(R.id.imageTypeWeatherPlusTwoDayNight);
        imageTypeWeatherPlusThreeDay = layout.findViewById(R.id.imageTypeWeatherPlusThreeDay);
        imageTypeWeatherPlusThreeDayNight = layout.findViewById(R.id.imageTypeWeatherPlusThreeDayNight);
        textCurrentDayPlusOne = layout.findViewById(R.id.textCurrentDayPlusOne);
        textCurrentDayPlusTwo = layout.findViewById(R.id.textCurrentDayPlusTwo);
        textCurrentDayPlusThree = layout.findViewById(R.id.textCurrentDayPlusThree);
        textTempCurrentDayPlusOne = layout.findViewById(R.id.textTempCurrentDayPlusOne);
        textTempCurrentDayPlusOneNight = layout.findViewById(R.id.textTempCurrentDayPlusOneNight);
        textTempCurrentDayPlusTwo = layout.findViewById(R.id.textTempCurrentDayPlusTwo);
        textTempCurrentDayPlusTwoNight = layout.findViewById(R.id.textTempCurrentDayPlusTwoNight);
        textTempCurrentDayPlusThree = layout.findViewById(R.id.textTempCurrentDayPlusThree);
        textTempCurrentDayPlusThreeNight = layout.findViewById(R.id.textTempCurrentDayPlusThreeNight);
        textWindNow = layout.findViewById(R.id.textWindNow);
        textWindPlusOneHour = layout.findViewById(R.id.textWindPlusOneHour);
        textWindPlusTwoHours = layout.findViewById(R.id.textWindPlusTwoHours);
        textWindPlusThreeHours = layout.findViewById(R.id.textWindPlusThreeHours);
        textPressureNow = layout.findViewById(R.id.textPressureNow);
        textPressurePlusOneHour = layout.findViewById(R.id.textPressurePlusOneHour);
        textPressurePlusTwoHours = layout.findViewById(R.id.textPressurePlusTwoHours);
        textPressurePlusThreeHours = layout.findViewById(R.id.textPressurePlusThreeHours);
        imgBtnSettings = layout.findViewById(R.id.imgBtnSettings);
        textUnitWindNow = layout.findViewById(R.id.textUnitWindNow);
        textUnitWindPlusOne = layout.findViewById(R.id.textUnitWindPlusOne);
        textUnitWindPlusTwo = layout.findViewById(R.id.textUnitWindPlusTwo);
        textUnitWindPlusThree = layout.findViewById(R.id.textUnitWindPlusThree);
        textUnitPressureNow = layout.findViewById(R.id.textUnitPressureNow);
        textUnitPressurePlusOne = layout.findViewById(R.id.textUnitPressurePlusOne);
        textUnitPressurePlusTwo = layout.findViewById(R.id.textUnitPressurePlusTwo);
        textUnitPressurePlusThree = layout.findViewById(R.id.textUnitPressurePlusThree);
        imgBtnWeatherToYandex = layout.findViewById(R.id.imageBtnWeatherFromYandex);



    }
}

