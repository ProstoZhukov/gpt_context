package ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data

import io.reactivex.Observable
import ru.tensor.sbis.communication_decl.selection.SelectionConfig

/**
 * Интерфейс состояния головной кнопки компонента выбора.
 *
 * @author vv.chekuda
 */
interface SelectionHeaderButtonLiveData {

    /**
     * Для подписки на видимость кнопки.
     */
    val isHeaderButtonVisible: Observable<Boolean>

    /**
     * Для подписки на обновление конфигурации компонента выбора при обработке кликов по кнопке.
     */
    val updateConfig: Observable<SelectionConfig>
}