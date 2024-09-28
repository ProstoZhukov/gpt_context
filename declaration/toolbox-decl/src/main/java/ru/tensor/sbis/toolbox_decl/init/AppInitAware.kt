package ru.tensor.sbis.toolbox_decl.init

import android.os.Bundle

/**
 * Интерфейс осведомленности об состоянии инициализации МП.
 *
 * @author ar.leschev
 */
interface AppInitAware {
    /**
     * Инициализация плагинной системы и платформы завершена.
     * Параматры с которыми запускалась активность [savedInstanceState].
     */
    fun appInitializationSucceeded(savedInstanceState: Bundle?) = Unit

    /**
     * Произошла ошибка во время инициализации.
     * @return true, если дальнейшая обработка [state] не требуется и\или реализации метода достаточно.
     */
    fun appInitializationFail(state: InitializationState): Boolean = false

    /**
     * Кастомное действие, если приложение ещё не инициализировано.
     * Если НЕ задано, будет произведена подписка на состояние инициализации и вызваны [appInitializationSucceeded] и [appInitializationFail].
     */
    val appNotInitializedYet: (() -> Unit)?
        get() = null
}