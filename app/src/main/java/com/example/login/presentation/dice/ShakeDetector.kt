package com.example.login.presentation.dice

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

// Detector de agitación
class ShakeDetector(private val onShake: () -> Unit) : android.hardware.SensorEventListener {
    private var lastUpdate: Long = 0
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lastZ: Float = 0f
    private val shakeThreshold = 800

    override fun onSensorChanged(event: android.hardware.SensorEvent) {
        val currentTime = System.currentTimeMillis()
        if ((currentTime - lastUpdate) > 100) {
            val timeDiff = currentTime - lastUpdate
            lastUpdate = currentTime

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val speed = Math.abs(x + y + z - lastX - lastY - lastZ) / timeDiff * 10000

            if (speed > shakeThreshold) {
                onShake()
            }

            lastX = x
            lastY = y
            lastZ = z
        }
    }

    override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {}
}
