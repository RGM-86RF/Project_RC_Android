package com.antoniogage.projectrc

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

val CHARACTERISTIC_UUID: UUID = UUID.fromString("8451f5a9-bc8b-419b-b075-3838072fda82")
val SERVICE_UUID: UUID = UUID.fromString("fca99450-0455-4e26-8023-7657ec9bb1eb")

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    FAILED,
    READY
}

object Commands{
    val forward : ByteArray = "f".toByteArray()
    val backward : ByteArray = "b".toByteArray()
    val left : ByteArray = "l".toByteArray()
    val right : ByteArray = "r".toByteArray()

}

@SuppressLint("MissingPermission")
class BLEViewModel(application: Application) : AndroidViewModel(application) {
    private var bleGatt: BluetoothGatt? = null
    private var characteristic: BluetoothGattCharacteristic? = null




   private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
   val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus




    private val bleGattCallback = object : BluetoothGattCallback(){

        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int)
        {
            if(status == BluetoothGatt.GATT_SUCCESS) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        _connectionStatus.value = ConnectionStatus.CONNECTED
                        gatt.discoverServices()
                    }

                    BluetoothProfile.STATE_DISCONNECTED -> {
                        _connectionStatus.value = ConnectionStatus.DISCONNECTED
                        bleGatt?.close()
                        bleGatt = null
                        characteristic = null
                    }


                }

            }else {
                _connectionStatus.value = ConnectionStatus.FAILED
                bleGatt?.close()
                bleGatt = null
                characteristic = null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if(status == BluetoothGatt.GATT_SUCCESS){
                characteristic = gatt.getService(SERVICE_UUID)?.getCharacteristic(CHARACTERISTIC_UUID)
                if(characteristic == null){
                    _connectionStatus.value = ConnectionStatus.FAILED
                }else{
                    _connectionStatus.value = ConnectionStatus.READY
                    logConnection("BLE")
                }
            }else{
                _connectionStatus.value = ConnectionStatus.FAILED
            }
        }


    }



    fun connect(context: Context, device: BluetoothDevice){
        if(bleGatt == null) {
            _connectionStatus.value = ConnectionStatus.CONNECTING
            bleGatt = device.connectGatt(context, false, bleGattCallback)
        }

    }



    fun disconnect() {
        bleGatt?.disconnect()
    }


    fun motorWrite(command: ByteArray){
        val gatt = bleGatt
        val char = characteristic
        if(gatt == null || char == null){ return}
        gatt.writeCharacteristic(char,command,BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
    }


    fun onRead(){
        bleGatt?.readCharacteristic(characteristic)
    }

    private fun logConnection(connectionType: String) {
        viewModelScope.launch(Dispatchers.IO){
            val connectionDao = AppDatabase.getDatabase(application).connectionDao()

            val connection = Connections(
                connectionType = connectionType,
                dateTime = System.currentTimeMillis()
            )
            connectionDao.insertAll(connection)
        }
    }




    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCleared() {
        super.onCleared()
        disconnect()
    }












}