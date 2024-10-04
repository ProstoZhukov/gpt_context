package ru.tensor.sbis.design_tile_view.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.view_ext.SimplifiedTextView
import ru.tensor.sbis.design_tile_view.R
import timber.log.Timber

private const val TEXT_LENGTH_THRESHOLD = 10000
private const val TEXT_PREVIEW_LENGTH = 64

/**
 * View компонента "Плитка".
 *
 * - [Стандарт](http://axure.tensor.ru/MobileStandart8/настраиваемый_шаблон_плитки.html)
 *
 * @author us.bessonov
 */
class SbisTileContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.sbisTileContentViewTheme,
    @StyleRes defStyleRes: Int = R.style.SbisTileContentViewTheme
) : ViewGroup(ThemeContextBuilder(context, defStyleAttr, defStyleRes).build(), attrs, defStyleAttr) {

    private val title = SimplifiedTextView(getContext()).apply {
        isVerticalScrollBarEnabled = false
        maxLines = Int.MAX_VALUE
    }

    private val description = SimplifiedTextView(getContext()).apply {
        isVerticalScrollBarEnabled = false
        maxLines = Int.MAX_VALUE
    }

    init {
        getContext().withStyledAttributes(attrs, R.styleable.SbisTileContentView, defStyleAttr, defStyleRes) {
            applyTextAppearance(R.styleable.SbisTileContentView_SbisTileContentView_titleAppearance, title)
            applyTextAppearance(R.styleable.SbisTileContentView_SbisTileContentView_descriptionAppearance, description)
            getInt(R.styleable.SbisTileContentView_SbisTileContentView_titleMaxLines, 0)
                .takeIf { it > 0 }
                ?.let(::setTitleMaxLines)
            getInt(R.styleable.SbisTileContentView_SbisTileContentView_descriptionMaxLines, 0)
                .takeIf { it > 0 }
                ?.let(::setDescriptionMaxLines)
        }
        addView(title, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(description, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        setDefaultPadding()
    }

    /** @SelfDocumented */
    fun setTitle(title: CharSequence?) {
        this.title.setText(title)
    }

    /** @SelfDocumented */
    fun setDescription(description: CharSequence?) {
        this.description.setText(description)
    }

    /** @SelfDocumented */
    fun setTitleColor(@ColorInt color: Int?) {
        color?.let(title::setTextColor)
    }

    /** @SelfDocumented */
    fun setDescriptionColor(@ColorInt color: Int?) {
        color?.let(description::setTextColor)
    }

    /** @SelfDocumented */
    fun setTitleMaxLines(maxLines: Int) {
        title.maxLines = maxLines
    }

    /** @SelfDocumented */
    fun setDescriptionMaxLines(maxLines: Int) {
        description.maxLines = maxLines
    }

    /** @SelfDocumented */
    fun setDescriptionEllipsize(ellipsize: TextUtils.TruncateAt?) {
        description.setEllipsize(ellipsize)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxHeight = MeasureSpec.getSize(heightMeasureSpec)
        val fullWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = maxHeight - paddingTop - paddingBottom
        val availableWidth = fullWidth - paddingLeft - paddingRight

        title.measureText(availableWidth, availableHeight)

        val availableForDescriptionHeight = availableHeight - title.measuredHeight
        description.measureText(availableWidth, availableForDescriptionHeight)

        setMeasuredDimension(
            fullWidth,
            title.measuredHeight + description.measuredHeight + paddingTop + paddingBottom
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        title.layout(paddingLeft, paddingTop, paddingLeft + title.measuredWidth, paddingTop + title.measuredHeight)
        description.layout(
            paddingLeft,
            title.bottom,
            paddingLeft + description.measuredWidth,
            title.bottom + description.measuredHeight
        )
    }

    private fun TypedArray.applyTextAppearance(index: Int, textView: SimplifiedTextView) {
        getResourceId(index, ResourcesCompat.ID_NULL)
            .takeIf { it != ResourcesCompat.ID_NULL }
            ?.let { textView.setTextAppearance(context, it) }
    }

    private fun SimplifiedTextView.measureText(@Px availableWidth: Int, @Px availableHeight: Int) {
        measure(
            MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.AT_MOST)
        )
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun SimplifiedTextView.setText(text: CharSequence?) {
        text?.let {
            if (it.length > TEXT_LENGTH_THRESHOLD) {
                Timber.w(
                    "Text length is very big (${it.length}), and it can cause slow processing and " +
                        "freezes during scrolling. Consider passing shorter version of the text that starts with " +
                        "'${it.take(TEXT_PREVIEW_LENGTH)}...'"
                )
            }
        }
        this.text = text
        isVisible = !text.isNullOrBlank()
    }

    private fun setDefaultPadding() {
        val defaultPadding = resources.getDimensionPixelSize(R.dimen.design_tile_view_content_default_padding)
        setPadding(
            paddingLeft.takeIf { it > 0 } ?: defaultPadding,
            paddingTop.takeIf { it > 0 } ?: defaultPadding,
            paddingRight.takeIf { it > 0 } ?: defaultPadding,
            paddingBottom.takeIf { it > 0 } ?: defaultPadding
        )
    }
}