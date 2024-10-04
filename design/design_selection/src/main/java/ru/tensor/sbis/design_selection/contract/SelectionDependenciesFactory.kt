package ru.tensor.sbis.design_selection.contract

import android.content.Context
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener
import ru.tensor.sbis.design_selection.ui.content.di.SelectionStubFactory
import ru.tensor.sbis.design_selection.contract.customization.SelectionCustomization
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonContract
import ru.tensor.sbis.design_selection.contract.controller.SelectionControllerWrapper
import ru.tensor.sbis.design_selection.contract.customization.DefaultSelectionCustomization
import ru.tensor.sbis.design_selection.contract.customization.SelectionStrings
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterFactory
import ru.tensor.sbis.design_selection.contract.stubs.DefaultSelectionStubFactory
import java.io.Serializable

/**
 * Фабрика зависимостей для компонента выбора.
 *
 * @author vv.chekurda
 */
interface SelectionDependenciesFactory<ITEM : SelectionItem> {

    /**
     * Получить поставщика обертки контроллера компонента выполучателей выбора.
     */
    fun getSelectionControllerProvider(appContext: Context): SelectionControllerWrapper.Provider<*, *, *, *, *, *, ITEM>

    /**
     * Получить фабрику фильтров для запросов на контроллер.
     */
    fun getFilterFactory(appContext: Context): SelectionFilterFactory<*, *>

    /**
     * Получить слушателя результата выбора.
     */
    fun getSelectionResultListener(appContext: Context): SelectionResultListener<ITEM, *>

    /**
     * Получить фабрику для создания заглушек.
     */
    fun getStubFactory(appContext: Context): SelectionStubFactory =
        DefaultSelectionStubFactory()

    /**
     * Получить кастомизацию выбора.
     */
    fun getSelectionCustomization(appContext: Context): SelectionCustomization<ITEM> =
        DefaultSelectionCustomization()

    /**
     * Получить комплект строк для отображения в компоненте.
     */
    fun getSelectorStrings(appContext: Context, config: SelectionConfig): SelectionStrings =
        SelectionStrings(
            searchHint = config.stringsConfig?.searchHint ?: R.string.design_search_panel_hint
        )

    /**
     * Получить контракт работы для дополнительной кнопки в шапке.
     */
    fun getHeaderButtonContract(appContext: Context): HeaderButtonContract<ITEM, *>? = null

    /**
     * Сериализуемый поставщик фабрики зависимостей для компонента выбора.
     */
    interface Provider<ITEM : SelectionItem, CONFIG : SelectionConfig> : Serializable {

        /**
         * Получить фабрику зависимостей для [config].
         */
        fun getFactory(appContext: Context, config: CONFIG): SelectionDependenciesFactory<ITEM>
    }
}