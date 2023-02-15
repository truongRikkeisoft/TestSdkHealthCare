package com.example.testsdkhealthcare

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.zeroner.blemidautumn.Constants
import com.zeroner.blemidautumn.bean.WristBand
import com.zeroner.blemidautumn.bluetooth.IBle
import com.zeroner.blemidautumn.bluetooth.IDataReceiveHandler
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK
import com.zeroner.blemidautumn.bluetooth.impl.BleService
import com.zeroner.blemidautumn.utils.ByteUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainApplication : Application() {

    private lateinit var mService: BleService;
    private var mBle: IBle? = null

    private val mServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, rawBinder: IBinder?) {
                try {
                    mService = (rawBinder as BleService.LocalBinder).service
                    mBle = mService.ble
                } catch (e: Exception) {
                    Log.d("TestApp Application", "onServiceConnected: $e")
                }
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                TODO("Not yet implemented")
            }

        }
    }

    override fun onCreate() {
        super.onCreate()

        /*Register Service*/
        val bindIntent = Intent(this, BleService::class.java)
        stopService(bindIntent)
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE)

        SuperBleSDK.addBleListener(this, object : IDataReceiveHandler {
            override fun onDataArrived(ble_sdk_type: Int, dataType: Int, data: String?) {
                Log.d("TestApp Application", "onDataArrived: $ble_sdk_type $dataType $data")
                val intent = Intent(BaseActionUtils.ON_DATA_ARRIVED)
                intent.putExtra(BaseActionUtils.BLE_SDK_TYPE, ble_sdk_type)
                intent.putExtra(BaseActionUtils.BLE_DATA_TYPE, dataType)
                intent.putExtra(BaseActionUtils.BLE_ARRIVED_DATA, data)
                LocalBroadcastManager.getInstance(this@MainApplication).sendBroadcast(intent)
            }

            override fun onScanResult(device: WristBand?) {
                Log.d("TestApp Application", "onScanResult: $device")
                val intent = Intent(BaseActionUtils.ON_SCAN_RESULT)
                intent.putExtra(BaseActionUtils.BLE_SCAN_RESULT_DEVICE, device)
                LocalBroadcastManager.getInstance(this@MainApplication).sendBroadcast(intent)
            }

            override fun onBluetoothInit() {
                Log.d("TestApp Application", "onBluetoothInit: ")
                val intent = Intent(BaseActionUtils.ON_BLUETOOTH_INIT)
                LocalBroadcastManager.getInstance(this@MainApplication).sendBroadcast(intent)
            }

            override fun connectStatue(isConnect: Boolean) {
                Log.d("TestApp Application", "connectStatue: $isConnect")
                val intent = Intent(BaseActionUtils.ON_CONNECT_STATUE)
                intent.putExtra(BaseActionUtils.BLE_CONNECT_STATUE, isConnect)
                LocalBroadcastManager.getInstance(this@MainApplication).sendBroadcast(intent)
            }

            override fun onDiscoverService(serviceUUID: String?) {
                Log.d("TestApp Application", "onDiscoverService: $serviceUUID")
                val intent = Intent(BaseActionUtils.ON_DISCOVER_SERVICE)
                intent.putExtra(BaseActionUtils.BLE_SERVICE_UUID, serviceUUID)
                LocalBroadcastManager.getInstance(this@MainApplication).sendBroadcast(intent)
            }

            override fun onDiscoverCharacter(characterUUID: String?) {
                Log.d("TestApp Application", "onDiscoverCharacter: $characterUUID")
                val intent = Intent(BaseActionUtils.ON_DISCOVER_CHARACTER)
                intent.putExtra(BaseActionUtils.BLE_CHARACTER_UUID, characterUUID)
                LocalBroadcastManager.getInstance(this@MainApplication).sendBroadcast(intent)
            }

            override fun onCommonSend(data: ByteArray?) {
                Log.d("TestApp Application", "onCommonSend: $data")
                val intent = Intent(BaseActionUtils.ON_COMMON_SEND)
                intent.putExtra(BaseActionUtils.BLE_COMMON_SEND, data)
                LocalBroadcastManager.getInstance(this@MainApplication).sendBroadcast(intent)
            }

            override fun onCmdReceive(data: ByteArray?) {
                Log.d("TestApp Application", "onCmdReceive: ${ByteUtil.bytesToString(data)}")
                val intent = Intent(BaseActionUtils.ON_COMMON_RECEIVER)
                intent.putExtra(BaseActionUtils.ON_COMMON_RECEIVER, data)
                LocalBroadcastManager.getInstance(this@MainApplication).sendBroadcast(intent)
            }

            override fun onCharacteristicChange(address: String?) {
                Log.d("TestApp Application", "onCharacteristicChange: $address")
                val intent = Intent(BaseActionUtils.ON_CHARACTERISTIC_CHANGE)
                intent.putExtra(BaseActionUtils.BLE_BLUETOOTH_ADDRESS, address)
                LocalBroadcastManager.getInstance(this@MainApplication).sendBroadcast(intent)
            }

            override fun onBluetoothError() {
                Log.d("TestApp Application", "onBluetoothError: ")
                val intent = Intent(BaseActionUtils.ON_BLUETOOTH_ERROR)
                LocalBroadcastManager.getInstance(this@MainApplication).sendBroadcast(intent)
            }

            override fun onPreConnect() {
                Log.d("TestApp Application", "onPreConnect: ")
                val intent = Intent(BaseActionUtils.BLE_PRE_CONNECT)
                LocalBroadcastManager.getInstance(this@MainApplication).sendBroadcast(intent)
            }

            override fun noCallback() {
                Log.d("TestApp Application", "noCallback: ")
                val intent = Intent(BaseActionUtils.BLE_NO_CALLBACK)
                LocalBroadcastManager.getInstance(this@MainApplication).sendBroadcast(intent)
            }

            override fun onConnectionStateChanged(state: Int, newState: Int) {
                Log.d("TestApp Application", "onConnectionStateChanged: $state $newState")
            }

            override fun onSdkAutoReconnectTimesOut() {
                Log.d("TestApp Application", "onSdkAutoReconnectTimesOut: ")
            }

        })


    }

    fun getmService(): BleService {
        return mService
    }

    companion object {
        // 实例化一次
        var instance: MainApplication? = null
            private set
    }
}