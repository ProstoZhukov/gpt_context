package ru.tensor.sbis.communicator.dialog_selection.data.factory

import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import javax.inject.Inject

/**
 * Реализация функции загрузки выбранных элементов при инициализации
 *
 * @author vv.chekurda
 */
internal class DialogSelectionLoader @Inject constructor()
    : MultiSelectionLoader<SelectorItemModel> {

    /** На экране выбора диалога/участников не может быть предвыбранных элементов */
    override fun loadSelectedItems(): List<SelectorItemModel> = listOf()
}