/**
 * Инструменты для работы со списками данных для отображения
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.selection.ui.utils

import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.view.item.Item

private val WORD_REGEX = "\\s+".toRegex()
internal typealias ListItem = Item<SelectorItemModel, *>

/**
 * Исключение элементов [selection] из оригинального списка по [SelectorItemModel.id]
 *
 * Эффективность основана на предположении, что размер списка [selection] много меньше оригинального списка и
 * оригинальный список без повторений
 *
 * Производительность сравнивалась с примитивной реализацией
 * ```
 * filterNot { item -> selection.any { selectedItem -> selectedItem.id == item.id } }
 * ```
 * На 3000 элементах с выборкой 1, 10, 100, 1000.
 * Примитивная реализация ∽ 300мс. Предложенная реализация ∽ 50мс
 */
internal fun List<SelectorItemModel>.minusItems(selection: List<SelectorItemModel>): List<SelectorItemModel> =
    when {
        this.isEmpty() || selection.isEmpty() -> this
        /*
        Копия списка для удаления элементов при их нахождении. Это позволяет проводить меньше оценок для последующих
        операций. Хвост оригинального списка будет скопирован с минимальными проверками, если все выбранные элементы
        будут уже найдены
         */
        else -> {
            val selectedItems = ArrayList(selection)
            filterNotTo(ArrayList(size)) { item ->
                if (selectedItems.isNotEmpty()) {
                    val iterator = selectedItems.iterator()
                    while (iterator.hasNext()) {
                        val selectedItem = iterator.next()
                        if (item.id == selectedItem.id) {
                            iterator.remove()
                            return@filterNotTo true
                        }
                    }
                }
                false
            }
        }
    }

/**
 * Получение диапазонов индексов в строке заголовка, где встречаются слова запроса
 *
 * @return [emptyList], если какое-то из слов запроса не содержится в [SelectorItemModel.title]
 */
internal fun SelectorItemModel.getQueryRangeList(searchQuery: String): List<IntRange> {
    val wordList = if (searchQuery.isEmpty())
        emptyList()
    else
        searchQuery.split(WORD_REGEX).filter(String::isNotBlank)
    if (wordList.isEmpty()) {
        return emptyList()
    }
    val rangeList = ArrayList<IntRange>(wordList.size)
    for (word in wordList) {
        val rangeCount = rangeList.size
        var startIndex = title.indexOf(word, ignoreCase = true)
        // найдём все вхождения слова без пересечений
        while (startIndex != -1) {
            val range = startIndex.rangeTo(startIndex + word.length)
            rangeList.add(range)
            startIndex = title.indexOf(word, range.last, ignoreCase = true)
        }
        if (rangeList.size == rangeCount) {
            // какое-то из слов не найдено в заголовке (ни одного нового диапазона). Дальше искать не имеет смысла
            return emptyList()
        }
    }

    if (rangeList.size < 2) {
        // нечего фильтровать, возвращаем ка есть
        return rangeList
    }

    // все слова найдены, нужно избавится от вложенных диапазонов
    rangeList.sortBy { it.first }
    val result = ArrayList<IntRange>()
    with(rangeList.iterator()) {
        var current = next()
        do {
            val next = next()
            // подозрительно на смену диапазона
            if (current.last < next.last) {
                if (current.first != next.first) {
                    // если current не входит в состав next, добавим в результат
                    result.add(current)
                }
                current = next
            }
        } while (hasNext())
        result.add(current)
    }

    // если какое-то из слов полностью входит в состав другого, оно считается не найденным
    return if (result.size < wordList.size) emptyList() else result
}

/**
 *  Подготавливает список специализированных моделей [SearchSpan] для выделения текста
 */
internal fun List<IntRange>.toSearchSpanList(): List<SearchSpan> =
    map { SearchSpan(it.first, it.last) }