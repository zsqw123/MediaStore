package com.zsqw123.mediastore

import android.app.Application
import android.content.Context

/**
 * Author zsqw123
 * Create by damyjy
 * Date 2021/7/7 22:32
 */
lateinit var app: App

class App : Application() {
    init {
        app = this
        storageInit(this)
    }
}