package ru.tensor.sbis.red_button.interactor

import io.reactivex.Completable
import io.reactivex.Single
import ru.tensor.sbis.red_button.data.RedButtonState
import ru.tensor.sbis.red_button.data.RedButtonStubType
import ru.tensor.sbis.red_button.repository.data_source.RedButtonPreferences
import ru.tensor.sbis.red_button.repository.mapper.RedButtonIntToStateMapper
import ru.tensor.sbis.red_button.repository.mapper.RedButtonStubMapper
import javax.inject.Inject

/**
 * Интерактор, реализующий работу с [RedButtonPreferences]
 * @property preferences объект реализующий работу с [SharedPreferences]
 *
 * @author ra.stepanov
 */
@Suppress("KDocUnresolvedReference")
class RedButtonPreferencesInteractor @Inject constructor(private val preferences: RedButtonPreferences) {

    /**
     * Получение данных о заглушке
     * @return [Single] излучающий [RedButtonStubType]
     */
    fun getStubPreference(): Single<RedButtonStubType> =
        Single.fromCallable { RedButtonStubMapper().apply(preferences.getRedButtonRefreshApp()) }

    /**
     * Запись данных о заглушке
     * @param stubType enum-значение типа заглушки
     * @return [Single] излучающий [Unit] по завершению операции
     */
    fun putStubPreference(stubType: RedButtonStubType) =
        Completable.fromCallable { preferences.setRedButtonRefreshApp(stubType.value) }

    /**
     * Очистка данных о заглушке
     * @return [Single] излучающий [Unit] по завершению операции
     */
    fun clearStubPreference() = Completable.fromCallable { preferences.clearRedButtonRefreshApp() }

    /**
     * Получение данных о состоянии кнопки
     * @return [Single] излучающий [RedButtonState]
     */
    fun getStatePreference() = RedButtonIntToStateMapper().apply(preferences.getRedButtonState())

    /**
     * Запись данных о состоянии кнопки
     * @param state состояние красной кнопки
     * @return [Single] излучающий [Unit] по завершению операции
     */
    fun putState(state: RedButtonState) = preferences.setRedButtonState(state.value)
}