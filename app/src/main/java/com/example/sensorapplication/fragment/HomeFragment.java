package com.example.sensorapplication.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.sensorapplication.R;

public class HomeFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private SensorEventListener gyroscopeSensorListener;
    private Sensor lightSensor, proximitySensor, gyroscopeSensor;
    private TextView lightTv, proximityTv;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        lightTv = view.findViewById(R.id.tv_light_sensor);
        proximityTv = view.findViewById(R.id.tv_proximity_sensor);


        if (proximitySensor == null) {
            Toast.makeText(getContext(), "No proximity sensor found in device.", Toast.LENGTH_SHORT).show();
        } else {
            // registering our sensor with sensor manager.
            SensorEventListener proximitySensorEventListener = new SensorEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {

                    if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                        if (sensorEvent.values[0] == 0) {
                            proximityTv.setText("Near");
                        } else {
                            proximityTv.setText("Away");
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };

            sensorManager.registerListener(proximitySensorEventListener,
                    proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.values[2] > 0.5f) { // anticlockwise
                    requireActivity().getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.yellow));
                } else if (sensorEvent.values[2] < -0.5f) { // clockwise
                    requireActivity().getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.green));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

    } // onViewCreated

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float value = sensorEvent.values[0];
        lightTv.setText(String.format("Current light level: %s", value));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyroscopeSensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        sensorManager.unregisterListener(gyroscopeSensorListener);
    }
}