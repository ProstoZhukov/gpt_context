package ru.tensor.sbis.link_opener.domain.router.producer

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.GET_RESOLVED_FILTER
import android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
import android.content.pm.ResolveInfo
import android.net.Uri
import ru.tensor.sbis.link_opener.domain.parser.LinkUriMapper
import ru.tensor.sbis.link_opener.domain.handler.LinkOpenEventHandlerImpl
import ru.tensor.sbis.link_opener.domain.router.LinkOpenHandlerFactory
import ru.tensor.sbis.link_opener.domain.utils.OnDocumentOpenListenerCreator
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenEventHandler
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandlerPriority
import javax.inject.Inject


/**
 * Производитель зарегистрированных в других МП семейства Сбис обработчиков.
 * Ссылка будет открыта на карточке в одном из других установленных на устройстве МП семейства Сбис посредством
 * перенаправления вызова интентом с прикладным [Uri].
 *
 * Пример конфигурации манифеста другого МП Сбис для обработки такого Uri:
 * ```
 * <activity
 * ...
 * android:exported="true"
 * ...
 * <intent-filter>
 *     <action android:name="android.intent.action.VIEW"/>
 *     <category android:name="android.intent.category.DEFAULT"/>
 *     <data
 *          android:scheme="sabylink"
 *          android:host="person"/>     // DocType.PERSON
 * </intent-filter>
 * ```
 *
 * @author as.chadov
 */
internal class OtherInAppLinkOpenHandlerProducer @Inject constructor(
    private val context: Context,
    private val mapper: LinkUriMapper
) : LinkOpenHandlerFactory.LinkOpenHandlerProducer {

    override fun produce(preview: LinkPreview): LinkOpenEventHandler? {
        val intent = createIntent(preview)
        val info = findSupportingComponent(intent)
        return if (info != null) {
            LinkOpenEventHandlerImpl(
                types = mapper.unmarshalDocTypes(info.filter),
                subtypes = mapper.unmarshalDocSubtypes(info.filter),
                actionRouter = OnDocumentOpenListenerCreator.createIntent { _, _ -> intent },
                // По-умолчанию считаем что приоритет нормальный. Мусорные обработчики не должны регистрироваться приложениями.
                priority = LinkOpenHandlerPriority.NORMAL
            )
        } else null
    }

    private fun createIntent(preview: LinkPreview): Intent =
        Intent(Intent.ACTION_VIEW).apply {
            data = mapper.marshal(preview)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            )
        }

    private fun findSupportingComponent(intent: Intent): ResolveInfo? {
        val info = context.packageManager.queryIntentActivities(
            intent, MATCH_DEFAULT_ONLY or GET_RESOLVED_FILTER
        ).firstOrNull {
            context.packageName != it.activityInfo.packageName
        } ?: return null
        return info.takeIf { it.activityInfo.exported }
    }
}