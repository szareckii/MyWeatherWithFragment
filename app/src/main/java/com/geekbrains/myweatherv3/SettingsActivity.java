package com.geekbrains.myweatherv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity  implements SeekBar.OnSeekBarChangeListener{
    private ImageButton imgBtnGoBack;
    private String cityName;
    private TextView editNameChooseCity;
    private TextView textValueCountHoursBetweenForecasts;
    private SeekBar seekBarCountHoursBetweenForecasts;
    private static final String TAG = "myLogs";
    private int countHoursBetweenForecasts = 3;
    private TextView textBtnMoscow;
    private TextView textBtnLondon;
    private TextView textBtnNewYork;
    private CheckBox checkBoxSetVisibleWind;
    private CheckBox checkBoxSetVisiblePressure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findView();
        textValueCountHoursBetweenForecasts.setText("3");

        String instanceState;
        if (savedInstanceState == null){
            instanceState = "Первый запуск!";
        }
        else{
            instanceState = "Повторный запуск!";
        }

        Log.d(TAG, "SettingsAct." + instanceState + " - onCreate()");

        seekBarCountHoursBetweenForecasts.setOnSeekBarChangeListener(this);
        setBackBtnClickBehavior();
        setChooseCityBtnEnterClickBehavior();
        textBtnMoscow.setOnClickListener(new cityNameBtnClickListener());
        textBtnLondon.setOnClickListener(new cityNameBtnClickListener());
        textBtnNewYork.setOnClickListener(new cityNameBtnClickListener());

        setSettings();

    }
    /*Метод получения из ActiveMain и выставление текущих настроек*/
    private void setSettings() {
        cityName = Objects.requireNonNull(getIntent().getExtras()).getString("CityName");
        editNameChooseCity.setText(cityName);

        if (getIntent().getExtras().getBoolean("WindyVisible")) {
            checkBoxSetVisibleWind.setChecked(true);
        } else {
            checkBoxSetVisibleWind.setChecked(false);
        }

        if(getIntent().getExtras().getBoolean("PressureVisible")) {
            checkBoxSetVisiblePressure.setChecked(true);
        } else {
            checkBoxSetVisiblePressure.setChecked(false);
        }
    }

    /*Метод-слушатель нажатия по кнопкам с названиями городов */
    public class cityNameBtnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            TextView tv = (TextView)view;
            switch (tv.getId()) {
                case R.id.textCityMoscow: editNameChooseCity.setText(textBtnMoscow.getText().toString());
                    break;
                case R.id.textCityLondon: editNameChooseCity.setText(textBtnLondon.getText().toString());
                    break;
                case R.id.textCityNewYork: editNameChooseCity.setText(textBtnNewYork.getText().toString());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    /*Т.к. у SeekBar минимальное значение 0 (до 26 API- ненастраиваемое), то делаем поправку на 1,
    чтобы минимальное значение получилось равное 1*/
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        textValueCountHoursBetweenForecasts.setText(String.valueOf(seekBar.getProgress() + 1));
        countHoursBetweenForecasts = seekBar.getProgress() + 1;
    }

    @Override
    protected void onRestoreInstanceState(@androidx.annotation.NonNull Bundle saveInstanceState){
        super.onRestoreInstanceState(saveInstanceState);
        Log.d(TAG, "SettingsAct. Повторный запуск!! - onRestoreInstanceState()");
        countHoursBetweenForecasts = saveInstanceState.getInt("CountHoursBetweenForecasts");              // Восстанавливаем количество часов между прогнозами
        textValueCountHoursBetweenForecasts.setText(String.valueOf(countHoursBetweenForecasts));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle saveInstanceState){
        super.onSaveInstanceState(saveInstanceState);
        Log.d(TAG, "SettingsAct. onSaveInstanceState()");
        saveInstanceState.putInt("CountHoursBetweenForecasts", countHoursBetweenForecasts); // Сохраняем количество часов между прогнозами
    }

    /*Метод возврата на Activity_Main*/
    private void setBackBtnClickBehavior() {
        imgBtnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentResult = new Intent();
                if (!editNameChooseCity.getText().toString().equals("")) {
                    cityName = editNameChooseCity.getText().toString();
                }

                if (cityName != null) {
                    intentResult.putExtra("CityName", cityName);
                    Log.d(TAG, "SettingsAct. CityName: " + cityName);
                }

                if(checkBoxSetVisibleWind.isChecked()){
                    intentResult.putExtra("windyVisible", true);
                    Log.d(TAG, "SettingsAct. WindyVisible: true");
                }
                else {
                    intentResult.putExtra("windyVisible", false);
                    Log.d(TAG, "SettingsAct. WindyVisible: false");
                }

                if(checkBoxSetVisiblePressure.isChecked()){
                    intentResult.putExtra("pressureVisible", true);
                    Log.d(TAG, "SettingsAct. PressureVisible: true");
                }
                else {
                    intentResult.putExtra("pressureVisible", false);
                    Log.d(TAG, "SettingsAct. PressureVisible: false");
                }

                setResult(RESULT_OK, intentResult);
                finish();
            }
        });
    }

    /*Метод выбора города по нажатию по Enter*/
    private void setChooseCityBtnEnterClickBehavior() {
        editNameChooseCity.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    cityName = editNameChooseCity.getText().toString();
                    return true;
                }
                return false;
            }
        });
    }

    /*Метод инициализации полей из ресурсов*/
    private void findView() {
        imgBtnGoBack = findViewById(R.id.imgBtnGoBack);
        editNameChooseCity = findViewById(R.id.editNameChooseCity);
        textValueCountHoursBetweenForecasts = findViewById(R.id.textValueCountHoursBetweenForecasts);
        seekBarCountHoursBetweenForecasts = findViewById(R.id.seekBarCountHoursBetweenForecasts);
        textBtnMoscow = findViewById(R.id.textCityMoscow);
        textBtnLondon = findViewById(R.id.textCityLondon);
        textBtnNewYork = findViewById(R.id.textCityNewYork);
        checkBoxSetVisibleWind = findViewById(R.id.checkBoxSetVisibleWind);
        checkBoxSetVisiblePressure = findViewById(R.id.checkBoxSetVisiblePressure);
    }
}
