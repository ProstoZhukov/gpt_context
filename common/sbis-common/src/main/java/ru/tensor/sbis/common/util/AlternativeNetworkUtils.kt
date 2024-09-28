package ru.tensor.sbis.common.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Альтернативный вариант подписки на состояние сети. Допускает только асинхронные подписки. Более точно отслеживает состояние сети.
 * @author ma.kolpakov
 */
// TODO: Добавить синхронный метод плучения состояния, НЕ использовать его внутри колл беков. И запстить устаревание старой версии https://online.sbis.ru/opendoc.html?guid=6a6cb239-98c9-4dbc-bc65-5393b3b3fb42 
class AlternativeNetworkUtils(context: Context) : ConnectivityManager.NetworkCallback(), Feature {
    private val mConnectivityManager: ConnectivityManager
    private val behaviorSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
        .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        .build()

    val mConnectivityStateObservable: Observable<Boolean> = behaviorSubject.observeOn(AndroidSchedulers.mainThread())

    init {
        mConnectivityManager = getConnectivityManager(context)
        mConnectivityManager.registerNetworkCallback(networkRequest, this)
    }

    override fun onAvailable(network: Network) {
        behaviorSubject.onNext(true)
    }

    override fun onLost(network: Network) {
        behaviorSubject.onNext(false)
    }

    fun isConnected(): Boolean {
        //Оборачиваем в try/catch, т.к. при неясных обстоятельсвах бросается SecurityException
        //Проблема в коде Android 11 https://issuetracker.google.com/issues/175055271
        try {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isConnectedApi23()
            } else {
                isConnectedLegacy()
            }
        } catch (exception: Exception) {
            CommonUtils.handleException(exception)
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isConnectedApi23(): Boolean {
        val activeNetwork = mConnectivityManager.activeNetwork
        val capabilities = mConnectivityManager.getNetworkCapabilities(activeNetwork)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }

    private fun isConnectedLegacy(): Boolean {
        //Метод getActiveNetworkInfo помечен устаревшим с 29 API
        val activeNetwork = mConnectivityManager.activeNetworkInfo
        return if (activeNetwork != null && activeNetwork.isConnectedOrConnecting) {
            activeNetwork.isConnectedOrConnecting && activeNetwork.type in arrayOf(
                ConnectivityManager.TYPE_MOBILE,
                ConnectivityManager.TYPE_WIFI,
                ConnectivityManager.TYPE_VPN,
                ConnectivityManager.TYPE_ETHERNET
            )
        } else false
    }

    private fun getConnectivityManager(context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}