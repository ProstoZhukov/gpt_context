package ru.tensor.sbis.design.selection.bl.vm.selection.multi.command

import android.annotation.SuppressLint
import io.reactivex.subjects.Subject
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import timber.log.Timber

/**
 * Набор команд для модификации списка выбранных элементов.
 * Если команда должна быть проигнорирована, нужно вернуть оригинальный список
 *
 * @author ma.kolpakov
 */
internal sealed class SelectionCommand<DATA : SelectorItem> : (List<DATA>) -> List<DATA> {
    protected abstract val data: DATA
}

internal data class OverrideSelectionCommand<DATA : SelectorItem>(
    override val data: DATA
) : SelectionCommand<DATA>() {

    override fun invoke(ignored: List<DATA>): List<DATA> =
        data.apply { meta.isSelected = true }.run(::listOf)
}

internal data class AddSelectionCommand<DATA : SelectorItem>(
    override val data: DATA,
    private val limit: Int,
    private val limitSubject: Subject<Int>
) : SelectionCommand<DATA>() {

    @SuppressLint("BinaryOperationInTimber")
    override fun invoke(selection: List<DATA>): List<DATA> {
        val duplicateIndex = selection.indexOfFirst { it.id == data.id }
        if (duplicateIndex != -1) {
            Timber.w(
                "Adding element that is already selected. " +
                    "This might happen because of abnormal user input or incorrect UI state"
            )
            data.meta.isSelected = true
            return selection.mapIndexed { index, original -> if (index == duplicateIndex) data else original }
        }
        val filteredSelection = removeChildren(selection)
        return if (filteredSelection.size == limit) {
            limitSubject.onNext(limit)
            filteredSelection
        } else {
            data.meta.isSelected = true
            filteredSelection.plus(data)
        }
    }

    private fun removeChildren(selection: List<DATA>) = selection.filterNot { it.parentId == data.id }
}

internal data class RemoveSelectionCommand<DATA : SelectorItem>(
    override val data: DATA
) : SelectionCommand<DATA>() {

    @SuppressLint("BinaryOperationInTimber")
    override fun invoke(selection: List<DATA>): List<DATA> {
        val index = selection.indexOfFirst { it.id == data.id }
        if (index == -1) {
            Timber.w(
                "Removing element that is not selected." +
                    "This might happen because of abnormal user input or incorrect UI state"
            )
            return selection
        }
        data.meta.isSelected = false
        return selection.filterIndexedTo(ArrayList(selection.size - 1)) { i, _ -> i != index }
    }
}