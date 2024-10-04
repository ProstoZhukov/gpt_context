package ru.tensor.sbis.design.breadcrumbs

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.breadcrumbs.databinding.BreadcrumbsCurrentFolderViewBinding
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.R as RDesign

/**
 * [View] разделителя-заголовка "Назад".
 * Предназначен для отображения заголовка текущего раздела при проваливании и возврата на уровень выше.
 *
 * - [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=кнопка_назад&g=1)
 *
 * @author us.bessonov
 */
class CurrentFolderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.currentFolderViewTheme
) : ConstraintLayout(
    ThemeContextBuilder(
        context,
        defStyleAttr = defStyleAttr,
        defaultStyle = R.style.DefaultCurrentFolderViewTheme
    ).build(),
    attrs,
    defStyleAttr
) {

    /** @SelfDocumented */
    @ColorInt
    internal var backgroundColor: Int = Color.MAGENTA
        set(value) {
            field = value
            setBackgroundColor(value)
        }

    /** @SelfDocumented */
    @ColorInt
    internal var dividerBackgroundColor: Int = Color.MAGENTA
        set(value) {
            field = value
            viewBinding.breadcrumbsDivider.setBackgroundColor(value)
        }

    private val viewBinding = BreadcrumbsCurrentFolderViewBinding.inflate(LayoutInflater.from(getContext()), this)

    @Px
    private val viewHeight = getContext().getDimenPx(ru.tensor.sbis.design.R.attr.inlineHeight_m)

    /**
     * Стиль разделителя-заголовка
     * Если false - "По умолчанию",если true - "Дополнительный"
     */
    var isAdditionalStyle: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            backgroundColor = if (field) {
                showDivider()
                BackgroundColor.DEFAULT.getValue(context)
            } else {
                hideDivider()
                getColorFromAttr(ru.tensor.sbis.design.R.attr.unaccentedAdaptiveBackgroundColor)
            }
            invalidate()
        }

    init {
        context.withStyledAttributes(attrs, R.styleable.CurrentFolderView, defStyleAttr, 0) {
            val title = getString(R.styleable.CurrentFolderView_CurrentFolderView_title).orEmpty()
            setTitle(title)

            //Оставил не на глобальных для обратной совместимости
            backgroundColor = getColor(
                R.styleable.CurrentFolderView_CurrentFolderView_backgroundColor,
                ContextCompat.getColor(context, RDesign.color.palette_color_white1)
            )

            //Оставил не на глобальных для обратной совместимости
            dividerBackgroundColor = getColor(
                R.styleable.CurrentFolderView_CurrentFolderView_dividerColor,
                ContextCompat.getColor(context, RDesign.color.palette_alpha_color_black2)
            )

            val isDividerVisible = getBoolean(R.styleable.CurrentFolderView_CurrentFolderView_showDivider, true)
            viewBinding.breadcrumbsDivider.isVisible = isDividerVisible
        }
    }

    /** @SelfDocumented */
    fun setTitle(text: String) {
        viewBinding.breadcrumbsTitle.text = text
    }

    /** @SelfDocumented */
    fun setTitle(@StringRes textRes: Int) {
        viewBinding.breadcrumbsTitle.setText(textRes)
    }

    /**
     * Отображается ли разделитель.
     * Требуется для определения необходимости отрисовки разделителя в родительском [View]
     */
    internal fun isDividerVisible() = viewBinding.breadcrumbsDivider.isVisible

    /**
     * Задаёт отступ заголовка справа.
     * Использование padding'а приводило бы к некорректному отображению разделителя
     */
    internal fun setTitleMarginEnd(@Px endMargin: Int) {
        viewBinding.breadcrumbsTitle.updateLayoutParams<MarginLayoutParams> {
            marginEnd = endMargin
        }
    }

    /**
     * Скрывает разделитель снизу.
     * Требуется для случая, когда отрисовка разделителя выполняется в родительском [View]
     */
    internal fun hideDivider() {
        viewBinding.breadcrumbsDivider.isVisible = false
    }

    /**
     * Показывает разделитель снизу.
     */
    private fun showDivider() {
        viewBinding.breadcrumbsDivider.isVisible = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY))
    }

    override fun getBaseline() = with(viewBinding.breadcrumbsTitle) { top + baseline }
}