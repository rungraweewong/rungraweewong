package com.adapterdigital.covid

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nearbytes.sdk.NearBytes
import com.nearbytes.sdk.NearBytes.NBException
import com.nearbytes.sdk.NearBytes.NearBytesListener
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.zip.Inflater


class ChatActivity : AppCompatActivity() {

    var mNearBytes: NearBytes? = null
    var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var appKey = "1bb94139fd5eee3aab0c0a0d3fad46dd90368791d6070743df5c63f36acffe40aa6d90ec7119a45d603d259fc4edba92"
        var idApp = 1960
        try {
            mNearBytes = NearBytes(this, idApp, appKey)
            mNearBytes!!.debugMode()
            mNearBytes!!.startListening()
            mNearBytes!!.setNearBytesListener(object : NearBytesListener {
                override fun OnReceiveError(code: Int, msg: String) {

                    Log.e("ChatActivity" , "code =  , msg = $msg")

                }
                override fun OnReceiveData(bytes: ByteArray) {
//                    tvChat.text = tvChat.text.toString() +"\n"+String(bytes)

                    val vFriendMessage = layoutInflater.inflate(R.layout.friend_message, null);
                    val tvFriendMessage = vFriendMessage.findViewById<TextView>(R.id.tvFriendMessage)
                    tvFriendMessage.setText(String(bytes))

                    llContainerMessage.addView(vFriendMessage)
                }
            })
        } catch (e: NBException) {
            e.printStackTrace()
        }

        btnSend.setOnClickListener {

        }

        val r: Runnable = object : Runnable {
            override fun run() {
                val msg = (1..100).shuffled().last().toString()
                edtInput.setText(msg)

                mNearBytes!!.send(NearBytes.stringToBytes(msg))
                val vMyMessage = layoutInflater.inflate(R.layout.my_message, null);
                val tvMyMessage = vMyMessage.findViewById<TextView>(R.id.tvMyMessage)
                tvMyMessage.text = msg

                llContainerMessage.addView(vMyMessage)

                mHandler.postDelayed(this, 10000)
            }
        }

        mHandler.postDelayed(r, 10000)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (NearBytes.shared() != null) mNearBytes = NearBytes.shared()
    }

    override fun onResume() {
        super.onResume()
        if (mNearBytes != null) NearBytes.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (mNearBytes != null) NearBytes.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mNearBytes != null) NearBytes.onDestroy()
    }
}
