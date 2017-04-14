package com.madebyatomicrobot.thingsdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.SeekBar
import android.widget.ToggleButton
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManagerService
import com.google.android.things.pio.Pwm
import android.widget.SeekBar.OnSeekBarChangeListener









class MainActivity : AppCompatActivity() {
    private lateinit var service: PeripheralManagerService

    private lateinit var ledToggleView: ToggleButton

    private lateinit var ledBrightnessView: SeekBar

    private lateinit var pin17: Gpio

    private lateinit var pin22: Gpio
    private lateinit var pin22Callback: GpioCallback

    private lateinit var pwm0: Pwm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        service = PeripheralManagerService()

        setupDemo1()
        setupDemo2()
        setupDemo3()
    }

    private fun setupDemo1() {
        ledToggleView = findViewById(R.id.ledToggle) as ToggleButton

        pin17 = service.openGpio("BCM17")
        pin17.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)

        ledToggleView.isChecked = pin17.value
        ledToggleView.setOnCheckedChangeListener { _, isChecked -> pin17.value = isChecked }
    }

    private fun setupDemo2() {
        pin22 = service.openGpio("BCM22")
        pin22.setDirection(Gpio.DIRECTION_IN)
        pin22.setActiveType(Gpio.ACTIVE_HIGH)
        pin22.setEdgeTriggerType(Gpio.EDGE_FALLING)

        pin22Callback = object : GpioCallback() {
            override fun onGpioEdge(gpio: Gpio?): Boolean {
                ledToggleView.isChecked = !ledToggleView.isChecked
                return true
            }

            override fun onGpioError(gpio: Gpio?, error: Int) {
                Log.e("Things", "Error during pin22 gpio callback: " + error)
            }
        }
        pin22.registerGpioCallback(pin22Callback)
    }

    private fun setupDemo3() {
        ledBrightnessView = findViewById(R.id.ledBrightness) as SeekBar

        pwm0 = service.openPwm("PWM0")
        pwm0.setPwmFrequencyHz(120.0)
        pwm0.setEnabled(true)

        ledBrightnessView.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                pwm0.setPwmDutyCycle(progress.toDouble())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) { /* Don't care */ }

            override fun onStopTrackingTouch(seekBar: SeekBar) { /* Don't care */ }
        })
    }

    override fun onDestroy() {
        pin17.close()

        pin22.unregisterGpioCallback(pin22Callback)
        pin22.close()

        pwm0.close()

        super.onDestroy()
    }
}
