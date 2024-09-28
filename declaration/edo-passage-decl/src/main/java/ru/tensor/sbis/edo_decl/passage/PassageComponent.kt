package ru.tensor.sbis.edo_decl.passage

import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.edo_decl.passage.config.PassageConfig

/**
 * Компонент переходов
 * Для старта процесса перехода необходимо вызвать [start]
 *
 * @see PassageEventsProvider
 *
 * @author sa.nikitin
 */
interface PassageComponent : PassageEventsProvider {

    /**
     * Начать процесс перехода
     * Если из текущего этапа только один переход, не требующий ввода комментария или выбора исполнителя,
     * то он будет выполнен сразу, без отображения панели
     * Иначе будет отображена панель переходов
     *
     * @param fragmentManager [FragmentManager], в который будет добавлен фрагмент компонента
     */
    fun start(fragmentManager: FragmentManager, config: PassageConfig)
}