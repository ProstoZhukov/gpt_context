package ru.tensor.sbis.toolbox_decl.linkopener.action

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview

/**
 * Определение интерфейса для обратного вызова, который будет вызываться при открытии ссылки документа и
 * обработке его через роутер приложения.
 *
 * @author as.chadov
 */
interface OnDocumentIntentListener {
    /**
     * Вызывается при открытии документа.
     *
     * @param data Данные о ссылке.
     * @param context Опциональный контекст приложения.
     * @return намерение для открытия экрана документа через роутер приложения.
     */
    fun onOpenIntent(data: LinkPreview, context: Context?): Intent?
}