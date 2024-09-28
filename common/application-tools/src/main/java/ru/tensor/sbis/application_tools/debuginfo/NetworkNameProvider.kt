package ru.tensor.sbis.application_tools.debuginfo

import android.content.Context
import android.net.ConnectivityManager

/**
 * @author du.bykov
 *
 * @SelfDocumented */
class NetworkNameProvider(private val context: Context) {

    fun getNetworkName(): String {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return if (activeNetwork == null || !activeNetwork.isConnected) {
            "Disconnected"
        } else {
            activeNetwork.typeName
        }
    }
}