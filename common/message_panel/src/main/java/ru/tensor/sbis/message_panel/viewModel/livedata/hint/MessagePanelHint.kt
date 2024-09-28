package ru.tensor.sbis.message_panel.viewModel.livedata.hint

import androidx.annotation.StringRes
import io.reactivex.Observable

/**
 * Интерфейс взаимодействия с подсказкой в поле ввода
 *
 * @author vv.chekurda
 */
interface MessagePanelHint {

    /**
     * Настройка подсказок
     */
    var hintConfig: MessagePanelHintConfig

    /**
     * Подписка на изменение подсказок
     */
    val messageHint: Observable<Int>

    /**
     * Устанавливает подсказку
     *
     * @see resetHint
     */
    fun setHint(@StringRes hintRes: Int)

    /**
     * Обновить конфиг подсказок
     */
    fun updateHintConfig(config: MessagePanelHintConfig)

    /**
     * Устанавливает подсказку при выключении панели ввода
     *
     * @see resetHint
     */
    fun applyDisabledHint()

    /**
     * Устанавливает подсказку при включении панели ввода
     *
     * @see resetHint
     */
    fun applyEnabledHint()

    /**
     * Откатывает подсказку [setHint] к последнему значению *applyXHint* метода
     */
    fun resetHint()
}