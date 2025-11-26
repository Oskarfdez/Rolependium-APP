package com.example.login.presentation.dice

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ShakeDetector(
    private val onShake: () -> Unit
) : SensorEventListener {

    private var lastUpdate: Long = 0
    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f
    private var lastZ: Float = 0.0f

    companion object {
        private const val SHAKE_THRESHOLD = 800
        private const val UPDATE_INTERVAL = 100
    }

    override fun onSensorChanged(event: SensorEvent) {
        val currentTime = System.currentTimeMillis()

        if ((currentTime - lastUpdate) > UPDATE_INTERVAL) {
            val diffTime = currentTime - lastUpdate
            lastUpdate = currentTime

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

            if (speed > SHAKE_THRESHOLD) {
                onShake()
            }

            lastX = x
            lastY = y
            lastZ = z
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se necesita implementación
    }
}