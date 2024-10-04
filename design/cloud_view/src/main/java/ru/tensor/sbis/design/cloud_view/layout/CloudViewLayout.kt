package ru.tensor.sbis.design.cloud_view.layout

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.CloudViewStylesProvider.paddingStyleProvider
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.cloud_view.content.MessageBlockView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudDateTimeView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudStatusView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudTitleView
import ru.tensor.sbis.design.cloud_view.model.AttachmentCloudContent
import ru.tensor.sbis.design.cloud_view.model.CloudViewData
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.profile.person.PersonView
import androidx.core.view.isVisible
import ru.tensor.sbis.design.cloud_view.content.utils.MessagesViewPool
import ru.tensor.sbis.design.cloud_view.model.EmptyCloudContent
import ru.tensor.sbis.design.cloud_view.model.LinkCloudContent
import java.util.UUID

/**
 * Базовая реализация разметки контейнера ячейки-облака.
 * Содержит базовые параметры разметки, а также механики для работы с дополнительным View контентом,
 * который был передан снаружи в качестве дочерних элементов [CloudView].
 *
 * [measureAdditionalContent] - метод для измерения суммарной высоты дополнительного контента.
 * [additionalContentList] - список дополнительного View контента, который необходимо разместить внутри облачка.
 *
 * @property parent родительский [ViewGroup] разметки.
 * @param styleRes стиль разметки.
 *
 * @author vv.chekurda
 */
internal abstract class CloudViewLayout(
    protected val parent: ViewGroup,
    @StyleRes styleRes: Int = R.style.IncomeCloudViewCellStyle
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

    /**
     * Количество внутренних дочерних элементов [CloudView].
     */
    private var cloudChildrenCount = 0

    /**
     * Наибольшая ширина дополнительного контента.
     */
    protected var additionalContentMaxWidth = 0

    /**
     * Получить список view дополнительного контента.
     */
    protected val additionalContentList: List<View>
        get() {
            val childrenCountDiff = parent.childCount - cloudChildrenCount
            return if (childrenCountDiff > 0) {
                children.toList().takeLast(childrenCountDiff)
            } else emptyList()
        }

    /**
     * Признак наличия вложений в контенте.
     */
    protected var hasAttachments: Boolean = false
        private set
    /**
     * Признак наличия вложения в качестве первого элемента контента.
     */
    protected var hasFirstAttachment: Boolean = false
        private set
    protected val firstAttachmentTopSpacing = resources.getDimensionPixelSize(
        R.dimen.cloud_view_attachments_collage_margin_top
    )

    protected var leftPos = 0
    protected var topPos = 0
    protected var rightPos = 0
    protected var bottomPos = 0

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
     * Контент сообщения.
     */
    val contentView: MessageBlockView = MessageBlockView(context)

    /**
     * Фон облачка сообщения.
     */
    val backgroundView: View = View(context)

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
     * Установить [MessagesViewPool] для повторного использования контента [CloudView].
     */
    abstract fun setViewPool(viewPool: MessagesViewPool)

    /**
     * Добавить [view] в родительский контейнер.
     */
    protected fun addView(view: View, index: Int = -1) {
        parent.addView(view, index)
        cloudChildrenCount++
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
     * Измерить дополнительный контент [additionalContentList] по заданным спецификациям.
     */
    protected fun measureAdditionalContent(widthMeasureSpec: Int, heightMeasureSpec: Int): Int {
        var sumHeight = 0
        additionalContentList.forEach {
            if (!it.isVisible) return@forEach
            val width = minOf(it.layoutParams.width, View.MeasureSpec.getSize(widthMeasureSpec))
            val widthSpec =
                if (width > 0) makeExactlySpec(width)
                else widthMeasureSpec
            val heightSpec =
                if (it.layoutParams.height > 0) makeExactlySpec(it.layoutParams.height)
                else heightMeasureSpec
            it.measure(widthSpec, heightSpec)
            additionalContentMaxWidth = maxOf(additionalContentMaxWidth, it.measuredWidth)
            sumHeight += it.measuredHeight
        }
        return sumHeight
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
     * Задаёт обработчик нажатий на сообщение.
     */
    fun setOnMessageClickListener(listener: View.OnClickListener?) {
        timeView.setOnClickListener(listener)
        dateView.setOnClickListener(listener)
        backgroundView.setOnClickListener(listener)
        // На контент тоже вешается кликлистенер, так как у RichText могут использоваться свои обработчики
        contentView.setOnMessageClickListener(listener)
    }

    /**
     * Дополнительная проверка контента вложений для специфичных настроек отображения.
     */
    open fun checkAttachments(data: CloudViewData) {
        if (!data.isAuthorBlocked) {
            val content = data.content.filter { it !is EmptyCloudContent && it !is LinkCloudContent }
            hasAttachments = content.any { it is AttachmentCloudContent }
            hasFirstAttachment = hasAttachments && data.text.isNullOrBlank() &&
                content[data.rootElements.first()] is AttachmentCloudContent
        } else {
            hasAttachments = false
            hasFirstAttachment = false
        }
    }

    /**
     * Установить идентификатор сообщения.
     */
    open fun setMessageUuid(uuid: UUID?) = Unit

    /**
     * Установить цвета фона облачка.
     */
    fun setCloudBackgroundColor(color: Int) {
        val drawable = backgroundView.background
        if (drawable is GradientDrawable) {
            drawable.color = ColorStateList.valueOf(color)
        } else {
            backgroundView.setBackgroundColor(color)
        }
    }
}