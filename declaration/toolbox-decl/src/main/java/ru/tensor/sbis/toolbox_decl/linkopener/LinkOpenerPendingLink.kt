package ru.tensor.sbis.toolbox_decl.linkopener

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Сохранение интента на открытие ссылки на экране авторизации для открытии после авторизации.
 */
interface LinkOpenerPendingLinkFeature : Feature {

    /**
     * Сохранить интент на открытие ссылки.
     */
    fun saveLink(context: Context, intent: Intent)

    /**
     * Получить интент на открытие ссылки.
     */
     fun getLink(context: Context): Intent?

    /** Поставщик [LinkOpenerPendingLinkFeature]. */
    interface Provider : Feature {
        val linkOpenerPendingLinkFeature: LinkOpenerPendingLinkFeature
    }

}