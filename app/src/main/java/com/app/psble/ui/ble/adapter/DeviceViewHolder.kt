package com.app.psble.ui.ble.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.recyclerview.widget.RecyclerView
import com.app.psble.databinding.ItemBleCardBinding
import com.app.psble.utils.getDeviceName

@SuppressLint("MissingPermission", "NewApi")
class DeviceViewHolder(
    private val binding: ItemBleCardBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(device: BluetoothDevice, position: Int) {
        val pos = (position + 1).toString()
        binding.countTv.text = pos
        binding.nameTv.text = device.getDeviceName()

        binding.typeTv.text = "Type: ${device.type}"
        binding.addressTv.text = "MAC: ${device.address}"
        binding.stateTv.text = "Bound State: ${device.bondState}"
        binding.uuidTv.text = "UUID: ${device.uuids}".uppercase()
    }

}