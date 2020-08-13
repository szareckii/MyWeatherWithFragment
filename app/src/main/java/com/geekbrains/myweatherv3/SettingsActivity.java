package com.geekbrains.myweatherv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity  implements SeekBar.OnSeekBarChangeListener{
    private TextView textValueCountHoursBetweenForecasts;
    private SeekBar seekBarCountHoursBetweenForecasts;
    private static final String TAG = "myLogs";
    private int countHoursBetweenForecasts = 3;
    private CheckBox checkBoxSetVisibleWind;
    private CheckBox checkBoxSetVisiblePressure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        findView();
        textValueCountHoursBetweenForecasts.setText("1");

        String instanceState;
        if (savedInstanceState == null){
            instanceState = "Первый запуск!";
        }
        else{
            instanceState = "Повторный запуск!";
        }

        Log.d(TAG, "SettingsAct." + instanceState + " - onCreate()");

        seekBarCountHoursBetweenForecasts.setOnSeekBarChangeListener(this);
        setSettings();

    }

    /*Метод нажатия на назад в баре*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intentResult = new Intent();

            if (checkBoxSetVisibleWind.isChecked()) {
                intentResult.putExtra("windyVisible", true);
                Log.d(TAG, "SettingsAct. WindyVisible: true");
            } else {
                intentResult.putExtra("windyVisible", false);
                Log.d(TAG, "SettingsAct. WindyVisible: false");
            }

            if (checkBoxSetVisiblePressure.isChecked()) {
                intentResult.putExtra("pressureVisible", true);
                Log.d(TAG, "SettingsAct. PressureVisible: true");
            } else {
                intentResult.putExtra("pressureVisible", false);
                Log.d(TAG, "SettingsAct. PressureVisible: false");
            }

            intentResult.putExtra("countHoursBetweenForecasts", countHoursBetweenForecasts);
            setResult(RESULT_OK, intentResult);

            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*Метод получения из ActiveMain и выставление текущих настроек*/
    private void setSettings() {

        if (Objects.requireNonNull(getIntent().getExtras()).getBoolean("WindyVisible")) {
            checkBoxSetVisibleWind.setChecked(true);
        } else {
            checkBoxSetVisibleWind.setChecked(false);
        }

        if(getIntent().getExtras().getBoolean("PressureVisible")) {
            checkBoxSetVisiblePressure.setChecked(true);
        } else {
            checkBoxSetVisiblePressure.setChecked(false);
        }

        if (getIntent().getExtras().getInt("СountHoursBetweenForecasts") != 0) {
            countHoursBetweenForecasts = getIntent().getExtras().getInt("СountHoursBetweenForecasts");
        }
        textValueCountHoursBetweenForecasts.setText(String.valueOf(countHoursBetweenForecasts));
        seekBarCountHoursBetweenForecasts.setProgress(countHoursBetweenForecasts - 1);
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

    /*Метод инициализации полей из ресурсов*/
    private void findView() {
        textValueCountHoursBetweenForecasts = findViewById(R.id.textValueCountHoursBetweenForecasts);
        seekBarCountHoursBetweenForecasts = findViewById(R.id.seekBarCountHoursBetweenForecasts);
        checkBoxSetVisibleWind = findViewById(R.id.checkBoxSetVisibleWind);
        checkBoxSetVisiblePressure = findViewById(R.id.checkBoxSetVisiblePressure);
    }
}
