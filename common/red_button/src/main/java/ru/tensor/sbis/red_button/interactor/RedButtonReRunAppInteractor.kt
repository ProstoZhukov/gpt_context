package ru.tensor.sbis.red_button.interactor

import android.annotation.SuppressLint
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.red_button.events.RedButtonNeedRefreshApp
import ru.tensor.sbis.red_button.repository.data_source.RedButtonDataSource
import javax.inject.Inject

/**
 * Интерактор, реализующий работу с событием необходимости перезагрузить приложение [RedButtonNeedRefreshApp]
 * @property preferencesInteractor интерактор для работы с [SharedPreferences]
 * @property rxBus требуется для отправки событий о необходимости перезапустить приложение
 * @property dataSource обёртка для работы с контроллером
 *
 * @author ra.stepanov
 */
@Suppress("KDocUnresolvedReference")
class RedButtonReRunAppInteractor @Inject constructor(
    private val preferencesInteractor: RedButtonPreferencesInteractor,
    private val rxBus: RxBus,
    private val dataSource: RedButtonDataSource
) {

    /**
     * Подписка на событие контроллера о необходимости перезапустить приложение,
     * Записывает значение показываемой заглушки в [SharedPreferences] и отправляет событие в [RxBus]
     */
    @SuppressLint("CheckResult")
    fun subscribeOnAppReRun() {
        preferencesInteractor.clearStubPreference().subscribe {
            dataSource.subscribeOnRefreshApp {
                preferencesInteractor.putStubPreference(it).subscribe { rxBus.post(RedButtonNeedRefreshApp(it)) }
            }
        }
    }

}