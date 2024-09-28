package ru.tensor.sbis.network_native.httpclient

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.tensor.sbis.network_native.error.SbisError
import timber.log.Timber
import java.io.IOException


/**
 * Реализация колбека, которая делегирует обработку в [_callback] положительного и негативного ответа. Неудачный вызов,
 * при этом, тоже обрабатывается как негативный результат.
 */
internal class CustomCallbackHandler(private val _callback: HttpResponseCallback) : Callback {
    override fun onResponse(call: Call, response: Response) {
        if (!response.isSuccessful) {
            _callback.onFailure(SbisError(response.message, response.code))
        } else {
            try {
                _callback.onResponse(
                    HttpResponse(
                        if (response.body != null) response.body!!
                            .string() else null, response.code
                    )
                )
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    override fun onFailure(call: Call, e: IOException) {
        Timber.d(e)
        _callback.onFailure(SbisError(e.message, SbisError.ERROR_CONNECTION_TIMEOUT_CANCEL))
    }
}