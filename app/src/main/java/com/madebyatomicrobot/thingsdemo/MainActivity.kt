package com.madebyatomicrobot.thingsdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ToggleButton
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService

class MainActivity : AppCompatActivity() {

    private lateinit var service: PeripheralManagerService

    private lateinit var ledToggleView: ToggleButton

    private lateinit var pin17: Gpio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        service = PeripheralManagerService()

        setupDemo1()
    }

    private fun setupDemo1() {
        ledToggleView = findViewById(R.id.ledToggle) as ToggleButton

        pin17 = service.openGpio("BCM17")
        pin17.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)

        ledToggleView.isChecked = pin17.value
        ledToggleView.setOnCheckedChangeListener { _, isChecked -> pin17.value = isChecked }
    }

    override fun onDestroy() {
        pin17.close()

        super.onDestroy()
    }
}
