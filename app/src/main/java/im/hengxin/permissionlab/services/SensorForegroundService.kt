package im.hengxin.permissionlab.services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import androidx.core.content.ContextCompat
import im.hengxin.permissionlab.core.LabStore
import im.hengxin.permissionlab.core.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class SensorForegroundService : Service(), SensorEventListener, LocationListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var locationManager: LocationManager
    private var lastAcceleration = "等待加速度"
    private var lastLocation = "等待 GPS"

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SensorManager::class.java)
        locationManager = getSystemService(LocationManager::class.java)
        startForeground(NotificationHelper.ID_SENSOR, NotificationHelper.sensorNotification(this, "正在初始化传感器"))
        registerSensors()
        registerLocation()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateNotification()
        return START_STICKY
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        locationManager.removeUpdates(this)
        CoroutineScope(Dispatchers.IO).launch {
            LabStore.saveSensorService(this@SensorForegroundService, false)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            lastAcceleration = "x=${event.values[0].format1()}, y=${event.values[1].format1()}, z=${event.values[2].format1()}"
            updateNotification()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onLocationChanged(location: Location) {
        lastLocation = "${location.latitude.format5()}, ${location.longitude.format5()}"
        updateNotification()
    }

    @Deprecated("Deprecated in framework")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit

    private fun registerSensors() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun registerLocation() {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fine && !coarse) return
        runCatching {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10_000L, 5f, this)
        }
    }

    private fun updateNotification() {
        val text = "$lastAcceleration | GPS: $lastLocation"
        getSystemService(android.app.NotificationManager::class.java)
            .notify(NotificationHelper.ID_SENSOR, NotificationHelper.sensorNotification(this, text))
    }
}

private fun Float.format1(): String = String.format(Locale.ROOT, "%.1f", this)
private fun Double.format5(): String = String.format(Locale.ROOT, "%.5f", this)
