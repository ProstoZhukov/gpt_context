package ru.tensor.sbis.design.period_picker.decl

import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerRequestKey
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerResultKey
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.Calendar

/**
 * Фича компонента Компактный выбор периода для отображения его как фрагмент.
 *
 * @author mb.kruglova
 */
interface SbisCompactPeriodPickerFragmentFeature : Feature {

    /**
     * Отобразить Компактный выбор периода.
     *
     * @param host фрагмент, где должен быть отображен компонент.
     * @param containerId id контейнера, куда необходимо поместить фрагмент.
     * @param startValue начало выбранного пользователем периода.
     * @param endValue конец выбранного пользователем периода.
     * @param isEnabled возможность взаимодействовать с компонентом.
     * @param selectionType режим выделения диапазона дат.
     * @param dayType тип дня.
     * @param displayedRanges список доступных для отображения периодов.
     * Каждый элемент - это пара "начало-конец периода".
     * Компонент поддерживает только один доступный период (согласовано с iOS), а не как на web-е - несколько.
     * Api не меняем, оставляем List, на случай, если в будущем нужно будет поддерживать несколько доступных периодов.
     * @param isDayAvailable переменная-функция, которая определяет, может ли пользователь выбирать конкретный день.
     * @param dayBackgroundFormatter переменная-функция для получения цвета заливки конкретного дня.
     * По умолчанию не определена, день отрисовывается без заливки.
     * @param isBottomPosition возможность позиционировать текущую даты или исходный выбранный период снизу.
     * @param anchorDate дата, к которой скроллируется календарь.
     * Если null - скроллирование по умолчанию календаря, в противном случае, скроллируется к данной дате
     * (исходя из параметра isBottomPosition).
     * @param requestKey прикладной ключ запроса для получения выбранного периода через Fragment Result API.
     * @param resultKey прикладной ключ результата для получения выбранного периода через Fragment Result API.
     * @param dayCountersRepFactory - фабрика репозитория, предоставляющего счётчики по дням.
     */
    fun showCompactPeriodPicker(
        host: Fragment,
        containerId: Int,
        startValue: Calendar? = null,
        endValue: Calendar? = null,
        isEnabled: Boolean = true,
        selectionType: SbisPeriodPickerSelectionType = SbisPeriodPickerSelectionType.Single,
        dayType: SbisPeriodPickerDayType = SbisPeriodPickerDayType.Simple,
        displayedRanges: List<SbisPeriodPickerRange>? = null,
        isDayAvailable: ((Calendar) -> Boolean)? = null,
        dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme) = { SbisPeriodPickerDayCustomTheme() },
        isBottomPosition: Boolean = false,
        anchorDate: Calendar? = null,
        requestKey: String = periodPickerRequestKey,
        resultKey: String = periodPickerResultKey,
        dayCountersRepFactory: SbisPeriodPickerDayCountersRepository.Factory? = null
    )
}