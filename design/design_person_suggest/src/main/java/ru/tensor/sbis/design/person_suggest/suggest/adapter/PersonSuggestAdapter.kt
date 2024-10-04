package ru.tensor.sbis.design.person_suggest.suggest.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.vmadapter.DiffCallBack
import ru.tensor.sbis.base_components.autoscroll.LinearAutoScroller
import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData
import ru.tensor.sbis.design.person_suggest.suggest.PersonSuggestView
import ru.tensor.sbis.design.person_suggest.suggest.contract.PersonSelectionListener
import ru.tensor.sbis.design.profile.person.PersonView
import androidx.core.view.updatePadding

/**
 * Адаптер списка панели выбора персоны [PersonSuggestView].
 *
 * @property theme модель темы компонента для создания ячеек списка.
 * @property selectionListener слушатель выбранной персоны.
 *
 * @author vv.chekurda
 */
internal class PersonSuggestAdapter(
    private val theme: PersonSuggestTheme,
    private val selectionListener: PersonSelectionListener
) : RecyclerView.Adapter<PersonSuggestViewHolder>() {

    private var autoScroller: LinearAutoScroller? = null

    /**
     * Установить/получить контент адаптера.
     */
    var content: List<PersonSuggestData> = emptyList()
        set(value) {
            autoScroller?.onBeforeContentChanged(field)
            val diffResult = DiffCallBack(field, value, ::areItemsTheSame, ::areContentsTheSame)
                .let(DiffUtil::calculateDiff)
            field = value
            diffResult.dispatchUpdatesTo(this)
            autoScroller?.onAfterContentChanged(content)
        }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.recycledViewPool.setMaxRecycledViews(PERSON_ITEM_TYPE, MAX_RECYCLED_PERSONS)
        autoScroller = LinearAutoScroller(
            recyclerView.layoutManager as LinearLayoutManager,
            AUTO_SCROLL_THRESHOLD
        ) { i1, i2 -> i1 == i2 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonSuggestViewHolder =
        PersonSuggestViewHolder(
            createItemView(parent.context),
            selectionListener
        )

    override fun onBindViewHolder(holder: PersonSuggestViewHolder, position: Int) {
        holder.bind(content[position])
    }

    override fun getItemViewType(position: Int): Int =
        PERSON_ITEM_TYPE

    override fun getItemCount() =
        content.size

    private fun createItemView(context: Context) =
        PersonView(context).apply {
            setSize(theme.photoSize)
            updatePadding(
                left = theme.personHorizontalPadding,
                top = theme.personVerticalPadding,
                right = theme.personHorizontalPadding,
                bottom = theme.personVerticalPadding
            )
        }

    private fun areItemsTheSame(item1: Any, item2: Any): Boolean =
        (item1 as PersonSuggestData).uuid == (item2 as PersonSuggestData).uuid

    private fun areContentsTheSame(item1: Any, item2: Any): Boolean =
        item1 == item2
}

private const val PERSON_ITEM_TYPE = 1
private const val AUTO_SCROLL_THRESHOLD = 20
private const val MAX_RECYCLED_PERSONS = 30