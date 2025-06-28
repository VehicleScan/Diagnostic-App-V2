package com.example.diagnostic333

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.diagnostic333.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val speedometer1: SpeedometerView = findViewById(R.id.speedometer1)
        speedometer1.configure(0f, 220f, Color.WHITE)

        val speedometer2: SpeedometerView = findViewById(R.id.speedometer2)
        speedometer2.configure(0f, 220f, Color.WHITE)

        val oilTempSeekBar: SeekBarView = findViewById(R.id.oilTempSeekBar)
        oilTempSeekBar.configure(50f, 150f, 0f)

        val tirePressureSeekBar: SeekBarView = findViewById(R.id.tirePressureSeekBar)
        tirePressureSeekBar.configure(20f, 40f,0f)

        val massAirFlowSeekBar: SeekBarView = findViewById(R.id.massAirFlowSeekBar)
        massAirFlowSeekBar.configure(0f, 200f, 0f)

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val randomValue1 = 20f + (0..200).random().toFloat()
                speedometer1.updateValue(randomValue1)
                val randomValue2 = 20f + (0..200).random().toFloat()
                speedometer2.updateValue(randomValue2)
                val oilTemp = 50f + (0..100).random().toFloat()
                oilTempSeekBar.updateValue(oilTemp)
                val tirePressure = 20f + (0..20).random().toFloat()
                tirePressureSeekBar.updateValue(tirePressure)
                val massAirFlow = 0f + (0..200).random().toFloat()
                massAirFlowSeekBar.updateValue(massAirFlow)
                handler.postDelayed(this, 2000)
            }
        }
        handler.postDelayed(runnable, 2000)
    }
}