package ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section.contract

/**
 * Интерфейс для передачи события
 * начала добавления получателей ПиВ от фрагмента держателя
 */
interface AddRecipientListener {
    /** @SelfDocumented */
    fun startAddRecipientProcess()
}