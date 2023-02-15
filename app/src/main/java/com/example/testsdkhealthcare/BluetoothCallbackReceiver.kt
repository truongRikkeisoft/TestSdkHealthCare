package com.example.testsdkhealthcare

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zeroner.blemidautumn.bean.WristBand

open class BluetoothCallbackReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val extras = intent.extras
        when (action) {
            BaseActionUtils.ON_SCAN_RESULT -> {
                val device =
                    extras!!.getParcelable<WristBand>(BaseActionUtils.BLE_SCAN_RESULT_DEVICE)
                onScanResult(device)
            }
            BaseActionUtils.ON_BLUETOOTH_INIT -> onBluetoothInit()
            BaseActionUtils.ON_COMMON_SEND -> {
                val commons = extras!!.getByteArray(BaseActionUtils.BLE_COMMON_SEND)
                onCommonSend(commons)
                val receivers = extras.getByteArray(BaseActionUtils.ON_COMMON_RECEIVER)
                onCmdReceiver(receivers)
            }
            BaseActionUtils.ON_COMMON_RECEIVER -> {
                val receivers = extras!!.getByteArray(BaseActionUtils.ON_COMMON_RECEIVER)
                onCmdReceiver(receivers)
            }
            BaseActionUtils.ON_CONNECT_STATUE -> {
                val connected = extras!!.getBoolean(BaseActionUtils.BLE_CONNECT_STATUE)
                connectStatue(connected)
            }
            BaseActionUtils.ON_DATA_ARRIVED -> {
                val sdk = extras!!.getInt(BaseActionUtils.BLE_SDK_TYPE, 0)
                val dataType = extras.getInt(BaseActionUtils.BLE_DATA_TYPE, 0)
                val data = extras.getString(BaseActionUtils.BLE_ARRIVED_DATA)
                onDataArrived(context, sdk, dataType, data)
            }
            BaseActionUtils.ON_CHARACTERISTIC_CHANGE -> onCharacteristicChange()
            BaseActionUtils.ON_BLUETOOTH_ERROR -> onBluetoothError()
            BaseActionUtils.BLE_PRE_CONNECT -> onPreConnect()
        }
    }

    open fun onScanResult(device: WristBand?) {}
    open fun onDataArrived(context: Context?, ble_sdk_type: Int, dataType: Int, data: String?) {}
    open fun connectStatue(isConnect: Boolean) {}
    open fun onCharacteristicChange() {}
    open fun onBluetoothError() {}
    open fun onBluetoothInit() {}
    open fun onCommonSend(data: ByteArray?) {}
    open fun onCmdReceiver(data: ByteArray?) {}
    open fun onPreConnect() {}
}