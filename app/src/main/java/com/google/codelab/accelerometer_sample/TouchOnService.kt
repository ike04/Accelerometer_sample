package com.google.codelab.accelerometer_sample

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.IBinder

class TouchOnService : Service(), SensorEventListener {
    private val threshold: Float = 10f
    private var oldValue: Float = 0f
    private lateinit var cameraManager: CameraManager
    private var cameraID: String? = null
    private var lightOn: Boolean = false

    override fun onCreate() {
        super.onCreate()
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                super.onTorchModeChanged(cameraId, enabled)
                cameraID = cameraId
                lightOn = enabled
            }
        }, Handler())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)

        cameraID?.let {
            try {
                if (lightOn) {
                    cameraManager.setTorchMode(it, false)
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val zDiff = Math.abs(event.values[2] - oldValue)
            if (zDiff > threshold) {
                torchOn()
            }
            oldValue = event.values[2]
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    private fun torchOn() {
        cameraID?.let {
            try {
                if (!lightOn) {
                    cameraManager.setTorchMode(it, true)
                } else {
                    cameraManager.setTorchMode(it, false)
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }
}
