package com.plweegie.mqttboss

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.plweegie.mqttboss.helpers.MqttHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.client.mqttv3.*


class MainActivity : AppCompatActivity(), MqttCallbackExtended, IMqttActionListener {

    private lateinit var mqttHelper: MqttHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        startMqtt()
    }

    private fun startMqtt() {
        mqttHelper = MqttHelper(applicationContext).apply {
            setMqttCallback(this@MainActivity)
            setSubscribeListener(this@MainActivity)
            connect()
        }
    }

    override fun onStop() {
        super.onStop()
        mqttHelper.unsubscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttHelper.disconnect()
    }

    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
        val toast = if (reconnect) "Reconnected" else "Connected"
        Toast.makeText(this@MainActivity, "$toast to $serverURI", Toast.LENGTH_SHORT).show()
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        data_received?.text = message.toString()
    }

    override fun connectionLost(cause: Throwable?) {
        Toast.makeText(this@MainActivity, "Connection lost", Toast.LENGTH_SHORT).show()
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        Toast.makeText(this@MainActivity, "Delivery complete: ${token?.message}", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onSuccess(asyncActionToken: IMqttToken?) {
        Toast.makeText(this@MainActivity, "Subscribed", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
        Toast.makeText(this@MainActivity, "Subscribe failed", Toast.LENGTH_SHORT).show()
    }
}
