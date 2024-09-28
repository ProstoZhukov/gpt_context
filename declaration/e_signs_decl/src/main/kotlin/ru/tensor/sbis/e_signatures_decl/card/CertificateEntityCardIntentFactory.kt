package ru.tensor.sbis.e_signatures_decl.card

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика интентов для запуска карточки сертификата/заявки.
 *
 * @author vv.malyhin
 */
interface CertificateEntityCardIntentFactory : Feature {

    /**
     * Создать интент активити деталей сертификата/заявки.
     * @param context Контекст.
     * @param config Конфигурация для запуска.
     */
    fun newIntent(
        context: Context,
        config: CertificateEntityCardConfig,
    ): Intent
}