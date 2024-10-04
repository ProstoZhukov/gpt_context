package ru.tensor.sbis.design.recipient_selection.ui.items.single_line.person

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem

/**
 * Вью-холдер однострочной персоны для списка компонента выбора.
 *
 * @author vv.chekurda
 */
class SingleLineSelectionPersonItemViewHolder private constructor(
    private val personItemView: SingleLineSelectionPersonItemView,
    private val personClickListener: PersonClickListener? = null
) : RecyclerView.ViewHolder(personItemView) {

    constructor(
        parentView: ViewGroup,
        personClickListener: PersonClickListener? = null
    ) : this(
        SingleLineSelectionPersonItemView(parentView.context),
        personClickListener
    )

    private lateinit var data: SelectionPersonItem
    private lateinit var clickDelegate: SelectionClickDelegate

    init {
        setupClickListeners()
    }

    /**
     * Привязать данные к ячейке.
     *
     * @param data данные для отображения.
     * @param clickDelegate делегат обработки кликов по ячейке.
     * @param isMultiSelection true, если ячейка должна отображаться в режиме мультивыбора, false для одиночного.
     */
    fun bind(
        data: SelectionPersonItem,
        clickDelegate: SelectionClickDelegate,
        isMultiSelection: Boolean
    ) {
        this.data = data
        this.clickDelegate = clickDelegate

        personItemView.setData(data)
        personItemView.changeSelectButtonVisibility(isVisible = isMultiSelection)
    }

    private fun setupClickListeners() {
        personItemView.selectionIconView.setOnClickListener {
            clickDelegate.onAddButtonClicked(data)
        }
        personItemView.personView.setOnClickListener {
            val listener = personClickListener ?: return@setOnClickListener
            clickDelegate.onNavigateClicked(data)
            personItemView.postDelayed(
                {
                    val uuid = data.photoData.uuid ?: return@postDelayed
                    listener.onPersonClicked(personItemView.context, uuid)
                },
                NAVIGATE_DELAY_MS
            )
        }
    }
}

private const val NAVIGATE_DELAY_MS = 70L