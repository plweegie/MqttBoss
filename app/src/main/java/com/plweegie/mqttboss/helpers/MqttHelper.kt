package com.plweegie.mqttboss.helpers

import android.content.Context
import android.util.Log
import com.plweegie.mqttboss.R
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class MqttHelper(private val context: Context) {

    companion object {
        private const val SERVER_URI = "tcp://m24.cloudmqtt.com:18272"
        private const val CLIENT_ID = "MqttAndroidBoss"
        private const val SUBSCRIPTION_TOPIC = "sensor/+"
    }

    private var mqttAndroidClient: MqttAndroidClient? = null
    private var mqttCallback: MqttCallbackExtended? = null
    private var subscribeListener: IMqttActionListener? = null

    private val mqttConnectOptions = MqttConnectOptions().apply {
        isAutomaticReconnect = true
        isCleanSession = false
        connectionTimeout = 30
        keepAliveInterval = 60
        userName = context.resources.getString(R.string.user_name)
        password = context.resources.getString(R.string.user_password).toCharArray()
    }

    private val disconnectedBufferOptions = DisconnectedBufferOptions().apply {
        isBufferEnabled = true
        bufferSize = 100
        isPersistBuffer = false
        isDeleteOldestMessages = false
    }

    private val connectListener = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            subscribe(SUBSCRIPTION_TOPIC, subscribeListener!!)
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Log.d("MQTT", "Error connecting to MQTT")
        }
    }

    fun setMqttCallback(callback: MqttCallbackExtended) {
        mqttCallback = callback
    }

    fun setSubscribeListener(listener: IMqttActionListener) {
        subscribeListener = listener
    }

    fun connect() {
        if (isConnected()) {
            return
        }

        try {
            mqttAndroidClient = MqttAndroidClient(context, SERVER_URI, CLIENT_ID).apply {
                setCallback(mqttCallback)
                connect(mqttConnectOptions, context, connectListener)
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        if (!isConnected()) {
            return
        }

        try {
            mqttAndroidClient?.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String, listener: IMqttActionListener) {
        if (!isConnected()) {
            return
        }

        mqttAndroidClient?.setBufferOpts(disconnectedBufferOptions)

        try {
            mqttAndroidClient?.subscribe(topic, 0, context, listener)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unsubscribe() {
        if (!isConnected()) {
            return
        }

        try {
            mqttAndroidClient?.unsubscribe(SUBSCRIPTION_TOPIC)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, message: String, listener: IMqttActionListener) {
        if (!isConnected()) {
            return
        }

        try {
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttAndroidClient?.publish(topic, mqttMessage, context, listener)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun isConnected(): Boolean = (mqttAndroidClient != null && mqttAndroidClient?.isConnected == true)
}