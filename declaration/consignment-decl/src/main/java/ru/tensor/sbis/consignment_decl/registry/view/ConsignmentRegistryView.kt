package ru.tensor.sbis.consignment_decl.registry.view

import android.view.View
import ru.tensor.sbis.crud3.ListComponentView
import ru.tensor.sbis.design.view.input.searchinput.SearchInput

/**
 * Интерфейс визуального представления реестра ЭТРН.
 *
 * @author kv.martyshenko
 */
interface ConsignmentRegistryView {
    val root: View

    val listView: ListComponentView

    val searchFilter: SearchInput
}