package ru.tensor.sbis.richtext.converter.sabydoc

import ru.tensor.sbis.jsonconverter.generated.SabyDocMetaAttributes
import ru.tensor.sbis.jsonconverter.generated.SabyDocMetaHandler

/**
 * Обработчик оглавления .sabydoc файла.
 * Является реализацией обработчика атрибута "meta", который идет в начала файла.
 * В теле атрибута хранится json с оглавлением.
 * В случае отсутствия атрибута "meta" в теле документа [SabyDocMetaHandler] проанализирует содержимое файла
 * на предмет наличия заголовков в виде тегов h1..h6 и по окончанию потокового разбора файла сообщит о готовности
 * оглавления посредством обратного вызова [SabyDocMetaHandler.onMetaAttributes].
 *
 * @author am.boldinov
 */
internal class SabyDocContentsProcessor : SabyDocMetaHandler() {

    private var tableOfContents: SabyDocTableOfContents? = null

    override fun onMetaAttributes(attributes: SabyDocMetaAttributes): Boolean {
        val items = attributes.tableOfContents.map {
            SabyDocContentsItem(it.id, it.text, it.level)
        }
        tableOfContents = SabyDocTableOfContents(items)
        return true
    }

    /**
     * Возвращает результат обработки оглавления - модель, содержащую элементы оглавления.
     * Необходимо использовать после завершения потоковой обработки sabydoc файла.
     */
    fun buildResult(): SabyDocTableOfContents {
        val result = tableOfContents ?: SabyDocTableOfContents()
        tableOfContents = null
        return result
    }
}