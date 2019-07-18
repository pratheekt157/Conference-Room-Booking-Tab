package com.example.conferenceroomtabletversion.repository

import android.os.AsyncTask
import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.TransportEnum

class WebSocketRepository {
    lateinit var hubConnection: HubConnection
    companion object {
        private var mWebSocketRepository: WebSocketRepository? = null
        fun getInstance(): WebSocketRepository {
            if (mWebSocketRepository == null) {
                mWebSocketRepository = WebSocketRepository()
            }
            return mWebSocketRepository!!
        }
    }

    fun connectToHub(listener: SocketListener) {
        try {
           hubConnection = HubConnectionBuilder.create("http://192.168.3.189/s/move")
                .withTransport(TransportEnum.LONG_POLLING)
                .build()
            hubConnection.on("ReceiveMessage", { message -> println("New Message: $message")
                listener.onReceive(message)
            }, String::class.java)
            HubConnectionTask().execute(hubConnection)
        } catch (e: Exception) {
            Log.e("--------socket error", e.message)
        }
    }

    fun sendMessage() {
        if(hubConnection.connectionState.toString() == "CONNECTED") {
            hubConnection.send("Move", "Hello Prateek")
        }
    }

    internal inner class HubConnectionTask : AsyncTask<HubConnection, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg hubConnections: HubConnection): Void? {
            val hubConnection = hubConnections[0]
            try {
                hubConnection.start().blockingAwait()
            } catch (e: Exception) {
                Log.i("-------Error in ", e.message)
            }
            Log.i("------connection state", hubConnection.connectionState.toString())
            return null
        }
    }
    interface SocketListener {
        fun onReceive(message: String)
    }

}