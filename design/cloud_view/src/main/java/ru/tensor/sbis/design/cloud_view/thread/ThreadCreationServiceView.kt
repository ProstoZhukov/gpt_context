package ru.tensor.sbis.design.cloud_view.thread

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.Offset

/**
 * View сервисного сообщения о создании треда.
 *
 * @author vv.chekurda
 */
class ThreadCreationServiceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val iconLayout = TextLayout.createTextLayoutByStyle(getContext(), R.style.ThreadCreationServiceIconStyle) {
        text = SbisMobileIcon.Icon.smi_newDialog.character.toString()
    }
    private val textLayout = TextLayout.createTextLayoutByStyle(getContext(), R.style.ThreadCreationServiceTextStyle)
    private val dateLayout = TextLayout.createTextLayoutByStyle(getContext(), R.style.ThreadCreationServiceDateStyle) {
        isVisibleWhenBlank = false
    }
    private val buttonDefaultHeight = InlineHeight.XS.getDimenPx(getContext())
    private val buttonBottomOffset = Offset.XS.getDimenPx(getContext())

    var text: CharSequence?
        get() = textLayout.text
        set(value) {
            val isChanged = textLayout.configure {
                text = value ?: ""
            }
            if (isChanged) requestLayout()
        }

    var date: CharSequence?
        get() = dateLayout.text
        set(value) {
            val isChanged = dateLayout.configure {
                text = value ?: ""
            }
            if (isChanged) requestLayout()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = dateLayout.height + buttonDefaultHeight + buttonBottomOffset
        val textAvailableWidth = width - iconLayout.width
        textLayout.configure { layoutWidth = textAvailableWidth }
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        dateLayout.layout(width - dateLayout.width, 0)
        textLayout.layout(iconLayout.width, dateLayout.bottom)
        iconLayout.layout(0, dateLayout.bottom + textLayout.baseline - iconLayout.baseline)
    }

    override fun onDraw(canvas: Canvas) {
        dateLayout.draw(canvas)
        textLayout.draw(canvas)
        iconLayout.draw(canvas)
    }
}