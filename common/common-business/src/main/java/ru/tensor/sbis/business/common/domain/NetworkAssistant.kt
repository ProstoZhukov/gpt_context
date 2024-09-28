package ru.tensor.sbis.business.common.domain

import androidx.annotation.AnyThread
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.network_native.httpclient.Server
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Ассистент работы с сетью
 *
 * @param networkUtils класс-утилита проверки состояния интернет соединения
 */
class NetworkAssistant @Inject constructor(
    private val networkUtils: NetworkUtils
) {
    val isConnected: Boolean
        get() = networkUtils.isConnected

    val isDisconnected: Boolean
        get() = networkUtils.isConnected.not()

    fun subscribeToConnected() = networkUtils.networkStateObservable()

    /**
     * Выполнить действие [action] при появлении подключения к сети если предикат [actionFilter] истенен
     *
     * @param action действие при появлении подключения
     * @param actionFilter предикат [action]
     */
    @AnyThread
    fun addOnConnectAction(
        action: () -> Unit,
        actionFilter: () -> Boolean = { true }
    ): Observable<Unit> =
        subscribeToConnected()
            .filter { it && actionFilter() }
            .throttleFirst(ON_CONNECTION_WINDOW_DELAY_MILL_SEC, TimeUnit.MILLISECONDS)
            .delay(connectionActionDelay, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { action() }

    private val connectionActionDelay: Long
        get() {
            return if (host == Server.Host.TEST || host == Server.Host.PRETEST) {
                ON_VPN_CONNECTION_ACTION_DELAY_MILL_SEC
            } else ON_CONNECTION_ACTION_DELAY_MILL_SEC
        }

    private val host by lazy { Server.getInstance().host }
}

private const val ON_CONNECTION_WINDOW_DELAY_MILL_SEC = 500L
/**
 * Задержка выполнения действия при появлении подключения, должна помочь предотвратить
 * ошибку (ErrorMsg=Couldn't resolve host name) для тестовых стендов
 */
private const val ON_VPN_CONNECTION_ACTION_DELAY_MILL_SEC = 1500L
private const val ON_CONNECTION_ACTION_DELAY_MILL_SEC = 0L