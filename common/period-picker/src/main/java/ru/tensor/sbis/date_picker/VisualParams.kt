package ru.tensor.sbis.date_picker

import android.view.View
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.date_picker.current.CurrentPeriod
import java.io.Serializable

/**
 * Класс для настройки отображения компонента выбора периода
 * @property defaultTitle идентификатор ресурса для заголовка по умолчанию
 * @property titleClickable кликабельность заголовка
 * @property modeVisibility видимость кнопки переключения режима ("гамбургера")
 * @property modeClickable кликабельность кнопки переключения режима ("гамбургера")
 * @property homeVisibility кликабельность кнопки "Домой"
 * @property selectCurrentPeriodVisibility видимость кнопки "Текущий"
 * @property dateFromAndToFocusableInTouchMode кликабельность полей ввода периода
 * @property doneVisibility видимость кнопки подтверждения выбора
 * @property resetVisibility видимость кнопки сброса
 * @property doneVisibleOnPeriodChanged кнопка подтверждения не отображается, пока пользователь не начнет выбирать новый
 * период, работает только если doneVisibility true
 * @property isImmersiveFullScreen нужно ли отображать диалог в полноэкранном immersive режиме (скрывая статусбар и панель навигации)
 *
 * @author mb.kruglova
 */
data class VisualParams(
    @StringRes val defaultTitle: Int = R.string.date_picker_title,
    val titleClickable: Boolean = true,
    val modeVisibility: Int = View.VISIBLE,
    val modeClickable: Boolean = true,
    val homeVisibility: Int = View.VISIBLE,
    val selectCurrentPeriodVisibility: Int = View.VISIBLE,
    val dateFromAndToFocusableInTouchMode: Boolean = true,
    val doneVisibility: Int = View.VISIBLE,
    val resetVisibility: Int = View.GONE,
    val yearLabelsClickable: Boolean = true,
    val visibleCurrentPeriods: List<CurrentPeriod> = listOf(*CurrentPeriod.values()),
    @StyleRes val styleId: Int = R.style.PickerStyle,
    val doneVisibleOnPeriodChanged: Boolean = false,
    val preferredMode: Mode? = null,
    var isImmersiveFullScreen: Boolean = false
) : Serializable {
    companion object {
        val sabygetVisualParams = VisualParams(
            defaultTitle = R.string.date_picker_date_picker_title,
            titleClickable = false,
            modeVisibility = View.GONE,
            homeVisibility = View.INVISIBLE,
            selectCurrentPeriodVisibility = View.GONE
        )

        val taskTermVisualParams = VisualParams(
            defaultTitle = R.string.date_picker_date_picker_title,
            modeVisibility = View.VISIBLE,
            modeClickable = false,
            selectCurrentPeriodVisibility = View.INVISIBLE,
            doneVisibleOnPeriodChanged = true
        )

        val taskVisualParams = VisualParams(
            defaultTitle = R.string.date_picker_date_picker_title,
            modeVisibility = View.VISIBLE,
            modeClickable = true,
            selectCurrentPeriodVisibility = View.GONE,
            resetVisibility = View.VISIBLE
        )

        val motivationVisualParams = VisualParams(
            titleClickable = false,
            modeVisibility = View.GONE,
            selectCurrentPeriodVisibility = View.GONE,
            doneVisibility = View.GONE,
            yearLabelsClickable = false
        )

        val periodOnlySelectionVisualParams = VisualParams(
            modeVisibility = View.GONE,
            dateFromAndToFocusableInTouchMode = false
        )
    }
}