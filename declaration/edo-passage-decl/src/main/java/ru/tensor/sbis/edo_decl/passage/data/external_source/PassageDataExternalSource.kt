package ru.tensor.sbis.edo_decl.passage.data.external_source

import ru.tensor.sbis.edo_decl.passage.Passage
import ru.tensor.sbis.edo_decl.passage.data.PassageData
import java.io.Serializable

/**
 * Внешний источник данных для перехода
 * Реализация не должна захватывать что-либо несериализуемое, можно использовать [Transient]
 *
 * @author sa.nikitin
 */
interface PassageDataExternalSource : Serializable {

    /**
     * Обратный вызов для возврата данных для перехода
     */
    interface Callback {

        /**
         * Определение данных завершено
         */
        fun onComplete(passageData: PassageData)

        /**
         * Определение данных отменено
         */
        fun onCancel()
    }

    /**
     * Запросить данные для перехода
     */
    fun getData(passage: Passage, callback: Callback)
}