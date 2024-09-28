package ru.tensor.sbis.version_checker.domain.service

import android.os.Build
import android.os.Bundle
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockedStatic
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.common.testing.mockStatic
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.network_native.apiservice.WorkerService
import ru.tensor.sbis.network_native.apiservice.api.MethodRequestBody
import ru.tensor.sbis.network_native.apiservice.api.RequestResult
import ru.tensor.sbis.network_native.apiservice.api.certificate.SSLCertificateResolver
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.network_native.error.ISbisError
import ru.tensor.sbis.network_native.error.SbisError
import ru.tensor.sbis.network_native.httpclient.Server
import ru.tensor.sbis.network_native.parser.model.BaseModelList
import ru.tensor.sbis.network_native.parser.model.SingleResult
import ru.tensor.sbis.version_checker.data.RemoteVersioningSettingResult
import ru.tensor.sbis.version_checker.data.VersionMapper
import ru.tensor.sbis.version_checker.data.VersioningSettingsHolder
import ru.tensor.sbis.version_checker.domain.service.VersionServiceChecker.Companion.VERSION_SERVICE
import ru.tensor.sbis.version_checker.testUtils.*
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R])
@ExperimentalCoroutinesApi
internal class VersionServiceCheckerTest {

    private val mockSettingsHolder = mock<VersioningSettingsHolder> {
        on { cleanAppId } doReturn TEST_APP_ID
    }
    private var mockVersionMapper = VersionMapper(mockSettingsHolder)
    private var mockApiService = spy(TestApiServiceImpl(mock(), mock(), mock(), mock()))
    private val apiResponseQueue: MutableList<Any> = mutableListOf()
    private val testDispatcher = UnconfinedTestDispatcher()
    private var apiServiceIsPrimed = true
    private var hasSubscribedOnNetworkState = false
    private var mockIsConnected = true

    private lateinit var versionServiceChecker: VersionServiceChecker
    private lateinit var mockNetworkUtils: NetworkUtils
    private lateinit var mNetworkConnectionBehaviorSubject: BehaviorSubject<Boolean>

    private var server: MockedStatic<Server>? = null
    private val mockServer: Server = mock {
        on { host } doAnswer { Server.Host.PROD }
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        buildVersionServiceChecker()
        server = mockStatic {
            on<Server> { Server.getInstance() } doAnswer { mockServer }
        }
    }

    @After
    fun tearDown() {
        server?.close()
        mockIsConnected = true
        apiServiceIsPrimed = true
        hasSubscribedOnNetworkState = false
    }

    @Test
    fun `Receive result and parse successfully`(): Unit = runTest {
        apiResponseQueue.add(getSuccessfulResponse())

        launch {
            versionServiceChecker.update().collect {
                verifyResult(it)
                verify(mockApiService).requestRawResult(any(), eq(VERSION_SERVICE), any())
                cancel()
            }
        }
    }

    @Test
    fun `Try up to 3 times if error continues to happen`() = runTest {
        apiResponseQueue.add("Ошибка!")

        launch {
            versionServiceChecker.update().collect { }
            verify(mockApiService, times(3)).requestRawResult(any(), eq(VERSION_SERVICE), any())
            cancel()
        }
    }

    @Test
    fun `On internet failure subscribe to network state`() = runTest {
        apiResponseQueue.add(getFailureResponse(ISbisError.ERROR_UNAUTHORIZED))
        apiResponseQueue.add(getFailureResponse(SbisError.ERROR_NO_INTERNET))
        apiResponseQueue.add(getFailureResponse(SbisError.ERROR_CONNECTION_LIMIT))

        launch {
            mNetworkConnectionBehaviorSubject.onNext(true)
            versionServiceChecker.update().collect { }
            verify(mockApiService, times(3)).requestRawResult(any(), eq(VERSION_SERVICE), any())
            assertTrue(hasSubscribedOnNetworkState)
            cancel()
        }
    }

    @Test
    fun `On network utils is not connected subscribe to network state`() = runTest {
        apiResponseQueue.add(getFailureResponse(0)) // Неизвестная ошибка
        apiResponseQueue.add(getSuccessfulResponse())
        mockIsConnected = false

        launch {
            mNetworkConnectionBehaviorSubject.onNext(true)
            versionServiceChecker.update().collect {
                verifyResult(it)
                verify(mockApiService, times(2)).requestRawResult(
                    any(),
                    eq(VERSION_SERVICE),
                    any()
                )
                assertTrue(hasSubscribedOnNetworkState)
                cancel()
            }
        }
    }

    @Test
    fun `If it is not network error, do not subscribe to network state`() = runTest {
        apiResponseQueue.add(getFailureResponse(0))
        apiResponseQueue.add(getFailureResponse(-1))
        apiResponseQueue.add(getFailureResponse(-2))

        launch {
            versionServiceChecker.update().collect {
                verifyResult(it)
            }
            verify(mockApiService, times(3)).requestRawResult(any(), eq(VERSION_SERVICE), any())
            assertFalse(hasSubscribedOnNetworkState)
            cancel()
        }
    }

    @Test
    fun `If api service is not primed, do nothing`() = runTest {
        apiResponseQueue.add(getSuccessfulResponse())
        apiResponseQueue.add(getSuccessfulResponse())
        apiResponseQueue.add(getSuccessfulResponse())
        apiServiceIsPrimed = false

        launch {
            versionServiceChecker.update().collect {
                assertNull(it)
                verify(mockApiService, never()).requestRawResult(any(), eq(VERSION_SERVICE), any())
                assertFalse(hasSubscribedOnNetworkState)
                cancel()
            }
        }
    }

    private fun buildVersionServiceChecker() {
        mNetworkConnectionBehaviorSubject = spy(BehaviorSubject.create())
        clearInvocations(mockApiService)
        mockNetworkUtils = mock {
            on { networkStateObservable() } doAnswer {
                hasSubscribedOnNetworkState = true
                mNetworkConnectionBehaviorSubject
            }
            on { isConnected } doAnswer {
                // При проверке обратно подключаемся, чтобы выйти из цикла
                mockIsConnected.also { mockIsConnected = true }
            }
        }
        versionServiceChecker = VersionServiceChecker(
            mockVersionMapper,
            mockApiService,
            mockNetworkUtils,
            testDispatcher,
            testDispatcher
        )
    }

    private fun verifyResult(result: RemoteVersioningSettingResult?) {
        assertNotNull(result)
        result?.let {
            assertEquals(TEST_CRITICAL_VERSION, it.critical?.version)
            assertEquals(TEST_RECOMMENDED_VERSION, it.recommended?.version)
        }
    }

    private fun getSuccessfulResponse(): String {
        val data = Base64.getEncoder().encodeToString(getAndroidVersionsJson().toString().toByteArray())

        val result = JSONObject()
        result.put("ИмяФайла", "android_versions.json")
        result.put("Данные", data)

        val response = JSONObject()
        response.put("result", result)
        response.put("id", 1)
        response.put("protocol", 4)
        return response.toString()
    }

    private fun getFailureResponse(errorCode: Int) = Bundle().apply {
        putInt(WorkerService.EXTRA_ERROR_CODE, errorCode)
        putString(WorkerService.EXTRA_ERROR_BODY_MESSAGE, "")
    }

    private inner class TestApiServiceImpl(
        override val certificateResolver: SSLCertificateResolver,
        override val frescoOkHttpClient: OkHttpClient.Builder,
        override val okHttpClientBuilder: OkHttpClient.Builder,
        override val httpClient: OkHttpClient
    ) : ApiService {
        override fun setBaseUrl(baseUrl: String?) {}
        override fun isPrimed(): Boolean = apiServiceIsPrimed
        override fun <T> request(call: Call, clazz: Class<T>, callback: ApiService.ResultListener<T>) {}
        override fun request(call: Call, callback: ApiService.ResultListener<String?>) {}
        override fun <T> syncRequest(call: Call, clazz: Class<T>): T? = null
        override fun <T> syncRequest(requestBody: MethodRequestBody?, urlPath: String?, clazz: Class<T>): T? = null
        override fun syncRequestList(requestBody: MethodRequestBody?, urlPath: String?): BaseModelList? = null
        override fun syncRequestList(requestBody: RequestBody?, urlPath: String?): SingleResult? = null
        override fun requestList(requestBody: MethodRequestBody, urlPath: String): RequestResult<BaseModelList?> =
            RequestResult(null)

        override fun requestList(
            requestBody: MethodRequestBody?,
            urlPath: String?,
            callback: ApiService.ResultListener<BaseModelList?>
        ) {
        }

        override fun requestList(
            headers: Map<String?, String?>?,
            requestBody: MethodRequestBody?,
            urlPath: String?,
            callback: ApiService.ResultListener<BaseModelList?>
        ) {
        }

        override fun requestResult(requestBody: MethodRequestBody, urlPath: String): RequestResult<SingleResult?> =
            RequestResult(null)

        override fun requestRaw(requestBody: MethodRequestBody, urlPath: String): RequestResult<ResponseBody?> =
            RequestResult(null)

        override fun requestRaw(requestBody: RequestBody, urlPath: String): RequestResult<ResponseBody?> =
            RequestResult(null)

        override fun requestSingleResult(
            requestBody: MethodRequestBody?,
            urlPath: String?,
            callback: ApiService.ResultListener<SingleResult?>
        ) {
        }

        override fun requestSingleResult(
            headers: Map<String?, String?>?,
            requestBody: MethodRequestBody?,
            urlPath: String?,
            callback: ApiService.ResultListener<SingleResult?>
        ) {
        }

        override fun syncRequestSingleResult(requestBody: MethodRequestBody?, urlPath: String?): SingleResult? = null
        override fun syncRequestSingleResult(
            headers: Map<String?, String?>?,
            requestBody: MethodRequestBody?,
            urlPath: String?
        ): SingleResult? = null

        override fun requestRawResult(
            requestBody: MethodRequestBody?,
            urlPath: String?,
            callback: ApiService.ResultListener<String?>
        ) {
            val result = apiResponseQueue.first()
            if (result is String) {
                callback.onSuccess(result)
            } else if (result is Bundle) {
                callback.onFailure(result)
            }
            apiResponseQueue.removeFirst()
        }

        override fun requestRawResult(
            requestBody: MethodRequestBody,
            urlPath: String,
            callback: ApiService.ResultListener<String?>,
            host: Server.Host
        ) {
        }

    }
}
