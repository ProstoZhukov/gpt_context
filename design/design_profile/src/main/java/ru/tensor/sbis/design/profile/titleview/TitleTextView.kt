package ru.tensor.sbis.design.profile.titleview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutAutoTestsHelper
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.profile.R
import kotlin.math.roundToInt

/**
 * Вью для отображения названия экрана.
 *
 * @author mb.kruglova
 */
class TitleTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {
    private var names: List<CharSequence?>? = null
    private var hiddenNamesCount = 0
    private var isExactlyHeightSpec: Boolean = false

    @Px
    private var defaultTextSize: Float

    @Px
    private var smallerTextSize: Float

    @StringRes
    private var counterTextRes = R.string.design_profile_dialog_title_counter
    private var shouldRedoAfterMeasure = false

    /**
     * Поддержка отображения списка имен в полную ширину строки (длинное имя уходит в многоточие).
     */
    private var supportNamesFullWidth = false

    private val textLayout: TextLayout = TextLayout {
        paint.typeface = TypefaceManager.getRobotoRegularFont(context)
        includeFontPad = false
        paint.textSize =
            resources.getDimension(ru.tensor.sbis.design.R.dimen.toolbar_title_without_subtitle_text_size_dp)
        ellipsize = TextUtils.TruncateAt.END
        maxLines = 1
    }

    var text: CharSequence
        get() = textLayout.text
        set(value) {
            if (text != value) {
                updateConfig(newText = value)
            }
        }

    var textSize: Float = textLayout.textPaint.textSize
        private set

    private var textColors: ColorStateList = ColorStateList.valueOf(Color.WHITE)

    var maxLines: Int
        get() = textLayout.maxLines
        set(value) {
            if (maxLines != value) {
                updateConfig(newMaxLines = value)
            }
        }

    init {
        accessibilityDelegate = TextLayoutAutoTestsHelper(this, textLayout)
        context.withStyledAttributes(attrs, R.styleable.DesignSbisTitleView) {
            val defaultTextColor = ContextCompat.getColor(context, ru.tensor.sbis.design.R.color.palette_color_white1)
            textLayout.configure {
                paint.apply {
                    color = getColor(
                        R.styleable.DesignSbisTitleView_DesignSbisTitleView_titleColor,
                        defaultTextColor
                    )
                }
            }
        }

        defaultTextSize =
            resources.getDimension(ru.tensor.sbis.design.R.dimen.toolbar_title_without_subtitle_text_size_dp)
        smallerTextSize = resources.getDimension(R.dimen.design_profile_title_text_view_names_smaller_size)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val oldMeasuredWidth = measuredWidth
        val measuredWidth: Int = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(widthMeasureSpec)
        } else {
            measuredWidth
        }

        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        val names = names
        val width: Int
        val height: Int
        when {
            names != null -> {
                if (shouldRedoAfterMeasure || oldMeasuredWidth != measuredWidth) {
                    setupTitleString(names, hiddenNamesCount, measuredWidth)
                    shouldRedoAfterMeasure = false
                    textLayout.configure {
                        layoutWidth = availableWidth
                        maxHeight = getMaxHeight(availableHeight)
                    }
                }
                width = measuredWidth
                height = if (isExactlyHeightSpec) textLayout.height else MeasureSpec.getSize(heightMeasureSpec)
            }

            maxLines > 1 -> {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    if (textLayout.getDesiredWidth(text) <= measuredWidth) defaultTextSize else smallerTextSize
                )
                textLayout.configure {
                    layoutWidth = availableWidth
                    maxHeight = getMaxHeight(availableHeight)
                }
                width = textLayout.width
                height = textLayout.height
            }

            else -> {
                val desiredWidth = textLayout.getDesiredWidth(text)
                val resolvedWidth = resolveSize(desiredWidth, widthMeasureSpec)
                textLayout.configure { layoutWidth = resolvedWidth }
                width = textLayout.width
                height = textLayout.height
            }
        }
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        textLayout.layout(0, 0)
    }

    override fun onDraw(canvas: Canvas) {
        textLayout.draw(canvas)
    }

    fun getEllipsisCount(line: Int): Int = textLayout.getEllipsisCount(line)

    /**
     * Обновление конфигурации textLayout.
     */
    private fun updateConfig(
        newText: CharSequence? = text,
        newMaxLines: Int = maxLines,
        @Px newTextSize: Float = textLayout.textPaint.textSize
    ) {
        textLayout.configure {
            text = newText ?: StringUtils.EMPTY
            paint.textSize = newTextSize
            maxLines = newMaxLines
            padding = TextLayout.TextLayoutPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        }
        safeRequestLayout()
    }

    /**
     * Получение максимальной высоты для textLayout.
     */
    private fun getMaxHeight(availableHeight: Int): Int {
        val maxFitLines = calculateFitLines(availableHeight)
        return if (maxFitLines < maxLines) {
            availableHeight
        } else {
            calculateHeight(maxLines)
        }
    }

    /**
     * @see [TextView.setTextAppearance]
     */
    fun setTextAppearance(context: Context, @StyleRes style: Int) {
        val styleParams = TitleTextViewCanvasStylesProvider.textStyleProvider.getStyleParams(context, style)
        var shouldInvalidate = false
        var shouldLayout = false
        textLayout.configure {
            paint.apply {
                styleParams.textColor?.let {
                    if (color != it) {
                        color = it
                        shouldInvalidate = true
                    }
                }
                styleParams.textSize?.let {
                    if (textSize != it) {
                        textSize = it
                        shouldLayout = true
                    }
                }
                styleParams.typeface?.let {
                    if (typeface != it) {
                        typeface = it
                        shouldLayout = true
                    }
                }
                styleParams.includeFontPad?.let {
                    if (includeFontPad != it) {
                        includeFontPad = it
                        shouldLayout = true
                    }
                }
            }
        }
        if (shouldLayout) {
            safeRequestLayout()
        } else if (shouldInvalidate) {
            invalidate()
        }
    }

    /**
     * @see [Paint.setTypeface]
     */
    fun setTypeface(typeface: Typeface?) {
        textLayout.configure {
            paint.typeface = typeface
        }
        safeRequestLayout()
    }

    /**
     * @see [TextView.setLines]
     */
    fun setLines(lines: Int) {
        updateConfig(newMaxLines = lines)
    }

    /**
     * @see [TextView.setEllipsize]
     */
    fun setEllipsize(ellipsize: TextUtils.TruncateAt?) {
        textLayout.configure {
            this.ellipsize = ellipsize
        }
        safeRequestLayout()
    }

    /**
     * @see [TextView.setTextSize]
     */
    fun setTextSize(unit: Int, size: Float) {
        val newTextSize = TypedValue.applyDimension(unit, size, resources.displayMetrics)
        if (newTextSize != textLayout.textPaint.textSize) {
            updateConfig(newTextSize = newTextSize)
        }
    }

    /**
     * @see [TextView.setTextColor]
     */
    fun setTextColor(@ColorInt color: Int) {
        textColors = ColorStateList.valueOf(color)
        textLayout.colorStateList = null
        textLayout.textPaint.color = color
        invalidate()
    }

    /**
     * Настройка параметра includeFontPad для textLayout.
     */
    fun setIncludeFontPad(isIncluded: Boolean) {
        textLayout.configure {
            paint.apply {
                includeFontPad = isIncluded
            }
        }
    }

    /**
     * Вычисление необходимого количества строк для textLayout исходя из допустимых размеров.
     */
    private fun calculateFitLines(@Px availableHeight: Int): Int = when {
        availableHeight >= getFirstLineHeight() + getLastLineHeight() -> {
            2 + (availableHeight - getFirstLineHeight() - getLastLineHeight()) / getInnerLineHeight()
        }

        availableHeight >= getFirstLineHeight() -> 1
        else -> 0
    }

    /**
     * Вычисление высоты textLayout на основании количества строк.
     */
    private fun calculateHeight(lineCount: Int): Int {
        val fm = getFontMetrics()
        return when {
            lineCount <= 0 -> 0
            lineCount == 1 -> (fm.bottom - fm.top).roundToInt()
            else -> getFirstLineHeight() + getLastLineHeight() + (lineCount - 2) * getInnerLineHeight()
        }
    }

    /**SelfDocumented */
    private fun getFontMetrics() = textLayout.textPaint.fontMetrics

    /**SelfDocumented */
    private fun getFirstLineHeight() = getFontMetrics().run { (descent - top).roundToInt() }

    /**SelfDocumented */
    private fun getLastLineHeight() = getFontMetrics().run { (bottom - ascent).roundToInt() }

    /**SelfDocumented */
    private fun getInnerLineHeight() = getFontMetrics().run { (descent - ascent).roundToInt() }

    /**SelfDocumented */
    fun setSimpleTitle(text: CharSequence?) {
        names = null
        updateConfig(text)
    }

    /**SelfDocumented */
    fun setSingleParticipantName(name: String?) {
        names = null
        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimensionPixelSize(R.dimen.design_profile_toolbar_title_with_subtitle_text_size_dp).toFloat()
        )
        maxLines = SINGLE_DIALOG_MAX_LINES
        updateConfig(name)
    }

    /**
     * Установить список имен для отображения в заголовке в одну строку.
     *
     * @param names список имен
     * @param hiddenNamesCount счетчик скрытых имен
     */
    fun setParticipantsNamesSingleLine(names: List<String?>?, hiddenNamesCount: Int, hasSubTitle: Boolean = false) {
        isExactlyHeightSpec = hasSubTitle
        setParticipantsNames(names, hiddenNamesCount, SINGLE_DIALOG_MAX_LINES)
    }

    /**
     * Установить список имен для отображения в заголовке.
     *
     * @param names список имен
     * @param hiddenNamesCount счетчик скрытых имен
     */
    fun setParticipantsNames(names: List<CharSequence?>?, hiddenNamesCount: Int) {
        isExactlyHeightSpec = true
        setParticipantsNames(names, hiddenNamesCount, GROUP_DIALOG_MAX_LINES)
    }

    /**SelfDocumented */
    private fun setParticipantsNames(names: List<CharSequence?>?, hiddenNamesCount: Int, maxLines: Int) {
        this.names = names
        this.hiddenNamesCount = hiddenNamesCount
        this.maxLines = maxLines
        if (names != null) {
            setupTitleString(names, hiddenNamesCount, measuredWidth)
        } else {
            updateConfig(text)
        }
        shouldRedoAfterMeasure = true
    }

    /**SelfDocumented */
    private fun setupTitleString(names: List<CharSequence?>, hiddenNamesCount: Int, availableWidth: Int) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize)
        val namesCount = names.size
        val textPaint = textLayout.textPaint
        val defaultCounterText = resources.getString(counterTextRes, 0)
        var defaultSize = true
        var firstLine = true
        var nextIterationTitleText: String?
        val titleMetaData = TitleMetaData(hiddenNamesCount)
        var firstNameInLine = 0
        for (nameIndex in 0 until namesCount) {
            nextIterationTitleText = TextUtils.join(DELIMITER, names.subList(firstNameInLine, nameIndex + 1))
            if (textPaint.measureText(nextIterationTitleText) > availableWidth) {
                if (firstLine && maxLines != 1) {
                    if (defaultSize) {
                        setTextSize(TypedValue.COMPLEX_UNIT_PX, smallerTextSize)
                        defaultSize = false
                        if (textPaint.measureText(nextIterationTitleText) > availableWidth) {
                            firstNameInLine = nameIndex
                            firstLine = false
                        }
                    } else {
                        firstNameInLine = nameIndex
                        firstLine = false
                    }
                } else {
                    if (supportNamesFullWidth || nameIndex == 0) {
                        // в данном режиме отображаем имена, которые поместились хотя бы частично
                        // поэтому последнее имя (которое отображается не полностью) считаем видимым
                        titleMetaData.firstHiddenNameIndex = nameIndex + 1
                    }
                    titleMetaData.counterValue = namesCount - titleMetaData.firstHiddenNameIndex
                    if (titleMetaData.counterValue > 0) {
                        val widthWithoutDefaultCounter = availableWidth - textPaint.measureText(defaultCounterText)
                        updateMetaDataToFitCounter(
                            names,
                            textPaint,
                            titleMetaData,
                            firstNameInLine,
                            widthWithoutDefaultCounter
                        )
                    }
                    break
                }
            }
            titleMetaData.firstHiddenNameIndex = nameIndex + 1
        }
        if (titleMetaData.firstHiddenNameIndex == namesCount && titleMetaData.counterValue > 0) {
            updateMetaDataToFitCounter(
                names,
                textPaint,
                titleMetaData,
                firstNameInLine,
                availableWidth - textPaint.measureText(defaultCounterText)
            )
        }
        var titleText = TextUtils.join(DELIMITER, names.subList(0, titleMetaData.firstHiddenNameIndex))
        if (titleMetaData.counterValue > 0) {
            if (titleMetaData.lastNameHiddenCharactersCount > 0) {
                titleText = if (supportNamesFullWidth) {
                    // последнее отображаемое имя не помещается целиком, заменяем последние символы многоточием
                    val length =
                        titleText.length - titleMetaData.lastNameHiddenCharactersCount - 1
                    titleText.substring(0, length) + Typography.ellipsis
                } else {
                    titleText.substring(0, titleText.length - titleMetaData.lastNameHiddenCharactersCount)
                }
            }
            titleText += resources.getString(counterTextRes, titleMetaData.counterValue)
        }
        updateConfig(titleText)
    }

    /**
     * Задаёт размер шрифта по умолчанию.
     */
    fun setDefaultTextSize(@Px size: Float) {
        if (defaultTextSize != size) {
            defaultTextSize = size
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

    /**
     * Задаёт размер шрифта, используемый при указании списка отображаемых элементов, если они не
     * умещаются на одной строке.
     *
     * @see .setParticipantsNames
     */
    fun setSmallerNamesTextSize(@Px size: Float) {
        if (smallerTextSize != size) {
            smallerTextSize = size
            requestLayout()
        }
    }

    /**
     * Задает ресурс для отображения счетчика имен.
     *
     * @see .setupTitleString
     * @param counterTextRes ресурс
     */
    fun setCounterTextRes(@StringRes counterTextRes: Int) {
        if (this.counterTextRes != counterTextRes) {
            this.counterTextRes = counterTextRes
            requestLayout()
        }
    }

    /**
     * Задает режим отображения списка имен в полную ширину строки.
     *
     * @param supportNamesFullWidth
     * - true - если имя не помещается полностью,
     * отображаем на всю доступную ширину и сокращаем многоточием,
     * в счетчике такое имя не учитываем.
     * - false - отображем только те имена, которые помещаются целиком.
     */
    fun setSupportNamesFullWidth(supportNamesFullWidth: Boolean) {
        if (this.supportNamesFullWidth != supportNamesFullWidth) {
            this.supportNamesFullWidth = supportNamesFullWidth
            requestLayout()
        }
    }

    private fun updateMetaDataToFitCounter(
        names: List<CharSequence?>,
        textPaint: TextPaint,
        titleMetaData: TitleMetaData,
        firstNameInLine: Int,
        width: Float
    ) {
        var counterText = titleMetaData.counterValue.toString()
        var counterWidth = textPaint.measureText(counterText)
        var freeWidth = width - textPaint.measureText(
            TextUtils.join(
                DELIMITER,
                names.subList(firstNameInLine, titleMetaData.firstHiddenNameIndex)
            )
        )
        while (isFitted(titleMetaData, firstNameInLine, counterWidth - freeWidth, names, textPaint)) {
            titleMetaData.firstHiddenNameIndex--
            titleMetaData.counterValue++
            titleMetaData.lastNameHiddenCharactersCount = 0
            freeWidth = width - textPaint.measureText(
                TextUtils.join(
                    DELIMITER,
                    names.subList(firstNameInLine, titleMetaData.firstHiddenNameIndex)
                )
            )
            counterText = titleMetaData.counterValue.toString()
            counterWidth = textPaint.measureText(counterText)
        }
    }

    private fun isFitted(
        titleMetaData: TitleMetaData,
        firstNameInLine: Int,
        width: Float,
        names: List<CharSequence?>,
        textPaint: TextPaint
    ): Boolean {
        return if (width > 0 && titleMetaData.firstHiddenNameIndex > 0) {
            val lastVisibleName = names[titleMetaData.firstHiddenNameIndex - 1]!!
            titleMetaData.lastNameHiddenCharactersCount =
                getNameHiddenCharactersCount(lastVisibleName, textPaint, width)
            firstNameInLine < titleMetaData.firstHiddenNameIndex && (
                titleMetaData.lastNameHiddenCharactersCount == 0 ||
                    lastVisibleName.length - titleMetaData.lastNameHiddenCharactersCount < MIN_VISIBLE_CHARS_COUNT
                )
        } else {
            false
        }
    }

    private fun getNameHiddenCharactersCount(name: CharSequence, textPaint: TextPaint, neededWidth: Float): Int {
        var newName = name
        val originalWidth = textPaint.measureText(newName.toString())
        var removedCharsCount = 0
        while (newName.isNotEmpty()) {
            if (originalWidth - textPaint.measureText(newName.toString()) > neededWidth) {
                return removedCharsCount
            }
            newName = newName.substring(0, newName.length - 1)
            removedCharsCount++
        }
        return 0
    }

    private class TitleMetaData(var counterValue: Int) {
        var firstHiddenNameIndex = 0
        var lastNameHiddenCharactersCount = 0
    }

    companion object {
        private const val SINGLE_DIALOG_MAX_LINES = 1
        private const val GROUP_DIALOG_MAX_LINES = 2
        private const val MIN_VISIBLE_CHARS_COUNT = 4
        private const val DELIMITER = ", "
    }
}

private object TitleTextViewCanvasStylesProvider : CanvasStylesProvider()