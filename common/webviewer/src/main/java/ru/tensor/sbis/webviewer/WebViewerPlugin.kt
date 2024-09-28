package ru.tensor.sbis.webviewer

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.webkit.WebView
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.network_native.cookies.clearCookiesSafe
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.verification_decl.login.event.AuthEvent
import ru.tensor.sbis.webviewer.contract.WebViewerDependency
import ru.tensor.sbis.webviewer.contract.WebViewerFeature
import ru.tensor.sbis.webviewer.contract.WebViewerFeatureImpl
import ru.tensor.sbis.webviewer.di.DaggerWebViewerSingletonComponent
import ru.tensor.sbis.webviewer.di.WebViewerSingletonComponent
import ru.tensor.sbis.webviewer.utils.DOC_WEB_VIEW_BROADCAST_ACTION
import ru.tensor.sbis.webviewer.utils.WebViewDebugBroadcastReceiver

/**
 * Плагин для просмотра документов через [WebView].
 *
 * @author ma.kolpakov
 */
object WebViewerPlugin : BasePlugin<Unit>(), WebViewerFeature.Provider {

    override val webViewerFeature: WebViewerFeature by lazy {
        WebViewerFeatureImpl()
    }

    private lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>
    private lateinit var apiServiceProvider: FeatureProvider<ApiService.Provider>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(WebViewerFeature::class.java) { webViewerFeature },
        FeatureWrapper(WebViewerFeature.Provider::class.java) { this }
    )

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
            .require(ApiService.Provider::class.java) { apiServiceProvider = it }
            .build()
    }

    override val customizationOptions: Unit = Unit

    /* Public, т.к. вызывается из .java классов. */
    val webViewerComponent: WebViewerSingletonComponent by lazy {
        val dependency = object : WebViewerDependency,
            LoginInterface.Provider by loginInterfaceProvider.get(),
            ApiService.Provider by apiServiceProvider.get() {
        }
        DaggerWebViewerSingletonComponent.factory().create(dependency)
    }

    // Для регистрации не используем ContextCompat.registerReceiver.
    // Применение ContextCompat приводит к падению тестов плагинной системы,
    // т.к. требуется замокать статический метод.
    // Добавление моков в более чем 25 приложений не выглядит целесообразным,
    // тест располагается на уровне application.
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun doAfterInitialize() {
        super.doAfterInitialize()

        val receiver = WebViewDebugBroadcastReceiver()
        val intentFilter = IntentFilter(DOC_WEB_VIEW_BROADCAST_ACTION)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU &&
            application.applicationInfo.targetSdkVersion > Build.VERSION_CODES.TIRAMISU
        ) {
            application.registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            application.registerReceiver(receiver, intentFilter)
        }
        subscribeOnAccountChangeEvent()
    }

    @SuppressLint("CheckResult")
    private fun subscribeOnAccountChangeEvent() {
        loginInterfaceProvider.get().loginInterface.eventsObservable
            .filter { it.eventType == AuthEvent.EventType.LOGIN && it.isNewAccount }
            .subscribe { clearCookies() }
    }

    private fun clearCookies() = clearCookiesSafe()
}