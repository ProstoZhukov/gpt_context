package ru.tensor.sbis.version_checker.domain.service

import android.os.Bundle
import android.util.Base64
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import ru.tensor.sbis.common.exceptions.LoadDataException
import ru.tensor.sbis.common.exceptions.LoadDataException.Type.DEFAULT
import ru.tensor.sbis.common.exceptions.LoadDataException.Type.NO_INTERNET_CONNECTION
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.network_native.apiservice.WorkerService
import ru.tensor.sbis.network_native.apiservice.api.MethodRequestBody
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.network_native.apiservice.contract.SERVICE
import ru.tensor.sbis.network_native.error.ISbisError.ERROR_CONNECTION_TIMEOUT_CANCEL
import ru.tensor.sbis.network_native.error.ISbisError.ERROR_UNAUTHORIZED
import ru.tensor.sbis.network_native.error.SbisError.ERROR_CONNECTION_LIMIT
import ru.tensor.sbis.network_native.error.SbisError.ERROR_NO_INTERNET
import ru.tensor.sbis.network_native.httpclient.Server
import ru.tensor.sbis.version_checker.data.RemoteVersioningSettingResult
import ru.tensor.sbis.version_checker.data.VersionMapper
import ru.tensor.sbis.version_checker.di.DefaultDispatcher
import ru.tensor.sbis.version_checker.di.IoDispatcher
import ru.tensor.sbis.version_checker.di.VersioningSingletonScope
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * @author as.chadov
 */
@VersioningSingletonScope
internal class VersionServiceChecker @Inject constructor(
    private val versionMapper: VersionMapper,
    private val apiService: ApiService,
    private val networkUtils: NetworkUtils,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {

    private var isAwaitingConnection = AtomicBoolean(false)
    private var networkJob: Job? = null

    /**
     * Получить актуальные облачные настройки версионирования (android_versions.json) через сервис сбис.
     */
    fun update(): Flow<RemoteVersioningSettingResult?> = request()
        .map { result ->
            if (result.isSuccess) {
                Timber.d("Versioning: Convert json ${result.getOrNull()}")
                result.getOrNull()?.let { versionMapper.apply(it) }
            } else {
                throw result.exceptionOrNull()!!
            }
        }
        .retry(RETRIES_MAX_COUNT) {
            Timber.d("Versioning: Retry request on $it")
            delay(RETRY_SECONDS_DELAY)
            true
        }
        .catch { Timber.d(it) }

    private fun request(): Flow<Result<JSONObject?>> = callbackFlow {
        val callback = apiServiceListener(
            onSuccess = { result ->
                try {
                    send(Result.success(convertToJson(result)))
                } catch (e: Exception) {
                    send(Result.failure<JSONObject>(e))
                }
            },
            onFailure = { errorBundle ->
                val code = errorBundle?.getInt(WorkerService.EXTRA_ERROR_CODE)
                val isConnectionError =
                    code == ERROR_CONNECTION_LIMIT ||
                        code == ERROR_NO_INTERNET || code == ERROR_CONNECTION_TIMEOUT_CANCEL
                val isHostError = code == ERROR_UNAUTHORIZED
                if (isConnectionError || isHostError || !networkUtils.isConnected) {
                    subscribeToNetworkState {
                        launch {
                            send(Result.failure<JSONObject>(LoadDataException(NO_INTERNET_CONNECTION)))
                        }
                    }
                } else {
                    val message = errorBundle?.getString(WorkerService.EXTRA_ERROR_BODY_MESSAGE)
                    val error = message?.let(::LoadDataException) ?: LoadDataException(DEFAULT)
                    send(Result.failure<JSONObject>(error))
                }
            }
        )
        Timber.d("Versioning: Request version file")
        // Если выбран кастомный хост, то запрашиваем версии с прода,
        // так как оффлайн не поддерживает проксирование наших вызовов
        // Будет удалено после задачи: https://dev.sbis.ru/opendoc.html?guid=af7c7438-b10e-4e0e-86af-25ad626503bd&client=3
        if (Server.getInstance().host == Server.Host.CUSTOM) {
            apiService.requestRawResult(buildRequestBody(), VERSION_SERVICE, callback, Server.Host.PROD)
        } else if (apiService.isPrimed()) {
            apiService.requestRawResult(buildRequestBody(), VERSION_SERVICE, callback)
        } else {
            send(Result.success(null))
        }
        awaitClose { }
    }.flowOn(ioDispatcher)

    private fun apiServiceListener(onSuccess: suspend (String?) -> Unit, onFailure: suspend (Bundle?) -> Unit) =
        object : ApiService.ResultListener<String?> {
            override fun onSuccess(result: String?) {
                CoroutineScope(defaultDispatcher).launch { onSuccess(result) }
            }

            override fun onFailure(errorBundle: Bundle?) {
                CoroutineScope(defaultDispatcher).launch { onFailure(errorBundle) }
            }
        }

    private fun buildRequestBody(): MethodRequestBody {
        val requestParams = hashMapOf<String, Any>(REPORT_PARAM_NAME to COMMUNICATOR_REPORT_PARAM)
        return MethodRequestBody(
            VERSION_METHOD,
            requestParams
        )
    }

    @Throws(LoadDataException::class)
    private fun convertToJson(result: String?): JSONObject {
        val trimResult = result?.trim { it <= ' ' }.orEmpty()
        val jsonResult = JSONObject(trimResult).getJSONObject(VERSION_RESULT_KEY)
        if (jsonResult.optString(VERSION_FILE_KEY) != VERSION_FILE_NAME) {
            throw LoadDataException(
                "Method $VERSION_METHOD with param $COMMUNICATOR_REPORT_PARAM returned an unexpected result," +
                    "file named ${jsonResult.optString(VERSION_FILE_KEY)} instead of the expected $VERSION_FILE_NAME"
            )
        }
        val base64data = jsonResult.optString(VERSION_DATA_KEY)
        val jsonDataString = String(Base64.decode(base64data, 0))
        if (jsonDataString.isEmpty()) {
            throw LoadDataException(PARSE_ERROR)
        }
        return JSONObject(jsonDataString.trim { it <= ' ' })
    }

    private fun subscribeToNetworkState(onConnected: () -> Unit) {
        isAwaitingConnection.getAndSet(true) && return

        networkJob?.cancel()
        networkJob = CoroutineScope(ioDispatcher).launch {
            networkUtils
                .networkStateObservable()
                .filter { available -> isAwaitingConnection.get().and(available) }
                .subscribe({
                    onConnected()
                    isAwaitingConnection.set(false)
                    networkJob?.cancel()
                }, { Timber.d(it) })
        }
    }

    internal companion object {
        @VisibleForTesting
        const val VERSION_SERVICE = "apps/$SERVICE"
        private const val VERSION_METHOD = "MobileVersionControl.LoadReport"
        private const val REPORT_PARAM_NAME = "name"
        private const val COMMUNICATOR_REPORT_PARAM = "android-communicator"

        private const val VERSION_RESULT_KEY = "result"
        private const val VERSION_DATA_KEY = "Данные"
        private const val VERSION_FILE_KEY = "ИмяФайла"
        private const val VERSION_FILE_NAME = "android_versions.json"

        private const val RETRY_SECONDS_DELAY: Long = 4000
        private const val RETRIES_MAX_COUNT: Long = 2

        private const val PARSE_ERROR = "Failed to convert file $VERSION_FILE_NAME view to json string"
    }
}
