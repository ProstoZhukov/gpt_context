package ru.tensor.sbis.design_selection.contract.customization.selection.person

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.design_selection.R
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem

/**
 * Вью-холдер персоны для списка компонента выбора.
 *
 * @author vv.chekurda
 */
class SelectionPersonItemViewHolder private constructor(
    private val personItemView: SelectionPersonItemView,
    personClickListener: PersonClickListener?
) : RecyclerView.ViewHolder(personItemView) {

    constructor(
        parentView: ViewGroup,
        personClickListener: PersonClickListener? = null
    ) : this(personItemView = SelectionPersonItemView(parentView.context), personClickListener)

    private lateinit var data: SelectionPersonItem
    private lateinit var clickDelegate: SelectionClickDelegate

    init {
        personItemView.id = R.id.selection_person_item_view
        personItemView.findViewById<View>(R.id.selection_person_item_selection_icon_click_area).setOnClickListener {
            clickDelegate.onAddButtonClicked(data)
        }
        personItemView.findViewById<View>(R.id.selection_person_item_photo)
            .setOnClickListener {
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
        personItemView.changeSelectButtonVisibility(isVisible = isMultiSelection)
        personItemView.setData(data)
    }
}

private const val NAVIGATE_DELAY_MS = 70L