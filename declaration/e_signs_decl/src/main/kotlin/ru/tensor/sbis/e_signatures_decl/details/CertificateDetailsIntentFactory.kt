package ru.tensor.sbis.e_signatures_decl.details

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика интентов для запуска активити деталей сертификата.
 *
 * @author vv.malyhin
 */
@Deprecated("Переход на ru.tensor.sbis.e_signatures_decl.card")
interface CertificateDetailsIntentFactory : Feature {

    /**
     * Создать интент активити деталей сертификата.
     * @param context Контекст.
     * @param config Конфигурация для запуска.
     */
    fun newIntent(
        context: Context,
        config: CertificateDetailsScreenConfig
    ): Intent
}