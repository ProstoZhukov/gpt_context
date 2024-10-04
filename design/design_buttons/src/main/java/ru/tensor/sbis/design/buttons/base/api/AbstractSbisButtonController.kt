package ru.tensor.sbis.design.buttons.base.api

import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.CallSuper
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.buttons.base.AbstractSbisButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonDrawableIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.offset.SbisViewOffsetBehaviorImpl
import ru.tensor.sbis.design.buttons.base.models.offset.SbisViewOffsetBehavior
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.utils.drawers.ButtonComponentDrawer
import ru.tensor.sbis.design.buttons.base.utils.drawers.DrawableIconDrawer
import ru.tensor.sbis.design.buttons.base.utils.drawers.ProgressDrawer
import ru.tensor.sbis.design.buttons.base.utils.drawers.TextIconDrawer
import ru.tensor.sbis.design.buttons.base.utils.drawers.updateVisibilityByState
import ru.tensor.sbis.design.buttons.base.utils.style.SbisButtonStyleHolder
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.theme.models.AbstractHeightModel

/**
 * Базовый контроллер для управления состоянием и внешним видом кнопки
 *
 * @author ma.kolpakov
 */
abstract class AbstractSbisButtonController<SIZE : AbstractHeightModel> internal constructor(
    defaultSize: SIZE,
    internal var styleHolder: SbisButtonCustomStyle,
    private val viewOffsetBehavior: SbisViewOffsetBehaviorImpl = SbisViewOffsetBehaviorImpl()
) : AbstractButtonApi<SIZE>,
    SbisViewOffsetBehavior by viewOffsetBehavior {

    /**
     * Метка разрешает первичную установку при инициализации, чтобы сосредоточить обновление
     * стилей в методе [onStyleUpdated]
     */
    private var isStyleLoaded = false

    /**
     * Метка разрешает первичную установку при инициализации, чтобы сосредоточить обновление
     * размеров в методе [onSizeUpdated]
     */
    private var isSizeLoaded = false

    /**
     * Отметка о том, является ли кнопка плавающей
     */
    internal var isFloating = false

    /**
     * Тень у кнопки.
     */
    internal var elevation: Float = 0F

    /**
     * Кнопка. Доступна после вызовы [attach]
     */
    internal lateinit var button: View
        private set

    internal var iconDrawer: ButtonComponentDrawer? = null
        private set

    internal lateinit var progressDrawer: ProgressDrawer
        private set

    internal lateinit var globalStyleHolder: SbisButtonStyleHolder
        private set

    final override var state: SbisButtonState = SbisButtonState.ENABLED
        set(value) {
            if (field != value) {
                field = value

                state.mapStateToAvailability()?.let {
                    button.isEnabled = it
                }

                onStateUpdated(field)

                if (state == SbisButtonState.IN_PROGRESS) {
                    with(button as AbstractSbisButton<*, *>) {
                        setInProgress()
                    }
                }
            }
        }

    private var attrs: AttributeSet? = null

    final override var style: SbisButtonStyle = SbisButtonStyle.DEFAULT
        set(value) {
            if (value != field || !isStyleLoaded) {
                field = value
                isStyleLoaded = true
                onStyleUpdated(field, attrs)
                attrs = null
                button.invalidate()
            }
        }

    final override var size: SIZE = defaultSize
        set(value) {
            if (value != field || !isSizeLoaded) {
                field = value
                isSizeLoaded = true
                onSizeUpdated(field)
            }
        }

    override var scaleOn: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            onSizeUpdated(size)
        }

    @CallSuper
    internal open fun attach(
        button: View,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int,
        globalStyleHolder: SbisButtonStyleHolder
    ) {
        this.button = button
        this.attrs = attrs
        this.globalStyleHolder = globalStyleHolder
        progressDrawer = ProgressDrawer(button, globalStyleHolder)
        viewOffsetBehavior.initSbisView(button)
    }

    /**
     * Метод вызывается для размещения внутренних элементов кнопки и вызывается в момент [View.onLayout].
     */
    internal open fun onLayout() = Unit

    /**
     * Метод вызывается для обработки нажатий, активности и других состояний view для обновления
     * внешнего вида
     */
    internal open fun onViewStateUpdated(): Boolean = false

    /**
     * Метод вызывается при обновлении стиля в [style]
     */
    protected open fun onStyleUpdated(style: SbisButtonStyle, attrs: AttributeSet?) = Unit

    /**
     * Метод вызывается при обновлении размера в [size]
     */
    protected open fun onSizeUpdated(size: SIZE) = Unit

    /**
     * Метод вызывается при обновлении состояния в [state] до изменения [View.isEnabled] атрибута
     * кнопки
     */
    protected open fun onStateUpdated(state: SbisButtonState) = Unit

    /**
     * Обновить иконку или её размер.
     */
    protected fun updateIconDrawer(
        view: View,
        icon: SbisButtonIcon?,
        size: SbisButtonIconSize
    ): Boolean {
        if (icon == null) {
            val updated = iconDrawer != null
            iconDrawer = null
            return updated
        }

        val iconSize = globalStyleHolder.getIconSize(view, size, icon.scaleOn ?: scaleOn)

        val newIconDrawer = when (icon) {
            is SbisButtonDrawableIcon ->
                DrawableIconDrawer(icon.iconRes, icon.icon, iconSize, view.context)
            is SbisButtonTextIcon -> {
                icon.icon?.let {
                    TextIconDrawer(it, iconSize, view.minimumWidth, view.context)
                }
            }
        }

        return if (newIconDrawer != iconDrawer) {
            iconDrawer = newIconDrawer
            true
        } else {
            false
        }
    }

    protected fun updateIcon() {
        updateIconStyle(button, styleHolder)
        iconDrawer?.updateVisibilityByState(state)
    }

    abstract fun updateIconStyle(
        view: View,
        styleHolder: SbisButtonCustomStyle
    ): Boolean

    protected fun updateProgress(
        size: SbisRoundButtonSize,
        color: Int
    ): Boolean {
        val sizeChanged = progressDrawer.setSize(size)
        val colorChanged = progressDrawer.setTint(color)

        return (sizeChanged || colorChanged)
    }
}
