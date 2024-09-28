package ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section.contract

import ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section.AchievementsStubContent
import ru.tensor.sbis.design.stubview.StubViewCase

/**
 * Контракт обработчика показа заглушки для секции ПиВ
 *
 * Подразумевается, что секции шапки ПиВ является "ведущей" на экране, то есть предоставляет
 * основную информацию, при отсутствии которой показ остальной информации нецелесообразен.
 * Поэтому секция с помощью данного интерфейса умеет уведомлять потребителя секции о необходимости
 * показывать/скрывать заглушки.
 *
 * Если секция попросила спрятать заглушку, значит данные для показа у неё точно есть.
 */
interface AchievementsStubListener {

    /** Показать заглушку */
    fun showStub(content: AchievementsStubContent)

    /** Скрыть заглушку */
    fun hideStub()
}