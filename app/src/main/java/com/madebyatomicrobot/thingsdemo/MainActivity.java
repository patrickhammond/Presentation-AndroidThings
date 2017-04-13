package com.madebyatomicrobot.thingsdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.Pwm;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private PeripheralManagerService service;
    private ToggleButton ledToggleView;
    private SeekBar ledBrightnessView;

    private Gpio pin17;

    private Gpio pin22;
    private GpioCallback pin22Callback;

    private Pwm pwm0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        service = new PeripheralManagerService();

        setupDemo1();
        setupDemo2();
        setupDemo3();
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

    private void setupDemo2() {
        try {
            pin22 = service.openGpio("BCM22");
            pin22.setDirection(Gpio.DIRECTION_IN);
            pin22.setActiveType(Gpio.ACTIVE_HIGH);
            pin22.setEdgeTriggerType(Gpio.EDGE_FALLING);

            pin22Callback = new GpioCallback() {
                @Override
                public boolean onGpioEdge(Gpio gpio) {
                    ledToggleView.setChecked(!ledToggleView.isChecked());
                    return true;
                }

                @Override
                public void onGpioError(Gpio gpio, int error) {
                    Log.e(TAG, "Error during pin22 gpio callback: " + error);
                }
            };
            pin22.registerGpioCallback(pin22Callback);
        } catch (IOException ex) {
            Log.e(TAG, "Error during onCreate!", ex);
        }
    }

    private void setupDemo3() {
        ledBrightnessView = (SeekBar) findViewById(R.id.ledBrightness);

        try {
            pwm0 = service.openPwm("PWM0");
            pwm0.setPwmFrequencyHz(120);
            pwm0.setEnabled(true);
        } catch (IOException ex) {
            Log.e(TAG, "Error during onCreate!", ex);
        }

        ledBrightnessView.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    pwm0.setPwmDutyCycle(progress);
                } catch (IOException ex) {
                    Log.e(TAG, "Error during pwm0 setPwmDutyCycle!", ex);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Don't care
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Don't care
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            pin17.close();

            pin22.unregisterGpioCallback(pin22Callback);
            pin22.close();

            pwm0.close();
        } catch (IOException ex) {
            Log.e(TAG, "Error during onDestroy!", ex);
        }
        super.onDestroy();
    }
}
