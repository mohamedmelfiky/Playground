package com.example.mvisample.util

import com.example.domain.udf.Logger
import timber.log.Timber

class AndroidLogger : Logger {

    override val tag: String = "Mvi"

    override fun log(message: String) {
        Timber.tag(tag).d(message)
    }

}