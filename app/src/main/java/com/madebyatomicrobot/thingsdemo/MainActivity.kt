package com.madebyatomicrobot.thingsdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.google.android.things.pio.PeripheralManagerService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service = PeripheralManagerService()
    }

    override fun onDestroy() {
        // Cleanup goes here!
        super.onDestroy()
    }
}
