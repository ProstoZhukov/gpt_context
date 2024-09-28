package ru.tensor.sbis.common.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import io.reactivex.Observable
import java.lang.IllegalStateException

/**
 * Класс для работы с bluetooth устройствами
 */
class BluetoothUtils {

    private val bthAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    /**
     * Поле для получения информации об активности bluetooth
     * @return true - если bluetooth включен
     */
    val enable: Boolean
        get() = bthAdapter?.isEnabled ?: throw IllegalStateException("На устройстве отсутствует Bluetooth")

    private val devices: List<BluetoothDevice>
        get() = bthAdapter?.bondedDevices?.toList() ?: throw IllegalStateException("На устройстве отсутствует Bluetooth")

    /**
     * Функция для получения информации об активности bluetooth
     * @return true - если bluetooth включен
     */
    fun getBluetoothActivity(): Observable<Boolean> = Observable.fromCallable { enable }

    /**
     * Функция для получения списка подключенных устройств
     */
    fun loadDevices(): Observable<List<BluetoothDevice>> = Observable.fromCallable { devices }

    /** Функция для получения списка подключенных устройств, эмиттит девайсы по отдельности. */
    fun loadDevicesIterable(): Observable<BluetoothDevice> = Observable.fromIterable(devices)
}