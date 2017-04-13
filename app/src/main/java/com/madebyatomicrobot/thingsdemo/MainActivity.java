package com.madebyatomicrobot.thingsdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private PeripheralManagerService service;
    private ToggleButton ledToggleView;
    private Gpio pin17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        service = new PeripheralManagerService();

        setupDemo1();
    }

    private void setupDemo1() {
        ledToggleView = (ToggleButton) findViewById(R.id.ledToggle);

        try {
            pin17 = service.openGpio("BCM17");
            pin17.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException ex) {
            Log.e(TAG, "Error during onCreate!", ex);
        }

        try {
            ledToggleView.setChecked(pin17.getValue());
        } catch (IOException ex) {
            Log.e(TAG, "Error during setChecked!", ex);
        }

        ledToggleView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    pin17.setValue(isChecked);
                } catch (IOException ex) {
                    Log.e(TAG, "Error during onCheckedChanged!", ex);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            pin17.close();
        } catch (IOException ex) {
            Log.e(TAG, "Error during onDestroy!", ex);
        }
        super.onDestroy();
    }
}
