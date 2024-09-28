package ru.tensor.sbis.list.base.domain.entity.paging

import androidx.annotation.IntRange
import ru.tensor.sbis.list.base.crud3.data.StrongReference
import ru.tensor.sbis.list.base.data.ResultHelper
import timber.log.Timber
import java.util.TreeMap

/**
 * Содержит в себе полученные от микросервиса данные [DATA] , и хранит постранично, а также операции для получения информации по ним с помощью [helper].
 * @param [maxPages] определяет сколько страниц может хранится единовременно, при получении страницы сверх этакого количества,
 * крайняя страница с "обратного" направления удаляется. Количество не может быть меньше [DEFAULT_PAGES], если параметр содержит меньше этого значения,
 * то лимит все равно будет выставлен в [DEFAULT_PAGES] страницы.
 */
class PagingData<DATA> internal constructor(
    private val helper: ResultHelper<*, DATA>,
    @IntRange(from = MIN_PAGES.toLong()) maxPages: Int = DEFAULT_PAGES,
    private val isEmpty: (Map<Int, DATA>) -> Boolean = { mapToCheck ->
        mapToCheck.isEmpty() || mapToCheck.values.all(
            helper::isEmpty
        )
    },
    private var page: StrongReference = StrongReference(0),
    private val hasPrevious: (Map<Int, DATA>) -> Boolean = { mapToCheck ->
        page.value > 0 || (mapToCheck.isNotEmpty() && (mapToCheck.keys.first() > 0 || helper.hasPrevious(mapToCheck.values.last())))
    },
    private val checkHasNext: (Map<Int, DATA>) -> Boolean = CheckHasNext(helper) { page.value },

    ) {
    constructor(resultHelper: ResultHelper<*, DATA>, maxPages: Int = DEFAULT_PAGES) : this(
        helper = resultHelper,
        maxPages = maxPages
    )

    private val _map: TreeMap<Int, DATA> = TreeMap<Int, DATA>()

    /**
     * Содержит список страниц с индексами, соответствующими порядку отображения страницы на экране в общем списке.
     */
    val map: Map<Int, DATA> = _map

    private val _maxPages = if (maxPages < MIN_PAGES) MIN_PAGES else maxPages
    private val statesMap = TreeMap<Int, PageState>()

    /**
     * Состояния страниц, которые загружались в [PagingData].
     */
    internal val pagingState: Map<Int, PageState> = statesMap

    /**
     * Возвращает true если наименьший индекс страницы больше 0 - считаем, что можем получить предыдущую страницу данных с микросервис.
     * Важно! Пагинация к предыдущим не поддерживается, если начинаем самую первую загрузку данных в центре списка.
     */
    fun hasPrevious() = hasPrevious(map)

    /**
     * Возвращает true если последняя страница содержит признак, что есть еще данные.
     */
    fun hasNext() = checkHasNext(map)

    /**
     * Очистить содержащиеся данные.
     */
    fun clear() {
        _map.clear()
        statesMap.onEach { (page, _) ->
            statesMap[page] = PageState.CLEARED
        }
        page.value = 0
    }

    /**
     * true если содержит хотя бы одну страницу и которая содержит данные.
     */
    fun isEmpty() = isEmpty(map)

    /**
     * Возвращает true если это одна страница и она - информация о заглушке.
     */
    fun isStub(): Boolean {
        return _map.values.singleOrNull()?.run(helper::isStub) ?: false
    }

    /**
     * Возвращает данные-заглушку или null, если данных нет вообще. Метод запрещено вызывать, если [isStub] вернул false.
     */
    fun getStubData() = when (_map.values.size) {
        0 -> null
        1 -> _map.values.single()
        else -> {
            Timber.e(IllegalStateException("Unexpected method call on multiple data"))
            null
        }
    }

    /**
     * Получить данные одним списком.
     */
    fun listOfValues(): List<DATA> = _map.values.toList()

    /**
     * Обновить страницу с данными с порядковым номером [page] новыми [data].
     * Возвращает набор номеров страниц с учетом обрезания лишних сверху или снизу, т.к. количество страниц которые
     * держим в памяти лимитировано [_maxPages].
     */
    fun update(page: Int, data: DATA): Set<Int> {
        if (_map.isEmpty()
            || (page <= _map.lastKey() + 1 && page >= _map.firstKey() - 1)
        ) {
            statesMap[page] = if (_map.containsKey(page)) PageState.UPDATED else PageState.LOADED
            _map[page] = data
        }

        if (page == lastKeyOrZeroIfEmpty())
            cropFromStartUntilItRemains(_maxPages)
        else if (page == firstKeyOrZeroIfEmpty())
            cropFromEndUntilItRemains(_maxPages)

        return _map.keys.toSet()
    }

    /**
     * Удалить страницу с порядковым номером [page]
     */
    fun removePage(page: Int) {
        if (_map.containsKey(page)) {
            _map.remove(page)
            statesMap[page] = PageState.DELETED
        }
    }

    /**
     * Получить первую страницу с данными.
     */
    fun firstPageData(): DATA? = _map.firstEntry()?.value

    /**
     * Получить последнюю страницу с данными.
     */
    fun lastPageData(): DATA? = _map.lastEntry()?.value

    /**
     * Получить номер последней страницы или 0.
     */
    fun lastKeyOrZeroIfEmpty(): Int {
        return if (_map.isEmpty()) 0 else _map.lastKey()
    }

    /**
     * Получить номер первой страницы или 0 если страниц нет.
     */
    fun firstKeyOrZeroIfEmpty(): Int = if (_map.isEmpty()) 0 else _map.firstKey()

    /**
     * То же, что и [isEmpty], но не содержит сведений о наличии следующей или предыдущей страницы.
     */
    fun isEmptyAndNoNextOrPreviousData() = isEmpty() && !hasNextOrPreviousData()


    fun hasNextOrPreviousData() = hasPrevious() || hasNext()


    private fun cropFromStartUntilItRemains(maxPages: Int) {
        while (_map.size > maxPages) cropPage(_map.keys.first())
    }

    private fun cropFromEndUntilItRemains(maxPages: Int) {
        while (_map.size > maxPages) cropPage(_map.keys.last())
    }

    private fun cropPage(page: Int) {
        statesMap[page] = PageState.CROPPED
        _map.remove(page)
    }

    fun increasePage() {
        page.value++
    }

    fun decreasePage() {
        page.value--
    }
}