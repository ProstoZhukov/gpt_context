package ru.tensor.sbis.viewer.decl.slider.source

import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs

/**
 * Наблюдатель коллекции слайдера просмотрщиков
 *
 * @author sa.nikitin
 */
interface ViewerSliderCollectionObserver {

    /**
     * Коллекция сообщает, что все данные должны быть очищены и вставлены из параметра [args]
     */
    fun onReset(args: List<ViewerArgs>)

    /**
     * Коллекция сообщает, что необходимо добавить новые элементы по соответствующим индексам [indexedArgs]
     */
    fun onAdd(indexedArgs: List<IndexedViewerArgs>)

    /**
     * Коллекция сообщает, что необходимо удалить элементы по индексам из параметра [indices]
     */
    fun onRemove(indices: List<Long>)

    /**
     * Коллекция сообщает, что необходимо последовательно поменять местами элементы под данным из [indexPairs]
     */
    fun onMove(indexPairs: List<Pair<Long, Long>>)

    /**
     * Коллекция сообщает, что необходимо заменить элементы в заданных позициях [indexedArgs]
     */
    fun onReplace(indexedArgs: List<IndexedViewerArgs>)

    /**
     * Коллекция сообщает о добавлении крутилки по позиции [position]
     */
    fun onAddThrobber(position: ViewerSliderViewPosition)

    /**
     *  Коллекция сообщает об удалении крутилки
     */
    fun onRemoveThrobber()

    /**
     * Коллекция сообщает о добавлении заглушки по позиции [position]
     */
    fun onAddStub(type: ViewerSliderStubType, position: ViewerSliderViewPosition)

    /**
     * Коллекция сообщает об удалении заглушки
     */
    fun onRemoveStub()
}