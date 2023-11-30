package com.app.psble.ui.base

import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.actionBarTitle(title: String) {
    supportActionBar?.show()
    supportActionBar?.title = title
}

