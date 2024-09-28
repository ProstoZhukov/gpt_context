package ru.tensor.sbis.wizard.impl.router

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.launch
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.wizard.decl.result.WizardResult
import ru.tensor.sbis.wizard.decl.step.StepFragmentFactory
import ru.tensor.sbis.wizard.impl.WizardFragment
import javax.inject.Inject
import ru.tensor.sbis.design.R as RDesign

private typealias Route = (context: Context, fragment: WizardFragment) -> Unit

/**
 * Реализация [WizardRouter]
 * Для привязки к фрагменту необходимо вызвать [attachTo], для отвязки - [detach]
 *
 * @author sa.nikitin
 */
@Reusable
internal class WizardRouterImpl @Inject constructor(val wizardBackStackName: String?) : WizardRouter {

    private var fragment: WizardFragment? = null
    private val pendingRoutes = mutableListOf<Route>()
    private val completedRoutesSubject = PublishSubject.create<Route>()
    private val completedRoutesObservable: Observable<Route> = completedRoutesSubject.share()

    override fun toStep(fragmentFactory: StepFragmentFactory, nextStepTag: String): Completable =
        executeRoute { _, fragment ->
            //Костыль, т.к. анимации фрагментов из коробки работают криво, см. WizardFragmentContainerView
            fragment.fragmentContainerView.setDrawDisappearingViewsLast(true)
            fragment.childFragmentManager.beginTransaction().apply {
                //TODO Подумать над добавлением возможности включить/отключить анимации с прикладной стороны
                //if (fragment.childFragmentManager.fragments.isNotEmpty()) {
                //    setCustomAnimations(R.anim.right_in, R.anim.nothing)
                //}
                replace(fragment.fragmentContainerView.id, fragmentFactory.createFragment(fragment), nextStepTag)
                commitAllowingStateLoss()
            }
        }

    override fun toPreviousStep(fragmentFactory: StepFragmentFactory, previousStepTag: String): Completable =
        executeRoute { _, fragment ->
            //Костыль, т.к. анимации фрагментов из коробки работают криво, см. WizardFragmentContainerView
            fragment.fragmentContainerView.setDrawDisappearingViewsLast(false)
            fragment.childFragmentManager
                .beginTransaction()
                //TODO Подумать над добавлением возможности включить/отключить анимации с прикладной стороны
                //.setCustomAnimations(R.anim.nothing, R.anim.right_out)
                .replace(fragment.fragmentContainerView.id, fragmentFactory.createFragment(fragment), previousStepTag)
                .commitAllowingStateLoss()
        }

    override fun dispatchBackNavigationEventToStepFragment(stepTag: String): Boolean? =
        fragment?.let {
            when (val stepFragment = it.childFragmentManager.findFragmentByTag(stepTag)) {
                is FragmentBackPress -> stepFragment.onBackPressed()
                is Content -> stepFragment.onBackPressed()
                else -> null
            }
        }

    override fun finish(result: WizardResult): Completable =
        executeRoute { _, fragment ->
            //Костыль, т.к. анимации фрагментов из коробки работают криво, см. https://issuetracker.google.com/issues/137310379
            //Можно попробовать через FragmentContainerView, но придётся обязать каждого пользователя мастера использовать его,
            //что может быть совсем неудобно, да и гарантий всё равно нет, см. WizardFragmentContainerView
            @AnimRes val animationId: Int =
                when (result) {
                    is WizardResult.Complete -> RDesign.anim.fade_out
                    is WizardResult.Error -> RDesign.anim.fade_out
                    is WizardResult.Back -> RDesign.anim.right_out
                    is WizardResult.Cancel -> RDesign.anim.right_out
                }

            fragment.setFragmentResult(
                WizardFragment.RESULT_KEY,
                bundleOf(WizardFragment.RESULT_BUNDLE_KEY to result),
            )

            val animation = AnimationUtils.loadAnimation(fragment.requireContext(), animationId)
            animation.setAnimationListener(
                object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) = Unit

                    override fun onAnimationEnd(animation: Animation?) {
                        fragment.lifecycleScope.launch {
                            fragment.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                                val fragmentManager: FragmentManager =
                                    fragment.parentFragment?.childFragmentManager
                                        ?: fragment.activity?.supportFragmentManager
                                        ?: return@repeatOnLifecycle
                                if (wizardBackStackName != null) {
                                    for (i in 0 until fragmentManager.backStackEntryCount) {
                                        val entry: FragmentManager.BackStackEntry =
                                            fragmentManager.getBackStackEntryAt(i)
                                        if (entry.name == wizardBackStackName) {
                                            fragmentManager.popBackStack(
                                                wizardBackStackName,
                                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                                            )
                                            return@repeatOnLifecycle
                                        }
                                    }
                                }
                                fragmentManager
                                    .beginTransaction()
                                    .remove(fragment)
                                    .commitAllowingStateLoss()
                            }
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) = Unit
                }
            )

            fragment.requireView().startAnimation(animation)
        }

    /**
     * Пробросить события открытия/закрытия клавиатуры в дочерние фрагменты
     */
    internal fun onKeyboardStateChanged(isOpen: Boolean, keyboardHeight: Int) {
        fragment?.childFragmentManager?.fragments?.forEach { fragment ->
            if (fragment is AdjustResizeHelper.KeyboardEventListener) {
                if (isOpen) {
                    fragment.onKeyboardOpenMeasure(keyboardHeight)
                } else {
                    fragment.onKeyboardCloseMeasure(keyboardHeight)
                }
            }
        }
    }

    /**
     * Привязать роутер к [Fragment]
     * Следует вызывать на onResume
     */
    fun attachTo(fragment: WizardFragment) {
        this.fragment = fragment
        pendingRoutes.forEach { fragment.executeRoute(it) }
        pendingRoutes.clear()
    }

    /**
     * Отвязать роутер от ранее привязанного [Fragment], см. [attachTo]
     * Следует вызывать на onPause
     */
    fun detach() {
        this.fragment = null
    }

    private fun executeRoute(route: Route): Completable =
        Completable.merge(
            listOf(
                completedRoutesObservable
                    .filter { it === route }
                    .take(1)
                    .ignoreElements()
                    .doOnDispose { pendingRoutes.remove(route) },
                Completable.fromAction { fragment?.executeRoute(route) ?: savePendingRoute(route) },
            )
        )

    private fun WizardFragment.executeRoute(route: Route) {
        completedRoutesSubject.onNext(route)
        route.invoke(requireContext(), this)
    }

    private fun savePendingRoute(route: Route) {
        pendingRoutes.add(route)
    }
}