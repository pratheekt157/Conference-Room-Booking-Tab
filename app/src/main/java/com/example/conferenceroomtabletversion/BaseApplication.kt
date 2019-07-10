package com.example.conferenceroomtabletversion

import android.app.Application
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import java.net.URISyntaxException

class BaseApplication : Application() {

    private var mSocket: Socket? = null
    override fun onCreate() {
        super.onCreate()
        try {
            mSocket = IO.socket(URL)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }

    }
    fun getmSocket(): Socket? {
        return mSocket
    }

    companion object {
        private val URL = "http://192.168.1.189/CRB/Dashboard"
    }
}