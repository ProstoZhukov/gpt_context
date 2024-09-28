package ru.tensor.sbis.plugin_struct

import android.app.Application
import android.content.Context
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.plugin_struct.feature.FeatureRegistry

/**
 * Базовая реализация плагина, главное назначение которой сохранение экземпляра приложения.
 *
 * @author kv.martyshenko
 */
abstract class BasePlugin<C> : Plugin<C> {
    lateinit var application: Application
        private set

    /**
     * Темизированный контекст приложения.
     *
     * НУЖНО использовать во всех случаях, вместо [application], если вы используете его только ради контекста.
     *
     * К данному контексту будет применяться тема, выставленная в [Application] при инициализации плагинной системы.
     *
     * Необходим для работы ui-компонентов на глобальных переменных, создаваемых из контекста приложения.
     */
    var themedAppContext: SbisThemedContext? = null

    final override fun FeatureRegistry.setApplication(application: Application, themedContext: SbisThemedContext?) {
        this@BasePlugin.application = application
        themedAppContext = themedContext
    }
}