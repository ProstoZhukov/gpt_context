package ru.tensor.sbis.design.cloud_view.video.layout

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.cloud_view.content.utils.MessageBlockTextHolder
import ru.tensor.sbis.design.cloud_view.layout.children.CloudDateTimeView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudStatusView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudTitleView
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.video_message_view.R
import ru.tensor.sbis.design.cloud_view.video.VideoMessageCloudViewStylesProvider.paddingStyleProvider
import ru.tensor.sbis.design.video_message_view.message.VideoMessageView
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.cloud_view.R as RCloudView

/**
 * Базовая реализация разметки контейнера видеосообщения.
 * Содержит базовые параметры разметки, а также механики для работы с дополнительным View контентом,
 * который был передан снаружи в качестве дочерних элементов [VideoMessageCloudView].
 *
 * @property parent родительский [ViewGroup] разметки.
 * @param styleRes стиль разметки.
 *
 * @author vv.chekurda
 */
abstract class VideoMessageCloudViewLayout(
    protected val parent: ViewGroup,
    @StyleRes styleRes: Int = RCloudView.style.IncomeCloudViewCellStyle
) {

    protected val context: Context = parent.context
    protected val resources: Resources = context.resources
    protected val children: Sequence<View>
        get() = parent.children

    /**
     * Горизонтальный padding для облачка, внутри которого размещается заголовок и контент.
     */
    var cloudHorizontalPadding = 0

    /**
     * Вертикальный padding для облачка, внутри которого размещается заголовок и контент.
     */
    protected var cloudVerticalPadding = 0

    protected val titleBottomPadding = context.getDimenPx(RDesign.attr.offset_3xs)

    protected var leftPos = 0
    protected var topPos = 0
    protected var rightPos = 0
    private var bottomPos = 0

    private var width = 0
    private var height = 0

    /**
     * Измеренная ширина.
     */
    val measuredWidth: Int
        get() = width

    /**
     * Измеренная высота.
     */
    val measuredHeight: Int
        get() = height

    /**
     * Фото автора сообщения.
     */
    open val personView: PersonView? = null

    /**
     * Заголовок сообщения: автор + получатели.
     */
    abstract val titleView: CloudTitleView

    /**
     * Статус сообщения.
     */
    abstract val statusView: CloudStatusView

    /**
     * Время сообщения.
     */
    abstract val timeView: CloudDateTimeView

    /**
     * Дата сообщения.
     */
    abstract val dateView: CloudDateTimeView

    /**
     * Фон цитированного текста.
     */
    val backgroundView: View = View(context)

    /**
     * Холдер для отображения цитированного текста.
     */
    val textHolder: MessageBlockTextHolder
        get() = lateTextHolder

    /**
     * Разметка холдера для отображения цитированного текста.
     */
    val messageLayout: View by lazy { textHolder.getTextLayoutView(context) }

    /**
     *  Маркер цитирования.
     */
    val quoteMarkerView = object : View(context) {

        private val answerLayout = TextLayout {
            paint.color = context.getThemeColorInt(RDesign.attr.unaccentedTextColor)
            paint.textSize = context.getDimen(RDesign.attr.fontSize_3xs_scaleOff)
            padding = TextLayout.TextLayoutPadding(start = context.getDimenPx(RDesign.attr.offset_2xs))
        }

        private val quoteIcon = AppCompatResources.getDrawable(context, R.drawable.video_message_ic_quote)!!

        init {
            answerLayout.configure { text = resources.getString(R.string.design_video_player_view_answer) }
            isVisible = false
            updatePadding(right = context.getDimenPx(RDesign.attr.offset_s))
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension(
                measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
                measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
            )
        }

        override fun getSuggestedMinimumWidth(): Int =
            paddingStart + paddingEnd + answerLayout.width + quoteIcon.intrinsicWidth

        override fun getSuggestedMinimumHeight(): Int =
            paddingTop + paddingBottom + answerLayout.height

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            val quoteIconTop = paddingTop + dp(5)
            quoteIcon.setBounds(
                paddingStart,
                quoteIconTop,
                paddingStart + quoteIcon.intrinsicWidth,
                quoteIconTop + quoteIcon.intrinsicHeight
            )
            answerLayout.layout(quoteIcon.bounds.right, paddingTop)
        }

        override fun onDraw(canvas: Canvas) {
            quoteIcon.draw(canvas)
            answerLayout.draw(canvas)
        }
    }

    private lateinit var lateTextHolder: MessageBlockTextHolder

    /**
     * Вью видеосообщения.
     */
    var videoMessageView = VideoMessageView(context)

    init {
        context.withStyledAttributes(resourceId = styleRes, attrs = intArrayOf(android.R.attr.background)) {
            backgroundView.background = getDrawable(0)
        }
        paddingStyleProvider.getStyleParams(context, styleRes).run {
            cloudHorizontalPadding = paddingStart
            cloudVerticalPadding = paddingTop
        }
    }

    /**
     * Добавить [view] в родительский контейнер.
     */
    protected fun addView(view: View, index: Int = -1) {
        parent.addView(view, index)
    }

    /**
     * Добавить перечень [views] в родительский контейнер.
     */
    protected fun addViews(vararg views: View) {
        views.forEach(::addView)
    }

    /**
     * Измерить ширину и высоту разметки по заданным спецификациям.
     */
    abstract fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int)

    /**
     * Установить измеренную ширину [width] и высоту [height] разметки.
     */
    protected fun setMeasuredDimension(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    /**
     * Разместить разметку по позициям.
     */
    open fun layout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        leftPos = left
        topPos = top
        rightPos = right
        bottomPos = bottom
    }

    /**
     * Устанавливает холдер для отображения цитированного текста.
     */
    fun setTextHolder(textHolder: MessageBlockTextHolder) {
        if (this::lateTextHolder.isInitialized) return
        lateTextHolder = textHolder
        textHolder.getTextView(context).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                id = RCloudView.id.cloud_view_message_block_text_id
                setTextAppearance(RDesign.style.MessagesListItem_RegularText)
            } else {
                @Suppress("DEPRECATION")
                setTextAppearance(context, RDesign.style.MessagesListItem_RegularText)
            }
            includeFontPadding = false
            typeface = TypefaceManager.getRobotoRegularFont(context)
        }
        textHolder.getTextLayoutView(context).apply {
            id = RCloudView.id.cloud_view_message_block_rich_view_layout_id
            addView(this)
        }
    }

    /**
     * Задаёт обработчик нажатий на сообщение.
     */
    fun setOnMessageClickListener(clickListener: View.OnClickListener) {
        children.forEach { it.setOnClickListener(clickListener) }
        // На контент тоже вешается кликлистенер, так как у RichText могут использоваться свои обработчики
        videoMessageView.setOnMessageClickListener(clickListener)
    }
}