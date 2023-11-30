package com.app.psble.ui.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.psble.databinding.FragmentListBinding
import com.app.psble.ui.base.BaseFragment
import com.app.psble.ui.base.isPermissionGranted
import com.app.psble.ui.base.navigateTo
import com.app.psble.ui.ble.adapter.DeviceListAdapter
import com.app.psble.utils.gone
import com.app.psble.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BleFragment : BaseFragment<FragmentListBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentListBinding
        get() = FragmentListBinding::inflate

    private val viewModel: BleVM by viewModels()

    @Inject
    lateinit var bluetoothAdapter: BluetoothAdapter

    @Inject
    lateinit var bleScanner: BluetoothLeScanner

    private val mAdapter = DeviceListAdapter()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.devices.observe(this, Observer { devices ->

            devices?.let {
                Timber.e("devices ${devices.size}")
                val list = devices.filter { ble ->
                    ble.type == 2 || ble.type == 3
                }
                if (list.isNotEmpty()) {
                    mAdapter.values = list
                    binding.resTitleTv.gone()
                    binding.progress.gone()
                }

            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBLE()
    }

    private fun setupBLE() {
        checkPermission()
        mAdapter.onCardClick = {
            navigateTo(BleFragmentDirections.actionBleFragmentToConnectFragment(it))
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter

        }

    }

    private fun checkPermission() {
        lifecycleScope.launch {
            val isGranted = isPermissionGranted()
            if (isGranted.isGranted) {
                checkBluetooth()
            } else
                showMissingPermissionAlert()
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
        } else {
            // Bluetooth is enabled, proceed with BLE scanning
            binding.resTitleTv.visible()
            binding.progress.visible()
            viewModel.scanLeDevice(true, bleScanner)
        }
    }

    private fun showMissingPermissionAlert() {
        /*showAlert(
            getString(R.string.alert_title_missing_permission),
            getString(R.string.alert_subtitle_missing_permission),
            object : PositiveButtonCallback {
                override fun onYesClicked() {
                    checkPermission()
                }
            })*/
    }


    override fun onResume() {
        super.onResume()
        viewModel.scanLeDevice(true, bleScanner)
    }

    override fun onPause() {
        super.onPause()
        viewModel.scanLeDevice(false, bleScanner)
    }

}
