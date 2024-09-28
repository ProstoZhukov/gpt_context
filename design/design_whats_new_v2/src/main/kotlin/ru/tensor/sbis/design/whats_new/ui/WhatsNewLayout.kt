package ru.tensor.sbis.design.whats_new.ui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.getSystemService
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.extentions.getFullMeasuredHeight
import ru.tensor.sbis.design.utils.extentions.getFullMeasuredWidth
import ru.tensor.sbis.design.whats_new.R

/**
 * Layout для расположения элементов по стандарту в landscape.
 *
 * @author ps.smirnyh
 */
internal class WhatsNewLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = ID_NULL,
    defStyleRes: Int = ID_NULL
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val gradientView: View
        get() = findViewById(R.id.whats_new_gradient)

    private val logoView: View
        get() = findViewById(R.id.whats_new_logo)

    private val imageView: ImageView
        get() = findViewById(R.id.whats_new_image)

    private val closeView: View
        get() = findViewById(R.id.whats_new_button_close)

    private val titleView: View
        get() = findViewById(R.id.whats_new_title)

    private val scrollView: View
        get() = findViewById(R.id.whats_new_description_container)

    private val startView: View
        get() = findViewById(R.id.whats_new_button_start)

    private val bigOffset = Offset.X3L.getDimenPx(context)
    private val maxImageViewWidth = resources.getDimensionPixelSize(R.dimen.whats_new_image_max_width)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as? ViewGroup)?.updateLayoutParams<MarginLayoutParams> {
            setMargins(bigOffset)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightAvailable = MeasureSpec.getSize(heightMeasureSpec)
        val widthAvailable = MeasureSpec.getSize(widthMeasureSpec)
        var desiredHeight = 0
        var desiredWidth = 0
        logoView.measure(
            makeMeasureSpec(widthAvailable, AT_MOST),
            makeMeasureSpec(logoView.layoutParams.height, EXACTLY)
        )
        desiredHeight += logoView.layoutParams.height
        closeView.measure(
            makeMeasureSpec(widthAvailable, AT_MOST),
            makeMeasureSpec(heightAvailable, AT_MOST)
        )
        titleView.measure(
            makeMeasureSpec((getScreenWidth() * 0.55).toInt(), EXACTLY),
            makeMeasureSpec(heightAvailable, AT_MOST)
        )
        desiredHeight += titleView.getFullMeasuredHeight()
        imageView.measure(
            makeMeasureSpec(
                minOf(widthAvailable - titleView.getFullMeasuredWidth() - imageView.marginStart, maxImageViewWidth),
                EXACTLY
            ),
            makeMeasureSpec(widthAvailable, AT_MOST)
        )
        desiredWidth += imageView.getFullMeasuredWidth()
        startView.measure(
            makeMeasureSpec(widthAvailable, AT_MOST),
            makeMeasureSpec(heightAvailable, AT_MOST)
        )
        desiredHeight += startView.getFullMeasuredHeight()
        val availableHeightForScrollView = heightAvailable - desiredHeight - scrollView.marginTop
        scrollView.measure(
            makeMeasureSpec(titleView.measuredWidth, EXACTLY),
            makeMeasureSpec(availableHeightForScrollView, AT_MOST)
        )
        desiredHeight += scrollView.getFullMeasuredHeight()
        desiredWidth += scrollView.getFullMeasuredWidth()
        gradientView.measure(
            makeMeasureSpec(desiredWidth, EXACTLY),
            makeMeasureSpec(desiredHeight, EXACTLY)
        )
        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        gradientView.layout(0, 0, gradientView.measuredWidth, gradientView.measuredHeight)
        logoView.layout(
            logoView.marginStart,
            (logoView.layoutParams.height - logoView.measuredHeight) / 2
        )
        closeView.layout(
            measuredWidth - closeView.measuredWidth - closeView.marginEnd,
            logoView.top + (logoView.measuredHeight - closeView.measuredHeight) / 2
        )
        imageView.layout(
            imageView.marginStart,
            logoView.bottom + imageView.marginTop
        )
        titleView.layout(
            imageView.right + titleView.marginStart,
            imageView.top
        )
        scrollView.layout(
            titleView.left,
            titleView.bottom + scrollView.marginTop
        )
        startView.layout(
            (measuredWidth - startView.measuredWidth) / 2,
            scrollView.bottom + startView.marginTop
        )
    }

    @Suppress("DEPRECATION")
    private fun getScreenWidth(): Int {
        val windowManager = context.getSystemService<WindowManager>() ?: return 0
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            val insets = WindowInsetsCompat.toWindowInsetsCompat(metrics.windowInsets)
                .getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
            metrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    private fun View.layout(x: Int, y: Int) {
        layout(x, y, x + measuredWidth, y + measuredHeight)
    }
}