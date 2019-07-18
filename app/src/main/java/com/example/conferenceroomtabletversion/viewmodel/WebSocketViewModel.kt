package com.example.conferenceroomtabletversion.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferenceroomtabletversion.repository.WebSocketRepository

class WebSocketViewModel : ViewModel() {
    var mWebSocketRepository: WebSocketRepository? = null
    /**
     * A MutableLivedata variable which will hold the Value for positive response from repository
     */
    var mPositiveAcknowledge = MutableLiveData<String>()

    fun connectToHub() {
        mWebSocketRepository = WebSocketRepository.getInstance()
        mWebSocketRepository!!.connectToHub(object : WebSocketRepository.SocketListener {
            override fun onReceive(message: String) {
                mPositiveAcknowledge.value = message
            }

        })
    }

    fun sendMessage() {
        mWebSocketRepository!!.sendMessage()
    }

    fun returnPositiveAcknoledge(): MutableLiveData<String> {
        return mPositiveAcknowledge
    }
}