package ru.tensor.sbis.design.chips.api

import android.annotation.SuppressLint
import ru.tensor.sbis.design.chips.SbisChipsView
import ru.tensor.sbis.design.chips.list.SbisChipsAdapter
import ru.tensor.sbis.design.chips.models.SbisChipsConfiguration
import ru.tensor.sbis.design.chips.models.SbisChipsItem
import ru.tensor.sbis.design.chips.models.SbisChipsSelectionMode
import kotlin.properties.Delegates

/**
 * Класс для управления логикой [SbisChipsView].
 *
 * @author ps.smirnyh
 */
internal class SbisChipsController : SbisChipsViewApi {

    private var sbisChipsView: SbisChipsView by Delegates.notNull()
    private var adapter: SbisChipsAdapter by Delegates.notNull()

    override var selectionDelegate: SbisChipsSelectionDelegate? = null

    override var configuration: SbisChipsConfiguration = SbisChipsConfiguration()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            if (field == value) return

            var isNeedNotifyDataSetChanged = false

            val isSelectionModeChanged = field.selectionMode != value.selectionMode
            val isMultilineChanged = field.multiline != value.multiline
            val isStyleChanged = field.style != value.style
            val isReadOnlyChanged = field.readOnly != value.readOnly
            val isViewModeChanged = field.viewMode != value.viewMode
            val isSizeChanged = field.size != value.size

            field = value

            if (isSelectionModeChanged) {
                selectedKeys = selectedKeys
            }
            if (isStyleChanged) {
                adapter.style = field.style
                isNeedNotifyDataSetChanged = true
            }
            if (isReadOnlyChanged) {
                adapter.isReadOnly = field.readOnly
                isNeedNotifyDataSetChanged = true
            }
            if (isViewModeChanged) {
                adapter.viewMode = field.viewMode
                isNeedNotifyDataSetChanged = true
            }
            if (isSizeChanged) {
                adapter.size = field.size
                isNeedNotifyDataSetChanged = true
            }
            if (isMultilineChanged) {
                adapter.multiline = field.multiline
                sbisChipsView.changeMultiline(field.multiline)
            }

            if (isNeedNotifyDataSetChanged) {
                adapter.notifyDataSetChanged()
            }
        }

    override var items: List<SbisChipsItem> = emptyList()
        set(value) {
            field = value
            adapter.submitList(value)
        }

    override var selectedKeys: List<Int> = emptyList()
        set(value) {
            val newValue = when (configuration.selectionMode) {
                SbisChipsSelectionMode.Single -> value.takeLast(1)
                else -> value
            }
            if (field == newValue) return
            val keysForUpdate = field symmetricDifference newValue
            field = newValue
            adapter.selectedKeys = newValue
            selectionDelegate?.onChange(newValue)
            adapter.updateElements(keysForUpdate)
        }

    fun attach(
        sbisChipsView: SbisChipsView,
        recyclerAdapter: SbisChipsAdapter
    ) {
        this.sbisChipsView = sbisChipsView
        adapter = recyclerAdapter
    }

    /** Изменение выбранных элементов по клику. */
    internal fun handleSelectedChanged(id: Int, isSelected: Boolean) {
        selectedKeys = when (val mode = configuration.selectionMode) {
            SbisChipsSelectionMode.Single -> if (!isSelected) emptyList() else listOf(id)
            SbisChipsSelectionMode.Multiple -> if (isSelected) selectedKeys + id else selectedKeys - id
            is SbisChipsSelectionMode.Custom -> mode.selectionHandler(id)
        }
        if (isSelected) {
            selectionDelegate?.onSelect(id)
        } else {
            selectionDelegate?.onDeselect(id)
        }
    }

    /** Исключение общих элементов, оставляя только уникальные для каждой из коллекций элементы. */
    private infix fun <T> Collection<T>.symmetricDifference(other: Collection<T>): Set<T> {
        val left = this subtract other.toSet()
        val right = other subtract this.toSet()
        return left union right
    }
}
