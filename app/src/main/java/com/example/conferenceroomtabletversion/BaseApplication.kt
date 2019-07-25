package com.example.conferenceroomtabletversion

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.example.conferenceroomtabletversion.helper.Constants
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import java.net.URISyntaxException

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }
}

