package ru.tensor.sbis.network_native.httpclient

import okhttp3.Cookie
import okhttp3.HttpUrl
import timber.log.Timber

/**
 * Класс-заглушка для инициализации объекта [CookieManager],
 * используется для приложений, в которых не подключен модуль авторизации.
 *
 * @author da.pavlov1
 * */
class CookieManagerStub : CookieManager {

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        Timber.w("'loadForRequest' not implemented for ${this::class.simpleName}, use ${CookieManager::class.simpleName} from LoginSingletonModule")
        return mutableListOf()
    }

    override fun getFormattedCookie(): String? {
        Timber.w("'getFormattedCookie' not implemented for ${this::class.simpleName}, use ${CookieManager::class.simpleName} from LoginSingletonModule")
        return null
    }

    override fun getTokenId(): String? {
        Timber.w("'getTokenId' not implemented for ${this::class.simpleName}, use ${CookieManager::class.simpleName} from LoginSingletonModule")
        return null
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        Timber.w("'saveFromResponse' not implemented for ${this::class.simpleName}, use ${CookieManager::class.simpleName} from LoginSingletonModule")
    }
}