package ru.tensor.sbis.wizard.decl.step

import androidx.fragment.app.Fragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.base_components.keyboard.KeyboardDetector
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.design_dialogs.dialogs.content.Content

/**
 * Фабрика фрагмента шага
 * Получить шаг по его [Fragment]-у можно с помощью метода [getStep]
 *
 * @author sa.nikitin
 */
interface StepFragmentFactory {

    /**
     * Создать [Fragment] шага
     *
     * Фрагмент должен быть унаследован от [FragmentBackPress] или от [Content],
     * чтобы получать от мастера события перехода назад
     *
     * Фрагмент должен быть унаследован от [AdjustResizeHelper.KeyboardEventListener],
     * чтобы получать от мастера события открытия/закрытия клавиатуры
     * Либо во фрагменте можно использовать [KeyboardDetector]
     */
    fun createFragment(stepHolderFragment: Fragment): Fragment
}

/**
 * Получить шаг с помощью его фрагмента
 *
 * @return [Step], если фрагмент используется в качестве шага мастера, иначе null
 */
inline fun <reified S : Step> Fragment.getStep(): S? =
    (parentFragment as? StepHolder)?.getStep(S::class)