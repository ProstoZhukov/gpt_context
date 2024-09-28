package ru.tensor.sbis.design.buttons.link.api

import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonResourceStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitleSize
import ru.tensor.sbis.design.buttons.base.utils.drawers.updateVisibilityByState
import ru.tensor.sbis.design.buttons.base.utils.style.loadAlpha
import ru.tensor.sbis.design.buttons.base.utils.style.loadEnum
import ru.tensor.sbis.design.buttons.base.utils.style.loadFontIcon
import ru.tensor.sbis.design.buttons.base.utils.style.loadLinkButtonIconStyle
import ru.tensor.sbis.design.buttons.base.utils.style.loadLinkButtonTitleStyle
import ru.tensor.sbis.design.buttons.base.utils.style.loadState
import ru.tensor.sbis.design.buttons.base.utils.style.loadStyle
import ru.tensor.sbis.design.buttons.button.models.SbisButtonModel
import ru.tensor.sbis.design.buttons.base.utils.style.SbisButtonStyleHolder
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.buttons.icon_text.SbisIconAndTextButtonModel
import ru.tensor.sbis.design.buttons.icon_text.api.SbisIconAndTextButtonController
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.models.AbstractHeight
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Контроллер для управления состоянием и внешним видом кнопки [SbisLinkButton][ru.tensor.sbis.design.buttons.SbisLinkButton].
 *
 * @author mb.kruglova
 */
class SbisLinkButtonController internal constructor() :
    SbisIconAndTextButtonController<SbisButtonSize>(SbisButtonSize.S, SbisButtonCustomStyle()) {

    override val inlineHeight get() = size

    override fun setInlineHeight(height: AbstractHeight) {
        size = SbisButtonSize.values()
            .lastOrNull { it.globalVar.getDimen(button.context) <= height.getDimen(button.context) } ?: size
    }

    override var model: SbisButtonModel = SbisButtonModel()
        set(value) {
            val styleComponentChanged = field.compareIconTitleStyleTo(value)
            val stateChanged = field.compareStateTo(value)
            val styleButtonChanged = field.compareStyleTo(value)

            field = value

            val isInvalidated = updateComponentDrawer(field) ||
                styleComponentChanged ||
                stateChanged ||
                styleButtonChanged

            if (styleComponentChanged) updateComponentStyle(field)
            if (stateChanged) state = field.state
            if (styleButtonChanged) field.style?.let { style = it }

            if (isInvalidated) button.invalidate()

            updateAccessibilityText(field.title)
        }

    override var align = HorizontalAlignment.CENTER
        set(value) {
            if (field != value) {
                field = value
                button.invalidate()
            }
        }

    override fun attach(
        button: View,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
        globalStyleHolder: SbisButtonStyleHolder
    ) {
        super.attach(button, attrs, defStyleAttr, defStyleRes, globalStyleHolder)

        val styledAttributes = R.styleable.SbisLinkButton
        button.context.withStyledAttributes(attrs, styledAttributes, defStyleAttr, defStyleRes) {
            style = loadStyle(R.styleable.SbisLinkButton_SbisLinkButton_style, super.style)
            size = loadEnum(
                R.styleable.SbisLinkButton_SbisLinkButton_size,
                super.size,
                *SbisButtonSize.values()
            )
            val icon = loadFontIcon(
                R.styleable.SbisLinkButton_SbisLinkButton_icon,
                R.styleable.SbisLinkButton_SbisLinkButton_iconSize
            )
            val text = getString(R.styleable.SbisLinkButton_SbisLinkButton_title)
            val position = loadEnum(
                R.styleable.SbisLinkButton_SbisLinkButton_titlePosition,
                HorizontalPosition.RIGHT,
                *HorizontalPosition.values()
            )
            val titleSize = loadEnum(
                R.styleable.SbisLinkButton_SbisLinkButton_titleSize,
                size.titleSize,
                *SbisButtonTitleSize.values()
            )
            val title = SbisButtonTitle(text, position, titleSize)

            model = model.copy(
                icon = icon,
                title = title,
                state = loadState(R.styleable.SbisLinkButton_SbisLinkButton_state, super.state)
            )
        }
    }

    override fun onViewStateUpdated(): Boolean =
        updateTitleStyle(button, styleHolder) or updateIconStyle(button, styleHolder)

    override fun onStyleUpdated(style: SbisButtonStyle, attrs: AttributeSet?) {
        when (style) {
            is SbisButtonResourceStyle -> {
                val styleContext = ThemeContextBuilder(
                    button.context,
                    style.linkButtonStyle,
                    style.defaultLinkButtonStyle
                ).build()

                val loadedStyle = SbisButtonCustomStyle()
                styleContext.withStyledAttributes(
                    attrs,
                    R.styleable.SbisLinkButton,
                    ResourcesCompat.ID_NULL,
                    ResourcesCompat.ID_NULL
                ) {
                    loadedStyle.loadStyle(this)
                }

                styleHolder = loadedStyle
            }

            is SbisButtonCustomStyle -> style.apply {
                iconStyle?.loadStyle(button.context) { default, contrast, transparent ->
                    iconColors = default
                    iconContrastColors = contrast
                    iconTransparentColors = transparent
                }
                titleStyle?.loadStyle(button.context) { default, contrast, transparent ->
                    titleColors = default
                    titleContrastColors = contrast
                    titleTransparentColors = transparent
                }
                styleHolder = this
            }
        }
        onViewStateUpdated()
    }

    override fun onSizeUpdated(size: SbisButtonSize) {
        val icon = model.icon
        if (icon != null && updateIconDrawer(button, icon, getIconSize(model, size))) {
            updateIconStyle(button, styleHolder)
            iconDrawer?.updateVisibilityByState(state)
        }
        val title = model.title
        if (title != null && updateTitleDrawer(button, title, getTitleSize(model, size))) {
            updateTitleStyle(button, styleHolder)
            titleDrawer?.updateVisibilityByState(state)
        }

        button.apply {
            minimumHeight = globalStyleHolder.getSize(button, size)

            if (isLaidOut) {
                requestLayout()
            }
        }
    }

    override fun updateIconStyle(
        view: View,
        styleHolder: SbisButtonCustomStyle
    ) = with(styleHolder) {
        iconDrawer?.let {
            val color = iconColors.getColorForState(view.drawableState, iconColors.defaultColor)
            it.setTint(color)
        } ?: false
    }

    fun updateTitleStyle() {
        updateTitleStyle(button, styleHolder)
    }

    override fun updateTitleStyle(
        view: View,
        styleHolder: SbisButtonCustomStyle
    ) = with(styleHolder) {
        titleDrawer?.let {
            val color = titleColors.getColorForState(view.drawableState, titleColors.defaultColor)
            it.setTint(color)
        } ?: false
    }

    private fun updateAccessibilityText(title: SbisButtonTitle?) {
        accessibilityText = when {
            title != null -> title.text.toString()
            else -> ""
        }
    }

    /**
     * Загрузить стиль из атрибутов.
     */
    private fun SbisButtonCustomStyle.loadStyle(array: TypedArray) = with(array) {
        val alpha = loadAlpha(R.styleable.SbisLinkButton_SbisLinkButton_titleDisabledColorAlpha)

        loadLinkButtonTitleStyle(
            { default, contrast, transparent ->
                titleColors = default
                titleContrastColors = contrast
                titleTransparentColors = transparent
            },
            (255 * alpha).toInt()
        )

        loadLinkButtonIconStyle(
            { default, contrast, transparent ->
                iconColors = default
                iconContrastColors = contrast
                iconTransparentColors = transparent
            },
            (255 * alpha).toInt()
        )
    }

    override fun getIconSize(model: SbisIconAndTextButtonModel, size: SbisButtonSize): SbisButtonIconSize {
        val icon = model.icon
        val iconSize = icon?.size
        return when {
            icon == null -> size.iconSize
            iconSize != null -> iconSize
            model.title == null && size > SbisButtonSize.S -> SbisButtonIconSize.X5L
            else -> size.iconSize
        }
    }

    override fun getTitleSize(model: SbisIconAndTextButtonModel, size: SbisButtonSize): SbisButtonTitleSize {
        val title = model.title
        return when {
            title == null -> size.titleSize
            title.size != null -> title.size
            else -> size.titleSize
        }
    }
}