package com.app.psble.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //Android BLE Scanner
    @Provides
    fun provideBluetoothManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    @Provides
    fun provideBluetoothAdapter(bluetoothManager: BluetoothManager) = bluetoothManager.adapter

    @Provides
    fun provideBluetoothScanner(bluetoothAdapter: BluetoothAdapter) =
        bluetoothAdapter.bluetoothLeScanner

}