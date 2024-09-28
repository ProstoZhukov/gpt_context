package ru.tensor.sbis.network_native.apiservice.contract

import android.os.Bundle
import androidx.annotation.WorkerThread
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import ru.tensor.sbis.network_native.apiservice.api.MethodRequestBody
import ru.tensor.sbis.network_native.apiservice.api.RequestResult
import ru.tensor.sbis.network_native.apiservice.api.certificate.SSLCertificateResolver
import ru.tensor.sbis.network_native.httpclient.Server.Host
import ru.tensor.sbis.network_native.parser.model.BaseModelList
import ru.tensor.sbis.network_native.parser.model.SingleResult
import ru.tensor.sbis.plugin_struct.feature.Feature

/** Api объекта для работы с сетевыми запросами */
interface ApiService : Feature {

    /** Callback для получения результатов сетевого запроса */
    interface ResultListener<RESULT> {
        /** Сетевой запрос завершился успешно */
        fun onSuccess(result: RESULT)

        /** Сетевой запрос завершился с ошибкой */
        fun onFailure(errorBundle: Bundle?)
    }

    /** @SelfDocumented */
    val certificateResolver: SSLCertificateResolver

    /** @SelfDocumented */
    val frescoOkHttpClient: OkHttpClient.Builder

    /** @SelfDocumented */
    val okHttpClientBuilder: OkHttpClient.Builder

    /** @SelfDocumented */
    val httpClient: OkHttpClient

    /** Метод для установки базового URL, для выполнения сетевых запросов. */
    fun setBaseUrl(baseUrl: String?)

    /** Метод для проверки установленного базового URL. */
    fun isPrimed(): Boolean

    /** Метод для выполнения сетевого запроса. */
    fun <T> request(call: Call, clazz: Class<T>, callback: ResultListener<T>)

    /** Метод для выполнения сетевого запроса. */
    fun request(call: Call, callback: ResultListener<String?>)

    /** Метод для выполнения сетевого запроса. */
    fun <T> syncRequest(call: Call, clazz: Class<T>): T?

    /** Метод для выполнения сетевого запроса со списочным результатом. */
    fun syncRequestList(requestBody: MethodRequestBody?, urlPath: String?): BaseModelList?

    /** Метод для выполнения сетевого запроса. */
    fun <T> syncRequest(requestBody: MethodRequestBody?, urlPath: String?, clazz: Class<T>): T?

    /** Метод для выполнения сетевого запроса со списочным результатом. */
    fun syncRequestList(requestBody: RequestBody?, urlPath: String?): SingleResult?

    /** Метод для выполнения сетевого запроса со списочным результатом. */
    @WorkerThread
    fun requestList(requestBody: MethodRequestBody, urlPath: String): RequestResult<BaseModelList?>

    /** Метод для выполнения сетевого запроса с единственным результатом. */
    @WorkerThread
    fun requestResult(requestBody: MethodRequestBody, urlPath: String): RequestResult<SingleResult?>

    /** Метод для выполнения сетевого запроса с результатом в формате RAW. */
    @WorkerThread
    fun requestRaw(requestBody: MethodRequestBody, urlPath: String): RequestResult<ResponseBody?>

    /** Метод для выполнения сетевого запроса с результатом в формате RAW. */
    @WorkerThread
    fun requestRaw(requestBody: RequestBody, urlPath: String): RequestResult<ResponseBody?>

    /** Метод для выполнения сетевого запроса с единственным результатом. */
    fun requestSingleResult(
        requestBody: MethodRequestBody?,
        urlPath: String?,
        callback: ResultListener<SingleResult?>
    )

    /** Метод для выполнения сетевого запроса с единственным результатом. */
    fun requestSingleResult(
        headers: Map<String?, String?>?,
        requestBody: MethodRequestBody?,
        urlPath: String?,
        callback: ResultListener<SingleResult?>
    )

    /** Метод для выполнения сетевого запроса с единственным результатом. */
    fun syncRequestSingleResult(requestBody: MethodRequestBody?, urlPath: String?): SingleResult?

    /** Метод для выполнения сетевого запроса с единственным результатом. */
    fun syncRequestSingleResult(
        headers: Map<String?, String?>?,
        requestBody: MethodRequestBody?,
        urlPath: String?
    ): SingleResult?

    /** Метод для выполнения сетевого запроса с результатом в формате RAW. */
    fun requestRawResult(
        requestBody: MethodRequestBody?,
        urlPath: String?,
        callback: ResultListener<String?>
    )

    /** Метод для выполнения сетевого запроса на произвольном стенде [host] вне зависимости от текущего
     * с результатом в формате RAW. */
    fun requestRawResult(
        requestBody: MethodRequestBody,
        urlPath: String,
        callback: ResultListener<String?>,
        host: Host
    )

    /** Метод для выполнения сетевого запроса со списочным результатом. */
    fun requestList(
        requestBody: MethodRequestBody?,
        urlPath: String?,
        callback: ResultListener<BaseModelList?>
    )

    /** Метод для выполнения сетевого запроса со списочным результатом. */
    fun requestList(
        headers: Map<String?, String?>?,
        requestBody: MethodRequestBody?,
        urlPath: String?,
        callback: ResultListener<BaseModelList?>
    )

    /** Интерфейс предоставляет доступ к компоненту [ApiService] */
    fun interface Provider : Feature {
        fun apiService(): ApiService
    }
}