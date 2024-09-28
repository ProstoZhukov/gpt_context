package ru.tensor.sbis.consignment_decl.registry.widget

import android.view.View

/**
 * Виджет реестра ЭТРН.
 *
 * @author kv.martyshenko
 */
interface ConsignmentRegistryWidget {
    /**
     * Рутовый элемент. Используется для дальнейшего встраивания в addView().
     */
    val view: View

}