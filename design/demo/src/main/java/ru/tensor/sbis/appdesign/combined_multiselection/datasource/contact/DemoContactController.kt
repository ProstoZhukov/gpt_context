package ru.tensor.sbis.appdesign.combined_multiselection.datasource.contact

import ru.tensor.sbis.appdesign.combined_multiselection.data.contact.DemoContactServiceResult
import ru.tensor.sbis.appdesign.combined_multiselection.data.contact.DemoContactServiceResultData
import ru.tensor.sbis.appdesign.selection.data.DemoRecipientFilter
import ru.tensor.sbis.appdesign.selection.datasource.DemoController
import java.util.*
import kotlin.math.min

/**
 * @author ma.kolpakov
 */
object DemoContactController : DemoController<DemoContactServiceResult, DemoRecipientFilter> {

    private val data: List<DemoContactServiceResultData> = getDataList()

    @Throws(IllegalStateException::class)
    override fun list(filter: DemoRecipientFilter): DemoContactServiceResult = refresh(filter)

    @Throws(IllegalStateException::class)
    override fun refresh(filter: DemoRecipientFilter): DemoContactServiceResult =
        when {
            filter.query == "error" -> error("Demo error")
            filter.query.isEmpty()  -> data
            else                    -> data.filter {
                val title = it.title.toLowerCase(Locale.ROOT)
                val query = filter.query.toLowerCase(Locale.ROOT)
                title.contains(query)
            }
        }.getResult(filter.offset, filter.itemsOnPage)

    override fun loadSelectedItems(): DemoContactServiceResult = DemoContactServiceResult(emptyList(), hasMore = false)

    private fun getDataList(): List<DemoContactServiceResultData> =
        (0..5).map { count ->
            val subtitle = if (count % 2 == 0) "Person Subtitle $count" else ""
            DemoContactServiceResultData(
                id = count.toString(),
                title = "FirstName$count LastName$count",
                subtitle = subtitle,
                photoUrl = "",
            )
        }

    private fun List<DemoContactServiceResultData>.getResult(offset: Int, pageSize: Int): DemoContactServiceResult {
        val begin = offset * pageSize
        val end = begin + pageSize
        val data = subList(offset * pageSize, min((offset + 1) * pageSize, size))
        return DemoContactServiceResult(data, hasMore = end < size)
    }
}
