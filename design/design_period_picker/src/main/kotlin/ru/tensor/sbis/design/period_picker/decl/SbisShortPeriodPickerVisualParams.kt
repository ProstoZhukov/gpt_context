package ru.tensor.sbis.design.period_picker.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Визуальные параметры отображения компонента.
 *
 * @author mb.kruglova
 */
@Parcelize
data class SbisShortPeriodPickerVisualParams(
    /** Видимость стрелок переключения года. */
    val arrowVisible: Boolean = false,
    /** Возможность выбора полугодия. */
    val chooseHalfYears: Boolean = false,
    /** Возможность выбора месяца. */
    val chooseMonths: Boolean = false,
    /** Возможность выбора квартала. */
    val chooseQuarters: Boolean = false,
    /** Возможность выбора года. */
    val chooseYears: Boolean = false
) : Parcelable {

    /** Находится ли компонент в режиме год. */
    internal fun isYearMode() = chooseYears && !chooseMonths && !chooseQuarters && !chooseHalfYears
}