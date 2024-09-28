package ru.tensor.sbis.design_dialogs.dialogs.container.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import ru.tensor.sbis.design.design_dialogs.R
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.TabletContainerDialogFragment

/**
 * Контейнер со скруглёнными углами фиксированного размера для использования в [TabletContainerDialogFragment].
 * Решает проблему, связанную с тем, что при использовании для скругления [setClipToOutline], в т.ч в [CardView],
 * фон у верхнего и нижележащих [View] может обрезаться по-разному, образуя артефакты. Недостаток решения в отсутствии
 * сглаживания, что в данном случае некритично, учитывая небольшой радиус скругления
 *
 * @author us.bessonov
 */
internal class RoundedCornersFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val cornerRadius =
        context.resources.getDimensionPixelSize(R.dimen.design_dialogs_tablet_container_content_corner_radius).toFloat()
    private val mask = Path()

    override fun draw(canvas: Canvas) {
        canvas.clipPath(mask)
        super.draw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mask.reset()
        mask.addRoundRect(0f, 0f, width.toFloat(), height.toFloat(), cornerRadius, cornerRadius, Path.Direction.CW)
    }
}