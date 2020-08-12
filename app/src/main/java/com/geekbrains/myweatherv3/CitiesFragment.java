package com.geekbrains.myweatherv3;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.geekbrains.myweatherv3.WeatherFragment.PARCEL;

// Фрагмент выбора города из списка
public class CitiesFragment extends Fragment  implements IRVOnItemClick{
    private static final String TAG = "myLogs";
    View rootView;
    private RecyclerView recyclerView;

    boolean isExistWheather;
    Parcel currentParcel;

    // При создании фрагмента укажем его макет
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        findViews();
        setupRecyclerView();
        Log.d(TAG, "CitiesFragment. onCreateView()");
        return rootView;
    }

    private void findViews() {
        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void setupRecyclerView() {

        String[] cities = getResources().getStringArray(R.array.cities);
        ArrayList<String> listData = new ArrayList<>(Arrays.asList(cities));

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerDataAdapterForCity adapter = new RecyclerDataAdapterForCity(listData, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onItemClicked(String itemText) {
        Log.d(TAG, "RecyclerDataAdapter. setOnClickForItem() - " + itemText);
        boolean visibleWind = currentParcel.isVisibleWind();
        boolean visiblePressure = currentParcel.isVisiblePressure();
        int countHoursBetweenForecasts = currentParcel.getCountHoursBetweenForecasts();
        currentParcel = new Parcel(itemText, visibleWind, visiblePressure, countHoursBetweenForecasts);
        showWeather(currentParcel);

    }

//     activity создана, можно к ней обращаться. Выполним начальные действия
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Определение, можно ли будет расположить рядом погоду в другом фрагменте
        isExistWheather = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;

        // Если это не первое создание, то восстановим текущую позицию
        if (savedInstanceState != null) {
            // Восстановление текущей позиции.
            currentParcel = (Parcel) savedInstanceState.getSerializable("CurrentCity");
        } else {
            //+ Если восcтановить не удалось, то сделаем объект с первым индексом
            currentParcel = new Parcel(getResources().getStringArray(R.array.cities)[0], true, true, 1);
        }

        // Если можно нарисовать рядом погоду, то сделаем это
        if (isExistWheather) {
            showWeather(currentParcel);
        }
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
        if (isExistWheather) {
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
}
