package com.example.conferenceroomtabletversion.ui


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.viewmodel.WebSocketViewModel
import com.microsoft.signalr.HubConnection
import kotlinx.android.synthetic.main.activity_main.*

class Connection : AppCompatActivity() {

    private lateinit var mWebSocketViewModel: WebSocketViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mWebSocketViewModel = ViewModelProviders.of(this).get(WebSocketViewModel::class.java)
        observeFromHub()
        connectToHub()
        button.setOnClickListener {
            mWebSocketViewModel.sendMessage()
        }
    }
    override fun onDestroy() {
        super.onDestroy()

    }
    private fun connectToHub() {
        mWebSocketViewModel.connectToHub()
    }

    private fun observeFromHub() {
        mWebSocketViewModel.returnPositiveAcknoledge().observe(this, Observer {
            Log.i("------Message -->>", it)
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

}