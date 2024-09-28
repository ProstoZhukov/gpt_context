package ru.tensor.sbis.version_checker_decl

import android.content.Context
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс менеджера сбис-приложений
 *
 * @author ev.grigoreva
 */
interface SbisApplicationManager : Feature {

    /**
     * @return true если приложение уже установлено
     */
    fun isAppInstalled(targetPackageName: String, context: Context): Boolean

    /**
     * Открыть, либо установить приложение
     */
    fun openOrInstall(targetPackageName: String, context: Context)

    /**
     * Открыть магазин приложений.
     */
    fun openMarket(targetPackageName: String, context: Context)

    /**
     * Поставщик реализации [SbisApplicationManager]
     */
    interface Provider : Feature {
        val sbisApplicationManager: SbisApplicationManager
    }
}