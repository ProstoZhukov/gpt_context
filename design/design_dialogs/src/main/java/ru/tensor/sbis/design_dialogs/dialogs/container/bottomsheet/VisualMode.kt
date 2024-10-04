package ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.Gravity.*
import android.view.View
import ru.tensor.sbis.design.design_dialogs.R
import ru.tensor.sbis.design_dialogs.dialogs.container.util.restrictDialogContentMovablePanelWidthByPadding
import ru.tensor.sbis.design_dialogs.dialogs.container.util.restrictDialogContentWidthOnTabletByPadding
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel
import java.io.Serializable
import ru.tensor.sbis.design.R as RDesign
/**
 * Режимы отображения контента в [ContainerBottomSheet]. Применят модификации доступкой для контента области
 *
 * @author ma.kolpakov
 */
sealed class VisualMode : Serializable {

    abstract fun apply(contentView: View)
}

/**
 * Режим отображения для окна выбора
 */
object SelectionPaneVisualMode : VisualMode() {

    override fun apply(contentView: View) {
        if (contentView.resources.getBoolean(RDesign.bool.is_tablet)) {
            restrictDialogContentWidthOnTabletByPadding(contentView)
        }
    }
}

/**
 * Режим отображения для шторки [MovablePanel] в диалоговом окне [ContainerBottomSheet]:
 * * [NO_GRAVITY], [CENTER], [CENTER_HORIZONTAL] - размешение на всю ширину
 * * [LEFT], [START] - размещение слева на половину экрана
 * * [RIGHT], [END] - размещение справа на половину экрана
 *
 * Режим отображения применяется для планшета, на телефоне не оказывает влияния.
 *
 * @throws IllegalArgumentException если [landscapeGravity] или [portraitGravity] не входит в [supportedGravities]
 *
 * @see restrictDialogContentMovablePanelWidthByPadding
 * @see R.bool.is_tablet
 */
data class MovablePanelVisualMode(
    private val landscapeGravity: Int = CENTER_HORIZONTAL,
    private val portraitGravity: Int = CENTER_HORIZONTAL
) : VisualMode() {

    /**
     * Поддерживаемые варианты размещения. RTL не поддерживается. Варианты start и end указаны для удобства использования
     */
    @SuppressLint("RtlHardcoded")
    private val supportedGravities = intArrayOf(NO_GRAVITY, LEFT, START, CENTER, CENTER_HORIZONTAL, RIGHT, END)

    init {
        require(landscapeGravity in supportedGravities) {
            "Unsupported vertical gravity $landscapeGravity for landscape mode"
        }
        require(portraitGravity in supportedGravities) {
            "Unsupported vertical gravity $portraitGravity for portrait mode"
        }
    }

    override fun apply(contentView: View) = with(contentView) {
        if (resources.getBoolean(RDesign.bool.is_tablet)) {
            val gravity = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                landscapeGravity
            else
                portraitGravity
            restrictDialogContentMovablePanelWidthByPadding(contentView, gravity)
        }
    }
}
