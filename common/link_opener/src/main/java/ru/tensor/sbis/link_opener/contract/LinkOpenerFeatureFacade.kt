package ru.tensor.sbis.link_opener.contract

import android.content.Context
import ru.tensor.sbis.link_opener.contract.LinkOpenerFeatureFacade.configure
import ru.tensor.sbis.link_opener.di.LinkOpenerSingletonComponent
import ru.tensor.sbis.link_opener.di.LinkOpenerSingletonComponentInitializer
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerRegistrar
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController

/**
 * Фасадная реализация [LinkOpenerFeature].
 * Для корректной работы необходимо вызвать метод [configure].
 *
 * @author as.chadov
 */
internal object LinkOpenerFeatureFacade : LinkOpenerFeature {

    private lateinit var appContext: Context
    private lateinit var dependencies: LinkOpenerDependency
    private lateinit var featureConfiguration: LinkOpenerFeatureConfiguration
    private var registrarCallback: ((LinkOpenerRegistrar) -> Unit)? = null

    /**
     * Метод конфигурации фасада, необходимо вызвать до использования фичи через фасад.
     *
     * @param context контекст приложения.
     * @param dependency зависимости модуля.
     * @param configuration конфигурация использования компонента.
     * @param registerCallback коллбэк отложенной регистрации прикладных обработчиков.
     */
    @JvmOverloads
    fun configure(
        context: Context,
        dependency: LinkOpenerDependency,
        configuration: LinkOpenerFeatureConfiguration = LinkOpenerFeatureConfiguration.DEFAULT,
        registerCallback: ((LinkOpenerRegistrar) -> Unit)? = null
    ) {
        appContext = context
        dependencies = dependency
        featureConfiguration = configuration
        registrarCallback = registerCallback
    }

    /** @SelfDocumented */
    private val linkOpenerSingletonComponent: LinkOpenerSingletonComponent by lazy {
        LinkOpenerSingletonComponentInitializer(
            context = appContext,
            dependency = dependencies,
            configuration = featureConfiguration
        ).init()
    }

    override val openLinkController: OpenLinkController
        get() {
            registrarCallback?.invoke(linkOpenerRegistrar)
            registrarCallback = null
            return linkOpenerSingletonComponent.getOpenLinkController()
        }

    override val linkOpenerRegistrar get() = linkOpenerSingletonComponent.getLinkOpenerRegistrar()

    override val linkOpenerHandlerCreator get() = linkOpenerSingletonComponent.getLinkOpenHandlerCreator()

    override val linkOpenerPendingLinkFeature get() = linkOpenerSingletonComponent.getLinkOpenerPendingLinkFeature()
}
