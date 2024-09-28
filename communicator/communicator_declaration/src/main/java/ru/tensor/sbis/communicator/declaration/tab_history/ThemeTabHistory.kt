package ru.tensor.sbis.communicator.declaration.tab_history

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для фичи сохранения последней открытой вкладки диалоги/каналы.
 *
 * @author dv.baranov
 */
interface ThemeTabHistory : Feature {

    /**
     * Сохранить navxId последней открытой вкладки диалоги/каналы в SharedPreferences.
     */
    fun saveLastSelectedTab(navxId: String)

    /**
     * Каналы - последняя выбранная вкладка.
     */
    fun chatsIsLastSelectedTab(): Boolean
}