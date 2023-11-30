package com.app.psble.ui.ble.connect

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.app.psble.databinding.FragmentDeviceControlBinding
import com.app.psble.services.BluetoothLeService
import com.app.psble.services.BluetoothLeService.Companion.ACTION_GATT_CONNECTED
import com.app.psble.services.BluetoothLeService.Companion.ACTION_GATT_DISCONNECTED
import com.app.psble.services.BluetoothLeService.Companion.ACTION_READ_DATA
import com.app.psble.ui.base.BaseFragment
import com.app.psble.ui.base.actionBarTitle
import com.app.psble.utils.getDeviceName
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ConnectBleFragment : BaseFragment<FragmentDeviceControlBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDeviceControlBinding
        get() = FragmentDeviceControlBinding::inflate

    val args: ConnectBleFragmentArgs by navArgs()

    private var bluetoothLeService: BluetoothLeService? = null

    private var bound = false
    private var connected = false

    private val builder = StringBuilder()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            val binder = service as BluetoothLeService.LocalBinder
            bluetoothLeService = binder.getService()
            if (!bluetoothLeService!!.initialize()) {
                updateBuilderString("Unable to initialize Bluetooth")
                activity?.finish()
            }
            bound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            updateBuilderString("Service disconnected for component=> $componentName!")
            bound = false
        }
    }

    private fun updateBuilderString(text: String) {
        builder.append("\n$text")
        binding.stateTv.text = builder.toString()
    }

    private val gattUpdateReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_GATT_CONNECTED -> {
                    updateBuilderString("Connected to device GATT server.")
                    connected = true
                }

                ACTION_GATT_DISCONNECTED -> {
                    updateBuilderString("Disconnected from device GATT server.")
                    connected = false
                }

                ACTION_READ_DATA -> {
                    updateBuilderString("Reading data from device :${intent.extras}")
                    intent.let {
                        if (it.hasExtra("properties")) {
                            val prop = it.getIntExtra("properties", 0)
                            updateBuilderString("Values received :${prop}")
                        }
                    }
                    //updateUI(data)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionBarTitle("Control Device: " + args.bluetoothDevice.getDeviceName())
        binding.nameTv.text = "${args.bluetoothDevice.getDeviceName()}"
        binding.macAddTv.text = "Address: ${args.bluetoothDevice.address}"
        binding.typeTv.text = "Type: ${args.bluetoothDevice.type}"

        binding.connectButton.setOnClickListener {
            updateBuilderString("Connecting...")
            bluetoothLeService?.connect(args.bluetoothDevice.address)
        }
        binding.disconnectButton.setOnClickListener {
            updateBuilderString("Connection closed")
            bluetoothLeService?.disconnect()
        }
        binding.sendButton.setOnClickListener {
            updateBuilderString("Sending data to device")
            bluetoothLeService?.send(KEY_TO_OPEN)
        }
        // Bind to local service
        Intent(requireContext(), BluetoothLeService::class.java).also { intent ->
            activity?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(ACTION_GATT_CONNECTED)
            addAction(ACTION_GATT_DISCONNECTED)
        }
        activity?.registerReceiver(gattUpdateReceiver, filter)
        if (bluetoothLeService != null) {
            args.bluetoothDevice.address.let {
                val result: Boolean = bluetoothLeService!!.connect(args.bluetoothDevice.address)
                updateBuilderString("Connect request result=$result")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        activity?.unregisterReceiver(gattUpdateReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unbindService(connection)
        bound = false
    }

    companion object {
        private val TAG = ConnectBleFragment::class.simpleName!!
        private const val KEY_TO_OPEN = ""
    }

}