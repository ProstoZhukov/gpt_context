package ru.tensor.sbis.master.certificate.decl

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * @author as.gromilov
 *
 * Публичный интерфейс для создания приватной части ключа подписи, а так же установки сертификата в
 * контейнер.
 */
interface MasterCertificateProvider : Feature {

    /**
     * Создание интента для открытия экранов генерации ключа сертификата
     *
     * @param context     - ссылка на контекст
     * @param requestUuid - уникальный идентификатор заявки
     * @return [Intent]   - интент на создание новой активити. Может быть null
     * */
    fun getGenerateIntent(context: Context, requestUuid : UUID): Intent?

    /**
     * Создание интента для открытия экранов установки сертификата в контейнер
     *
     * @param context     - ссылка на контекст
     * @param requestUuid - уникальный идентификатор заявки
     * @return [Intent]   - интент на создание новой активити. Может быть null
     * */
    fun getInstallIntent(context: Context, requestUuid : UUID): Intent?
}