package ru.tensor.sbis.crypto_operation.decl

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * @author as.gromilov
 *
 * Интерфейс для вызова подписания мобильным сертификатом.
 */
interface DocumentSignProvider : Feature {
    /**
     * Создание интента для открытия экрана подписи документа используя мобильный сертификат
     *
     * @param context     - ссылка на контекст
     * @param isReject    - флаг, true - отмена операции подписания
     *                      false - инициируктся подписание документа
     * @param requestUuid - уникальный идентификатор документа
     * @param thumbprint  - отпечаток сертификата для подписания
     *
     * @return [Intent]   - интент на создание новой активити. Может быть null
     */
    fun getDocumentSignIntent(
        context: Context,
        isReject: Boolean,
        requestUuid: UUID,
        thumbprint: String? = null
    ): Intent?
}