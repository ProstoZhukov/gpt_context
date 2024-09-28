package ru.tensor.sbis.communicator.core.views.conversation_views.base

import android.content.Context
import android.graphics.Canvas
import android.text.Layout.Alignment.ALIGN_CENTER
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_chatDocumentIconPaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_documentIconPaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_documentNamePaint
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayout.TextLayoutPadding
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import kotlin.math.roundToInt

/**
 * Layout для отображения строки с иконкой и названием документа по диалогу.
 *
 * @property layoutWidth ширина разметки, доступная для отображения иконки и названия документа.
 * @param documentIconText текст иконки документа.
 * @param documentName текст названия документа.
 * @param documentHighlights список позиций выделения текста названия документа при поиске.
 *
 * @author vv.chekurda
 */
internal class ConversationDocumentLayout(
    private val layoutWidth: Int,
    documentIconText: String,
    documentName: CharSequence,
    documentHighlights: TextHighlights?,
    isChat: Boolean
) {
    /** Разметка иконки типа документа */
    val documentIconLayout = TextLayout(
        if (isChat) theme_chatDocumentIconPaint else theme_documentIconPaint
    ) {
        text = documentIconText
        alignment = ALIGN_CENTER
        padding = TextLayoutPadding(end = CommunicatorTheme.offset2XS)
    }

    /** Разметка названия документа. */
    val documentNameLayout = TextLayout(theme_documentNamePaint) {
        text = documentName
        layoutWidth = this@ConversationDocumentLayout.layoutWidth - documentIconLayout.width
        maxLines = 1
        highlights = documentHighlights
    }

    /** Верхний padding. */
    private val documentTopPadding = CommunicatorTheme.offset3XS

    /**
     * Высота разметки строки иконки и названия документа.
     */
    val height: Int
        get() = documentTopPadding + documentNameLayout.height

    /**
     * Разместить всю разметку строки документа [ConversationDocumentLayout] ячейки диалогов
     * по левой [left] и верхней [top] позициям, определяемой родителем.
     */
    fun layout(left: Int, top: Int) {
        val topPos = documentTopPadding + top
        documentIconLayout.layout(
            left,
            topPos + ((documentNameLayout.height - documentIconLayout.height) / 2f).roundToInt()
        )
        documentNameLayout.layout(
            documentIconLayout.right,
            topPos
        )
    }

    /**
     * Нарисовать разметку строки документа [ConversationDocumentLayout] ячейки диалогов.
     *
     * @param canvas canvas родительской view, в которой будет нарисована разметка.
     */
    fun draw(canvas: Canvas) {
        documentIconLayout.draw(canvas)
        documentNameLayout.draw(canvas)
    }
}