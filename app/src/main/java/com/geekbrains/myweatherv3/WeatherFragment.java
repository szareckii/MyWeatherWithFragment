package com.geekbrains.myweatherv3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

public class WeatherFragment extends Fragment {
    public static final String PARCEL = "parcel";
    private String cityName;
    private TextView textTempCurrent;
    private ImageView imageTypsWeather;
    private TextView textWindNow;
    private TextView textPressureNow;
    private ImageView imgBtnSettings;
    private static final String TAG = "myLogs";
    private static final  int REQUEST_CODE = 1;
    private TextView textUnitWindNow;
    private TextView textUnitPressureNow;
    private boolean windyVisible;
    private boolean pressureVisible;
    private int countHoursBetweenForecasts;
    private ImageView imgBtnWeatherToYandex;
    private TextView cityNameView;
    private Calendar cDayPlusOne, cDayPlusTwo, cDayPlusThree, cDayPlusFour, cDayPlusFive, cDayPlusSix,
             cDayPlusSeven, cPlusOneHour, cPlusTwoHours, cPlusThreeHours, cPlusFourHours,
             cPlusFiveHours, cPlusSixHours, cPlusSevenHours, cPlusEightHours, cPlusNineHours,
             cPlusTenHours, cPlusElevenHours, cPlusTwelveHours;
    private DateFormat df, dfHour;
    private ArrayList<DataClassOfHours> listHours;

    private RecyclerView daysRecyclerView;
    private DataClassOfDays[] dataDays;

    private RecyclerView hoursRecyclerView;
    private DataClassOfHours[] dataHours;
    private Parcel parcel;

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
        daysRecyclerView = layout.findViewById(R.id.daysRecyclerView);
        hoursRecyclerView = layout.findViewById(R.id.hoursRecyclerView);

        findViews(layout);
        parcel = getParcel();
        cityName = parcel.getCityName();
        cityNameView.setText(parcel.getCityName());
        cityName = parcel.getCityName();

        windyVisible = parcel.isVisibleWind();
        pressureVisible = parcel.isVisiblePressure();
        countHoursBetweenForecasts = parcel.getCountHoursBetweenForecasts();

        findCurrentHour(countHoursBetweenForecasts);

        setTemp(cityName);

        ArrayList<DataClassOfDays> listDays = new ArrayList<>(dataDays.length);
        listDays.addAll(Arrays.asList(dataDays));

        listHours = new ArrayList<>(dataHours.length);
        listHours.addAll(Arrays.asList(dataHours));

        setupRecyclerViewDays(listDays);

        setupRecyclerViewHours(listHours);

        setVisiblePressure(pressureVisible);
        setVisibleWindy(windyVisible);

        setSettingsBtnClickBehavior();
        setYandexBtnClickBehavior();

        return layout;
    }

    private void setupRecyclerViewHours(ArrayList<DataClassOfHours> list) {
        hoursRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerDataAdapterForHours hoursAdapter = new RecyclerDataAdapterForHours(list);
        hoursRecyclerView.setLayoutManager(layoutManager);
        hoursRecyclerView.setAdapter(hoursAdapter);
    }

    private void setupRecyclerViewDays(ArrayList<DataClassOfDays> list) {
        daysRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerDataAdapterForDays daysAdapter = new RecyclerDataAdapterForDays(list);
        daysRecyclerView.setLayoutManager(layoutManager);
        daysRecyclerView.setAdapter(daysAdapter);
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
            countHoursBetweenForecasts = data.getIntExtra("countHoursBetweenForecasts", 1);
            findCurrentHour(countHoursBetweenForecasts);

            setVisibleWindy(windyVisible);
            setVisiblePressure(pressureVisible);

            parcel.setVisibleWind(windyVisible);
            parcel.setVisiblePressure(pressureVisible);
            parcel.setCountHoursBetweenForecasts(countHoursBetweenForecasts);

            setTemp(cityName);

            listHours.clear();
            listHours.addAll(Arrays.asList(dataHours));
            setupRecyclerViewHours(listHours);

            Log.d(TAG, "WeatherFragment. WindyVisible - " + windyVisible);
            Log.d(TAG, "WeatherFragment. PressureVisible - " + pressureVisible);
        }
    }

    /*Метод скрытия/отображения из активити view относящихся к давлению*/
    private void setVisiblePressure(boolean pressureVisible) {
        if (!pressureVisible) {
            textPressureNow.setVisibility(View.GONE);
            textUnitPressureNow.setVisibility(View.GONE);
        }
        else {
            textPressureNow.setVisibility(View.VISIBLE);
            textUnitPressureNow.setVisibility(View.VISIBLE);
        }
    }

    /*Метод скрытия/отображения из активити view относящихся к ветру*/
    private void setVisibleWindy(boolean windyVisible) {
        if (!windyVisible) {
            textWindNow.setVisibility(View.GONE);
            textUnitWindNow.setVisibility(View.GONE);
        }
        else {
            textWindNow.setVisibility(View.VISIBLE);
            textUnitWindNow.setVisibility(View.VISIBLE);
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
        String windNow;
        String pressureNow;
        currentTemp = "+28";
        textTempCurrent.setText(currentTemp);
        imageTypsWeather.setImageResource(R.drawable.cloudysun);
        windNow = "4.5";
        pressureNow = "765";
        textWindNow.setText(windNow);
        textPressureNow.setText(pressureNow);

        dataDays = new DataClassOfDays[]{
                new DataClassOfDays(df.format(cDayPlusOne.getTime()), "+20",
                        ContextCompat.getDrawable(Objects.requireNonNull(getActivity()),R.drawable.cloudysun),
                        "+10",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon)),
                new DataClassOfDays(df.format(cDayPlusTwo.getTime()), "+21",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+11",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon)),
                new DataClassOfDays(df.format(cDayPlusThree.getTime()), "+22",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+12",
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy)),
                new DataClassOfDays(df.format(cDayPlusFour.getTime()), "+23",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moonandcloudy),
                        "+13",
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloud)),
                new DataClassOfDays(df.format(cDayPlusFive.getTime()), "+24",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+14",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun)),
                new DataClassOfDays(df.format(cDayPlusSix.getTime()), "+25",
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+15",
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy)),
                new DataClassOfDays(df.format(cDayPlusSeven.getTime()), "+26",
                        ContextCompat.getDrawable(getActivity(),R.drawable.snowy),
                        "+16",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moonandcloudy))};

        dataHours = new DataClassOfHours[]{
                new DataClassOfHours(dfHour.format(cPlusOneHour.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusTwoHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusThreeHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusFourHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysun),
                        "+22"),
                new DataClassOfHours(dfHour.format(cPlusFiveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+22"),
                new DataClassOfHours(dfHour.format(cPlusSixHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+23"),
                new DataClassOfHours(dfHour.format(cPlusSevenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusEightHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusNineHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusTenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusElevenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusTwelveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy),
                        "+21")};
    }

    /*Метод рандомного заполнения погоды в Лондоне*/
    private void setWeatherForLondon() {
        String currentTemp;
        String windNow;
        String pressureNow;
        currentTemp = "+25";
        textTempCurrent.setText(currentTemp);
        imageTypsWeather.setImageResource(R.drawable.cloudy);
        windNow = "1.0";
        pressureNow = "760";
        textWindNow.setText(windNow);
        textPressureNow.setText(pressureNow);

        dataDays = new DataClassOfDays[]{
                new DataClassOfDays(df.format(cDayPlusOne.getTime()), "+27",
                        ContextCompat.getDrawable(Objects.requireNonNull(getActivity()),R.drawable.sun),
                        "+20",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon)),
                new DataClassOfDays(df.format(cDayPlusTwo.getTime()), "+27",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+22",
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy)),
                new DataClassOfDays(df.format(cDayPlusThree.getTime()), "+28",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+23",
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy)),
                new DataClassOfDays(df.format(cDayPlusFour.getTime()), "+26",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moonandcloudy),
                        "+22",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun)),
                new DataClassOfDays(df.format(cDayPlusFive.getTime()), "+28",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+21",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun)),
                new DataClassOfDays(df.format(cDayPlusSix.getTime()), "+27",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+24",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun)),
                new DataClassOfDays(df.format(cDayPlusSeven.getTime()), "+29",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+26",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moonandcloudy))};

        dataHours = new DataClassOfHours[]{
                new DataClassOfHours(dfHour.format(cPlusOneHour.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusTwoHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusThreeHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusFourHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysun),
                        "+22"),
                new DataClassOfHours(dfHour.format(cPlusFiveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+22"),
                new DataClassOfHours(dfHour.format(cPlusSixHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+23"),
                new DataClassOfHours(dfHour.format(cPlusSevenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusEightHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusNineHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusTenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusElevenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusTwelveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy),
                        "+21")};
    }

    /*Метод рандомного заполнения погоды в Нью-Йорке*/
    private void setWeatherForNewYork() {
        String currentTemp;
        String windNow;
        String pressureNow;
        currentTemp = "+31";
        textTempCurrent.setText(currentTemp);
        imageTypsWeather.setImageResource(R.drawable.cloudysun);
        windNow = "0.5";
        pressureNow = "754";
        textWindNow.setText(windNow);
        textPressureNow.setText(pressureNow);

        dataDays = new DataClassOfDays[]{
                new DataClassOfDays(df.format(cDayPlusOne.getTime()), "+27",
                        ContextCompat.getDrawable(Objects.requireNonNull(getActivity()),R.drawable.sun),
                        "+20",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon)),
                new DataClassOfDays(df.format(cDayPlusTwo.getTime()), "+27",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+22",
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy)),
                new DataClassOfDays(df.format(cDayPlusThree.getTime()), "+28",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+23",
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy)),
                new DataClassOfDays(df.format(cDayPlusFour.getTime()), "+26",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moonandcloudy),
                        "+22",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun)),
                new DataClassOfDays(df.format(cDayPlusFive.getTime()), "+28",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+21",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun)),
                new DataClassOfDays(df.format(cDayPlusSix.getTime()), "+27",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+24",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun)),
                new DataClassOfDays(df.format(cDayPlusSeven.getTime()), "+29",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+26",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moonandcloudy))};

        dataHours = new DataClassOfHours[]{
                new DataClassOfHours(dfHour.format(cPlusOneHour.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusTwoHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusThreeHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusFourHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysun),
                        "+22"),
                new DataClassOfHours(dfHour.format(cPlusFiveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+22"),
                new DataClassOfHours(dfHour.format(cPlusSixHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+23"),
                new DataClassOfHours(dfHour.format(cPlusSevenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusEightHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusNineHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusTenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusElevenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusTwelveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy),
                        "+21")};
    }

    /*Метод рандомного заполнения погоды в Пекине*/
    private void setWeatherForBeijing() {
        String currentTemp;
        String windNow;
        String pressureNow;
        currentTemp = "+22";
        textTempCurrent.setText(currentTemp);
        imageTypsWeather.setImageResource(R.drawable.sun);
        windNow = "1.5";
        pressureNow = "744";
        textWindNow.setText(windNow);
        textPressureNow.setText(pressureNow);

        dataDays = new DataClassOfDays[]{
                new DataClassOfDays(df.format(cDayPlusOne.getTime()), "+27",
                        ContextCompat.getDrawable(Objects.requireNonNull(getActivity()),R.drawable.sun),
                        "+20",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon)),
                new DataClassOfDays(df.format(cDayPlusTwo.getTime()), "+27",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+22",
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy)),
                new DataClassOfDays(df.format(cDayPlusThree.getTime()), "+28",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+23",
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy)),
                new DataClassOfDays(df.format(cDayPlusFour.getTime()), "+26",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moonandcloudy),
                        "+22",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun)),
                new DataClassOfDays(df.format(cDayPlusFive.getTime()), "+28",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+21",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun)),
                new DataClassOfDays(df.format(cDayPlusSix.getTime()), "+27",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+24",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun)),
                new DataClassOfDays(df.format(cDayPlusSeven.getTime()), "+29",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+26",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moonandcloudy))};

        dataHours = new DataClassOfHours[]{
                new DataClassOfHours(dfHour.format(cPlusOneHour.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusTwoHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusThreeHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusFourHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysun),
                        "+22"),
                new DataClassOfHours(dfHour.format(cPlusFiveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+22"),
                new DataClassOfHours(dfHour.format(cPlusSixHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+23"),
                new DataClassOfHours(dfHour.format(cPlusSevenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusEightHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusNineHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusTenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusElevenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusTwelveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy),
                        "+21")};
    }

    /*Метод рандомного заполнения погоды в Париже*/
    private void setWeatherForParis() {
        String currentTemp;
        String windNow;
        String pressureNow;
        currentTemp = "+33";
        textTempCurrent.setText(currentTemp);
        imageTypsWeather.setImageResource(R.drawable.cloudysun);
        windNow = "1.0";
        pressureNow = "758";
        textWindNow.setText(windNow);
        textPressureNow.setText(pressureNow);

        dataDays = new DataClassOfDays[]{
                new DataClassOfDays(df.format(cDayPlusOne.getTime()), "+27",
                        ContextCompat.getDrawable(Objects.requireNonNull(getActivity()),R.drawable.sun),
                        "+20",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon)),
                new DataClassOfDays(df.format(cDayPlusTwo.getTime()), "+27",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+22",
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy)),
                new DataClassOfDays(df.format(cDayPlusThree.getTime()), "+28",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+23",
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy)),
                new DataClassOfDays(df.format(cDayPlusFour.getTime()), "+26",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moonandcloudy),
                        "+22",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun)),
                new DataClassOfDays(df.format(cDayPlusFive.getTime()), "+28",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+21",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun)),
                new DataClassOfDays(df.format(cDayPlusSix.getTime()), "+27",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+24",
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun)),
                new DataClassOfDays(df.format(cDayPlusSeven.getTime()), "+29",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+26",
                        ContextCompat.getDrawable(getActivity(),R.drawable.moonandcloudy))};

        dataHours = new DataClassOfHours[]{
                new DataClassOfHours(dfHour.format(cPlusOneHour.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusTwoHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusThreeHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusFourHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysun),
                        "+22"),
                new DataClassOfHours(dfHour.format(cPlusFiveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+22"),
                new DataClassOfHours(dfHour.format(cPlusSixHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.sun),
                        "+23"),
                new DataClassOfHours(dfHour.format(cPlusSevenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusEightHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusNineHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+20"),
                new DataClassOfHours(dfHour.format(cPlusTenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.moon),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusElevenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudy),
                        "+21"),
                new DataClassOfHours(dfHour.format(cPlusTwelveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(),R.drawable.cloudysunrainy),
                        "+21")};
    }

    /*Метод определения текущего дня и часа */
    @SuppressLint("SimpleDateFormat")
    private void findCurrentHour(int countHoursBetweenForecasts) {
        cPlusOneHour = Calendar.getInstance();
        cPlusOneHour.set(Calendar.MINUTE, 0);
        cPlusOneHour.add(Calendar.HOUR, countHoursBetweenForecasts);

        cPlusTwoHours = Calendar.getInstance();
        cPlusTwoHours.set(Calendar.MINUTE, 0);
        cPlusTwoHours.add(Calendar.HOUR, 2 * countHoursBetweenForecasts) ;

        cPlusThreeHours = Calendar.getInstance();
        cPlusThreeHours.set(Calendar.MINUTE, 0);
        cPlusThreeHours.add(Calendar.HOUR, 3 * countHoursBetweenForecasts);

        cPlusFourHours = Calendar.getInstance();
        cPlusFourHours.set(Calendar.MINUTE, 0);
        cPlusFourHours.add(Calendar.HOUR, 4 * countHoursBetweenForecasts);

        cPlusFiveHours = Calendar.getInstance();
        cPlusFiveHours.set(Calendar.MINUTE, 0);
        cPlusFiveHours.add(Calendar.HOUR, 5 * countHoursBetweenForecasts);

        cPlusSixHours = Calendar.getInstance();
        cPlusSixHours.set(Calendar.MINUTE, 0);
        cPlusSixHours.add(Calendar.HOUR, 6 * countHoursBetweenForecasts);

        cPlusSevenHours = Calendar.getInstance();
        cPlusSevenHours.set(Calendar.MINUTE, 0);
        cPlusSevenHours.add(Calendar.HOUR, 7 * countHoursBetweenForecasts);

        cPlusEightHours = Calendar.getInstance();
        cPlusEightHours.set(Calendar.MINUTE, 0);
        cPlusEightHours.add(Calendar.HOUR, 8 * countHoursBetweenForecasts);

        cPlusNineHours = Calendar.getInstance();
        cPlusNineHours.set(Calendar.MINUTE, 0);
        cPlusNineHours.add(Calendar.HOUR, 9 * countHoursBetweenForecasts);

        cPlusTenHours = Calendar.getInstance();
        cPlusTenHours.set(Calendar.MINUTE, 0);
        cPlusTenHours.add(Calendar.HOUR, 10 * countHoursBetweenForecasts);

        cPlusElevenHours = Calendar.getInstance();
        cPlusElevenHours.set(Calendar.MINUTE, 0);
        cPlusElevenHours.add(Calendar.HOUR, 11 * countHoursBetweenForecasts);

        cPlusTwelveHours = Calendar.getInstance();
        cPlusTwelveHours.set(Calendar.MINUTE, 0);
        cPlusTwelveHours.add(Calendar.HOUR, 12 * countHoursBetweenForecasts);

        dfHour = new SimpleDateFormat("HH:mm");

        cDayPlusOne = Calendar.getInstance();
        cDayPlusOne.add(Calendar.DAY_OF_MONTH, 1);
        cDayPlusTwo = Calendar.getInstance();
        cDayPlusTwo.add(Calendar.DAY_OF_MONTH, 2);
        cDayPlusThree = Calendar.getInstance();
        cDayPlusThree.add(Calendar.DAY_OF_MONTH, 3);
        cDayPlusFour = Calendar.getInstance();
        cDayPlusFour.add(Calendar.DAY_OF_MONTH, 4);
        cDayPlusFive = Calendar.getInstance();
        cDayPlusFive.add(Calendar.DAY_OF_MONTH, 5);
        cDayPlusSix = Calendar.getInstance();
        cDayPlusSix.add(Calendar.DAY_OF_MONTH, 6);
        cDayPlusSeven = Calendar.getInstance();
        cDayPlusSeven.add(Calendar.DAY_OF_MONTH, 7);

        df = new SimpleDateFormat("dd/MM");
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

    private void findViews(View layout) {
        cityNameView = layout.findViewById(R.id.textCity);
        textTempCurrent = layout.findViewById(R.id.textTempCurrent);
        imageTypsWeather = layout.findViewById(R.id.imageTypsWeather);
        textWindNow = layout.findViewById(R.id.textWindNow);
        textPressureNow = layout.findViewById(R.id.textPressureNow);
        imgBtnSettings = layout.findViewById(R.id.imgBtnSettings);
        textUnitWindNow = layout.findViewById(R.id.textUnitWindNow);
        textUnitPressureNow = layout.findViewById(R.id.textUnitPressureNow);
        imgBtnWeatherToYandex = layout.findViewById(R.id.imageBtnWeatherFromYandex);

    }
}

