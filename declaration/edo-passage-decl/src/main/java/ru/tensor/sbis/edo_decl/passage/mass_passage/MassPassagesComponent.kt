package ru.tensor.sbis.edo_decl.passage.mass_passage

import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.edo_decl.passage.PassageComponentFactory

/**
 * Компонент массового выполнения переходов
 *
 * Для создания нужно использовать [PassageComponentFactory.createMassPassageComponent]
 *
 * @author sa.nikitin
 */
interface MassPassagesComponent {

    /**
     * Событие о результате работы компонента
     */
    val resultEvent: Flow<MassPassagesResult>

    /**
     * Старт процесса переходов
     *
     * @param fragmentManager   [FragmentManager], в который будет добавлен фрагмент компонента
     * @param config            Конфигурация компонента
     */
    fun start(fragmentManager: FragmentManager, config: MassPassagesConfig)
}