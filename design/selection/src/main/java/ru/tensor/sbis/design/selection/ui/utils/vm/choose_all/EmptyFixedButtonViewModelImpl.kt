package ru.tensor.sbis.design.selection.ui.utils.vm.choose_all

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable

/**
 * Реализация [FixedButtonViewModel], которая не оказывает влияния на поведение пользователей
 *
 * @author ma.kolpakov
 */
internal class EmptyFixedButtonViewModelImpl : FixedButtonViewModel<Nothing> {

    override val fixedButtonData = MutableLiveData<Nothing>()
    override val showFixedButton = MutableLiveData<Int>()
    override val fixedButtonClicked = Observable.never<Nothing>()!!

    override fun setStubVisible(visibility: Int) = Unit

    override fun onFixedButtonClicked() = throw UnsupportedOperationException(
        "Choose all button should be unavailable for click with empty implementation"
    )
}