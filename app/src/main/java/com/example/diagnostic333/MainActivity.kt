package com.example.diagnostic333

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.diagnostic333.SpeedometerView
import com.example.diagnostic333.R
import android.os.Handler
import android.os.Looper
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val speedometerView: SpeedometerView = findViewById(R.id.speedometer)
        speedometerView.configure(0f, 500f, Color.BLUE) // Set range 0-500 and blue needle
        speedometerView.updateValue(250f) // Set initial value

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val randomValue = 20f + (0..200).random().toFloat()
                speedometerView.updateValue(randomValue)
                handler.postDelayed(this, 2000)
            }
        }
        handler.postDelayed(runnable, 2000)
    }
}