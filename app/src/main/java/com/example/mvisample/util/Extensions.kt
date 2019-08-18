package com.example.mvisample.util

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

val <T> T.exhaustive: T
    get() = this

fun View.setVisivilty(isVisible: Boolean) {
    visibility = if (isVisible) VISIBLE else GONE
}