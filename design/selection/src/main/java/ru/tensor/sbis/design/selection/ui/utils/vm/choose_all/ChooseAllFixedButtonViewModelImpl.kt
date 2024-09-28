package ru.tensor.sbis.design.selection.ui.utils.vm.choose_all

import android.view.View
import android.view.View.VISIBLE
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.utils.checkSafe

/**
 * @author ma.kolpakov
 */
internal class ChooseAllFixedButtonViewModelImpl : ChooseAllFixedButtonViewModel {

    private val stubViewVisibilitySubject = BehaviorSubject.create<Int>()

    /**
     * Состояние видимости заглушки, которое отправляет первое событие только при её отображении
     */
    private val stubViewVisibility = stubViewVisibilitySubject.skipWhile { isVisible -> isVisible != VISIBLE }

    private val buttonClickSubject = BehaviorSubject.create<Unit>()

    private val fixedButtonDataSubject = BehaviorSubject.create<SelectorItemModel>()

    override val fixedButtonData = MutableLiveData<SelectorItemModel>().apply {
        // TODO: 7/7/2020 https://online.sbis.ru/opendoc.html?guid=a8f69b89-5d80-464c-b16c-08c78736a2c8
        fixedButtonDataSubject.subscribe {
            postValue(it)
        }
    }

    override val showFixedButton = MutableLiveData(View.GONE).apply {
        Observable
            .combineLatest<Any, Int, Int>(
                // в этом потоке важно только наличие данных. Без данных активировать кнопку не нужно
                fixedButtonDataSubject,
                stubViewVisibility,
                { _, stubVisibility -> stubVisibility }
            )
            .distinctUntilChanged()
            // TODO: 7/7/2020 https://online.sbis.ru/opendoc.html?guid=a8f69b89-5d80-464c-b16c-08c78736a2c8
            .subscribe {
                postValue(it)
            }
    }

    override val fixedButtonClicked: Observable<SelectorItemModel> = buttonClickSubject
        .withLatestFrom(
            fixedButtonDataSubject,
            stubViewVisibility,
            { _, data, stubVisibility ->
                check(stubVisibility == VISIBLE)
                data
            }
        )

    @Synchronized
    override fun setData(data: SelectorItemModel) {
        require(data.meta.handleStrategy == ClickHandleStrategy.COMPLETE_SELECTION) {
            "Item should be handled with COMPLETE_SELECTION strategy"
        }

        /*
        Подозрительное на ошибки состояние, когда поступают разные элементы "Выбрать всё".
        Пока непонятно, существует ли оно и как с ним работать.
         */
        val currentData = fixedButtonData.value
        checkSafe(currentData == null || currentData.id == data.id) {
            "Unexpected state: choose all items are different (${currentData!!.id} != ${data.id})"
        }

        fixedButtonDataSubject.onNext(data)
    }

    override fun setStubVisible(visibility: Int) {
        stubViewVisibilitySubject.onNext(visibility)
    }

    override fun onFixedButtonClicked() {
        buttonClickSubject.onNext(Unit)
    }
}