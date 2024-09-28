package ru.tensor.sbis.communicator.common.ui.hostfragment.foldable

/**
 * Слушатель изменений foldable состояний
 *
 * @author vv.chekurda
 */
interface FoldableStateChangeListener {

    /**
     * Изменение foldable состояния
     *
     * @param newState              новое состояние [FoldableState]
     * @param withFragmentTransfers true, если были осуществлены перемещения фрагментов
     */
    fun onFoldableStateChanged(newState: FoldableState, withFragmentTransfers: Boolean)
}