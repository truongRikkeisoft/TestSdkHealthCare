package com.example.testsdkhealthcare

import android.content.IntentFilter

object BaseActionUtils {

    /**
     * bluetooth action
     */
    const val ON_SCAN_RESULT = "ON_SCAN_RESULT"
    const val ON_DATA_ARRIVED = "ON_DATA_ARRIVED"
    const val ON_BLUETOOTH_INIT = "ON_BLUETOOTH_INIT"
    const val ON_CONNECT_STATUE = "ON_CONNECT_STATUE"
    const val ON_DISCOVER_SERVICE = "ON_DISCOVER_SERVICE"
    const val ON_DISCOVER_CHARACTER = "ON_DISCOVER_CHARACTER"
    const val ON_COMMON_SEND = "ON_COMMON_SEND"
    const val ON_COMMON_RECEIVER = "ON_COMMON_RECEIVER"
    const val ON_CHARACTERISTIC_CHANGE = "ON_CHARACTERISTIC_CHANGE"
    const val ON_BLUETOOTH_ERROR = "ON_BLUETOOTH_ERROR"
    const val BLE_SDK_TYPE = "BLE_SDK_TYPE"
    const val BLE_DATA_TYPE = "BLE_DATA_TYPE"
    const val BLE_ARRIVED_DATA = "BLE_ARRIVED_DATA"
    const val BLE_SCAN_RESULT_DEVICE = "BLE_SCAN_RESULT_DEVICE"
    const val BLE_CONNECT_STATUE = "BLE_CONNECT_STAUE"
    const val BLE_SERVICE_UUID = "BLE_SERVICE_UUID"
    const val BLE_CHARACTER_UUID = "CHARACTER_UUID"
    const val BLE_COMMON_SEND = "BLE_COMMON_SEND"
    const val BLE_BLUETOOTH_ADDRESS = "BLE_BLUETOOTH_ADDRESS"
    const val BLE_PRE_CONNECT = "BLE_PRE_CONEECT"
    const val BLE_NO_CALLBACK = "BLE_NO_CALLBACK"
    const val ACTION_CONNECT_TIMEOUT = "ACTION_CONNECT_TIMEOUT"
    const val Action_Phone_Statue_Out = "ACTION_PHONE_STATUE_OUT"

    fun getIntentFilter(): IntentFilter = IntentFilter().apply {
        addAction(ON_SCAN_RESULT)
        addAction(ON_DATA_ARRIVED)
        addAction(ON_BLUETOOTH_INIT)
        addAction(ON_CONNECT_STATUE)
        addAction(ON_DISCOVER_SERVICE)
        addAction(ON_DISCOVER_CHARACTER)
        addAction(ON_COMMON_SEND)
        addAction(ON_CHARACTERISTIC_CHANGE)
        addAction(ON_BLUETOOTH_ERROR)
        addAction(BLE_DATA_TYPE)
        addAction(BLE_ARRIVED_DATA)
        addAction(BLE_SCAN_RESULT_DEVICE)
        addAction(BLE_CONNECT_STATUE)
        addAction(BLE_SERVICE_UUID)
        addAction(BLE_CHARACTER_UUID)
        addAction(BLE_COMMON_SEND)
        addAction(BLE_SDK_TYPE)
        addAction(BLE_BLUETOOTH_ADDRESS)
        addAction(BLE_PRE_CONNECT)
        addAction(BLE_NO_CALLBACK)
        addAction(ON_COMMON_RECEIVER)
    }
}