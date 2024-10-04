package ru.tensor.sbis.design.list_header

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.joda.time.LocalDateTime
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import java.util.Date
import java.util.Random
import java.util.TimeZone

/**
 *
 * Тест для ListDateViewUpdater. Так как основная его задача это отслеживание скролла и определение значений дат,
 * то мы будем вызывать onScrolled в RecyclerView.OnScrollListener принудительно и отслеживать результат форматирования
 *
 * @author ra.petrov
 *
 */
@RunWith(Parameterized::class)
class ListDateViewUpdaterTest(
    /**
     * findFirstVisibleItemPosition для текущего положения скролла. Собственно не так важны параметры передаваемые в
     * onScrolled, сколько результат метода topChildPosition ListDateViewUpdater, его мы будем "подкладывать" через моки
     *
     * @see ListDateViewUpdater.topChildPosition
     * @see RecyclerView.OnScrollListener
     */
    private var findFirstVisibleItemPosition: Int,

    /**
     * FormattedDateTime для заголовка
     */
    private val formattedDateTimeForHeader: FormattedDateTime,

    /**
     *  FormattedDateTime для ячейки
     */
    private val formattedDateTimeForItem: FormattedDateTime
) {

    companion object {
        private val content =
            generateSequence {
                LocalDateTime.now()
                    .plusDays(Random().nextInt(365 * 5))
                    .plusSeconds(60 * 60 * 24)
            }
                .take(10)
                .sorted()
                .toList()

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Iterable<Array<Any?>> {
            return content.mapIndexed { index, localDateTime ->
                arrayOf(
                    index,
                    ListDateFormatter.DateTime()
                        .format(localDateTime.toDate()),
                    ListDateFormatter.DateTime()
                        .format(
                            localDateTime.toDate(),
                            if (index == 0) null
                            else content[index - 1].toDate()
                        )
                )
            }
        }
    }

    private val listDateViewUpdater = ListDateViewUpdater(ListDateFormatter.DateTime())
    private val adapter = TestAdapter(content)

    private val linearLayoutManager: LinearLayoutManager = mock {
        on { findFirstVisibleItemPosition() } doReturn findFirstVisibleItemPosition
    }

    @Mock
    private val recyclerView: RecyclerView = mock {
        on { adapter } doReturn adapter
        on { layoutManager } doReturn linearLayoutManager
    }

    private val dateView: HeaderDateView = mock { }

    private val onScrollListenerCaptor: ArgumentCaptor<RecyclerView.OnScrollListener> =
        ArgumentCaptor.forClass(RecyclerView.OnScrollListener::class.java)
    private val formattedDateTimeCaptor: ArgumentCaptor<FormattedDateTime> =
        ArgumentCaptor.forClass(FormattedDateTime::class.java)

    @Test
    fun `When scroll recyclerView then set formatted date to view`() {
        findFirstVisibleItemPosition = 0
        listDateViewUpdater.bind(recyclerView, dateView)
        verify(recyclerView).addOnScrollListener(onScrollListenerCaptor.capture())

        val onScrollListener = onScrollListenerCaptor.value
        onScrollListener.onScrolled(recyclerView, 0, 0)

        verify(dateView).setFormattedDateTime(formattedDateTimeCaptor.capture())
        Assert.assertEquals(formattedDateTimeForHeader, formattedDateTimeCaptor.value)
    }

    @Test
    fun `When call getFormattedDate then returns formatted date`() {
        listDateViewUpdater.bind(recyclerView, dateView)
        Assert.assertEquals(
            formattedDateTimeForItem,
            listDateViewUpdater.getFormattedDate(findFirstVisibleItemPosition)
        )
    }
}

internal class TestViewHolder(view: View) : RecyclerView.ViewHolder(view)

internal class TestAdapter(
    private val content: List<LocalDateTime>
) : RecyclerView.Adapter<TestViewHolder>(), DateTimeAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        return TestViewHolder(mock())
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) = Unit

    override fun getItemCount(): Int = content.size

    override fun getItemDateTime(position: Int): Date? =
        // Стандартное условие проверки для методов getItem(position) в общих компонентах адаптеров списка
        if (position in 0..content.lastIndex) {
            content[position].toDate(TimeZone.getDefault())
        } else {
            null
        }
}