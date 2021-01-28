package com.google.codelab.accelerometer_sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startBtn = findViewById<Button>(R.id.startBtn)
        val stopBtn = findViewById<Button>(R.id.stopBtn)
        val intent = Intent(this, TouchOnService::class.java)

        startBtn.setOnClickListener {
            startService(intent)
        }

        stopBtn.setOnClickListener {
            stopService(intent)
        }
    }
}
