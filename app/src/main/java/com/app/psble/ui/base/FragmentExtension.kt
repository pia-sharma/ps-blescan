package com.app.psble.ui.base


import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.app.psble.R
import com.gun0912.tedpermission.TedPermissionResult
import com.gun0912.tedpermission.coroutine.TedPermission
import timber.log.Timber

fun Fragment.navigateTo(directions: NavDirections, clearStack: Boolean = false) {
    try {
        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.from_right).setExitAnim(R.anim.to_left)
            .setPopEnterAnim(R.anim.from_left).setPopExitAnim(R.anim.to_right).build()

        findNavController().navigate(directions, navBuilder.build())
    } catch (e: IllegalArgumentException) {
        Timber.e("Fragment Catching potential duplicate navigation event")
    }
}

fun Fragment.navigateBack() {
    try {
        findNavController().navigateUp()
    } catch (e: IllegalArgumentException) {
        Timber.e("Fragment Catching potential duplicate navigation event")
    }
}

fun Fragment.navigateBackId(id: Int) {
    try {
        findNavController().popBackStack(id, inclusive = false)
        Timber.e("findNavController() ${findNavController().currentBackStackEntryFlow}")
    } catch (e: IllegalArgumentException) {
        Timber.e("Fragment Catching potential duplicate navigation event")
    }
}

fun BaseFragment<*>.actionBarTitle(title: String) {
    (activity as? BaseActivity<*>)?.actionBarTitle(title)
}


fun BaseFragment<*>.showToast(message: String) {
    Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
}

suspend fun isPermissionGranted(): TedPermissionResult {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        TedPermission.create().setPermissions(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).check()
    } else {
        TedPermission.create().setPermissions(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).check()
    }
}