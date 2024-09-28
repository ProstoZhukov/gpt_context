package ru.tensor.sbis.edo_decl.passage

import ru.tensor.sbis.edo_decl.passage.config.PassageDocIds

/**
 * Событие перехода
 *
 * @author sa.nikitin
 */
sealed class PassageEvent {

    abstract val documentIds: PassageDocIds<*>

    /**
     * Переход выполнен
     */
    class Success(override val documentIds: PassageDocIds<*>, val passage: Passage?, val comment: String?) : PassageEvent()

    /**
     * Переход отменён
     */
    class Cancelled(override val documentIds: PassageDocIds<*>) : PassageEvent()

    /**
     * Ошибка перехода
     * Компонент при этом завершает работу, UI скрывается самостоятельно
     *
     * Следует обработать причины
     */
    class Failed(override val documentIds: PassageDocIds<*>, val reason: PassageFailReason) : PassageEvent()
}