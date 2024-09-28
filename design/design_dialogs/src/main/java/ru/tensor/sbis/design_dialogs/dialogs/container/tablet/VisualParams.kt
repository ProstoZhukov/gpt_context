package ru.tensor.sbis.design_dialogs.dialogs.container.tablet

import androidx.annotation.DimenRes
import android.view.Gravity.NO_GRAVITY
import android.view.WindowManager.LayoutParams.*
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.design_dialogs.R
import java.io.Serializable

private const val defaultSoftInputMode = SOFT_INPUT_STATE_UNCHANGED or SOFT_INPUT_ADJUST_RESIZE

/**
 * Класс, представляющий набор параметров, определяющих отображение и позиционирование контента в [TabletContainerDialogFragment]
 * @param gravity флаг позиционирования относительно экрана
 * @param boundingObject тип ограничивающего объекта
 * @param needHorizontalMargin флаг необходимости добавление отступа по бокам
 * @param overlayActionBar флаг необходимости перекрытия ActionBar-а контентом
 * @param anchor якорь
 * @param fixedWidth id ресурса, определяющего фиксированное значение ширины
 * @param wrapWidth true, если ширина по содержимому
 * @param wrapHeight true, если высота по содержимому
 * @param softInputMode режим отображения клавиатуры
 * @param listenAnchorLayoutAlways флаг необходимости отслеживания якоря всегда
 * @param dialogStyle стиль диалогового окна
 */
data class VisualParams(
    var gravity: Int = NO_GRAVITY,
    var boundingObject: BoundingObject? = null,
    var needHorizontalMargin: Boolean = false,
    var overlayActionBar: Boolean = true,
    var anchor: Anchor? = null,
    @DimenRes
    var fixedWidth: Int? = null,
    var wrapWidth: Boolean = false,
    var wrapHeight: Boolean = true,
    var softInputMode: Int = defaultSoftInputMode,
    var listenAnchorLayoutAlways: Boolean = false,
    @StyleRes
    var dialogStyle: Int = R.style.TabletContainerDialogStyle
) : Serializable