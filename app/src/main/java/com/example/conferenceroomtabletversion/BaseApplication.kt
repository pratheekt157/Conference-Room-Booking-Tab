package com.example.conferenceroomtabletversion

import android.app.Application
import com.example.conferenceroomtabletversion.helper.Constants
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
        private val URL = "${Constants.IP_ADDRESS_WEB_SOCKET}chathub"
    }
}

