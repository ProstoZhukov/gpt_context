package ru.tensor.sbis.cadres_docs_decl.achievements

/**
 * Контракт обработчика изменения состояния документа (просмотр/редактирование) ПиВ.
 */
interface AchievementsChangeScreenStateListener {
    fun changeScreenState(newState: AchievementsScreenState)
}