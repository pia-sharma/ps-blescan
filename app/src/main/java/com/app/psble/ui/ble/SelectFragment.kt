package com.app.psble.ui.ble

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.psble.databinding.FragmentSelectBinding
import com.app.psble.ui.base.BaseFragment
import com.app.psble.ui.base.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectFragment : BaseFragment<FragmentSelectBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSelectBinding
        get() = FragmentSelectBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actionStartScan.setOnClickListener {
            navigateTo(SelectFragmentDirections.actionSelectFragmentToBleFragment())
        }
    }
}
