package ru.tensor.sbis.main_screen_linkopen_handle_extension

import android.content.Intent
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.intent.IntentHandleExtension

/**
 * Расширение главного экрана для обработки открытия ссылок через [OpenLinkController].
 *
 * @property openLinkController
 *
 * @author k.martyshenko
 */
class LinkOpenMainScreenExtension(
    private val openLinkController: OpenLinkController
) : IntentHandleExtension<LinkOpenMainScreenExtension.Key> {

    override val key: Key = Key

    override fun resolveIntent(intent: Intent): IntentHandleExtension.ResolutionResult? {
        return intent
            .takeIf { it.action == Intent.ACTION_VIEW && it.data != null }
            ?.data
            ?.let {
                val intentCopy = Intent(intent)
                intent.data = null
                IntentHandleExtension.ResolutionResult.SideEffect {
                    this@LinkOpenMainScreenExtension.openLinkController.processAndForget(intentCopy)
                }
            }
    }

    object Key : IntentHandleExtension.ExtensionKey

}

/**
 * Метод для удобного получения расширения [LinkOpenMainScreenExtension].
 */
fun ConfigurableMainScreen.linkOpenHandleExtension(): LinkOpenMainScreenExtension? {
    return getIntentHandleExtension(LinkOpenMainScreenExtension.Key)
}