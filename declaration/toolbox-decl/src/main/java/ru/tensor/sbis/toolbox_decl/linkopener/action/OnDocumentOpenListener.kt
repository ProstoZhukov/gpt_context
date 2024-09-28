package ru.tensor.sbis.toolbox_decl.linkopener.action

import android.content.Context
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview

/**
 * Определение интерфейса для обратного вызова, который будет вызываться при открытии ссылки документа.
 *
 * @author as.chadov
 */
interface OnDocumentOpenListener {
    /**
     * Вызывается при открытии документа.
     *
     * @param data Данные о ссылке.
     * @param context Опциональный контекст приложения.
     */
    fun onOpen(data: LinkPreview, context: Context?)
}