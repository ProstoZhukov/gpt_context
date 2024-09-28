package ru.tensor.sbis.link_opener.domain

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.link_opener.domain.auth.PendingDeepLinkPrefs
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerPendingLinkFeature
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Сохранение интента на открытие ссылки после авторизации.
 */
@Singleton
internal class LinkOpenerPendingLinkFeatureImpl @Inject constructor() :
    LinkOpenerPendingLinkFeature {

    override fun saveLink(context: Context, intent: Intent) =
        PendingDeepLinkPrefs.saveIntent(context, intent)

    override fun getLink(context: Context) =
        PendingDeepLinkPrefs.getIntentIfAny(context)
}