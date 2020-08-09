package com.geekbrains.myweatherv3;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import static com.geekbrains.myweatherv3.WeatherFragment.PARCEL;

// Фрагмент выбора города из списка
public class CitiesFragment extends Fragment {
    boolean isExistWheather;
    Parcel currentParcel;

    // При создании фрагмента укажем его макет
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initList(view);
    }

    // activity создана, можно к ней обращаться. Выполним начальные действия
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Определение, можно ли будет расположить рядом герб в другом фрагменте
        isExistWheather = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;

        // Если это не первое создание, то восстановим текущую позицию
        if (savedInstanceState != null) {
            // Восстановление текущей позиции.
            currentParcel = (Parcel) savedInstanceState.getSerializable("CurrentCity");
        } else {
            //+ Если восcтановить не удалось, то сделаем объект с первым индексом
            currentParcel = new Parcel(getResources().getStringArray(R.array.cities)[0], true, true, 3);
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

    // создаем список городов на экране из массива в ресурсах
    private void initList(View view) {
        LinearLayout layoutView = (LinearLayout)view;
        String[] cities = getResources().getStringArray(R.array.cities);

        // В этом цикле создаем элемент TextView,
        // заполняем его значениями,
        // и добавляем на экран.
        // Кроме того, создаем обработку касания на элемент
        for(int i=0; i < cities.length; i++){
            String city = cities[i];
            TextView tv = new TextView(getContext());
            tv.setText(city);
            tv.setTextSize(30);
            tv.setPadding(30,10,0,10);
            layoutView.addView(tv);
            final int fi = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean visibleWind = currentParcel.isVisibleWind();
                    boolean visiblePressure = currentParcel.isVisiblePressure();
                    int countHoursBetweenForecasts = currentParcel.getCountHoursBetweenForecasts();
                    currentParcel = new Parcel(getResources().getStringArray(R.array.cities)[fi], visibleWind, visiblePressure, countHoursBetweenForecasts);
//                    currentParcel = new Parcel(getResources().getStringArray(R.array.cities)[fi], true, true, 3);
                    showWeather(currentParcel);
                }
            });
        }
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
