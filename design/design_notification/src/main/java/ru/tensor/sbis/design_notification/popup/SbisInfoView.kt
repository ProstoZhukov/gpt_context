package ru.tensor.sbis.design_notification.popup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.text.Layout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.AttrRes
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.updatePadding
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutAutoTestsHelper
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.toolbar.util.StatusBarHelper
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.getActivity
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design_notification.R
import ru.tensor.sbis.design_notification.databinding.DesignNotificationSbisInfoViewBinding
import java.lang.Integer.max
import ru.tensor.sbis.design.R as DesignR

/**
 * Стандартная реализация [View] панели-информера
 *
 * @author us.bessonov
 */
class SbisInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = ResourcesCompat.ID_NULL,
    @StyleRes defStyleRes: Int = ResourcesCompat.ID_NULL,
    private val message: String = "",
    icon: String? = null,
    closeCallback: (() -> Unit)? = null
) : ViewGroup(ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(), attrs, defStyleAttr) {

    @Px
    private var singleLineTextSize: Float = 0f

    @Px
    private var multiLineTextSize: Float = 0f

    private val binding = DesignNotificationSbisInfoViewBinding.inflate(LayoutInflater.from(getContext()), this)

    private val textLayout = TextLayout()

    private val bounds = Rect()

    val isEllipsized
        get() = textLayout.getEllipsisCount(max(0, textLayout.lineCount - 1)) > 0

    init {
        id = R.id.design_notification_info_view
        setWillNotDraw(false)
        getContext().withStyledAttributes(null, ATTRIBUTES) {
            setBackgroundColor(getColor(ATTRIBUTES.indexOf(R.attr.sbisPopupNotificationBackground), Color.MAGENTA))
            binding.designNotificationDivider.setBackgroundColor(
                getColor(
                    ATTRIBUTES.indexOf(R.attr.sbisPopupNotificationDividerColor),
                    Color.TRANSPARENT
                )
            )
            textLayout.configure {
                paint.color = getColor(ATTRIBUTES.indexOf(R.attr.sbisPopupNotificationTextColor), paint.color)
            }
            singleLineTextSize = getDimension(ATTRIBUTES.indexOf(DesignR.attr.fontSize_l_scaleOff), 0f)
            multiLineTextSize = getDimension(ATTRIBUTES.indexOf(DesignR.attr.fontSize_xs_scaleOff), 0f)
        }
        binding.designNotificationIcon.text = icon
        binding.designNotificationCloseButton.setOnClickListener { closeCallback?.invoke() }

        isClickable = true

        accessibilityDelegate = TextLayoutAutoTestsHelper(this, textLayout)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) = with(binding) {
        val paddingTop = getStatusBarHeight()
        binding.designNotificationIcon.updatePadding(top = paddingTop)
        binding.designNotificationCloseButton.updatePadding(top = paddingTop)

        val fullWidth = MeasureSpec.getSize(widthMeasureSpec)
        val width = fullWidth - getLeftMargin() - getRightMargin()
        val height = getViewHeight()

        val widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        val heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        children.forEach {
            measureChild(it, widthSpec, heightSpec)
        }

        val availableWidth = width - designNotificationIcon.measuredWidth - designNotificationCloseButton.measuredWidth
        with(textLayout) {
            configure {
                text = message
                alignment = Layout.Alignment.ALIGN_CENTER
                paint.textSize = singleLineTextSize
                maxWidth = availableWidth
            }
            if (getDesiredWidth(message) <= availableWidth) {
                configure {
                    maxLines = 1
                }
            } else {
                configure {
                    maxLines = 2
                    paint.textSize = multiLineTextSize
                }
            }
        }

        setMeasuredDimension(fullWidth, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) = with(binding) {
        bounds.set(left + getLeftMargin(), top, right - getRightMargin(), bottom)
        designNotificationIcon.layout(bounds.left, top)
        designNotificationCloseButton.layout(bounds.right - designNotificationCloseButton.measuredWidth, top)
        designNotificationDivider.layout(bounds.left, bounds.bottom - designNotificationDivider.measuredHeight)
        textLayout.layout(
            bounds.left + (bounds.width() - textLayout.width) / 2,
            (getStatusBarHeight() + measuredHeight - textLayout.height) / 2
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textLayout.draw(canvas)
    }

    @Px
    private fun getViewHeight() = getTopNavigationHeight() + getStatusBarHeight()

    @Px
    private fun getTopNavigationHeight() = context.getDimenPx(DesignR.attr.inlineHeight_l)

    @Px
    private fun getStatusBarHeight() = if (isWindowFullscreen()) {
        0
    } else {
        ViewCompat.getRootWindowInsets(this)?.getInsets(WindowInsetsCompat.Type.statusBars())?.top
            ?: StatusBarHelper.getStatusBarHeight(context)
    }

    @Px
    private fun getRightMargin() = getNavigationBarInsets()?.right ?: 0

    @Px
    private fun getLeftMargin() = getNavigationBarInsets()?.left ?: 0

    private fun getNavigationBarInsets() =
        ViewCompat.getRootWindowInsets(this)?.getInsets(WindowInsetsCompat.Type.navigationBars())

    private fun isWindowFullscreen() =
        getActivity().window.attributes.flags.and(WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0
}

private val ATTRIBUTES = intArrayOf(
    R.attr.sbisPopupNotificationBackground,
    R.attr.sbisPopupNotificationTextColor,
    R.attr.sbisPopupNotificationDividerColor,
    DesignR.attr.fontSize_l_scaleOff,
    DesignR.attr.fontSize_xs_scaleOff
)