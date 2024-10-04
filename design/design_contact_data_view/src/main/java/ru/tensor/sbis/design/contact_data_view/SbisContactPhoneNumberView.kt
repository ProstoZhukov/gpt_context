package ru.tensor.sbis.design.contact_data_view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatTextView
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.contact_data_view.api.SbisContactPhoneNumberApi
import ru.tensor.sbis.design.contact_data_view.api.SbisContactPhoneNumberController
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Вью для отображения контактных данных(номер телефона)
 *
 * @author av.efimov1
 */
class SbisContactPhoneNumberView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: SbisContactPhoneNumberController
) : AppCompatTextView(ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(), attrs, defStyleAttr),
    SbisContactPhoneNumberApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.SbisContactPhoneNumberDefaultTheme,
        @StyleRes defStyleRes: Int = R.style.SbisContactPhoneNumberDefaultTheme
    ) : this(context, attrs, defStyleAttr, defStyleRes, SbisContactPhoneNumberController())

    @Px
    private val paddingVertical = context.resources.getDimensionPixelSize(R.dimen.design_contact_data_vertical_padding)

    @Px
    private val paddingHorizontal =
        context.resources.getDimensionPixelSize(R.dimen.design_contact_data_horizontal_padding)

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
        typeface = TypefaceManager.getRobotoRegularFont(context)
        includeFontPadding = false
        ellipsize = TextUtils.TruncateAt.END
        maxLines = 1
        this.isClickable = true

        setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
    }
}