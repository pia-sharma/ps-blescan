package com.app.psble.services

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import timber.log.Timber
import java.lang.Thread.sleep
import java.util.*


@SuppressLint("MissingPermission")
class BluetoothLeService : Service() {

    private var connectionState = STATE_DISCONNECTED
    private var binder = LocalBinder()

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothGatt: BluetoothGatt? = null
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothDeviceAddress: String? = null

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val intentAction: String
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    intentAction = ACTION_GATT_CONNECTED
                    connectionState = STATE_CONNECTED
                    broadcastUpdate(intentAction)
                    bluetoothGatt?.discoverServices()
                    Timber.e("Attempting to start service discovery: " + bluetoothGatt?.discoverServices())
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    intentAction = ACTION_GATT_DISCONNECTED
                    connectionState = STATE_DISCONNECTED

                    Timber.e("Disconnected from GATT server.")
                    broadcastUpdate(intentAction)
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Timber.e("onServicesDiscovered $status")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val services = gatt!!.services

                for (service in services) {
                    val serviceUuid = service.uuid
                    BLUETOOTH_LE_CC254X_SERVICE = serviceUuid
                    BLUETOOTH_LE_CC254X_CHARACTERISTIC = serviceUuid

                    Timber.e("onServicesDiscovered $serviceUuid")
                    val serviceNew = gatt.getService(serviceUuid)
                    val characteristic = serviceNew?.getCharacteristic(serviceUuid)
                    characteristic?.let {
                        gatt.readCharacteristic(it)
                    }
                }
            }


            super.onServicesDiscovered(gatt, status)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {

            Timber.e("onCharacteristicChanged characteristic ::: $characteristic")
            Timber.e("onCharacteristicChanged byteArray ::: $value")
            val data = characteristic.properties
            val intent = Intent(ACTION_READ_DATA)
            intent.putExtra("properties", data)
            sendBroadcast(intent)

            broadcastUpdate(ACTION_READ_DATA)
            super.onCharacteristicChanged(gatt, characteristic, value)
        }
    }

    fun initialize(): Boolean {
        if (bluetoothManager == null) {
            bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            if (bluetoothManager == null) {
                Timber.e("Unable to initialize BluetoothManager.")
                return false
            }
        }

        bluetoothAdapter = bluetoothManager!!.adapter
        if (bluetoothAdapter == null) {
            Timber.e("Unable to obtain a BluetoothAdapter.")
            return false
        }

        return true
    }

    lateinit var device: BluetoothDevice

    fun connect(address: String): Boolean {
        if (bluetoothDeviceAddress != null && address == bluetoothDeviceAddress && bluetoothGatt != null) {
            Timber.e("Trying to use an existing mBluetoothGatt for connection.")
            return if (bluetoothGatt!!.connect()) {
                connectionState = STATE_CONNECTING
                true
            } else {
                false
            }
        }

        device = bluetoothAdapter.getRemoteDevice(address)
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
        Timber.e("Trying to create a new connection.")
        bluetoothDeviceAddress = address
        connectionState = STATE_CONNECTING

        return true
    }

    fun send(data: String) {
        val bluetoothGattService = bluetoothGatt?.getService(BLUETOOTH_LE_CC254X_SERVICE)
        val characteristic =
            bluetoothGattService?.getCharacteristic(BLUETOOTH_LE_CC254X_CHARACTERISTIC)
        when {
            data == null -> {
                Timber.e(TAG, "Data is null")
                return
            }

            bluetoothGattService == null -> {
                Timber.e(TAG, "Gatt service is null")
                return
            }

            characteristic == null -> {
                Timber.e(TAG, "Characteristic is null")
                return
            }

            else -> {}
        }
        // Bluetooth LE can send only 20 bytes of data
        val chunks = data.chunked(CHUNKSIZE)
        Timber.e("Chunks is $chunks")

        /*val command = "1" // or "0"
        val characteristic = gatt?.getService(serviceUuid)?.getCharacteristic(commandCharacteristicUuid)
        characteristic?.value = command.toByteArray()
        gatt?.writeCharacteristic(characteristic)*/
        for (chunk in chunks) {
            characteristic?.value = chunk.toByteArray()
            Timber.e("${characteristic.value}")
            bluetoothGatt?.writeCharacteristic(characteristic)
            Timber.e("Characteristic should be wrote")
            sleep(100)
        }
    }

    private fun broadcastUpdate(intentAction: String) {
        val intent = Intent(intentAction)
        sendBroadcast(intent)
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService = this@BluetoothLeService
    }

    fun disconnect() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Timber.e("BluetoothAdapter not initialized")
            return
        }
        bluetoothGatt?.disconnect()
    }

    private fun close() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    override fun onBind(intent: Intent?) = binder

    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    companion object {

        private val TAG = BluetoothLeService::class.java.simpleName

        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTING = 1
        private const val STATE_CONNECTED = 2

        private const val CHUNKSIZE = 20

        private var BLUETOOTH_LE_CC254X_SERVICE =
            UUID.fromString("00001801-0000-1000-8000-00805f9b34fb")
        private var BLUETOOTH_LE_CC254X_CHARACTERISTIC =
            UUID.fromString("00001801-0000-1000-8000-00805f9b34fb")

        const val ACTION_GATT_CONNECTED = "com.app.psble.bluetoothlescanner.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.app.psble.bluetoothlescanner.ACTION_GATT_DISCONNECTED"
        const val ACTION_READ_DATA = "com.app.psble.bluetoothlescanner.ACTION_READ_DATA"
    }
}