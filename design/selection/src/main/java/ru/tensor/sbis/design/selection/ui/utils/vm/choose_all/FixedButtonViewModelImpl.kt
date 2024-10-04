package ru.tensor.sbis.design.selection.ui.utils.vm.choose_all

import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import io.reactivex.subjects.PublishSubject

/**
 * Модель данных для отображения иконки и текста в [FixedButtonViewModelImpl]
 *
 * @author ma.kolpakov
 */
internal data class FixedButtonData(
    @StringRes val icon: Int,
    @StringRes val text: Int
)

/**
 * Базовая реализация [FixedButtonViewModel] для кнопок с иконкой и текстом без управления видимостью
 *
 * @author ma.kolpakov
 */
internal class FixedButtonViewModelImpl(
    private val data: FixedButtonData
) : FixedButtonViewModel<FixedButtonData> {

    override val fixedButtonData = MutableLiveData(data)
    override val showFixedButton = MutableLiveData(View.VISIBLE)
    override val fixedButtonClicked = PublishSubject.create<FixedButtonData>()

    override fun setStubVisible(visibility: Int) = Unit

    override fun onFixedButtonClicked() {
        fixedButtonClicked.onNext(data)
    }
}