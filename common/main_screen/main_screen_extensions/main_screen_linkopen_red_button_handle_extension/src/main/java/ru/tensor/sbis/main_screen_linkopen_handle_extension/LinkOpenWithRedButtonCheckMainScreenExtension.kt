package ru.tensor.sbis.main_screen_linkopen_handle_extension

import android.content.Context
import android.content.Intent
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.withContext
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.intent.IntentHandleExtension
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerPendingLinkFeature
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController

/**
 * Расширение главного экрана для обработки открытия ссылок через [OpenLinkController].
 * Аналог [LinkOpenWithRedButtonCheckMainScreenExtension], но с проверкой необходимости отображения
 * заглушки компонентом красной кнопки.
 * Также открывает ссылку на документ, если пытались открыть на экране авторизации.
 *
 * @property context контекст для чтения sharedPreferences
 * @property openLinkController контроллер открытия ссылок
 * @property pendingLinkFeature фича открытия ссылок, пришедших на этапе авторизации
 * @property isLockedUi заблокирован ли UI, например, когда включена "Красная кнопка" `RedButtonFeature`
 * @property disposables если используется реактивный подход
 * @property scope если используются корутины
 *
 * @author as.chadov
 */
class LinkOpenWithRedButtonCheckMainScreenExtension(
    private val context: Context,
    private val openLinkController: OpenLinkController,
    private val pendingLinkFeature: LinkOpenerPendingLinkFeature,
    private val isLockedUi: Single<Boolean> = Single.just(false),
    @Deprecated("Используйте scope", ReplaceWith("scope"))
    private val disposables: CompositeDisposable = CompositeDisposable(),
    private val scope: CoroutineScope? = null
) : IntentHandleExtension<LinkOpenWithRedButtonCheckMainScreenExtension.Key>,
    Disposable by disposables {

    override val key: Key = Key

    override fun resolveIntent(intent: Intent): IntentHandleExtension.ResolutionResult? {
        processPendingDeepLinkIntent()
        return intent
            .takeIf { it.action == Intent.ACTION_VIEW && it.data != null }
            ?.data
            ?.let {
                val intentCopy = Intent(intent)
                intent.data = null
                IntentHandleExtension.ResolutionResult.SideEffect {
                    ifUiIsNotLocked {
                        openLinkController.processAndForget(intentCopy, true)
                    }
                }
            }
    }

    object Key : IntentHandleExtension.ExtensionKey

    private fun processPendingDeepLinkIntent() {
        // Открытие ссылки, которая пришла на экране авторизации, но не была обработана,
        // т.к. требовалась авторизация
        pendingLinkFeature.getLink(context)?.let {
            ifUiIsNotLocked {
                openLinkController.processAndForget(it, true)
            }
        }
    }

    private fun ifUiIsNotLocked(action: () -> Unit) {
        if (scope != null) {
            scope.launch {
                isLockedUi.toObservable().asFlow().collect { locked ->
                    withContext(Dispatchers.Main) {
                        if (!locked) action()
                    }
                }
            }
        } else {
            disposables.add(isLockedUi.subscribe { locked -> if (!locked) action() })
        }
    }
}

/**
 * Метод для удобного получения расширения [LinkOpenWithRedButtonCheckMainScreenExtension].
 */
fun ConfigurableMainScreen.linkOpenWithRedButtonCheckHandleExtension(): LinkOpenWithRedButtonCheckMainScreenExtension? {
    return getIntentHandleExtension(LinkOpenWithRedButtonCheckMainScreenExtension.Key)
}