package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.appdesign.selection.data.DemoFilter
import ru.tensor.sbis.appdesign.selection.data.DemoServiceData
import ru.tensor.sbis.appdesign.selection.data.DemoServiceResult
import java.util.*
import kotlin.math.min
import kotlin.random.Random

internal val CHOOSE_ALL_ITEM = DemoServiceData(
    UUID.randomUUID(),
    title = "Выбрать все",
    subtitle = null,
    hasNested = false,
    counter = Random.nextInt(0, 1000000000)
)

/**
 * @author ma.kolpakov
 */
object DemoRegionController : DemoController<DemoServiceResult, DemoFilter> {

    private const val count: Int = 80

    private val data: List<DemoServiceData> = generateDataList("Demo data")

    private val subData: Map<UUID, List<DemoServiceData>> = mapOf(*data.mapNotNull {
        if (it.hasNested) it.id to generateDataList("Sub demo data") else null
    }.toTypedArray())

    /**
     * В примере метод эквивалентен [refresh] так как нет работы с DataRefreshCallback
     */
    override fun list(filter: DemoFilter): DemoServiceResult = refresh(filter)

    override fun refresh(filter: DemoFilter): DemoServiceResult =
        with(filter.parentId?.let(subData::get) ?: listOf(CHOOSE_ALL_ITEM) + data) {
            when {
                filter.query.isEmpty()  -> this
                filter.query == "error" -> error("Demo error")
                else                    -> this.filter { it.title.contains(filter.query) }
            }.getPage(filter.offset, filter.itemsOnPage)
        }

    /**
     * Случайный выбор от одного до трёх элементов
     */
    override fun loadSelectedItems(): DemoServiceResult =
        DemoServiceResult(setOf(data.random(), data.random(), data.random()).toList(), hasMore = false)

    private fun generateDataList(prefix: String) = (0..(Random.nextInt(5, count)))
        .map { order ->
            val hasNested = Random.nextBoolean()
            val subtitle = if (hasNested) "Has nested items" else null
            DemoServiceData(UUID.randomUUID(), "$prefix $order title", subtitle, order, hasNested)
        }

    private fun List<DemoServiceData>.getPage(offset: Int, pageSize: Int): DemoServiceResult {
        val begin = offset * pageSize
        val end = begin + pageSize
        val data = subList(offset * pageSize, min((offset + 1) * pageSize, size))
        return DemoServiceResult(data, hasMore = end < size)
    }
}