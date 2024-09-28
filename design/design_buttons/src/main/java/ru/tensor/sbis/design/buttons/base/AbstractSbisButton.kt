package ru.tensor.sbis.design.buttons.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.base.api.AbstractButtonApi
import ru.tensor.sbis.design.buttons.base.api.AbstractSbisButtonController
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.theme.models.AbstractHeightModel
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.theme.AbstractHeightCompatibilityView
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

/**
 * Базовая механика и инфраструктура кнопки.
 *
 * @author ma.kolpakov
 */
abstract class AbstractSbisButton<
    SIZE : AbstractHeightModel,
    out VIEW_CONTROLLER : AbstractSbisButtonController<SIZE>>
internal constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    protected val controller: VIEW_CONTROLLER
) : View(
    ThemeContextBuilder(context, defaultStyle = R.style.SbisButtonBaseTheme).build(),
    attrs,
    defStyleAttr,
    defStyleRes
),
    AbstractButtonApi<SIZE> by controller,
    AbstractHeightCompatibilityView<SIZE>,
    CoordinatorLayout.AttachedBehavior {

    private var progressState: SbisButtonState? = null

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (controller.onViewStateUpdated()) {
            invalidate()
        }
    }

    override fun setEnabled(enabled: Boolean) {
        controller.state = progressState
            ?: if (enabled) SbisButtonState.ENABLED else SbisButtonState.DISABLED
        super.setEnabled(enabled)
        refreshDrawableState()
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> =
        controller.getSbisViewBehavior()

    override fun offsetTopAndBottom(offset: Int) {
        if (controller.checkOffsetTopAndBottomInability(offset)) return
        super.offsetTopAndBottom(offset)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        controller.onLayout()

        // для Android 30 и ниже
        // Если прикладник выставляет layout кнопки по нулям, имитируя скрытие кнопки,
        // то тень все равно рисуется
        when {
            VERSION.SDK_INT < VERSION_CODES.S && left == 0 && top == 0 && right == 0 && bottom == 0 ->
                this.elevation = 0f

            this.elevation != 0f ->
                controller.elevation = this.elevation

            else ->
                this.elevation = controller.elevation
        }
    }

    /**
     * Установить стиль кнопки контроллером Дзен темизации.
     *
     * Метод требуется, т.к. у разных кнопок по-разному работает установка стиля через поле
     * [AbstractSbisButtonController.style]. Чтобы не изменять правила установки стиля через вышеописанное поле, принято
     * решение использовать отдельный метод, где можно описать условия для каждой конкретной кнопки по отдельности.
     */
    internal open fun setZenButtonStyle(style: SbisButtonStyle) {
        this.style = style
    }

    internal fun setInProgress() {
        progressState = SbisButtonState.IN_PROGRESS
        isEnabled = false
        progressState = null
    }
}
