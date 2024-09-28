package ru.tensor.sbis.wizard.impl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.base_components.keyboard.KeyboardDetector
import ru.tensor.sbis.common.util.AdjustResizeHelper.KeyboardEventListener
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.wizard.R
import ru.tensor.sbis.wizard.decl.WizardSteps
import ru.tensor.sbis.wizard.decl.result.WizardResult
import ru.tensor.sbis.wizard.decl.step.Step
import ru.tensor.sbis.wizard.decl.step.StepHolder
import ru.tensor.sbis.wizard.impl.WizardFragment.Companion.createAddTransaction
import ru.tensor.sbis.wizard.impl.di.WizardDI
import ru.tensor.sbis.wizard.impl.di.WizardDIFactory
import ru.tensor.sbis.wizard.impl.router.WizardRouterImpl
import ru.tensor.sbis.wizard.impl.state.StateSaver
import javax.inject.Inject
import kotlin.reflect.KClass
import ru.tensor.sbis.design.R as RDesign

/**
 * Фрагмент мастера создания прикладной сущности
 *
 * Инициализация
 *  1.  Создать [FragmentTransaction] на добавление [WizardFragment], см. [createAddTransaction]
 *  2.  Вызвать [FragmentTransaction.commit]
 *
 * Обработка переходов "назад"
 * Во время события:
 *  1.  Найдите фрагмент мастера в прикладном [FragmentManager]-е
 *  2.  Приведите его к [FragmentBackPress]
 *  3.  Вызовите [FragmentBackPress.onBackPressed]
 *      Мастер всегда возвращает true из этого метода, т.к. событие перехода "назад"
 *      либо будет обработано текущим шагом,
 *      либо произойдёт возврат к предыдущему шагу,
 *      либо мастер удалит сам себя из прикладного [FragmentManager]-а, если шагов больше не осталось
 * Фрагмент каждого шага мастера должен быть унаследован от [FragmentBackPress] или от [Content],
 * чтобы получать от мастера события перехода назад
 *
 * Обработка событий открытия/закрытия клавиатуры
 * Во время событий:
 *  1.  Найдите фрагмент мастера в прикладном [FragmentManager]-е
 *  2.  Приведите его к [KeyboardEventListener]
 *  3.  Вызовите [KeyboardEventListener.onKeyboardOpenMeasure]/[KeyboardEventListener.onKeyboardCloseMeasure]
 *      Мастер всегда возвращает true из этого метода, т.к. возвращать false нелогично,
 *      т.к. каждый фрагмент может реагировать на события открытия/закрытия клавиатуры независимо от других фрагментов
 * Фрагмент каждого шага мастера должен быть унаследован от [KeyboardEventListener],
 * чтобы получать от мастера события открытия/закрытия клавиатуры
 * Либо можно использовать [KeyboardDetector] во фрагментах шагов, что предпочтительней
 */
class WizardFragment : Fragment(), StepHolder, FragmentBackPress, KeyboardEventListener {

    private lateinit var diFactory: WizardDIFactory
    private val di: WizardDI by viewModels(factoryProducer = ::diFactory)
    private lateinit var router: WizardRouterImpl
    private lateinit var stepHolder: StepHolder
    private lateinit var stateSaver: StateSaver
    private lateinit var backNavigationEventHandler: BackNavigationEventHandler

    companion object {
        internal const val RESULT_KEY = "WizardFragment.result_key"
        internal const val RESULT_BUNDLE_KEY = "WizardFragment.result_bundle_key"

        internal const val STEPS_ARG = "WizardFragment.steps_arg"
        internal const val BACK_STACK_NAME = "WizardFragment.back_stack_name"

        /**
         * Создать транзакцию на добавление [WizardFragment]
         *
         * @param steps                 Прикладные шаги
         * @param fragmentManager       Прикладной [FragmentManager], в который будет добавлен фрагмент
         * @param containerViewId       Идентификатор вью-контейнера для фрагмента
         * @param tag                   Тэг фрагмента
         * @param backStackName         Имя для добавления в back stack
         *                              Если не null, то будет вызов addToBackStack на транзакции
         * @param resultListener        Слушатель результата, null если не требуется
         */
        fun createAddTransaction(
            steps: WizardSteps,
            fragmentManager: FragmentManager,
            @IdRes containerViewId: Int,
            tag: String,
            backStackName: String? = null,
            resultListener: ((WizardResult) -> Unit)? = null,
        ): FragmentTransaction =
            WizardFragment()
                .withArgs {
                    putParcelable(STEPS_ARG, steps)
                    putString(BACK_STACK_NAME, backStackName)
                }
                .run {
                    if (resultListener != null) {
                        fragmentManager.setFragmentResultListener(this, resultListener)
                    }
                    fragmentManager.beginTransaction().apply {
                        setCustomAnimations(RDesign.anim.right_in, RDesign.anim.nothing)
                        add(containerViewId, this@run, tag)
                        if (backStackName.isNullOrEmpty().not()) {
                            addToBackStack(backStackName)
                        }
                    }
                }

        private fun FragmentManager.setFragmentResultListener(
            fragment: WizardFragment,
            resultListener: ((WizardResult) -> Unit)
        ) {
            setFragmentResultListener(RESULT_KEY, fragment) { requestKey, resultBundle ->
                if (requestKey == RESULT_KEY) {
                    val result: WizardResult? = resultBundle.getParcelableUniversally(RESULT_BUNDLE_KEY)
                    if (result != null) {
                        resultListener.invoke(result)
                    } else {
                        illegalState { "Wizard result was null" }
                    }
                } else {
                    illegalState { "Unexpected request key - $requestKey" }
                }
            }
        }
    }

    internal lateinit var fragmentContainerView: WizardFragmentContainerView

    override fun <S : Step> getStep(stepClass: KClass<S>): S? =
        stepHolder.getStep(stepClass)

    override fun onCreate(savedInstanceState: Bundle?) {
        diFactory =
            WizardDIFactory(
                appContext = requireContext().applicationContext,
                steps = requireArguments().getParcelableUniversally(STEPS_ARG)!!,
                savedState = savedInstanceState,
                wizardBackStackName = requireArguments().getString(BACK_STACK_NAME)
            )
        di.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.wizard_fragment, container, false)
        fragmentContainerView = rootView.findViewById(R.id.wizardFragmentContainerView)
        return rootView
    }

    @Inject
    internal fun inject(
        router: WizardRouterImpl,
        stepHolder: StepHolder,
        stateSaver: StateSaver,
        backNavigationEventHandler: BackNavigationEventHandler
    ) {
        this.router = router
        this.stepHolder = stepHolder
        this.stateSaver = stateSaver
        this.backNavigationEventHandler = backNavigationEventHandler
    }

    override fun onResume() {
        super.onResume()
        router.attachTo(this)
    }

    override fun onPause() {
        router.detach()
        super.onPause()
    }

    override fun onBackPressed(): Boolean {
        backNavigationEventHandler.handleBackNavigationEvent()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver.saveTo(outState)
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        router.onKeyboardStateChanged(true, keyboardHeight)
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        router.onKeyboardStateChanged(false, keyboardHeight)
        return true
    }
}