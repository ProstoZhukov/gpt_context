package ru.tensor.sbis.list.view.container

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import ru.tensor.sbis.list.base.presentation.StubViewContentFactory

/**
 * Реализация интерфейса позволит использовать класс для data binding.
 *
 * @property stubViewVisibility LiveData<Int> видим ли виджет заглушки.
 * @property listVisibility LiveData<Int> видимость виджета списка.
 * @property progressVisibility LiveData<Int> видимость виджета индикатора прогресса.
 * @property stubContent LiveData<StubContent> контент заглушки.
 */
interface ListContainerViewModel {

    val stubViewVisibility: LiveData<Int>
    val listVisibility: LiveData<Int>
    val progressVisibility: LiveData<Int>
    val stubContent: LiveData<StubViewContentFactory>

    /**
     * @SelDocumented
     */
    @AnyThread
    fun showOnlyProgress()

    /**
     * @param immediate должна ли заглушка отображаться без какой-либо задержки.
     */
    @AnyThread
    fun showOnlyStub(immediate: Boolean = false)

    /**
     * @SelDocumented
     */
    @AnyThread
    fun showOnlyList()

    /**
     * @SelDocumented
     */
    @AnyThread
    fun setStubContentFactory(factory: StubViewContentFactory)
}