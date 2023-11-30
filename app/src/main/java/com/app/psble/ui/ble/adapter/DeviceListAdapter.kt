package com.app.psble.ui.ble.adapter

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.psble.databinding.ItemBleCardBinding


class DeviceListAdapter : RecyclerView.Adapter<DeviceViewHolder>() {

    var onCardClick: ((BluetoothDevice) -> Unit)? = null

    var values = emptyList<BluetoothDevice>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DeviceViewHolder(
            ItemBleCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        )

    override fun getItemCount() = values.size

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = values[position]
        holder.bind(device, position)

        holder.itemView.setOnClickListener {
            onCardClick?.invoke(device)
        }
    }
}