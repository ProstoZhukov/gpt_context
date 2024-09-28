package ru.tensor.sbis.fresco.bincontent

import android.os.Looper
import androidx.annotation.WorkerThread
import com.facebook.imagepipeline.image.EncodedImage
import com.facebook.imagepipeline.producers.BaseProducerContextCallbacks
import com.facebook.imagepipeline.producers.Consumer
import com.facebook.imagepipeline.producers.FetchState
import com.facebook.imagepipeline.producers.NetworkFetcher
import com.facebook.imagepipeline.producers.ProducerContext
import ru.tensor.sbis.CXX.SbisException
import ru.tensor.sbis.common.util.CommonUtils
import ru.tensor.sbis.desktop.bincontent.generated.*
import ru.tensor.sbis.frescoutils.FrescoHostIndependentKeyFactory
import ru.tensor.sbis.frescoutils.UiGenericDownloadException
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.lang.ref.WeakReference
import java.net.UnknownHostException
import java.util.concurrent.Executor
import ru.tensor.sbis.common.exceptions.ServiceUnavailableException as UiServiceUnavailableException

/**
 * Реализация [NetworkFetcher], взаимодействующая с микросервисом бинарного контента [BinContentService]
 *
 * @property cancelTaskExecutor [Executor] для публикации событий об отмене в сервис бинарного контента.
 *
 * @author sa.nikitin
 */
internal class BinContentNetworkFetcher(
    private val binContentService: Lazy<BinContentService>,
    private val cancelTaskExecutor: Executor
) : NetworkFetcher<FetchState> {

    override fun createFetchState(consumer: Consumer<EncodedImage>, producerContext: ProducerContext): FetchState =
        FetchState(consumer, producerContext)

    override fun fetch(fetchState: FetchState, callback: NetworkFetcher.Callback) {
        try {
            val binContentUri: String = fetchState.binContentUri()
            fetchAsFile(binContentUri, callback)
            fetchState.context.addCallbacks(
                object : BaseProducerContextCallbacks() {
                    override fun onCancellationRequested() {
                        if (Looper.myLooper() != Looper.getMainLooper()) {
                            cancel(binContentUri, callback)
                        } else {
                            cancelTaskExecutor.execute { cancel(binContentUri, callback) }
                        }
                    }
                }
            )
        } catch (e: Exception) {
            CommonUtils.handleException(e)
            callback.onFailure(e)
        }
    }

    @Throws(
        NoInternetConnectionException::class,
        ServiceUnavailableException::class,
        GenericDownloadException::class,
        SbisException::class
    )
    private fun fetchAsFile(binContentUri: String, callback: NetworkFetcher.Callback) {
        try {
            val wCallback = WeakReference(callback)
            binContentService.value.loadFile(
                binContentUri,
                0,
                object : BinContentCallBack() {
                    override fun bcSBinContentCallBack(filePath: String) {
                        val contentFile = File(filePath)
                        val contentLength: Int = contentFile.length().toInt()
                        wCallback.get()?.onResponse(FileInputStream(filePath), contentLength)
                    }

                    override fun bcSCancelLoadingTaskCallBack(url: String) {
                        wCallback.get()?.onCancellation()
                    }

                    override fun onError(error: BinContentExceptionContainer?) {
                        try {
                            error?.throwException() ?: throw SbisException("Unknown error $binContentUri")
                        } catch (exception: Exception) {
                            onError(exception, wCallback.get() ?: return)
                        }
                    }
                }
            )
        } catch (exception: Exception) {
            onError(exception, callback)
        }
    }

    private fun onError(exception: Exception, callback: NetworkFetcher.Callback) {
        when (exception) {
            is NoInternetConnectionException ->
                callback.onFailure(UnknownHostException(exception.message))
            is UnknownHostException ->
                callback.onFailure(exception)
            is ServiceUnavailableException ->
                callback.onFailure(UiServiceUnavailableException(exception))
            is GenericDownloadException ->
                callback.onFailure(UiGenericDownloadException(exception.errorCode, exception.domain, exception))
            else -> {
                Timber.e(exception)
                callback.onFailure(exception)
            }
        }
    }

    @WorkerThread
    private fun cancel(binContentUri: String, callback: NetworkFetcher.Callback) {
        try {
            binContentService.value.cancelLoadingTask(binContentUri)
            callback.onCancellation()
        } catch (e: Exception) {
            CommonUtils.handleException(e)
            callback.onFailure(e)
        }
    }

    override fun shouldPropagate(fetchState: FetchState?): Boolean = false

    //Реализация необязательна, см. документацию
    override fun onFetchCompletion(fetchState: FetchState?, byteSize: Int) = Unit

    //Возвращаемое значение необязательно, см. документацию
    override fun getExtraMap(fetchState: FetchState?, byteSize: Int): MutableMap<String, String>? = null

    private fun FetchState.binContentUri(): String =
        FrescoHostIndependentKeyFactory.cutSbisHostFromUri(uri) ?: uri.toString()
}