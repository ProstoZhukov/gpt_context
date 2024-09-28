package ru.tensor.sbis.onboarding.ui.utils

import io.reactivex.Single
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import ru.tensor.sbis.onboarding.contract.providers.OnboardingProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.verification_decl.login.LoginInterface
import timber.log.Timber

/**
 * Класс для работы с набором провайдеров и предоставления нужного экземпляра при запросе.
 *
 * @author ps.smirnyh
 */
@Deprecated("Используется только старым Онбордингом и Что нового")
class OnboardingProviderMediator internal constructor(
    private val preferenceManager: OnboardingPreferenceManager,
    providers: Set<FeatureProvider<OnboardingProvider>>,
    loginInterface: LoginInterface?,
    ioDispatcher: CoroutineDispatcher
) {
    /**
     * Асинхронное получение списка провайдеров с кэшированием из FeatureProvider.
     *
     * ВАЖНО: должен выполняться только на MainThread'e, т.к. в противном случае
     * может создать DeadLock, который трудно диагностировать. (Стрельнуло в Waiter'e).
     * p.s.
     * Польза от этого подхода непонятна, т.к. ниже все равно используется '.blockingGet()'.
     */
    private var providersList = Single
        .fromCallable {
            providers.map { provider ->
                provider.get()
            }
        }
        .map { list -> list.sortedByDescending { it.showPriority } }
        .cache()

    private var activeProvider: OnboardingProvider? = null
    private var providerForSession: OnboardingProvider? = null
    private val coroutineScope = MainScope()

    init {
        coroutineScope.launch(ioDispatcher) {
            refreshActiveProvider()
            loginInterface?.run {
                userAccountObservable.asFlow()
                    .catch { Timber.e(it, "Unable to switch onboarding provider. Active is $activeProvider") }
                    .collect {
                        providerForSession = null
                        refreshActiveProvider()
                    }
            }
        }
    }

    /**
     * Проверка необходимости показа onboarding.
     *
     * @return true, если показывать не нужно, иначе false
     */
    fun check(): Boolean {
        refreshActiveProvider()
        return activeProvider == null
    }

    /**
     * Получение активного провайдера из набора провайдеров либо первого из списка.
     */
    fun getActiveProvider(): OnboardingProvider? = activeProvider

    /**
     * Перепроверка условий работы провайдеров для обновления состояния неоходимости показа онбординга.
     */
    private fun getActiveProviderInternal(): OnboardingProvider? {
        providersList.blockingGet().forEach { provider ->
            val rules = provider.getCustomOnboardingPreferenceManger() ?: preferenceManager
            if (rules.restoreProcessed().not()) {
                if (providerForSession == null || provider == providerForSession) {
                    providerForSession = provider
                    return provider
                }
            }
        }
        return null
    }

    private fun refreshActiveProvider() {
        activeProvider = getActiveProviderInternal()
    }
}