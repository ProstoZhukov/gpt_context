package ru.tensor.sbis.appdesign.selection.datasource

import android.graphics.Color
import ru.tensor.sbis.appdesign.selection.data.DemoRecipientFilter
import ru.tensor.sbis.appdesign.selection.data.DemoRecipientServiceData
import ru.tensor.sbis.appdesign.selection.data.DemoRecipientServiceResult
import ru.tensor.sbis.appdesign.selection.data.RecipientType.*
import ru.tensor.sbis.design.profile.person.data.InitialsStubData
import ru.tensor.sbis.design.profile.person.data.PersonData
import java.util.*
import kotlin.math.min
import kotlin.random.Random

/**
 * @author ma.kolpakov
 */
object DemoRecipientController : DemoController<DemoRecipientServiceResult, DemoRecipientFilter> {

    private const val count: Int = 80

    private val data: List<DemoRecipientServiceData> = generateDataList()

    /**
     * В примере метод эквивалентен [refresh] так как нет работы с DataRefreshCallback
     */
    override fun list(filter: DemoRecipientFilter): DemoRecipientServiceResult = refresh(filter)

    override fun refresh(filter: DemoRecipientFilter): DemoRecipientServiceResult = when {
        filter.query.isEmpty()  -> data
        filter.query == "error" -> error("Demo error")
        else                    -> data.filter { it.title.contains(filter.query) }
    }.getPage(filter.offset, filter.itemsOnPage)

    /**
     * Случайный выбор от одного до трёх элементов
     */
    override fun loadSelectedItems(): DemoRecipientServiceResult =
        DemoRecipientServiceResult(setOf(data.random(), data.random(), data.random()).toList(), hasMore = false)

    private fun generateDataList() = (0..(Random.nextInt(5, count))).map { order ->
        val recipientType = values().random()
        val hasSubtitle = Random.nextBoolean()
        when (recipientType) {
            PERSON     -> {
                val firstName = "FirstName$order"
                val lastName = "LastName$order"
                DemoRecipientServiceData(
                    id = UUID.randomUUID(),
                    title = "$firstName $lastName",
                    subtitle = if (hasSubtitle) "Person subtitle" else null,
                    recipientType = recipientType,
                    personData = generatePersonData(),
                    firstName = firstName,
                    lastName = lastName
                )
            }
            GROUP      -> DemoRecipientServiceData(
                id = UUID.randomUUID(),
                title = "Group $order title",
                subtitle = if (hasSubtitle) "Group subtitle" else null,
                recipientType = recipientType,
                imageUrl = "",
                membersCount = Random.nextInt(1, 30)
            )
            DEPARTMENT -> DemoRecipientServiceData(
                id = UUID.randomUUID(),
                title = "Department $order title",
                subtitle = if (hasSubtitle) "Department subtitle" else null,
                recipientType = recipientType,
                membersCount = Random.nextInt(1, 30)
            )
        }
    }

    private fun generatePersonData() = PersonData(
        UUID.randomUUID(),
        "",
        generateInitials()
    )

    private fun generateInitials(): InitialsStubData {
        val charRange = 'A'..'Z'
        val initials = "${ charRange.random() }${ charRange.random() }"
        val colorRange = Color.parseColor("#000000")..Color.parseColor("#FFFFFF")
        return InitialsStubData(initials, colorRange.random())
    }

    private fun List<DemoRecipientServiceData>.getPage(offset: Int, pageSize: Int): DemoRecipientServiceResult {
        val begin = offset * pageSize
        val end = begin + pageSize
        val data = subList(offset * pageSize, min((offset + 1) * pageSize, size))
        return DemoRecipientServiceResult(data, hasMore = end < size)
    }
}