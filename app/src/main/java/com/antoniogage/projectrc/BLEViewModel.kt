package com.antoniogage.projectrc

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

object commands{
    val forward : ByteArray = "f".toByteArray()
    val backward : ByteArray = "b".toByteArray()
    val left : ByteArray = "l".toByteArray()
    val right : ByteArray = "r".toByteArray()

    val stop : ByteArray = "s".toByteArray()

}

@SuppressLint("MissingPermission")
class BLEViewModel() : ViewModel() {
    private var BLEgatt: BluetoothGatt? = null
    private var characteristic: BluetoothGattCharacteristic? = null


   private val connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
   val _connectionStatus: StateFlow<ConnectionStatus> = connectionStatus




    private val bleGatt = object : BluetoothGattCallback(){

        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int)
        {
            when(newState){
                BluetoothProfile.STATE_CONNECTED -> {
                    connectionStatus.value = ConnectionStatus.CONNECTED
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    connectionStatus.value = ConnectionStatus.DISCONNECTED
                    BLEgatt?.close()
                    BLEgatt = null
                    characteristic = null
                }
                else -> {
                    connectionStatus.value = ConnectionStatus.FAILED
                }

                }

        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if(status == BluetoothGatt.GATT_SUCCESS){
                characteristic = gatt.getService(SERVICE_UUID).getCharacteristic(CHARACTERISTIC_UUID)
                if(characteristic == null){
                    connectionStatus.value = ConnectionStatus.FAILED
                }else{
                    connectionStatus.value = ConnectionStatus.READY
                }
            }else{
                connectionStatus.value = ConnectionStatus.FAILED
            }
        }


    }



    fun connect(context: Context, device: BluetoothDevice){
        if(BLEgatt == null) {
            connectionStatus.value = ConnectionStatus.CONNECTING
            BLEgatt = device.connectGatt(context, false, bleGatt)
        }

    }



    fun disconnect() {
        BLEgatt?.disconnect()
    }


    fun motorWrite(command: ByteArray){
        if(bleGatt == null || characteristic == null){ return}
        BLEgatt?.writeCharacteristic(characteristic!!,command,BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
    }


    fun onRead(){
        BLEgatt?.readCharacteristic(characteristic)
    }




    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCleared() {
        super.onCleared()
        disconnect()
    }












}