package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.devices.settings.generated.Application
import ru.tensor.devices.settings.generated.InterfaceScale
import ru.tensor.devices.settings.generated.WorkplaceSettings as ControllerWorkplaceSettings

/**
 * Модель настроек рабочего места
 *
 * @param workplaceId Int - идентификатор рабочего места
 * @param fixateMenu Boolean - Зафиксировать боковое меню
 * @param interfaceTheme InterfaceTheme - Тема интерфейса
 * @param showScrollButtons Boolean - Показывать кнопки прокрутки
 * @param simplifiedSaleSettings Настройки для упрощённой продажи
 * @param useSaleOrder Boolean - useSaleOrder
 *
 * @see InterfaceTheme
 */
data class WorkplaceSettings(
    val workplaceId: Long,
    val fixateMenu: Boolean,
    var interfaceTheme: InterfaceTheme,
    val showScrollButtons: Boolean,
    var simplifiedSaleSettings: SimplifiedSaleSettings,
    var useSaleOrder: Boolean
) {

    companion object {
        fun stub(): WorkplaceSettings = WorkplaceSettings(
            0,
            false,
            interfaceTheme = InterfaceTheme.LIGHT,
            showScrollButtons = false,
            SimplifiedSaleSettings.stub,
            useSaleOrder = false
        )
    }
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerWorkplaceSettings.map(): WorkplaceSettings = WorkplaceSettings(
    workplaceId,
    fixateMenu,
    interfaceTheme.map(),
    showScrollButtons,
    simplifiedSaleSettings.map(),
    useSaleOrder
)

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun WorkplaceSettings.map(): ControllerWorkplaceSettings = ControllerWorkplaceSettings(
    workplaceId,
    Application.RETAIL,
    fixateMenu,
    InterfaceScale.LARGE,
    interfaceTheme.map(),
    showScrollButtons,
    useSaleOrder,
    simplifiedSaleSettings.map(),
)
