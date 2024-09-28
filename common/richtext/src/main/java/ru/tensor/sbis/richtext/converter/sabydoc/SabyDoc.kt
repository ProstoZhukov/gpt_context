package ru.tensor.sbis.richtext.converter.sabydoc

import android.text.Spannable

/**
 * Модель-представление .sabydoc файла после парсинга и конвертации.
 *
 * @property text стилизованная строка для рендера во View
 * @property tableOfContents оглавление документа
 *
 * @author am.boldinov
 */
class SabyDoc(val text: Spannable, val tableOfContents: SabyDocTableOfContents)

/**
 * Модель-представление оглавления .sabydoc файла.
 *
 * @property items список элементов оглавления
 */
data class SabyDocTableOfContents(val items: List<SabyDocContentsItem> = emptyList()) {

    /**
     * Является ли оглавление пустым (признак отсутствия оглавления для документа).
     */
    fun isEmpty() = items.isEmpty()
}

/**
 * Модель элемента оглавления .sabydoc файла.
 *
 * @property id идентификатор элемента (заголовка h1...h6 в тексте)
 * @property text название элемента (совпадает с заголовком h1...h6 в тексте)
 * @property level уровень вложенности элемента в другие элементы оглавления
 *           (можно использовать для смещения относительно края при отрисовке)
 */
data class SabyDocContentsItem(
    val id: String,
    val text: String,
    val level: Int
)