package ru.tensor.sbis.design.buttons.base.utils.drawers

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import ru.tensor.sbis.design.buttons.base.utils.style.SbisButtonStyleHolder
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize

/**
 * @author ma.kolpakov
 */
internal class ProgressDrawer(
    val button: View,
    private val globalStyleHolder: SbisButtonStyleHolder
) : ButtonComponentDrawer {

    private val drawable by lazy {
        CircularProgressDrawable(button.context).apply {
            callback = button
        }
    }

    private var isSizeLoaded = false

    private var pendingButtonSize: SbisRoundButtonSize? = null

    /**
     * Цвет индикатора прогресса.
     */
    private var tintColor: Int? = null

    /**
     * Размер кнопки, в которой создан прогресс.
     */
    var buttonSize: SbisRoundButtonSize = SbisRoundButtonSize.M
        private set(value) {
            field = value
            with(button.context.resources) {
                drawable.strokeWidth = getDimension(buttonSize.progressWidth)
                drawable.centerRadius =
                    getDimension(buttonSize.progressSize) / 2F - drawable.strokeWidth
                val xyCoordinate = globalStyleHolder.getIconSize(button, buttonSize.iconSize)
                drawable.bounds = Rect(0, 0, xyCoordinate, xyCoordinate)
            }
        }

    override var isVisible: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            if (field) {
                applyPendingButtonSize()
                drawable.start()
            } else {
                drawable.stop()
            }
        }

    override val width: Float
        get() = drawable.bounds.width().toFloat()
    override val height: Float
        get() = drawable.bounds.height().toFloat()

    override fun setTint(color: Int): Boolean {
        return if (color == tintColor) {
            false
        } else {
            tintColor = color
            drawable.setColorSchemeColors(color)
            true
        }
    }

    override fun draw(canvas: Canvas) {
        if (isVisible) {
            drawable.draw(canvas)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProgressDrawer

        if (buttonSize != other.buttonSize) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 31 + drawable.hashCode()
        result = 31 * result + buttonSize.progressSize
        result = 31 * result + buttonSize.progressWidth
        return result
    }

    /**
     * Сравнение внутренного [Drawable] с [other].
     *
     * @return true, если равны, иначе false.
     */
    fun verify(other: Drawable) = drawable == other

    /**
     * Установить размер прогресса.
     *
     * @return true, если размер обновлен, иначе false.
     */
    fun setSize(size: SbisRoundButtonSize): Boolean =
        if (size != buttonSize || !isSizeLoaded) {
            if (isVisible) {
                buttonSize = size
            } else {
                pendingButtonSize = size
            }
            isSizeLoaded = true
            true
        } else {
            false
        }

    private fun applyPendingButtonSize() {
        pendingButtonSize?.let {
            buttonSize = it
            pendingButtonSize = null
        }
    }
}