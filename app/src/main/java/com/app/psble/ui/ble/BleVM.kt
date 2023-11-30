package com.app.psble.ui.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Handler
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class BleVM @Inject constructor() : ViewModel(), LifecycleObserver {
    val response = MutableLiveData<String>()
    val res: LiveData<String>
        get() = response

    private val _devices by lazy { MutableLiveData<MutableList<BluetoothDevice>>() }
    val devices: LiveData<MutableList<BluetoothDevice>>
        get() = _devices

    private val handler = Handler()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            val scannedDevices = devices.value ?: mutableListOf()
            if (!scannedDevices.contains(result?.device)) {
                result?.device?.let { scannedDevices.add(it) }
                _devices.value = scannedDevices
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun scanLeDevice(enable: Boolean, bluetoothLeScanner: BluetoothLeScanner) {
        when (enable) {
            true -> {
                handler.postDelayed({
                    bluetoothLeScanner.stopScan(scanCallback)
                }, SCAN_PERIOD)
                bluetoothLeScanner.startScan(scanCallback)
            }

            else -> {
                bluetoothLeScanner.stopScan(scanCallback)
            }
        }
    }

    companion object {
        private const val SCAN_PERIOD = 10_000L
    }
}