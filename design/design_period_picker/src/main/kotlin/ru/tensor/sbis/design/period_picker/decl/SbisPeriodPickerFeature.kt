package ru.tensor.sbis.design.period_picker.decl

import android.content.Context
import android.view.View
import androidx.annotation.IntRange
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayType.Simple
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType.Range
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.Calendar
import ru.tensor.sbis.design.period_picker.view.utils.heightPercent as periodPickerHeightPercent

/**
 * Фича компонента Выбор периода.
 *
 * @author mb.kruglova
 */
interface SbisPeriodPickerFeature : Feature {

    companion object {
        /** Ключ запроса для получения выбранного периода через Fragment Result API. */
        const val periodPickerRequestKey = "sbisPeriodPickerRequestKey"

        /** Ключ результата для получения выбранного периода через Fragment Result API. */
        const val periodPickerResultKey = "sbisPeriodPickerResultKey"
    }

    /**
     * Отобразить Большой выбор периода.
     *
     * @param context контекст.
     * @param fragmentManager менеджер фрагмента для установления взаимодействия с фрагментом, в котором отображается
     * компонент.
     * @param startValue начало выбранного пользователем периода.
     * @param endValue конец выбранного пользователем периода.
     * @param isEnabled возможность взаимодействовать с компонентом.
     * @param selectionType режим выделения диапазона дат.
     * @param displayedRanges список доступных для отображения периодов.
     * @param isOneDaySelection возможность переключения в режим выбора одного дня: в шапке компонента будет отображено
     * только одно поле ввода даты, режим Год будет заблокирован для взаимодействия.
     * Каждый элемент - это пара "начало-конец периода".
     * Компонент поддерживает только один доступный период (согласовано с iOS), а не как на web-е - несколько.
     * Api не меняем, оставляем List, на случай, если в будущем нужно будет поддерживать несколько доступных периодов.
     * @param anchors якорь для прикреплена компонента большой выбор периода к вызывающему его View.
     * Вызывающим View может быть любой элемент фрагмента, который инициирует отображение компонента.
     * Используется только для планшетов. Если якорь не задан, то размещение на планшете будет происходить по центру.
     * @param headerMask маска для полей ввода даты.
     * @param isBottomPosition возможность позиционировать текущую даты или исходный выбранный период снизу.
     * @param presetStartValue дата, которая будет установлена после сброса значения по кнопке текущей даты
     * в качестве начала выбранного периода.
     * @param presetEndValue дата, которая будет установлена после сброса значения по кнопке текущей даты
     * в качестве конца выбранного периода.
     * @param mode режим календаря. По умолчанию режим Год.
     * @param anchorDate дата, к которой скроллируется календарь.
     * Если null - скроллирование по умолчанию календаря, в противном случае, скроллируется к данной дате
     * (исходя из параметра isBottomPosition).
     * @param requestKey прикладной ключ запроса для получения выбранного периода через Fragment Result API.
     * @param resultKey прикладной ключ результата для получения выбранного периода через Fragment Result API.
     */
    fun showPeriodPicker(
        context: Context,
        fragmentManager: FragmentManager,
        startValue: Calendar? = null,
        endValue: Calendar? = null,
        isEnabled: Boolean = true,
        selectionType: SbisPeriodPickerSelectionType = Range,
        displayedRanges: List<SbisPeriodPickerRange>? = null,
        isOneDaySelection: Boolean = false,
        anchors: SbisPeriodPickerAnchor? = null,
        headerMask: SbisPeriodPickerHeaderMask = SbisPeriodPickerHeaderMask.DEFAULT,
        isBottomPosition: Boolean = false,
        presetStartValue: Calendar? = null,
        presetEndValue: Calendar? = null,
        mode: SbisPeriodPickerMode = SbisPeriodPickerMode.YEAR,
        anchorDate: Calendar? = null,
        requestKey: String = periodPickerRequestKey,
        resultKey: String = periodPickerResultKey
    )

    /**
     * Отобразить Быстрый выбор периода.
     *
     * @param context контекст.
     * @param fragmentManager менеджер фрагмента для установления взаимодействия с фрагментом, в котором отображается
     * компонент.
     * @param visualParams визуальные параметры отображения компонента.
     * @param displayedRanges список доступных для отображения периодов.
     * Каждый элемент - это пара "начало-конец периода".
     * Компонент поддерживает только один доступный период (согласовано с iOS), а не как на web-е - несколько.
     * Api не меняем, оставляем List, на случай, если в будущем нужно будет поддерживать несколько доступных периодов.
     * @param isBottomPosition возможность позиционировать выбранной период снизу.
     * @param startValue начало исходного выбранного пользователем периода.
     * @param endValue конец исходного выбранного пользователем периода.
     * @param isEnabled возможность взаимодействовать с компонентом.
     * @param anchors якорь для прикреплена компонента большой выбор периода к вызывающему его View.
     * Вызывающим View может быть любой элемент фрагмента, который инициирует отображение компонента.
     * Используется только для планшетов. Если якорь не задан, то размещение на планшете будет происходить по центру.
     * @param anchorDate дата, к которой скроллируется календарь.
     * Если null - скроллирование по умолчанию календаря, в противном случае, скроллируется к данной дате
     * (исходя из параметра isBottomPosition).
     * @param requestKey прикладной ключ запроса для получения выбранного периода через Fragment Result API.
     * @param resultKey прикладной ключ результата для получения выбранного периода через Fragment Result API.
     */
    fun showShortPeriodPicker(
        context: Context,
        fragmentManager: FragmentManager,
        visualParams: SbisShortPeriodPickerVisualParams,
        displayedRanges: List<SbisPeriodPickerRange>? = null,
        isBottomPosition: Boolean = false,
        startValue: Calendar? = null,
        endValue: Calendar? = null,
        isEnabled: Boolean = true,
        anchors: SbisPeriodPickerAnchor? = null,
        anchorDate: Calendar? = null,
        requestKey: String = periodPickerRequestKey,
        resultKey: String = periodPickerResultKey
    )

    /**
     * Отобразить Компактный выбор периода.
     *
     * @param context контекст.
     * @param fragmentManager менеджер фрагмента для установления взаимодействия с фрагментом, в котором отображается
     * компонент.
     * @param startValue начало выбранного пользователем периода.
     * @param endValue конец выбранного пользователем периода.
     * @param isEnabled возможность взаимодействовать с компонентом.
     * @param selectionType режим выделения диапазона дат.
     * @param dayType тип дня.
     * @param displayedRanges список доступных для отображения периодов.
     * Каждый элемент - это пара "начало-конец периода".
     * Компонент поддерживает только один доступный период (согласовано с iOS), а не как на web-е - несколько.
     * Api не меняем, оставляем List, на случай, если в будущем нужно будет поддерживать несколько доступных периодов.
     * @param customView кастомная вьюшка, отображается снизу компонента.
     * @param anchors якорь для прикреплена компонента компактный выбор периода к вызывающему его View.
     * Вызывающим View может быть любой элемент фрагмента, который инициирует отображение компонента.
     * Используется только для планшетов. Если якорь не задан, то размещение на планшете будет происходить по центру.
     * @param isDayAvailable переменная-функция, которая определяет, может ли пользователь выбирать конкретный день.
     * @Deprecated("используйте dayCustomTheme")
     * @param dayBackgroundFormatter переменная-функция для получения цвета заливки конкретного дня.
     * По умолчанию не определена, день отрисовывается без заливки.
     * @param isBottomPosition возможность позиционировать текущую даты или исходный выбранный период снизу.
     * @param heightPercent высота компонента в процентах относительно экрана планшета.
     * @param anchorDate дата, к которой скроллируется календарь.
     * Если null - скроллирование по умолчанию календаря, в противном случае, скроллируется к данной дате
     * (исходя из параметра isBottomPosition).
     * @param requestKey прикладной ключ запроса для получения выбранного периода через Fragment Result API.
     * @param resultKey прикладной ключ результата для получения выбранного периода через Fragment Result API.
     * @param dayCountersRepFactory - фабрика репозитория, предоставляющего счётчики по дням.
     */
    fun showCompactPeriodPicker(
        context: Context,
        fragmentManager: FragmentManager,
        startValue: Calendar? = null,
        endValue: Calendar? = null,
        isEnabled: Boolean = true,
        selectionType: SbisPeriodPickerSelectionType = Range,
        dayType: SbisPeriodPickerDayType = Simple,
        displayedRanges: List<SbisPeriodPickerRange>? = null,
        customView: ((Context) -> View)? = null,
        anchors: SbisPeriodPickerAnchor? = null,
        isDayAvailable: ((Calendar) -> Boolean)? = null,
        dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme) = { SbisPeriodPickerDayCustomTheme() },
        isBottomPosition: Boolean = false,
        @IntRange(from = 1, to = 100) heightPercent: Int = periodPickerHeightPercent,
        anchorDate: Calendar? = null,
        requestKey: String = periodPickerRequestKey,
        resultKey: String = periodPickerResultKey,
        dayCountersRepFactory: SbisPeriodPickerDayCountersRepository.Factory? = null
    )
}