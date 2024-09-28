package ru.tensor.sbis.red_button.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.red_button.BuildConfig
import ru.tensor.sbis.red_button.data.RedButtonActions
import ru.tensor.sbis.red_button.data.RedButtonData
import ru.tensor.sbis.red_button.data.RedButtonError
import ru.tensor.sbis.red_button.data.RedButtonState
import ru.tensor.sbis.red_button.events.RedButtonStateRefresh
import ru.tensor.sbis.red_button.repository.data_source.RedButtonDataSource
import ru.tensor.sbis.red_button.ui.host.di.HostScope
import ru.tensor.sbis.red_button_service.generated.RedButtonWrongConnectionEx
import timber.log.Timber
import javax.inject.Inject

/**
 * Реализация репозитория для компонента "Красная кнопка", используется только во фрагментах красной кнопки.
 * Для работы с микросервисом [RedButtonService] вне фрагментов красной кнопки используется [RedButtonDataSource] напрямую
 * @property rxBus требуется для обновления состояния красной кнопки на UI
 * @property dataSource обёртка для работы с контроллером
 *
 * @author ra.stepanov
 */
@Suppress("KDocUnresolvedReference")
@HostScope
class RedButtonRepository @Inject constructor(
    private val rxBus: RxBus,
    private val dataSource: RedButtonDataSource
) {

    private var redButtonData: RedButtonData? = null
    private var state: BehaviorSubject<RedButtonState> = BehaviorSubject.create()
    private var action = RedButtonActions.HIDE_MANAGEMENT

    init {
        initState()
    }

    /**
     * Получить значение текущего статуса "Красной кнопки"
     * @return [Observable] излучает [RedButtonState]
     */
    fun getState(): Observable<RedButtonState> = state

    /**
     * Получить значение номера телефона, на который отправлена смс
     * @return [String] номер телефона с маской
     */
    fun getPhone(): String = redButtonData?.phone ?: ""

    /**
     * Отражает операцию нажатия пользователем "Красной Кнопки".
     * Проверяет доступность функционала "Красная кнопка" и возможность нажатия кнопки пользователем.
     * Если доступ есть,то запрашивается подтверждение по СМС изменения состояния "Красной кнопки".
     * Не важно, нажали ее или нет, важно запросить подтверждение на смену состояния.
     * @param pin 5-ти значный пин код для закрытия/открытия "Красной Кнопки"
     * @return [Completable]
     */
    fun turnOn(pin: String) =
        Completable.fromSingle(dataSource.setPinCode(pin).map { redButtonData = it })

    /**
     * Отключает действие "Красной Кнопки" при условии прохождения проверки достоверности указанного пин-кода.
     * @param pinCode передается 5-ти значный пин-код, необходимый для нажатия/отжатия "Красной кнопки".
     * @return [Completable]
     */
    fun turnOff(pinCode: String) =
        Completable.fromSingle(dataSource.off(pinCode).map { setStateWithReset(RedButtonState.OPEN_IN_PROGRESS) })

    /**
     * Вызывается после нажатия красной кнопки с введением кода подтверждения и пина.
     * Проверяет доступность функционала "Красная кнопка" и возможность нажатия кнопки пользователем.
     * Если доступ есть, то проверяется достоверность кода подтверждения переданного пользователю по СМС.
     * @param smsCode передается 5-ти значный пин-код, необходимый для нажатия/отжатия "Красной Кнопки".
     * @return [Completable]
     */
    fun confirmOn(smsCode: String): Completable {
        return Completable.fromSingle(dataSource.on(
            redButtonData?.operationUuid ?: "",
            smsCode.toInt(),
            redButtonData?.pin ?: ""
        ).map { setStateWithReset(RedButtonState.CLOSE_IN_PROGRESS) })
    }

    /**
     * Инициализация состояния "Красной Кнопки", получает состояние кнопки из контроллера
     */
    private fun initState() {
        var disposable = Disposables.disposed()
        disposable = dataSource.getState()
            .doAfterSuccess { disposable.dispose() }
            .subscribe(
                {
                    setState(it)
                    if (isAvailable(it)) initAction()
                },
                {
                    setState(RedButtonState.ACCESS_DENIED)
                    when {
                        /*
                        проблемы с сетью нет смысла логировать как нефатальные ошибки
                        https://online.sbis.ru/opendoc.html?guid=34eedaa7-b42c-42f1-a014-f3a291880234
                        */
                        it is RedButtonWrongConnectionEx -> Unit
                        it is RedButtonError.NoInternet -> Unit
                        BuildConfig.DEBUG -> throw it
                        else -> Timber.w(it)
                    }
                }
            )
    }

    /**
     * Инициализация текущего действия [RedButtonActions] "Красной Кнопки", получает текущее действие кнопки из контроллера
     */
    private fun initAction() {
        var disposable = Disposables.disposed()
        disposable = dataSource.getAction()
            .doAfterSuccess { disposable.dispose() }
            .subscribe(
                { action = it },
                { if (BuildConfig.DEBUG) throw it else Timber.e(it) }
            )
    }

    /**
     * Обработчик вызываемый после операции закрытия/открытия "Красной Кнопки",
     * устанавливает новое состояние "Красной Кнопки" и очищает данные операции
     * @param state новое состояние красной кнопки
     */
    private fun setStateWithReset(state: RedButtonState) {
        redButtonData = RedButtonData()
        setState(state)
    }

    /**
     * Установка нового значения состояния "Красной кнопки" [RedButtonState]
     * @param state новое enum-значение состояния
     */
    private fun setState(state: RedButtonState) {
        if (state != this.state.value) {
            this.state.onNext(state)
            rxBus.post(RedButtonStateRefresh(state))
        }
    }

    /**
     * Проверяет доступность функционала "Красной Кнопки" и возможность нажатия кнопки пользователем.
     * @return true если кнопку можно нажать действия с ней доступы, иначе false
     */
    private fun isAvailable(state: RedButtonState) = state == RedButtonState.CLICK || state == RedButtonState.NOT_CLICK
}