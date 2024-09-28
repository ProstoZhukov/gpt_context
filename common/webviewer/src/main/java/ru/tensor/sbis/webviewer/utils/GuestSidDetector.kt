package ru.tensor.sbis.webviewer.utils

import ru.tensor.sbis.network_native.cookies.clearCookiesSafe
import ru.tensor.sbis.network_native.cookies.getCookieManagerSafe
import timber.log.Timber

/**
 * Очистить куки, если замечены гостевые sid'ы
 * Гостевые uuid могут приводить к редиректам на страницу авторизации, т.к при наличии авторизации в МП мы всегда имеем токен.
 * Веб не смотрит на него, при наличии гостевых sid.
 *
 * @author ar.leschev
 */
class GuestSidDetector {
    private val manager by lazy(LazyThreadSafetyMode.NONE) { getCookieManagerSafe() }

    /**
     * Произвести очистку кук при наличии гостевых sid'ов.
     *
     * После авторизации какой-то из запросов на онлайн может выставить гостевые sid`ы в куки (их несколько),
     * которые необходимо стереть, чтобы онлайн выставил уже не гостевые.
     */
    fun clearCookiesIfGuestSidsFound(url: String?) {
        url ?: return
        if (hasGuestSidCookies(url)) clear()
    }

    private fun hasGuestSidCookies(url: String): Boolean =
        manager?.getCookie(url)?.split(DELIMITER)
            ?.filter { it.contains(SID_SCHEMA) }
            ?.any { it.endsWith(GUEST_SID_SUFFIX) }
            ?: false

    private fun clear() {
        Timber.d("Guest sids detected, clear cookies")
        clearCookiesSafe()
    }

    private companion object {
        /** Разделитель кук */
        const val DELIMITER = "; "

        /** Ключ по которому можно собрать все куки сессий */
        const val SID_SCHEMA = "sid"

        /** Сессионные uuidы кончаются на данную последовательноть символов */
        const val GUEST_SID_SUFFIX = "1111111111111111"
    }
}