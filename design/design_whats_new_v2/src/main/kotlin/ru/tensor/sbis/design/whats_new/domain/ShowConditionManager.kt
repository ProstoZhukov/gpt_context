package ru.tensor.sbis.design.whats_new.domain

/**
 * Класс для сохранения версии, для которой показан "Что нового" а также проверки отображения для текущей версии.
 *
 * @author ps.smirnyh
 */
internal interface ShowConditionManager {

    /** Сохранить текущую версию, для которой был показан "Что нового". */
    fun saveShowing()

    /**
     * Проверить, отображался ли экран "Что нового" для текущей версии.
     * @return false если экран не отображался для текущей версии.
     * @return true если экран уже был показан для текущей версии.
     */
    fun checkShowing(): Boolean
}