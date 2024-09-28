package ru.tensor.sbis.communicator.core.views.conversation_views.base

import android.graphics.Canvas
import android.text.Layout
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_dialogNameIconPaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_dialogTitlePaint
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import kotlin.math.roundToInt

/**
 * Layout для отображения строки с иконкой и названием диалога.
 *
 * @property layoutWidth ширина разметки, доступная для отображения иконки и названия диалога.
 * @param documentIconText текст иконки документа.
 * @param dialogTitleText текст названия диалога.
 * @param dialogTitleHighlights список позиций выделения текста названия диалога при поиске.
 *
 * @author da.zhukov
 */
internal class ConversationDialogTitleLayout(
    private val layoutWidth: Int,
    dialogTitleText: CharSequence,
    documentIconText: String?,
    dialogTitleHighlights: TextHighlights?
) {
    /** Разметка иконки типа документа */
    private var documentIconLayout: TextLayout? = null

    // Подключить вместе с реализацией setData
    private val lazyDocumentIconLayout by lazy(LazyThreadSafetyMode.NONE) {
        TextLayout {
            isVisibleWhenBlank = false
            text = documentIconText ?: ""
            paint = theme_dialogNameIconPaint
            alignment = Layout.Alignment.ALIGN_CENTER
            padding = TextLayout.TextLayoutPadding(end = CommunicatorTheme.offset2XS)
        }
    }

    /** Разметка названия диалога */
    var dialogTitleLayout: TextLayout? = null
        private set

    // Подключить вместе с реализацией setData
    private val lazyDialogTitleLayout by lazy(LazyThreadSafetyMode.NONE) {
        TextLayout {
            isVisibleWhenBlank = false
            text = dialogTitleText
            paint = theme_dialogTitlePaint
            layoutWidth = this@ConversationDialogTitleLayout.layoutWidth - (documentIconLayout?.width ?: 0)
            highlights = dialogTitleHighlights
        }
    }

    /**
     * Высота разметки строки иконки и названия диалога.
     */
    val height: Int
        get() = dialogTitleLayout?.height ?: 0

    // Подключить вместе с реализацией setData
    init {
        if (!documentIconText.isNullOrBlank()) documentIconLayout = lazyDocumentIconLayout
        if (dialogTitleText.isNotBlank()) dialogTitleLayout = lazyDialogTitleLayout
    }

    /**
     * Разместить всю разметку строки названия диалога [ConversationDialogTitleLayout] ячейки диалогов
     * по левой [left] и верхней [top] позициям, определяемой родителем.
     */
    fun layout(left: Int, top: Int) {
        documentIconLayout?.layout(
            left,
            top + (((dialogTitleLayout?.height ?: 0) - documentIconLayout!!.height) / 2f).roundToInt()
        )
        dialogTitleLayout?.layout(
            documentIconLayout?.right ?: left,
            top
        )
    }

    /**
     * Нарисовать разметку строки названия диалога [ConversationDialogTitleLayout] ячейки диалогов.
     *
     * @param canvas canvas родительской view, в которой будет нарисована разметка.
     */
    fun draw(canvas: Canvas) {
        documentIconLayout?.draw(canvas)
        dialogTitleLayout?.draw(canvas)
    }
}