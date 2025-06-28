package com.example.diagnostic333

import HomeFragment
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.diagnostic333.R
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        HomeFragment()
        // Handle navigation item clicks
                bottomNavigation.setOnItemSelectedListener { item ->
                    val fragment: Fragment = when (item.itemId) {
                        R.id.nav_home -> HomeFragment()
                        R.id.nav_dtcs -> DtcsFragment()
                        else -> HomeFragment()
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit()
                    true
                }
    }
}