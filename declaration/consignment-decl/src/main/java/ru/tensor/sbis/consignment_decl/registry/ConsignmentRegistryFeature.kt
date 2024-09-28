package ru.tensor.sbis.consignment_decl.registry

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.tensor.sbis.consignment_decl.base.view.UIHost
import ru.tensor.sbis.consignment_decl.registry.model.ConsignmentRegistryElement
import ru.tensor.sbis.consignment_decl.registry.view.ConsignmentRegistryMapper
import ru.tensor.sbis.consignment_decl.registry.view.ConsignmentRegistryView
import ru.tensor.sbis.consignment_decl.registry.widget.ConsignmentRegistryWidget
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фича реестра ЭТРН.
 *
 * @author kv.martyshenko
 */
interface ConsignmentRegistryFeature : Feature {

    /**
     * Метод для создания [ConsignmentRegistryWidget].
     *
     * @param uiHost хост-экрана.
     * @param view реализация отображения экрана фильтра.
     * @param onConsignmentClick клик по элементру ЭТРН.
     * @param mapperFactory фабрика создания маппера для преобразования модели данных в модель представления.
     * @param maxItemsOnScreen максимальное количество элементов на экране (для расчета размера страницы)
     */
    fun createWidget(
        uiHost: UIHost,
        view: ConsignmentRegistryView,
        onConsignmentClick: (UIHost, ConsignmentRegistryElement) -> Unit = getDefaultClickHandler(),
        mapperFactory: ConsignmentRegistryMapper.Factory = getDefaultMapperFactory(),
        maxItemsOnScreen: UInt = DEFAULT_MAX_VISIBLE_ITEMS_ON_SCREEN.toUInt()
    ): ConsignmentRegistryWidget

    /**
     * Метод для создания стандартной [ConsignmentRegistryView].
     *
     * @param inflater
     * @param container родительский контейнер.
     */
    fun createDefaultView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): ConsignmentRegistryView

    /**
     * Метод для получения стандартной [ConsignmentRegistryMapper.Factory].
     */
    fun getDefaultMapperFactory(): ConsignmentRegistryMapper.Factory

    /**
     * Метод для получения станартного клика по элементу ЭТРН.
     */
    fun getDefaultClickHandler(): (UIHost, ConsignmentRegistryElement) -> Unit

    companion object {
        private const val DEFAULT_MAX_VISIBLE_ITEMS_ON_SCREEN = 10
    }

}