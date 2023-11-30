package com.app.psble.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice


@SuppressLint("MissingPermission", "NewApi")
fun BluetoothDevice.getDeviceName(): String {
    if (this.name == null && this.alias == null)
        return "N/A"
    if (this.name != null) {
        return "Name: ${this.name}"
    }
    return if (this.alias != null)
        "Name: ${this.alias}"
    else
        "N/A"
}


