package com.av.vtask

import android.app.Application
import android.content.Context
import com.av.vtask.providers.IDataProvider

class App : Application() {
    companion object {
        lateinit var appContext: Context
            private set
        lateinit var dataProvider: IDataProvider
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}