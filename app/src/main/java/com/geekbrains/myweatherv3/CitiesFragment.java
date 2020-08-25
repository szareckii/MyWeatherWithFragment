package com.geekbrains.myweatherv3;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.geekbrains.myweatherv3.model.SearchRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import static com.geekbrains.myweatherv3.WeatherFragment.PARCEL;

// Фрагмент выбора города из списка
public class CitiesFragment extends Fragment  implements IRVOnItemClick{
    private static final String TAG = "myLogs";
    View rootView;
    Button cityOkBtn;
    private RecyclerView recyclerView;
    private RecyclerDataAdapterForCity adapter;
    boolean isExistWeather;
    TextView newCityNameText;
    Parcel currentParcel;
    Pattern checkCityName = Pattern.compile("^[а-яА-ЯёЁa-zA-Z0-9]+$");
    ArrayList<String> listData;
    LinearLayoutManager layoutManager;
    private static final String WEATHER_URL_FOR_SEARCH = "https://api.openweathermap.org/data/2.5/weather?lang=ru&q=";
    private static final String WEATHER_SET_API_KEY_FOR_SEARCH = "&appid=";
    private static String WEATHER_URL_CITY_FOR_SEARCH;
    float lon;
    float lat;

    // При создании фрагмента укажем его макет
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        findViews();

        setupRecyclerView(savedInstanceState);
        Log.d(TAG, "CitiesFragment. onCreateView()");
        setCityAddClickBehavior();
        //выключим появление клавиатуры при запуске программы
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setChooseCityBtnEnterClickBehavior();
        return rootView;
    }

    /*Метод добавления нового города в список если он удовлетворяет регулярному выражению*/
    private void setCityAddClickBehavior() {
        cityOkBtn.setOnClickListener(view -> {
            String msgError = getString(R.string.check_cityname);
            String newCityName = newCityNameText.getText().toString();
            if (checkCityName.matcher(newCityName).matches()) {    // Проверим на основе регулярных выражений
                hideError(newCityNameText);
                Snackbar.make(view, getString(R.string.add_city_msg) + " " + newCityName + "?",  Snackbar.LENGTH_LONG)
                        .setAction(R.string.add_new_city_snackbar, v -> {
                            Log.d(TAG, "CitiesFragment. Add new city: " + newCityName);
                            WEATHER_URL_CITY_FOR_SEARCH = newCityName;
                            setFindCityOnOpenweathermap(newCityName);
                            newCityNameText.setText("");
                        }).show();
            } else {
                showError(newCityNameText, msgError);
            }
        });
    }

    // Показать ошибку
    private void showError(TextView view, String message) {
        view.setError(message);
    }

    // спрятать ошибку
    private void hideError(TextView view) {
        view.setError(null);
    }

    // Инициализация полей
    private void findViews() {
        recyclerView = rootView.findViewById(R.id.recyclerView);
        cityOkBtn = rootView.findViewById(R.id.cityOkButton);
        newCityNameText = rootView.findViewById(R.id.inputNewCityName);
    }

    private void setupRecyclerView(Bundle savedInstanceState) {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);

        if (savedInstanceState != null) {
            currentParcel = (Parcel) savedInstanceState.getSerializable("CurrentCity");
            assert currentParcel != null;
            listData = currentParcel.getData();

        } else {
            String[] cities = getResources().getStringArray(R.array.cities);
            listData = new ArrayList<>(Arrays.asList(cities));
        }

        adapter = new RecyclerDataAdapterForCity(listData, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //Метод обработки нажатия по городу из списка
    @Override
    public void onItemClicked(String itemText) {
        Snackbar.make(Objects.requireNonNull(getView()), getString(R.string.choose_city_snackbar) + " " + itemText + "?",  Snackbar.LENGTH_LONG)
                .setAction(R.string.ok_button, v -> {
                    Log.d(TAG, "RecyclerDataAdapter. setOnClickForItem() - " + itemText);
                    boolean visibleWind = currentParcel.isVisibleWind();
                    boolean visiblePressure = currentParcel.isVisiblePressure();
                    boolean darkTheme = currentParcel.isDarkTheme();
                    int countHoursBetweenForecasts = currentParcel.getCountHoursBetweenForecasts();
                    currentParcel = new Parcel(itemText, visibleWind, visiblePressure, countHoursBetweenForecasts, darkTheme, listData, lon, lat);
                    showWeather(currentParcel);
                }).show();
    }

//     activity создана, можно к ней обращаться. Выполним начальные действия
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Определение, можно ли будет расположить рядом погоду в другом фрагменте
        isExistWeather = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;

        // Если это не первое создание, то восстановим текущую позицию
        if (savedInstanceState != null) {
            // Восстановление текущей позиции.
            currentParcel = (Parcel) savedInstanceState.getSerializable("CurrentCity");
        } else {
            //+ Если востановить не удалось, то сделаем объект с первым индексом
            currentParcel = new Parcel(getResources().getStringArray(R.array.cities)[0], true,
                    true, 1, false, listData,
                    37.62f, 55.75f);
        }

        // Если можно нарисовать рядом погоду, то сделаем это
        if (isExistWeather) {
            showWeather(currentParcel);
        }
    }


    /*Метод выбора города по нажатию по Enter*/
    private void setChooseCityBtnEnterClickBehavior() {
        newCityNameText.setOnKeyListener((view, i, keyEvent) -> {
            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                    (i == KeyEvent.KEYCODE_ENTER)) {
                cityOkBtn.callOnClick();
                return true;
            }
            return false;
        });
    }

    // Сохраним текущую позицию (вызывается перед выходом из фрагмента)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //+ Также меняем текущую позицию на Parcel
        outState.putSerializable("CurrentCity", currentParcel);
        super.onSaveInstanceState(outState);
    }

    // Показать погоду. Если возможно, то показать рядом со списком,
    // если нет, то открыть вторую activity
    private void showWeather(Parcel parcel) {
        if (isExistWeather) {
            // Проверим, что фрагмент с погодой существует в activity
            assert getFragmentManager() != null;
            WeatherFragment detail = (WeatherFragment)
                    getFragmentManager().findFragmentById(R.id.coat_of_arms);
            // Если есть необходимость, то выведем погоду
            if (detail == null || !detail.getParcel().getCityName().equals(parcel.getCityName())) {
                // Создаем новый фрагмент с текущей позицией для вывода погоды
                detail = WeatherFragment.create(parcel);

                // Выполняем транзакцию по замене фрагмента
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.coat_of_arms, detail);  // замена фрагмента
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        } else {
            // Если нельзя вывести погоду рядом, откроем вторую activity
            Intent intent = new Intent();
            intent.setClass(Objects.requireNonNull(getActivity()), WeatherActivity.class);
            intent.putExtra(PARCEL, parcel);
            startActivity(intent);
        }
    }

    /*Поиск города на сайте погоды*/
    private void setFindCityOnOpenweathermap(String cityName) {
        try {
            final URL uri = new URL(WEATHER_URL_FOR_SEARCH + WEATHER_URL_CITY_FOR_SEARCH +
                    WEATHER_SET_API_KEY_FOR_SEARCH + BuildConfig.WEATHER_API_KEY);
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
                    final SearchRequest searchRequest = gson.fromJson(result, SearchRequest.class);
                    // Возвращаемся к основному потоку
                    handler.post(() -> searchCity(searchRequest, cityName));
                } catch (Exception e) {
                    Log.e(TAG, "Fail connection", e);

                    Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), R.string.сity_not_found,  Snackbar.LENGTH_LONG)
                            .setAction(R.string.ok_button, v -> {
                            }).show();

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

    /*Метод получения координат нового города*/
    private void searchCity(SearchRequest searchRequest, String cityName) {
        lon = Math.round(searchRequest.getCoord().getLon());
        lat = Math.round(searchRequest.getCoord().getLat());

        adapter.add(cityName);
        onItemClicked(cityName);
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

}
