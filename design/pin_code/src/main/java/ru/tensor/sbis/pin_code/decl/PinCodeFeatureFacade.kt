@file:JvmName("PinCodeFeatureFacade")
@file:Suppress("UNCHECKED_CAST")

package ru.tensor.sbis.pin_code.decl

import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.pin_code.feature.PinCodeFeatureFactory
import ru.tensor.sbis.pin_code.feature.PinCodeFeatureImpl

/**
 * Ленивая инициализация фичи для фрагмента.
 * @param fragment фрагмент в котором фича будет жить
 * @param repository поставщик внешнего репозитория
 * @param RESULT тип успешного результата выполнения проверки введенного пин-кода
 *
 * @author mb.kruglova
 */
@MainThread
fun <RESULT> createLazyPinCodeFeature(
    fragment: Fragment,
    repository: () -> PinCodeRepository<RESULT>
): Lazy<PinCodeFeature<RESULT>> = createLazyVm(repository, fragment)

/**
 * Ленивая инициализация фичи для активности.
 * @param activity активность в которой фича будет жить
 * @param repository поставщик внешнего репозитория
 * @param RESULT тип успешного результата выполнения проверки введенного пин-кода
 *
 * @author mb.kruglova
 */
@MainThread
fun <RESULT> createLazyPinCodeFeature(
    activity: AppCompatActivity,
    repository: () -> PinCodeRepository<RESULT>
): Lazy<PinCodeFeature<RESULT>> = createLazyVm(repository, activity)

/**
 * Создание фичи для фрагмента.
 * @param fragment фрагмент в котором фича будет жить
 * @param repository поставщик внешнего репозитория
 * @param RESULT тип успешного результата выполнения проверки введенного пин-кода
 *
 * @author mb.kruglova
 */
@MainThread
fun <RESULT> createPinCodeFeature(
    fragment: Fragment,
    repository: () -> PinCodeRepository<RESULT>
): PinCodeFeature<RESULT> = createVm(fragment.viewModelStore, repository)

/**
 * Создание фичи для активности.
 * @param activity активность в которой фича будет жить
 * @param repository поставщик внешнего репозитория
 * @param RESULT тип успешного результата выполнения проверки введенного пин-кода
 *
 * @author mb.kruglova
 */
@MainThread
fun <RESULT> createPinCodeFeature(
    activity: AppCompatActivity,
    repository: () -> PinCodeRepository<RESULT>
): PinCodeFeature<RESULT> = createVm(activity.viewModelStore, repository)

private fun <RESULT> createLazyVm(repositoryProducer: () -> PinCodeRepository<RESULT>, owner: ViewModelStoreOwner) =
    ViewModelLazy(
        PinCodeFeatureImpl::class,
        { owner.viewModelStore },
        { PinCodeFeatureFactory(repositoryProducer) }
    ) as ViewModelLazy<PinCodeFeatureImpl<RESULT>>

private fun <RESULT> createVm(store: ViewModelStore, repositoryProducer: () -> PinCodeRepository<RESULT>) =
    ViewModelProvider(
        store,
        PinCodeFeatureFactory(repositoryProducer)
    )[PinCodeFeatureImpl::class.java] as PinCodeFeatureImpl<RESULT>