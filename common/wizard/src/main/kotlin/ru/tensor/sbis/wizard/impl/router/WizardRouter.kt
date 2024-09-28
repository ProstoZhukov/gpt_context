package ru.tensor.sbis.wizard.impl.router

import io.reactivex.Completable
import ru.tensor.sbis.wizard.decl.result.WizardResult
import ru.tensor.sbis.wizard.decl.step.StepFragmentFactory

/**
 * Роутер мастера
 *
 * @author sa.nikitin
 */
internal interface WizardRouter {

    /**
     * Перейти к следующему шагу
     *
     * @param fragmentFactory   [StepFragmentFactory] следущего шага
     * @param nextStepTag       Тэг фрагмента следующего шага
     *
     * @return [Completable], испускающий результат при непосредственном выполнении перехода
     */
    fun toStep(fragmentFactory: StepFragmentFactory, nextStepTag: String): Completable

    /**
     * Перейти к предыдущему шагу
     *
     * @param fragmentFactory       [StepFragmentFactory] предыдущего шага
     * @param previousStepTag       Тэг фрагмента предыдущего шага
     *
     * @return [Completable], испускающий результат при непосредственном выполнении перехода
     */
    fun toPreviousStep(fragmentFactory: StepFragmentFactory, previousStepTag: String): Completable

    /**
     * Отправить событие навигации "назад" фрагменту шага
     *
     * @param stepTag Тэг фрагмента шага
     * @return  true, если шаг обработал событие
     *          false, если шаг не обработал событие
     *          null, если не удалось найти фрагмент шага, т.к. роутер отсоединён от фрагмента мастера
     */
    fun dispatchBackNavigationEventToStepFragment(stepTag: String): Boolean?

    /**
     * Закрыть экран мастера
     *
     * @return [Completable], испускающий результат при непосредственном закрытии экрана мастера
     */
    fun finish(result: WizardResult): Completable
}