package com.geekbrains.myweatherv3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.geekbrains.myweatherv3.model.WeatherRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class WeatherFragment extends Fragment {
    public static final String PARCEL = "parcel";
    private String cityName;
    private TextView textTempCurrent;
    private ImageView imageTypsWeather;
    private TextView textWindNow;
    private TextView textPressureNow;
    private TextView textWindDegree;
    private ImageView imgBtnSettings;
    private static final String TAG = "myLogs";
    private static final  int REQUEST_CODE = 1;
    private TextView textUnitWind;
    private TextView textUnitPressureNow;
    private boolean windyVisible;
    private boolean pressureVisible;
    private int countHoursBetweenForecasts;
    private ImageView imgBtnWeatherToYandex;
    private TextView cityNameView;
    private Calendar cDayPlusOne, cDayPlusTwo, cDayPlusThree, cDayPlusFour, cDayPlusFive, cDayPlusSix,
             cDayPlusSeven, cPlusOneHour, cPlusTwoHours, cPlusThreeHours, cPlusFourHours,
             cPlusFiveHours, cPlusSixHours, cPlusSevenHours, cPlusEightHours, cPlusNineHours,
             cPlusTenHours, cPlusElevenHours, cPlusTwelveHours, cPlusThirteenHours, cPlusFourteenHours,
            cPlusFifteenHours, cPlusSixteenHours, cPlusSeventeenHours, cPlusEighteenHours,
            cPlusNineteenHours, cPlusTwentyHours, cPlusTwentyOneHours, cPlusTwentyTwoHours,
            cPlusTwentyThreeHours, cPlusTwentyFourHours, cPlusTwentyFiveHours, cPlusTwentySixHours,
            cPlusTwentySevenHours, cPlusTwentyEightHours, cPlusTwentyNineHours, cPlusThirtyHours ,
            cPlusThirtyOneHours, cPlusThirtyTwoHours, cPlusThirtyThreeHours, cPlusThirtyFfourHours,
            cPlusThirtyFiveHours, cPlusThirtySixHours;
    private DateFormat df, dfHour;
    private ArrayList<DataClassOfHours> listHours;
    private ArrayList<DataClassOfDays> listDays;

    private RecyclerView daysRecyclerView;

    private RecyclerView hoursRecyclerView;
    private Parcel parcel;
    private boolean darkTheme;
    int a01n, a02n, a03n, a04n, a09n, a10n, a11n, a13n, a50n;
    int a01d, a02d, a03d, a04d, a09d, a10d, a11d, a13d, a50d;

    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/onecall?exclude=minutely&units=metric&appid=";
    private static String WEATHER_URL_CITY = "&lat=55.75&lon=37.62"; //по умолчанию - Москва

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
        cityNameView.setText(cityName);

        getGeoFromCityName(parcel.getLon(), parcel.getLat());

        windyVisible = parcel.isVisibleWind();
        pressureVisible = parcel.isVisiblePressure();
        countHoursBetweenForecasts = parcel.getCountHoursBetweenForecasts();
        darkTheme = parcel.isDarkTheme();

        findCurrentHour();

        if (!WEATHER_URL_CITY.equals("")) {
            setWhether(countHoursBetweenForecasts);
        }

        setupRecyclerViewDays(listDays);
        setupRecyclerViewHours(listHours);

        setVisiblePressure(pressureVisible);
        setVisibleWindy(windyVisible);

        setSettingsBtnClickBehavior();
        setYandexBtnClickBehavior();


        return layout;
    }

    /*Метод получения локкации города по ему имени (на openweathermap погода за 7 дней ищется только
     по координатам)*/
    private void getGeoFromCityName(float lon, float lat) {
        if (lat != 0.0f) {
            WEATHER_URL_CITY = "&lat=" + lat + "&lon=" + lon;
        }

        switch (cityName) {
            case "Moscow":
            case "Москва":
                WEATHER_URL_CITY = "&lat=55.75&lon=37.62";
                break;
            case "London":
            case "Лондон":
                WEATHER_URL_CITY = "&lat=51.50853&lon=-0.12574";
                break;
            case "New York":
            case "Нью-Йорк":
                WEATHER_URL_CITY = "&lat=43.000351&lon=-75.499901";
                break;
            case "Beijing":
            case "Пекин":
                WEATHER_URL_CITY = "&lat=39.907501&lon=116.397232";
                break;
            case "Paris":
            case "Париж":
                WEATHER_URL_CITY = "&lat=48.853401&lon=2.3486";
                break;
        }
    }

    /*Метод заполнения RecyclerView по часам*/
    private void setupRecyclerViewHours(ArrayList<DataClassOfHours> list) {
        hoursRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        RecyclerDataAdapterForHours hoursAdapter = new RecyclerDataAdapterForHours(list);
        hoursRecyclerView.setLayoutManager(layoutManager);
        hoursRecyclerView.setAdapter(hoursAdapter);
    }

    /*Метод заполнения RecyclerView по дням*/
    private void setupRecyclerViewDays(ArrayList<DataClassOfDays> list) {
        //если ориентация горизонтальная, то переопределяем layoutManager для выключения скрола у
        // RecyclerView

        if (Objects.requireNonNull(this.getContext()).getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(getActivity());
            layoutManager.setScrollEnabled(false);
            daysRecyclerView.setLayoutManager(layoutManager);

        } else {
            LinearLayoutManager layoutManager;
            layoutManager = new LinearLayoutManager(getActivity());
            daysRecyclerView.setLayoutManager(layoutManager);
        }

        daysRecyclerView.setHasFixedSize(true);
        RecyclerDataAdapterForDays daysAdapter = new RecyclerDataAdapterForDays(list);
        daysRecyclerView.setAdapter(daysAdapter);
    }

    /*Метод открытия окна с настройками*/
    private void setSettingsBtnClickBehavior() {
        imgBtnSettings.setOnClickListener(view -> {
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

            if (darkTheme) {
                intent.putExtra("DarkTheme", true);
            } else {
                intent.putExtra("DarkTheme", false);
            }

            startActivityForResult(intent, REQUEST_CODE);
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
            darkTheme =  data.getBooleanExtra("darkTheme", false);

            setVisibleWindy(windyVisible);
            setVisiblePressure(pressureVisible);

            parcel.setVisibleWind(windyVisible);
            parcel.setVisiblePressure(pressureVisible);
            parcel.setCountHoursBetweenForecasts(countHoursBetweenForecasts);
            parcel.setDarkTheme(darkTheme);

            Log.d(TAG, "WeatherFragment. WindyVisible - " + windyVisible);
            Log.d(TAG, "WeatherFragment. PressureVisible - " + pressureVisible);
            Log.d(TAG, "WeatherFragment. DarkTheme - " + darkTheme);
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
            textUnitWind.setVisibility(View.GONE);
            textWindDegree.setVisibility(View.GONE);
        }
        else {
            textWindNow.setVisibility(View.VISIBLE);
            textUnitWind.setVisibility(View.VISIBLE);
            textWindDegree.setVisibility(View.VISIBLE);
        }
    }

    /*Метод определения текущего дня и часа */
    @SuppressLint("SimpleDateFormat")
    private void findCurrentHour() {
        cPlusOneHour = Calendar.getInstance();
        cPlusOneHour.set(Calendar.MINUTE, 0);
        cPlusOneHour.add(Calendar.HOUR, 1);

        cPlusTwoHours = Calendar.getInstance();
        cPlusTwoHours.set(Calendar.MINUTE, 0);
        cPlusTwoHours.add(Calendar.HOUR, 2) ;

        cPlusThreeHours = Calendar.getInstance();
        cPlusThreeHours.set(Calendar.MINUTE, 0);
        cPlusThreeHours.add(Calendar.HOUR, 3);

        cPlusFourHours = Calendar.getInstance();
        cPlusFourHours.set(Calendar.MINUTE, 0);
        cPlusFourHours.add(Calendar.HOUR, 4);

        cPlusFiveHours = Calendar.getInstance();
        cPlusFiveHours.set(Calendar.MINUTE, 0);
        cPlusFiveHours.add(Calendar.HOUR, 5);

        cPlusSixHours = Calendar.getInstance();
        cPlusSixHours.set(Calendar.MINUTE, 0);
        cPlusSixHours.add(Calendar.HOUR, 6);

        cPlusSevenHours = Calendar.getInstance();
        cPlusSevenHours.set(Calendar.MINUTE, 0);
        cPlusSevenHours.add(Calendar.HOUR, 7);

        cPlusEightHours = Calendar.getInstance();
        cPlusEightHours.set(Calendar.MINUTE, 0);
        cPlusEightHours.add(Calendar.HOUR, 8);

        cPlusNineHours = Calendar.getInstance();
        cPlusNineHours.set(Calendar.MINUTE, 0);
        cPlusNineHours.add(Calendar.HOUR, 9);

        cPlusTenHours = Calendar.getInstance();
        cPlusTenHours.set(Calendar.MINUTE, 0);
        cPlusTenHours.add(Calendar.HOUR, 10);

        cPlusElevenHours = Calendar.getInstance();
        cPlusElevenHours.set(Calendar.MINUTE, 0);
        cPlusElevenHours.add(Calendar.HOUR, 11);

        cPlusTwelveHours = Calendar.getInstance();
        cPlusTwelveHours.set(Calendar.MINUTE, 0);
        cPlusTwelveHours.add(Calendar.HOUR, 12);

        cPlusThirteenHours = Calendar.getInstance();
        cPlusThirteenHours.set(Calendar.MINUTE, 0);
        cPlusThirteenHours.add(Calendar.HOUR, 13);

        cPlusFourteenHours = Calendar.getInstance();
        cPlusFourteenHours.set(Calendar.MINUTE, 0);
        cPlusFourteenHours.add(Calendar.HOUR, 14) ;

        cPlusFifteenHours = Calendar.getInstance();
        cPlusFifteenHours.set(Calendar.MINUTE, 0);
        cPlusFifteenHours.add(Calendar.HOUR, 15);

        cPlusSixteenHours = Calendar.getInstance();
        cPlusSixteenHours.set(Calendar.MINUTE, 0);
        cPlusSixteenHours.add(Calendar.HOUR, 16);

        cPlusSeventeenHours = Calendar.getInstance();
        cPlusSeventeenHours.set(Calendar.MINUTE, 0);
        cPlusSeventeenHours.add(Calendar.HOUR, 17);

        cPlusEighteenHours = Calendar.getInstance();
        cPlusEighteenHours.set(Calendar.MINUTE, 0);
        cPlusEighteenHours.add(Calendar.HOUR, 18);

        cPlusNineteenHours = Calendar.getInstance();
        cPlusNineteenHours.set(Calendar.MINUTE, 0);
        cPlusNineteenHours.add(Calendar.HOUR, 19);

        cPlusTwentyHours = Calendar.getInstance();
        cPlusTwentyHours.set(Calendar.MINUTE, 0);
        cPlusTwentyHours.add(Calendar.HOUR, 20);

        cPlusTwentyOneHours = Calendar.getInstance();
        cPlusTwentyOneHours.set(Calendar.MINUTE, 0);
        cPlusTwentyOneHours.add(Calendar.HOUR, 21);

        cPlusTwentyTwoHours = Calendar.getInstance();
        cPlusTwentyTwoHours.set(Calendar.MINUTE, 0);
        cPlusTwentyTwoHours.add(Calendar.HOUR, 22);

        cPlusTwentyThreeHours = Calendar.getInstance();
        cPlusTwentyThreeHours.set(Calendar.MINUTE, 0);
        cPlusTwentyThreeHours.add(Calendar.HOUR, 23);

        cPlusTwentyFourHours = Calendar.getInstance();
        cPlusTwentyFourHours.set(Calendar.MINUTE, 0);
        cPlusTwentyFourHours.add(Calendar.HOUR, 24);

        cPlusTwentyFiveHours = Calendar.getInstance();
        cPlusTwentyFiveHours.set(Calendar.MINUTE, 0);
        cPlusTwentyFiveHours.add(Calendar.HOUR, 25);

        cPlusTwentySixHours = Calendar.getInstance();
        cPlusTwentySixHours.set(Calendar.MINUTE, 0);
        cPlusTwentySixHours.add(Calendar.HOUR, 26) ;

        cPlusTwentySevenHours = Calendar.getInstance();
        cPlusTwentySevenHours.set(Calendar.MINUTE, 0);
        cPlusTwentySevenHours.add(Calendar.HOUR, 27);

        cPlusTwentyEightHours = Calendar.getInstance();
        cPlusTwentyEightHours.set(Calendar.MINUTE, 0);
        cPlusTwentyEightHours.add(Calendar.HOUR, 28);

        cPlusTwentyNineHours = Calendar.getInstance();
        cPlusTwentyNineHours.set(Calendar.MINUTE, 0);
        cPlusTwentyNineHours.add(Calendar.HOUR, 29);

        cPlusThirtyHours = Calendar.getInstance();
        cPlusThirtyHours.set(Calendar.MINUTE, 0);
        cPlusThirtyHours.add(Calendar.HOUR, 30);

        cPlusThirtyOneHours = Calendar.getInstance();
        cPlusThirtyOneHours.set(Calendar.MINUTE, 0);
        cPlusThirtyOneHours.add(Calendar.HOUR, 31);

        cPlusThirtyTwoHours = Calendar.getInstance();
        cPlusThirtyTwoHours.set(Calendar.MINUTE, 0);
        cPlusThirtyTwoHours.add(Calendar.HOUR, 32);

        cPlusThirtyThreeHours = Calendar.getInstance();
        cPlusThirtyThreeHours.set(Calendar.MINUTE, 0);
        cPlusThirtyThreeHours.add(Calendar.HOUR, 33);

        cPlusThirtyFfourHours = Calendar.getInstance();
        cPlusThirtyFfourHours.set(Calendar.MINUTE, 0);
        cPlusThirtyFfourHours.add(Calendar.HOUR, 34);

        cPlusThirtyFiveHours = Calendar.getInstance();
        cPlusThirtyFiveHours.set(Calendar.MINUTE, 0);
        cPlusThirtyFiveHours.add(Calendar.HOUR, 35);

        cPlusThirtySixHours = Calendar.getInstance();
        cPlusThirtySixHours.set(Calendar.MINUTE, 0);
        cPlusThirtySixHours.add(Calendar.HOUR, 36);

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
        imgBtnWeatherToYandex.setOnClickListener(view -> {
            Uri uri = Uri.parse(findYandexWeatherHttp(cityName));
            Intent browser = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(browser);
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

    /*Метод получения погоды из JSON"*/
    private void setWhether(int countHoursBetweenForecasts) {
        try {
            final URL uri = new URL(WEATHER_URL + BuildConfig.WEATHER_API_KEY + WEATHER_URL_CITY);
            Log.e(TAG, "URI: " + uri);
            final Handler handler = new Handler(); // Запоминаем основной поток
            new Thread(() -> {
                HttpsURLConnection urlConnection = null;
                try {

                    /*Настройки дла соединения с ПРОКСИ*/
//                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.1.1", 111));
//                    urlConnection = (HttpsURLConnection) uri.openConnection(proxy);
                    urlConnection = (HttpsURLConnection) uri.openConnection();


                    urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
                    urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
                    Log.e(TAG, "Connect: true");
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
                    String result = getLines(in);
                    // преобразование данных запроса в модель
                    Log.e(TAG, "getLines() result: true");
                    Gson gson = new Gson();
                    final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
                    // Возвращаемся к основному потоку
                    handler.post(() -> displayWeather(weatherRequest, countHoursBetweenForecasts));
                } catch (Exception e) {
                    Log.e(TAG, "Fail connection", e);

                    Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), R.string.fail_connection,  Snackbar.LENGTH_LONG)
                            .setAction(R.string.ok_button, v -> setClearTextView()).show();

                    e.printStackTrace();
                } finally {
                    if (null != urlConnection) {
                        urlConnection.disconnect();
                    }
                }
            }).start();
        } catch (MalformedURLException e) {
            Log.e(TAG, "Fail URI", e);
            e.printStackTrace();
            }
    }

    private String getLines(BufferedReader in) {

        StringBuilder rawData = new StringBuilder(1024);
        String tempVariable;

        while (true) {
            try {
                tempVariable = in.readLine();
                if (tempVariable == null) break;
                rawData.append(tempVariable).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rawData.toString();
    }

    /*Метод записи погоды в View"*/
    private void displayWeather(WeatherRequest weatherRequest, int hoursBetweenForecasts){
        int ft =  Math.round(weatherRequest.getCurrent().getTemp());

        textTempCurrent.setText(getStringTemp(ft));

        String wind = String.valueOf(weatherRequest.getCurrent().getWind_speed());
        textWindNow.setText(wind);

        String pressure = String.valueOf((int) Math.round(weatherRequest.getCurrent().getPressure() / 1.333));
        textPressureNow.setText(pressure);

        int degree = weatherRequest.getCurrent().getWind_deg();

        textWindDegree.setText(getDegreeWind(degree));

        imageTypsWeather.setImageResource(setIconWeather(weatherRequest, "Current", 0));

        textUnitWind.setText(R.string.textUnitWind);
        textUnitPressureNow.setText(R.string.textUnitPressureNow);



        /*Температура на 12 часов*/
        listHours = new ArrayList<>(48);

        /*почасова погода на 36, т.к. 12 почасовых прогнозо с возможностью выбора между прогнозами
        1, 2 или 3 часа, т.е. 12*3*/
        int temp1_hour = Math.round(weatherRequest.getHourly()[1].getTemp());
        int temp2_hour = Math.round(weatherRequest.getHourly()[2].getTemp());
        int temp3_hour = Math.round(weatherRequest.getHourly()[3].getTemp());
        int temp4_hour = Math.round(weatherRequest.getHourly()[4].getTemp());
        int temp5_hour = Math.round(weatherRequest.getHourly()[5].getTemp());
        int temp6_hour = Math.round(weatherRequest.getHourly()[6].getTemp());
        int temp7_hour = Math.round(weatherRequest.getHourly()[7].getTemp());
        int temp8_hour = Math.round(weatherRequest.getHourly()[8].getTemp());
        int temp9_hour = Math.round(weatherRequest.getHourly()[9].getTemp());
        int temp10_hour = Math.round(weatherRequest.getHourly()[10].getTemp());
        int temp11_hour = Math.round(weatherRequest.getHourly()[11].getTemp());
        int temp12_hour = Math.round(weatherRequest.getHourly()[12].getTemp());
        int temp13_hour = Math.round(weatherRequest.getHourly()[13].getTemp());
        int temp14_hour = Math.round(weatherRequest.getHourly()[14].getTemp());
        int temp15_hour = Math.round(weatherRequest.getHourly()[15].getTemp());
        int temp16_hour = Math.round(weatherRequest.getHourly()[16].getTemp());
        int temp17_hour = Math.round(weatherRequest.getHourly()[17].getTemp());
        int temp18_hour = Math.round(weatherRequest.getHourly()[18].getTemp());
        int temp19_hour = Math.round(weatherRequest.getHourly()[19].getTemp());
        int temp20_hour = Math.round(weatherRequest.getHourly()[20].getTemp());
        int temp21_hour = Math.round(weatherRequest.getHourly()[21].getTemp());
        int temp22_hour = Math.round(weatherRequest.getHourly()[22].getTemp());
        int temp23_hour = Math.round(weatherRequest.getHourly()[23].getTemp());
        int temp24_hour = Math.round(weatherRequest.getHourly()[24].getTemp());
        int temp25_hour = Math.round(weatherRequest.getHourly()[25].getTemp());
        int temp26_hour = Math.round(weatherRequest.getHourly()[26].getTemp());
        int temp27_hour = Math.round(weatherRequest.getHourly()[27].getTemp());
        int temp28_hour = Math.round(weatherRequest.getHourly()[28].getTemp());
        int temp29_hour = Math.round(weatherRequest.getHourly()[29].getTemp());
        int temp30_hour = Math.round(weatherRequest.getHourly()[30].getTemp());
        int temp31_hour = Math.round(weatherRequest.getHourly()[31].getTemp());
        int temp32_hour = Math.round(weatherRequest.getHourly()[32].getTemp());
        int temp33_hour = Math.round(weatherRequest.getHourly()[33].getTemp());
        int temp34_hour = Math.round(weatherRequest.getHourly()[34].getTemp());
        int temp35_hour = Math.round(weatherRequest.getHourly()[35].getTemp());
        int temp36_hour = Math.round(weatherRequest.getHourly()[36].getTemp());

        DataClassOfHours[] dataHours = new DataClassOfHours[]{
                new DataClassOfHours(dfHour.format(cPlusOneHour.getTime()),
                        ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), setIconWeather(weatherRequest,
                                "Hour", 1)),
                        getStringTemp(temp1_hour)),
                new DataClassOfHours(dfHour.format(cPlusTwoHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 2)),
                        getStringTemp(temp2_hour)),
                new DataClassOfHours(dfHour.format(cPlusThreeHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 3)),
                        getStringTemp(temp3_hour)),
                new DataClassOfHours(dfHour.format(cPlusFourHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 4)),
                        getStringTemp(temp4_hour)),
                new DataClassOfHours(dfHour.format(cPlusFiveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 5)),
                        getStringTemp(temp5_hour)),
                new DataClassOfHours(dfHour.format(cPlusSixHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 6)),
                        getStringTemp(temp6_hour)),
                new DataClassOfHours(dfHour.format(cPlusSevenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 7)),
                        getStringTemp(temp7_hour)),
                new DataClassOfHours(dfHour.format(cPlusEightHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 8)),
                        getStringTemp(temp8_hour)),
                new DataClassOfHours(dfHour.format(cPlusNineHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 9)),
                        getStringTemp(temp9_hour)),
                new DataClassOfHours(dfHour.format(cPlusTenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 10)),
                        getStringTemp(temp10_hour)),
                new DataClassOfHours(dfHour.format(cPlusElevenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 11)),
                        getStringTemp(temp11_hour)),
                new DataClassOfHours(dfHour.format(cPlusTwelveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 12)),
                        getStringTemp(temp12_hour)),
                new DataClassOfHours(dfHour.format(cPlusThirteenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 13)),
                        getStringTemp(temp13_hour)),
                new DataClassOfHours(dfHour.format(cPlusFourteenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 14)),
                        getStringTemp(temp14_hour)),
                new DataClassOfHours(dfHour.format(cPlusFifteenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 15)),
                        getStringTemp(temp15_hour)),
                new DataClassOfHours(dfHour.format(cPlusSixteenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 16)),
                        getStringTemp(temp16_hour)),
                new DataClassOfHours(dfHour.format(cPlusSeventeenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 17)),
                        getStringTemp(temp17_hour)),
                new DataClassOfHours(dfHour.format(cPlusEighteenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 18)),
                        getStringTemp(temp18_hour)),
                new DataClassOfHours(dfHour.format(cPlusNineteenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 19)),
                        getStringTemp(temp19_hour)),
                new DataClassOfHours(dfHour.format(cPlusTwentyHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 20)),
                        getStringTemp(temp20_hour)),
                new DataClassOfHours(dfHour.format(cPlusTwentyOneHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 21)),
                        getStringTemp(temp21_hour)),
                new DataClassOfHours(dfHour.format(cPlusTwentyTwoHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 22)),
                        getStringTemp(temp22_hour)),
                new DataClassOfHours(dfHour.format(cPlusTwentyThreeHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 23)),
                        getStringTemp(temp23_hour)),
                new DataClassOfHours(dfHour.format(cPlusTwentyFourHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 24)),
                        getStringTemp(temp24_hour)),
                new DataClassOfHours(dfHour.format(cPlusTwentyFiveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 25)),
                        getStringTemp(temp25_hour)),
                new DataClassOfHours(dfHour.format(cPlusTwentySixHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 26)),
                        getStringTemp(temp26_hour)),
                new DataClassOfHours(dfHour.format(cPlusTwentySevenHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 27)),
                        getStringTemp(temp27_hour)),
                new DataClassOfHours(dfHour.format(cPlusTwentyEightHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 28)),
                        getStringTemp(temp28_hour)),
                new DataClassOfHours(dfHour.format(cPlusTwentyNineHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 29)),
                        getStringTemp(temp29_hour)),
                new DataClassOfHours(dfHour.format(cPlusThirtyHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 30)),
                        getStringTemp(temp30_hour)),
                new DataClassOfHours(dfHour.format(cPlusThirtyOneHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 31)),
                        getStringTemp(temp31_hour)),
                new DataClassOfHours(dfHour.format(cPlusThirtyTwoHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 32)),
                        getStringTemp(temp32_hour)),
                new DataClassOfHours(dfHour.format(cPlusThirtyThreeHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 33)),
                        getStringTemp(temp33_hour)),
                new DataClassOfHours(dfHour.format(cPlusThirtyFfourHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 34)),
                        getStringTemp(temp34_hour)),
                new DataClassOfHours(dfHour.format(cPlusThirtyFiveHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 35)),
                        getStringTemp(temp35_hour)),
                new DataClassOfHours(dfHour.format(cPlusThirtySixHours.getTime()),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest,
                                "Hour", 36)),
                        getStringTemp(temp36_hour))};

        listHours.clear();
        for (int i = 0; i < 12; i++) {
            listHours.add(dataHours[i * hoursBetweenForecasts]);
        }

        setupRecyclerViewHours(listHours);

        /*Температура на 7 дней*/
        listDays = new ArrayList<>(7);

        int temp1_day =  Math.round(weatherRequest.getDaily()[0].getTemp().getDay());
        int temp1_night =  Math.round(weatherRequest.getDaily()[0].getTemp().getNight());
        int temp2_day =  Math.round(weatherRequest.getDaily()[1].getTemp().getDay());
        int temp2_night =  Math.round(weatherRequest.getDaily()[1].getTemp().getNight());
        int temp3_day =  Math.round(weatherRequest.getDaily()[2].getTemp().getDay());
        int temp3_night =  Math.round(weatherRequest.getDaily()[2].getTemp().getNight());
        int temp4_day =  Math.round(weatherRequest.getDaily()[3].getTemp().getDay());
        int temp4_night =  Math.round(weatherRequest.getDaily()[3].getTemp().getNight());
        int temp5_day =  Math.round(weatherRequest.getDaily()[4].getTemp().getDay());
        int temp5_night =  Math.round(weatherRequest.getDaily()[4].getTemp().getNight());
        int temp6_day =  Math.round(weatherRequest.getDaily()[5].getTemp().getDay());
        int temp6_night =  Math.round(weatherRequest.getDaily()[5].getTemp().getNight());
        int temp7_day =  Math.round(weatherRequest.getDaily()[6].getTemp().getDay());
        int temp7_night =  Math.round(weatherRequest.getDaily()[6].getTemp().getNight());

//        Drawable r = ContextCompat.getDrawable(getActivity(), a01d);

        /*заполения массива погоды на 7 дней*/
        DataClassOfDays[] dataDays = new DataClassOfDays[]{
                new DataClassOfDays(df.format(cDayPlusOne.getTime()), getStringTemp(temp1_day),
                        ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), setIconWeather(weatherRequest, "Day", 1)),
                        getStringTemp(temp1_night)),
                new DataClassOfDays(df.format(cDayPlusTwo.getTime()), getStringTemp(temp2_day),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest, "Day", 2)),
                        getStringTemp(temp2_night)),
                new DataClassOfDays(df.format(cDayPlusThree.getTime()), getStringTemp(temp3_day),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest, "Day", 3)),
                        getStringTemp(temp3_night)),
                new DataClassOfDays(df.format(cDayPlusFour.getTime()), getStringTemp(temp4_day),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest, "Day", 4)),
                        getStringTemp(temp4_night)),
                new DataClassOfDays(df.format(cDayPlusFive.getTime()), getStringTemp(temp5_day),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest, "Day", 5)),
                        getStringTemp(temp5_night)),
                new DataClassOfDays(df.format(cDayPlusSix.getTime()), getStringTemp(temp6_day),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest, "Day", 6)),
                        getStringTemp(temp6_night)),
                new DataClassOfDays(df.format(cDayPlusSeven.getTime()), getStringTemp(temp7_day),
                        ContextCompat.getDrawable(getActivity(), setIconWeather(weatherRequest, "Day", 7)),
                        getStringTemp(temp7_night))};


        listDays.clear();
        listDays.addAll(Arrays.asList(dataDays));
        setupRecyclerViewDays(listDays);
    }

    /* Метод вперевода направления ветра из градусов в стороны света*/
    private String getDegreeWind(int degree) {
        String degreeStr = getResources().getString(R.string.textUnitWindN);

        if ((degree >= 337 && degree <= 360) || (degree >= 0 && degree <= 23)) {
            degreeStr = getResources().getString(R.string.textUnitWindN);
        } else if (degree > 23 && degree < 67) {
            degreeStr = getResources().getString(R.string.textUnitWindNE);
        } else if (degree >= 67 && degree <= 113) {
            degreeStr = getResources().getString(R.string.textUnitWindE);
        } else if (degree > 113 && degree < 157) {
            degreeStr = getResources().getString(R.string.textUnitWindSE);
        } else if (degree >= 157 && degree <= 203) {
            degreeStr = getResources().getString(R.string.textUnitWindS);
        } else if (degree > 203 && degree < 248) {
            degreeStr = getResources().getString(R.string.textUnitWindSW);
        } else if (degree >= 248 && degree <= 293) {
            degreeStr = getResources().getString(R.string.textUnitWindW);
        } else if (degree > 293 && degree < 337) {
            degreeStr = getResources().getString(R.string.textUnitWindNW);
        }
        return degreeStr;
    }

    /*Метод поиска икноки погоды по информации из json*/
    private int setIconWeather(WeatherRequest weatherRequest, String time, int i) {
        int typeWeather = 0;
        if (time.equals("Current")) {
            switch (weatherRequest.getCurrent().getWeather()[0].getIcon()) {
                case "01d":
                    typeWeather = a01d;
                    break;
                case "02d":
                    typeWeather = a02d;
                    break;
                case "03d":
                    typeWeather = a03d;
                    break;
                case "04d":
                    typeWeather = a04d;
                    break;
                case "09d":
                    typeWeather = a09d;
                    break;
                case "10d":
                    typeWeather = a10d;
                    break;
                case "11d":
                    typeWeather = a11d;
                    break;
                case "13d":
                    typeWeather = a13d;
                    break;
                case "50d":
                    typeWeather = a50d;
                    break;
                case "01n":
                    typeWeather = a01n;
                    break;
                case "02n":
                    typeWeather = a02n;
                    break;
                case "03n":
                    typeWeather = a03n;
                    break;
                case "04n":
                    typeWeather = a04n;
                    break;
                case "09n":
                    typeWeather = a09n;
                    break;
                case "10n":
                    typeWeather = a10n;
                    break;
                case "11n":
                    typeWeather = a11n;
                    break;
                case "13n":
                    typeWeather = a13n;
                    break;
                case "50n":
                    typeWeather = a50n;
                    break;
            }
        } else if (time.equals("Day")) {
            switch (weatherRequest.getDaily()[i].getWeather()[0].getIcon()) {
                case "01d":
                    typeWeather = a01d;
                    break;
                case "02d":
                    typeWeather = a02d;
                    break;
                case "03d":
                    typeWeather = a03d;
                    break;
                case "04d":
                    typeWeather = a04d;
                    break;
                case "09d":
                    typeWeather = a09d;
                    break;
                case "10d":
                    typeWeather = a10d;
                    break;
                case "11d":
                    typeWeather = a11d;
                    break;
                case "13d":
                    typeWeather = a13d;
                    break;
                case "50d":
                    typeWeather = a50d;
                    break;
                case "01n":
                    typeWeather = a01n;
                    break;
                case "02n":
                    typeWeather = a02n;
                    break;
                case "03n":
                    typeWeather = a03n;
                    break;
                case "04n":
                    typeWeather = a04n;
                    break;
                case "09n":
                    typeWeather = a09n;
                    break;
                case "10n":
                    typeWeather = a10n;
                    break;
                case "11n":
                    typeWeather = a11n;
                    break;
                case "13n":
                    typeWeather = a13n;
                    break;
                case "50n":
                    typeWeather = a50n;
                    break;
            }
        } else {
            switch (weatherRequest.getHourly()[i].getWeather()[0].getIcon()) {
                case "01d":
                    typeWeather = a01d;
                    break;
                case "02d":
                    typeWeather = a02d;
                    break;
                case "03d":
                    typeWeather = a03d;
                    break;
                case "04d":
                    typeWeather = a04d;
                    break;
                case "09d":
                    typeWeather = a09d;
                    break;
                case "10d":
                    typeWeather = a10d;
                    break;
                case "11d":
                    typeWeather = a11d;
                    break;
                case "13d":
                    typeWeather = a13d;
                    break;
                case "50d":
                    typeWeather = a50d;
                    break;
                case "01n":
                    typeWeather = a01n;
                    break;
                case "02n":
                    typeWeather = a02n;
                    break;
                case "03n":
                    typeWeather = a03n;
                    break;
                case "04n":
                    typeWeather = a04n;
                    break;
                case "09n":
                    typeWeather = a09n;
                    break;
                case "10n":
                    typeWeather = a10n;
                    break;
                case "11n":
                    typeWeather = a11n;
                    break;
                case "13n":
                    typeWeather = a13n;
                    break;
                case "50n":
                    typeWeather = a50n;
                    break;
            }

        }
        return typeWeather;
    }

    /*Метод добавления знака температуре*/
    private String getStringTemp(int ft) {
        String temp;
        if (ft > 0) {
            temp = "+";
        } else {
            temp = "-";
        }
        temp += String.valueOf(ft);
        return temp;
    }

    /*Метод инициализации*/
    private void findViews(View layout) {
        cityNameView = layout.findViewById(R.id.textCity);
        textTempCurrent = layout.findViewById(R.id.textTempCurrent);
        imageTypsWeather = layout.findViewById(R.id.imageTypsWeather);
        textWindNow = layout.findViewById(R.id.textWindNow);
        textPressureNow = layout.findViewById(R.id.textPressureNow);
        imgBtnSettings = layout.findViewById(R.id.imgBtnSettings);
        textUnitWind = layout.findViewById(R.id.textUnitWind);
        textUnitPressureNow = layout.findViewById(R.id.textUnitPressureNow);
        imgBtnWeatherToYandex = layout.findViewById(R.id.imageBtnWeatherFromYandex);
        textWindDegree = layout.findViewById(R.id.textWindDegree);

        a01d = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a01d), "drawable", getActivity().getPackageName());
        a02d = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a02d), "drawable", getActivity().getPackageName());
        a03d = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a03d), "drawable", getActivity().getPackageName());
        a04d = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a04d), "drawable", getActivity().getPackageName());
        a09d = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a09d), "drawable", getActivity().getPackageName());
        a10d = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a10d), "drawable", getActivity().getPackageName());
        a11d = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a11d), "drawable", getActivity().getPackageName());
        a13d = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a13d), "drawable", getActivity().getPackageName());
        a50d = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a50d), "drawable", getActivity().getPackageName());
        a01n = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a01n), "drawable", getActivity().getPackageName());
        a02n = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a02n), "drawable", getActivity().getPackageName());
        a03n = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a03n), "drawable", getActivity().getPackageName());
        a04n = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a04n), "drawable", getActivity().getPackageName());
        a09n = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a09n), "drawable", getActivity().getPackageName());
        a10n = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a10n), "drawable", getActivity().getPackageName());
        a11n = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a11n), "drawable", getActivity().getPackageName());
        a13n = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a13n), "drawable", getActivity().getPackageName());
        a50n = Objects.requireNonNull(getActivity()).getResources().getIdentifier(
                String.valueOf(R.drawable.a50n), "drawable", getActivity().getPackageName());

        setClearTextView();

    }

    private void setClearTextView() {
        textTempCurrent.setText("");
        textWindNow.setText("");
        textUnitWind.setText("");
        textWindDegree.setText("");
        textPressureNow.setText("");
        textUnitPressureNow.setText("");
        imageTypsWeather.setImageResource(R.drawable.close);
    }

}

