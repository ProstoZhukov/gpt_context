package ru.tensor.sbis.network_native.apiservice.contract

import android.content.Context
import androidx.annotation.NonNull
import ru.tensor.sbis.network_native.httpclient.CookieManager

/** Объект, отвечающий за инициализацию объекта [ApiService] */
internal object ApiServiceInitializer {

    /** @SelfDocumented */
    fun buildApiService(@NonNull context: Context, @NonNull cookieManager: CookieManager): ApiService =
        ApiServiceImpl(context, cookieManager)
}