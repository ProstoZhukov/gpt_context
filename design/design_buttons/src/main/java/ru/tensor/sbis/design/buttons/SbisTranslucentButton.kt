package ru.tensor.sbis.design.buttons

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.utils.style.loadEnum
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground
import ru.tensor.sbis.design.buttons.translucent.api.SbisTranslucentButtonApi
import ru.tensor.sbis.design.buttons.translucent.models.SbisTranslucentButtonStyle
import ru.tensor.sbis.design.utils.delegateNotEqual

/**
 * Кнопка [SbisButton] с полупрозрачным оформлением.
 *
 * @author mb.kruglova
 */
class SbisTranslucentButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.sbisTranslucentButtonDarkTheme,
    @StyleRes defStyleRes: Int = R.style.SbisTranslucentButtonDefaultDarkTheme
) : SbisButton(
    context,
    attrs,
    defStyleAttr,
    defStyleRes
),
    SbisTranslucentButtonApi {
    override var backgroundType: SbisButtonBackground
        get() = SbisButtonBackground.Default
        set(_) = Unit // Не изменяется

    override var style: SbisButtonStyle
        get() = super.style
        set(_) = Unit // Не изменяется

    override var translucentStyle: SbisTranslucentButtonStyle by delegateNotEqual(
        SbisTranslucentButtonStyle.DARK
    ) { newStyle ->
        super.style = newStyle.getButtonStyle()
    }

    init {
        this.context.withStyledAttributes(attrs, R.styleable.SbisTranslucentButton, defStyleAttr, defStyleRes) {
            translucentStyle = loadEnum(
                R.styleable.SbisTranslucentButton_SbisTranslucentButton_translucentStyle,
                translucentStyle,
                *SbisTranslucentButtonStyle.values()
            )
        }
        super.style = translucentStyle.getButtonStyle()
    }

    /** @SelfDocumented */
    override fun setZenButtonStyle(style: SbisButtonStyle) {
        super.style = style
    }

}
