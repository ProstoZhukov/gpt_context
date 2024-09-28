package ru.tensor.sbis.business.common.ui.base.adapter

import ru.tensor.sbis.base_components.adapter.vmadapter.ItemChecker
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter
import ru.tensor.sbis.business.common.R
import ru.tensor.sbis.business.common.ui.base.adapter.data.BottomStub
import ru.tensor.sbis.business.common.ui.base.contract.InfoContract
import ru.tensor.sbis.business.common.ui.base.state_vm.InformationVM
import ru.tensor.sbis.swipeablelayout.swipeablevm.MutableSwipeableVmHolder
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeableVmHelper
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeableVmHolder
import ru.tensor.sbis.swipeablelayout.util.SwipeableViewmodelsHolder

/**
 * Адаптер для отображения списков
 * - реализует отступ и прогресс бар отдельной ячейкой
 * - обрабатывает вьюмодели с поддержкой свайпа/смахивания
 *
 * @property swipeableVmHelper опциональный хелпер для работы со свайпаемыми вью моделями
 * @property bottomStub ромашка в конце списка
 */
class BaseListAdapter(
    private val swipeableVmHelper: SwipeableVmHelper = SwipeableVmHelper(),
    private val bottomStub: BottomStub = BottomStub()
) : ViewModelAdapter() {

    init {
        cell(
            R.layout.business_item_bottom_stub,
            itemChecker = ItemChecker.ForDiffUtils<BottomStub>()
        )
    }

    /**
     * Обработчик события изменения данных в адаптере
     * @param newItems новый список данных
     */
    override fun reload(newItems: List<Any>) {
        swipeableVmHelper.setupSwipeableViewModels(items.filterIsInstance(SwipeableVmHolder::class.java))
        super.reload(newItems.addBottomStubIfNeeded())
    }

    /**
     * Показать заглушку [BottomStub] снизу списка с ProgressBar
     */
    override fun showLoadMoreProgress() {
        bottomStub.progressVisibility.set(true)
    }

    /**
     * Скрыть заглушку [BottomStub] снизу списка с ProgressBar
     */
    fun hideLoadMoreProgress() {
        bottomStub.forceHideLoadMoreProgress = true
    }

    /**
     * Добавляет заглушку при необходимости
     * Не добавляет:
     *  - если список пустой
     *  - если в списке только одна заглушка (В [StubDataBinding] проблемно обнаружить [BottomStub])
     */
    private fun List<Any>.addBottomStubIfNeeded() = when {
        isEmpty() -> this
        size == 1 && first() is InformationVM || last() is InfoContract -> this
        else -> this + provideStub()
    }

    private fun provideStub() = bottomStub.apply {
        stopTimer()
        if (forceHideLoadMoreProgress) return@apply
        startTimer()
        progressVisibility.set(true)
    }
}