@file:Suppress("DEPRECATION")

package ru.tensor.sbis.modalwindows.optionscontent.universal.mutable

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import ru.tensor.sbis.common.rx.consumer.FallbackErrorConsumer
import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOption
import ru.tensor.sbis.modalwindows.optionscontent.BaseOptionsContentPresenter
import ru.tensor.sbis.modalwindows.optionscontent.universal.mutable.interactor.MutableOptionsInteractor

/**
 * Базовая реализация презентера для динамического списка опций
 *
 * @author sr.golovkin
 */
class UniversalMutableOptionsContentPresenter<V : UniversalMutableOptionsContentContract.View<O>, I: MutableOptionsInteractor<O>, O : BottomSheetOption>(
    private val interactor: I
): BaseOptionsContentPresenter<V, O>(),
    UniversalMutableOptionsContentContract.Presenter<V, O> {

    private val disposables = CompositeDisposable()

    /**
     * Переопределить при необходимости отображения опций ДО загрузки.
     * Может быть использован для отображения ячейки загрузки при длительном формировании опций.
     */
    override fun createOptions(isLandscape: Boolean): List<O> {
        return emptyList()
    }

    override fun attachView(view: V) {
        super.attachView(view)
        updateOptions()
    }

    override fun onOptionsUpdated(newOptions: List<O>) {
        mView?.updateOptionList(newOptions)
    }

    override fun onOptionClick(option: O) {
        disposables.add(interactor.performActionOnOptionClick(option).subscribe(Action {
            mView?.closeDialog()
        }, FallbackErrorConsumer.DEFAULT))
    }
    /**
     * обновить опции в диалоговом окне
     */
    private fun updateOptions() = disposables.add(interactor.loadOptions().subscribe {
        onOptionsUpdated(it)
    })

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}