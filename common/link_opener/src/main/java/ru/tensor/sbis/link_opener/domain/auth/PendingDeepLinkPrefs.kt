package ru.tensor.sbis.link_opener.domain.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri

/**
 * Сохраняет интент с `LaunchActivity` для открытия на `MainActivity` после авторизации.
 * Используется, если пытались открыть документ по ссылке, когда пользователь еще не был авторизован.
 * Сохраняется в SharedPreferences, т.к. приложение может быть перезапущено после авторизации,
 * например, если языковая локаль не соответсвует текущей.
 */
internal object PendingDeepLinkPrefs {

    private var prefs: SharedPreferences? = null

    /**
     * Получить [Intent] на открытие ссылки, если она была.
     */
    fun getIntentIfAny(context: Context): Intent? {
        if (isLinkExpired(context)) {
            removeIntent(context)
            return null
        }

        val data = getPrefs(context).getString(PENDING_DEEP_LINK_DATA_KEY, null)
        return data?.let {
            Intent(Intent.ACTION_VIEW).apply { this.data = Uri.parse(it) }
        }
    }

    /**
     * Сохранить `data` из [intent] для открытия ссылки после авторизации, если попытались открыть
     * до авторизации.
     */
    fun saveIntent(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_VIEW || intent.data == null) {
            return
        }
        getPrefs(context).edit()
            .putString(PENDING_DEEP_LINK_DATA_KEY, intent.data.toString())
            .putLong(PENDING_DEEP_LINK_TIMESTAMP_KEY, System.currentTimeMillis())
            .apply()
    }

    /**
     * Удалить ссылку к открытию после открытия.
     */
    fun removeIntent(context: Context) =
        getPrefs(context).edit()
            .remove(PENDING_DEEP_LINK_DATA_KEY)
            .remove(PENDING_DEEP_LINK_TIMESTAMP_KEY)
            .apply()

    private fun isLinkExpired(context: Context): Boolean {
        val linkSavedTime = getPrefs(context).getLong(PENDING_DEEP_LINK_TIMESTAMP_KEY, 0)
        return (System.currentTimeMillis() - linkSavedTime) >= LINK_LIFE_EXPECTANCY_MS
    }

    private fun getPrefs(context: Context): SharedPreferences {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PENDING_DEEP_LINK_PREFS, Context.MODE_PRIVATE)
        }
        return prefs!!
    }

    private const val LINK_LIFE_EXPECTANCY_MS = 240000 // 4 минуты
    private const val PENDING_DEEP_LINK_PREFS = "PENDING_DEEP_LINK_PREFS"
    private const val PENDING_DEEP_LINK_DATA_KEY = "PENDING_DEEP_LINK_DATA_KEY"
    private const val PENDING_DEEP_LINK_TIMESTAMP_KEY = "PENDING_DEEP_LINK_TIMESTAMP"
}