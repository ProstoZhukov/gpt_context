package ru.tensor.sbis.e_signatures_decl.web_submission

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика интентов для запуска активити заявки на электронную подпись.
 *
 * @author vv.malyhin
 */
interface CertificateWebSubmissionIntentFactory : Feature {

    /**
     * Создать интент активити заявки на электронную подпись.
     * @param context Контекст.
     * @param url Ссылка на заявку.
     */
    fun newIntent(context: Context, url: String): Intent
}