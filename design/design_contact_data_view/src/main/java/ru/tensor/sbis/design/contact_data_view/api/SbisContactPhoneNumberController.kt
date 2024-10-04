package ru.tensor.sbis.design.contact_data_view.api

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.contact_data_view.ClickElementListener
import ru.tensor.sbis.design.contact_data_view.R
import ru.tensor.sbis.design.contact_data_view.SbisContactPhoneNumberView
import ru.tensor.sbis.design.contact_data_view.model.SbisContactPhoneNumberModel
import ru.tensor.sbis.design.contact_data_view.style.loadColorStateList
import ru.tensor.sbis.design.text_span.text.masked.formatter.phone.formatPhone
import ru.tensor.sbis.design.theme.global_variables.BorderColor
import ru.tensor.sbis.design.theme.global_variables.BorderThickness

/**
 * Описание API для управления [SbisContactPhoneNumberView]
 *
 * @author av.efimov1
 */
class SbisContactPhoneNumberController : SbisContactPhoneNumberApi {

    private lateinit var view: SbisContactPhoneNumberView

    private var currentModel: SbisContactPhoneNumberModel? = null

    private var radius: Float = 0f

    private var additionalShortText: String = ""

    override var clickListener: ClickElementListener? = null

    internal fun attach(view: SbisContactPhoneNumberView, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        view.context.theme.applyStyle(defStyleRes, false)
        this.view = view
        radius = view.resources.getDimension(R.dimen.design_contact_data_phone_number_background_radius)
        additionalShortText = view.resources.getString(R.string.design_contact_data_additional_short_text)

        val styledAttributes = R.styleable.SbisContactPhoneNumberView
        view.context.withStyledAttributes(attrs, styledAttributes, defStyleAttr, defStyleRes) {
            val backgroundColors = loadColorStateList(
                R.styleable.SbisContactPhoneNumberView_SbisContactPhoneNumberView_backgroundColorPressed,
                R.styleable.SbisContactPhoneNumberView_SbisContactPhoneNumberView_backgroundColorDisabled,
                R.styleable.SbisContactPhoneNumberView_SbisContactPhoneNumberView_backgroundColor
            )

            view.apply {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    getDimension(
                        R.styleable.SbisContactPhoneNumberView_SbisContactPhoneNumberView_textSize,
                        0f
                    )
                )
                setTextColor(
                    getColor(
                        R.styleable.SbisContactPhoneNumberView_SbisContactPhoneNumberView_textColor,
                        Color.MAGENTA
                    )
                )
            }

            view.background = createBackground(
                radius = radius,
                backgroundColors = backgroundColors
            )
        }

        view.setOnClickListener {
            currentModel?.let {
                clickListener?.invoke(it.phoneNumber, it.calleeId)
            }
        }
    }

    private fun createBackground(
        radius: Float,
        backgroundColors: ColorStateList,
        borderColor: BorderColor? = BorderColor.DEFAULT,
        borderThickness: BorderThickness? = BorderThickness.M
    ): Drawable {
        val radii = FloatArray(8) { radius }
        val background = ShapeDrawable(RoundRectShape(radii, null, null))
        background.setTintList(backgroundColors)
        return if (borderColor != null && borderThickness != null) {
            val borderWidth = borderThickness.getDimen(view.context)
            val borderInset = RectF(borderWidth, borderWidth, borderWidth, borderWidth)
            val border = ShapeDrawable(RoundRectShape(radii, borderInset, radii))
            border.setTintList(ColorStateList.valueOf(borderColor.getValue(view.context)))
            LayerDrawable(arrayOf(background, border))
        } else {
            background
        }
    }

    override fun setData(data: SbisContactPhoneNumberModel) {
        currentModel = data
        view.text = formatPhoneNumber(data)
    }

    private fun formatPhoneNumber(data: SbisContactPhoneNumberModel): String {
        val phoneNumberText = data.formattedPhoneNumber ?: formatPhone(data.phoneNumber)
        val additionalPhoneNumberText = data.additionalNumber
        return if (additionalPhoneNumberText != null) {
            "$phoneNumberText $additionalShortText $additionalPhoneNumberText"
        } else {
            "$phoneNumberText"
        }
    }
}