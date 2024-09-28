package ru.tensor.sbis.widget_player.converter.sabydoc

import ru.tensor.sbis.jsonconverter.generated.SabyDocMetaAttributes
import ru.tensor.sbis.jsonconverter.generated.SabyDocMetaHandler
import ru.tensor.sbis.richtext.converter.sabydoc.SabyDocContentsItem
import ru.tensor.sbis.richtext.converter.sabydoc.SabyDocTableOfContents

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
internal class SabyDocContentsBuilder : SabyDocMetaHandler() {

    private var tableOfContents: SabyDocTableOfContents? = null

    override fun onMetaAttributes(attributes: SabyDocMetaAttributes): Boolean {
        val items = attributes.tableOfContents.map {
            SabyDocContentsItem(it.id.replace("toc_", ""), it.text, it.level)
        }
        tableOfContents = SabyDocTableOfContents(items)
        return true
    }

    /**
     * Возвращает результат обработки оглавления - модель, содержащую элементы оглавления.
     * Необходимо использовать после завершения потоковой обработки sabydoc файла.
     */
    fun build(): SabyDocTableOfContents {
        val result = tableOfContents ?: SabyDocTableOfContents()
        tableOfContents = null
        return result
    }
}