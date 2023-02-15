package com.example.testsdkhealthcare

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.provider.Settings
import android.provider.SyncStateContract.Constants
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.testsdkhealthcare.databinding.ActivityMainBinding
import com.zeroner.blemidautumn.bean.WristBand
import com.zeroner.blemidautumn.bluetooth.BaseBleReceiver
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK
import com.zeroner.blemidautumn.bluetooth.cmdimpl.MtkSendBluetoothCmdImpl
import com.zeroner.blemidautumn.bluetooth.cmdimpl.ProtoBufSendBluetoothCmdImpl
import com.zeroner.blemidautumn.bluetooth.model.ProtoBufRealTimeData
import com.zeroner.blemidautumn.task.BackgroundThreadManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val wristBandLD = MutableLiveData<WristBand>()

    private val isReadStorePermissionGranted
        get() = hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

    private val isWriteStorePermissionGranted
        get() = hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val isLocationPermissionGranted
        get() = hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

    private val isBluetoothPermissionGranted
        get() = hasPermission(this, Manifest.permission.BLUETOOTH)

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            Log.d("TestApp Main Activity", "permission result: $permissions")

            if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true &&
                permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.BLUETOOTH] == true
            ) {
                Log.d("TestApp Main Activity", "permission result: true")
                scanDevice()
            } else {
                Log.d("TestApp Main Activity", "permission result: false")
            }
        }

    private val localBroadcastReceiver = object : BluetoothCallbackReceiver() {
        override fun onScanResult(device: WristBand?) {
            super.onScanResult(device)
            device?.let {
                //Log.d("TestApp Main Activity
                //", "onScanResult: $device")
                if (it.name != "Device-XXXX") {
                    Log.d("TestApp Main Activity", "device 0: $device")
                }

                if (it.address == "FA:FC:B2:C1:45:0E") {//it.address == "04:32:F4:6E:4B:4F"  it.name != "Device-XXXX" it.address == "FA:FC:B2:C1:45:0E"
                    Log.d("TestApp Main Activity", "device: $device")
                    wristBandLD.value = it
                }
            }
        }

        override fun onDataArrived(
            context: Context?,
            ble_sdk_type: Int,
            dataType: Int,
            data: String?
        ) {
            super.onDataArrived(context, ble_sdk_type, dataType, data)
            Log.d("TestApp Main Activity", "onDataArrived: $dataType $data")
        }

        override fun connectStatue(isConnect: Boolean) {
            super.connectStatue(isConnect)
            Log.d("TestApp Main Activity", "connectStatue Test: $isConnect")
        }

        override fun onCharacteristicChange() {
            super.onCharacteristicChange()
        }

        override fun onBluetoothError() {
            super.onBluetoothError()
        }

        override fun onBluetoothInit() {
            super.onBluetoothInit()
        }

        override fun onCommonSend(data: ByteArray?) {
            super.onCommonSend(data)
        }

        override fun onCmdReceiver(data: ByteArray?) {
            super.onCmdReceiver(data)
        }

        override fun onPreConnect() {
            super.onPreConnect()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val networkFilter = BaseActionUtils.getIntentFilter()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(localBroadcastReceiver, networkFilter)

        checkPermissions()

        onClickButton()

        SuperBleSDK.switchSDKTYpe(
            this.applicationContext,
            com.zeroner.blemidautumn.Constants.Bluetooth.Zeroner_protobuf_Sdk
        )
//        CoroutineScope(Dispatchers.Main).launch {
//            delay(3000)
//            MainApplication.instance?.getmService()?.setSDKType(
//                applicationContext,
//                com.zeroner.blemidautumn.Constants.Bluetooth.Zeroner_protobuf_Sdk
//            )
//        }
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            MainApplication.instance?.getmService()?.setSDKType(
                this.applicationContext,
                com.zeroner.blemidautumn.Constants.Bluetooth.Zeroner_protobuf_Sdk
            )
        }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver)
    }

    private fun hasPermission(context: Context, permissionType: String): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            permissionType
        ) == PackageManager.PERMISSION_GRANTED

    private fun checkPermissions(): Boolean {
        Log.d(
            "TestApp Main Activity",
            "checkPermissions: $isReadStorePermissionGranted $isWriteStorePermissionGranted $isLocationPermissionGranted $isBluetoothPermissionGranted"
        )
        if (isReadStorePermissionGranted &&
            isWriteStorePermissionGranted &&
            isLocationPermissionGranted && isBluetoothPermissionGranted
        ) {
            if (checkExternalManager()) {
                return true
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val getpermission = Intent()
                    getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivity(getpermission)
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                    )
                )
            } else {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
        }
        return false
    }

    private fun checkExternalManager(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            false
        }

    private fun scanDevice() {
        SuperBleSDK.createInstance(this).startScan(false)
    }

    private fun connectDevice() {
        Log.d("TestApp Main Activity", "connectDevice: click")
        //name='H110-7678', address='FA:FC:B2:C1:45:0E', pair_code='null', rssi=-51
        /*wristBandLD.observe(this) {
            Log.d("TestApp Main Activity", "connectDevice: ${it}")
            SuperBleSDK.createInstance(applicationContext).setNeedReconnect(true)
            SuperBleSDK.createInstance(this).stopScan()
            SuperBleSDK.createInstance(applicationContext).wristBand = it
            SuperBleSDK.createInstance(applicationContext).connect()
        }*/

        SuperBleSDK.createInstance(applicationContext).setNeedReconnect(true)
        SuperBleSDK.createInstance(this).stopScan()
        SuperBleSDK.createInstance(applicationContext).wristBand =
            WristBand("H110-7678", "FA:FC:B2:C1:45:0E", -43)
        SuperBleSDK.createInstance(applicationContext).connect()
    }

    private fun checkConnection() {
        Log.d(
            "TestApp Main Activity",
            "connectDevice: ${SuperBleSDK.createInstance(this).isConnected}"
        )
    }

    private fun disconnectDevice() {
        SuperBleSDK.createInstance(applicationContext).setNeedReconnect(false)
        val bytes = MtkSendBluetoothCmdImpl.getInstance(this).setUnbind()
        BackgroundThreadManager.getInstance().addWriteData(this, bytes)
        SuperBleSDK.createInstance(applicationContext).disconnect("FA:FC:B2:C1:45:0E", true)
        BackgroundThreadManager.getInstance().clearQueue()
    }

    private fun onClickButton() {
        binding.apply {
            btnScanDevice.setOnClickListener { scanDevice() }

            btnConnectDevice.setOnClickListener { connectDevice() }

            btnDisconnect.setOnClickListener { disconnectDevice() }

            btnCheckConnect.setOnClickListener { checkConnection() }

            /*-----------------SET----------------------------*/

            btnSetTime.setOnClickListener { setTime() }

            btnSetHeartRateAlarm.setOnClickListener {
                setHeartRateAlarm()
            }

            btnSetUserInfo.setOnClickListener { setUserInfo() }

            btnSetSportTarget.setOnClickListener { setSportTarget() }

            btnSetBp.setOnClickListener { setBp() }

            btnDoNotDisturb.setOnClickListener { setDoNotDisturb() }

            btnSetMessageNotify.setOnClickListener { setMessageNotify() }

            btnSetPhoneNotify.setOnClickListener { setPhoneNotify() }

            btnSetWeather.setOnClickListener { setWeather() }

            btnSetSedentaryReminder.setOnClickListener { setSedentaryReminder() }

            btnAddCalendar.setOnClickListener { addCalender() }

            btnSetLanguage.setOnClickListener { setLanguage() }

            btnSetDistance.setOnClickListener { setDistance() }

            btnSetTemp.setOnClickListener { setTemp() }

            btnSetHourFormat.setOnClickListener { setHour() }

            btnSetDateFormat.setOnClickListener { setDate() }

            btnSetAutoSport.setOnClickListener { setAutoSport() }

            btnSetMotorVibrate.setOnClickListener { setMotorVibrate() }

            btnSetSmartShot.setOnClickListener { setSmartShot() }

            /*-----------------GET-----------------------------*/

            btnGetDeviceInfo.setOnClickListener { getDeviceInfo() }

            btnGetHealthData.setOnClickListener { getHeathData() }

            btnGetBattery.setOnClickListener { getBattery() }

            btnItHisData.setOnClickListener { getItHisData() }

            btnGetDataInfo.setOnClickListener { getDataInfo() }

            btnSyncData.setOnClickListener { syncData() }
        }
    }

    /*Set data*/

    @SuppressLint("SimpleDateFormat")
    private fun setTime() {
        val dateString = "2023-02-10 7:34:56"
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = format.parse(dateString)
        val timestamp = date?.time

        /*val calendar = Calendar.getInstance()
        val timestamp = calendar.timeInMillis
        Log.d("TestApp MainActivity", "setTime: $timestamp")*/
        timestamp?.let {
            val bytes = ProtoBufSendBluetoothCmdImpl.getInstance().setTime(it)
            BackgroundThreadManager.getInstance().addWriteData(this, bytes)
        }

        /*val bytes =
            SuperBleSDK.getSDKSendBluetoothCmdImpl(this).setTime()
        BackgroundThreadManager.getInstance().addWriteData(this, bytes)*/
    }

    private fun setHeartRateAlarm() {
        val bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(this).setHeartAlarm(true, 76, 61, 100, 1)
        BackgroundThreadManager.getInstance().addWriteData(this, bytes)
    }

    private fun setUserInfo() {
        Log.d("TestApp Main Activity", "setUserInfo: ")
        val bytes =
            SuperBleSDK.getSDKSendBluetoothCmdImpl(this).setUserConf(175, 80, true, 25, 100, 100)
        BackgroundThreadManager.getInstance().addWriteData(this, bytes)
    }

    private fun setSportTarget() {

    }

    private fun setBp() {

    }

    private fun setDoNotDisturb() {

    }

    private fun setMessageNotify() {

    }

    private fun setPhoneNotify() {
    }

    private fun setWeather() {

    }

    private fun setSedentaryReminder() {
    }

    private fun addCalender() {
    }

    private fun setLanguage() {
        SuperBleSDK.getSDKSendBluetoothCmdImpl(this).setLanguage(this, 0)
    }

    private fun setDistance() {
    }

    private fun setTemp() {
    }

    //Set Hour Format
    private fun setHour() {
        val bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(this).setHourFormat(true)
        BackgroundThreadManager.getInstance().addWriteData(this, bytes)
    }

    //Set Date Format
    private fun setDate() {
        val bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(this).setDateFormat(false)
        BackgroundThreadManager.getInstance().addWriteData(this, bytes)
    }

    private fun setAutoSport() {

    }

    private fun setMotorVibrate() {

    }

    private fun setSmartShot() {
    }

    /*Get data*/

    private fun getDeviceInfo() {
        val bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(this).hardwareFeatures
        BackgroundThreadManager.getInstance().addWriteData(this, bytes)
    }

    private fun getHeathData() {
        val bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(this).realHealthData
        BackgroundThreadManager.getInstance().addWriteData(this, bytes)
    }

    private fun getBattery() {
        val bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(this).battery
        BackgroundThreadManager.getInstance().addWriteData(this, bytes)
    }

    private fun getItHisData() {
        val bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(this).itHisData(3)
        BackgroundThreadManager.getInstance().addWriteData(this, bytes)
    }

    private fun getDataInfo() {
        val bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(this).dataInfo
        BackgroundThreadManager.getInstance().addWriteData(this, bytes)
    }

    private fun syncData() {
        val bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(this).dataDate
        BackgroundThreadManager.getInstance().addWriteData(this, bytes)
    }

}