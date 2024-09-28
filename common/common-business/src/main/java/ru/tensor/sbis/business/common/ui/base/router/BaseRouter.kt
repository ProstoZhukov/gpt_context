package ru.tensor.sbis.business.common.ui.base.router

import androidx.annotation.StringRes
import ru.tensor.sbis.common.util.dateperiod.DatePeriod
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.event_bus.EventBus
import ru.tensor.sbis.mfb.generated.FileInfo
import java.util.Calendar
import java.util.UUID

/**
 * Интерфейс роутера.
 *
 * @author as.chadov
 */
interface BaseRouter {
    /**
     * Фича компонента Выбор периода.
     */
    val periodPickerFeature: SbisPeriodPickerFeature?

    /**
     * Подписаться на результат диалога выбора периода.
     *
     * @param key ключ.
     * @param resultKeyList список ключей результата.
     * @param action действие, которое будет вызвано при возвращении результат диалога выбора периода.
     */
    fun subscribePeriodPickerResult(
        key: UUID,
        resultKeyList: List<String> = listOf(SbisPeriodPickerFeature.periodPickerResultKey),
        action: (DatePeriod, String) -> Unit
    )

    /**
     * Отписаться от результата диалога выбора периода.
     *
     * @param key ключ.
     */
    fun unsubscribePeriodPickerResult(key: UUID)

    /**
     * Показывает Большой выбор периода.
     *
     * @param resultKey ключ результата.
     * @param startValue начало выбранного пользователем периода.
     * @param endValue конец выбранного пользователем периода.
     * @param selectionType Режим выделения диапазона дат, одиночный или диапазон.
     * @param minDate минимальная дата для отображения периода.
     * @param maxDate максимальная дата для отображения периода.
     */
    fun showPeriodPicker(
        resultKey: String?,
        startValue: Calendar?,
        endValue: Calendar?,
        selectionType: SbisPeriodPickerSelectionType = SbisPeriodPickerSelectionType.Single,
        minDate: Calendar? = null,
        maxDate: Calendar? = null
    )

    /**
     * Показывает Быстрый выбор периода.
     *
     * @param resultKey ключ результата.
     * @param startValue начало выбранного пользователем периода.
     * @param endValue конец выбранного пользователем периода.
     */
    fun showShortPeriodPicker(
        resultKey: String?,
        startValue: Calendar?,
        endValue: Calendar?
    )

    /**
     * Показывает Компактный выбор периода.
     *
     * @param resultKey ключ результата.
     * @param startValue начало выбранного пользователем периода.
     * @param endValue конец выбранного пользователем периода.
     * @param selectionType Режим выделения диапазона дат, одиночный или диапазон.
     * @param minDate минимальная дата для отображения периода.
     * @param maxDate максимальная дата для отображения периода.
     */
    fun showCompactPeriodPicker(
        resultKey: String?,
        startValue: Calendar?,
        endValue: Calendar?,
        selectionType: SbisPeriodPickerSelectionType = SbisPeriodPickerSelectionType.Range,
        minDate: Calendar? = null,
        maxDate: Calendar? = null
    )

    /**
     * Отображает на экране [Toast]
     *
     * @param message строка с отображаемым сообщением
     */
    fun showToast(message: String)

    /**
     * Отображает на экране [Toast].
     *
     * @param messageResId id ресурса строки с отображаемым сообщением
     */
    fun showToast(@StringRes messageResId: Int)

    /**
     * Выполняет возврат на предыдущий экран.
     */
    fun goBack()

    /**
     * Отображает информер на экране.
     *
     * @param message строка с отображаемым сообщением
     * @param style стиль оформления панели информера
     */
    fun showPopupNotification(message: String, style: SbisPopupNotificationStyle = SbisPopupNotificationStyle.ERROR)

    /**
     * Отображает информаер на экране.
     *
     * @param messageResId id ресурса строки с отображаемым сообщением
     * @param style стиль оформления панели информера
     */
    fun showPopupNotification(
        @StringRes messageResId: Int,
        style: SbisPopupNotificationStyle = SbisPopupNotificationStyle.ERROR
    )
}