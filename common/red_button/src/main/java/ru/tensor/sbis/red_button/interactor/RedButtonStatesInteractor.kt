package ru.tensor.sbis.red_button.interactor

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.red_button.data.RedButtonActions
import ru.tensor.sbis.red_button.data.RedButtonState
import ru.tensor.sbis.red_button.repository.data_source.RedButtonDataSource
import javax.inject.Inject

/**
 * Интерактор для получения состояния и типа действия красной кнопки
 * @property redButtonPreferencesInteractor интерактор для работы с [SharedPreferences]
 * @property dataSource обёртка для работы с контроллером
 *
 * @author ra.stepanov
 */
@Suppress("KDocUnresolvedReference")
class RedButtonStatesInteractor @Inject constructor(
    private val redButtonPreferencesInteractor: RedButtonPreferencesInteractor,
    private val dataSource: RedButtonDataSource
) : BaseInteractor() {

    /**
     * Получить значение текущего состояния "Красной кнопки"
     * @return [Single] излучающий [RedButtonState]
     */
    fun getState() = redButtonPreferencesInteractor.getStatePreference()

    /**
     * Получить значение текущего состояния "Красной кнопки" из контроллера и записать данные в [SharedPreferences]
     * @return [Single] излучающий [RedButtonState]
     */
    fun getStateDirectly(): Single<RedButtonState> = dataSource.getState()
        .doOnSuccess { redButtonPreferencesInteractor.putState(it) }

    /**
     * Получить значение текущего действия "Красной кнопки"
     * @return [Single] излучающий [RedButtonActions]
     */
    fun getAction(): Single<RedButtonActions> = dataSource.getAction()

    /**
     * Проверка не нажата ли "Красная кнопка"
     * @return [Single] излучающий true, если кнопка не нажата, иначе false
     */
    fun isButtonActivated(): Single<Boolean> = dataSource.getState()
        .map { it == RedButtonState.CLICK }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}