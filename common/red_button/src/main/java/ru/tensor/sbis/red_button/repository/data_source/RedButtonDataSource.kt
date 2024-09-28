package ru.tensor.sbis.red_button.repository.data_source

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.red_button.data.RedButtonActions
import ru.tensor.sbis.red_button.data.RedButtonState
import ru.tensor.sbis.red_button.data.RedButtonStubType
import ru.tensor.sbis.red_button.repository.mapper.RedButtonActionsMapper
import ru.tensor.sbis.red_button.repository.mapper.RedButtonDataMapper
import ru.tensor.sbis.red_button.repository.mapper.RedButtonErrorMapper
import ru.tensor.sbis.red_button.repository.mapper.RedButtonStateMapper
import ru.tensor.sbis.red_button.repository.mapper.RedButtonStubMapper
import ru.tensor.sbis.red_button_service.generated.RedButtonEventCallback
import ru.tensor.sbis.red_button_service.generated.RedButtonEventsManager
import ru.tensor.sbis.red_button_service.generated.RedButtonService
import javax.inject.Inject

/**
 * Класс-обёртка над контроллером.
 *
 * @author ra.stepanov
 */
class RedButtonDataSource @Inject constructor() {

    /**
     * Получить состояние красной кнопки
     * @return [Single] излучающий [RedButtonState]
     */
    fun getState(): Single<RedButtonState> = Single.fromCallable { RedButtonService.state() }
        .map(RedButtonStateMapper())
        .onErrorResumeNext { Single.error(RedButtonErrorMapper().apply(it)) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * Получить действие красной кнопки установленное на ОС.
     *
     * Если от контроллера пришло [RedButtonActions.EMPTY_CABINET], то на ОС выбрано действий "Переход в пустой кабинет".
     * После вызова метода [RedButtonService.confirmOn] все данные будут скрыты.
     *
     * Если от контроллера пришло [RedButtonActions.HIDE_MANAGEMENT], то на ОС выбрано действий "Скрытие управленческого учёта".
     * После вызова метода [RedButtonService.confirmOn] все данные сотрудников и документация будут скрыты.
     *
     * @return [Single] излучающий [RedButtonActions]
     */
    fun getAction(): Single<RedButtonActions> = Single.fromCallable { RedButtonService.selectAction() }
        .map(RedButtonActionsMapper())
        .onErrorResumeNext { Single.error(RedButtonErrorMapper().apply(it)) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * Установка значения пин-кода для открытия/закрытия данных
     * @param pinCode пин-код из 5 символов
     * @return [Single] излучающий [Unit]
     */
    fun setPinCode(pinCode: String) =
        Single.fromCallable { RedButtonService.on(pinCode) }
            .map(RedButtonDataMapper())
            .onErrorResumeNext { Single.error(RedButtonErrorMapper().apply(it)) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    /**
     * Открытие данных
     * @param pinCode пин-код из 5 символов
     * @return [Single] излучающий [Unit]
     */
    fun off(pinCode: String) = Single.fromCallable { RedButtonService.off(pinCode) }
        .onErrorResumeNext { Single.error(RedButtonErrorMapper().apply(it)) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * Закрытие данных
     * @param operationUuid глобальный идентификатор операции
     * @param smsCode код из смс
     * @param pinCode пин-код из 5 символов
     * @return [Single] излучающий [Unit]
     */
    fun on(operationUuid: String, smsCode: Int, pinCode: String) =
        Single.fromCallable { RedButtonService.confirmOn(operationUuid, smsCode, pinCode) }
            .onErrorResumeNext { Single.error(RedButtonErrorMapper().apply(it)) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    /**
     * Подписка на событие необходимости перезапустить приложение
     * @param callback коллбек
     */
    fun subscribeOnRefreshApp(callback: (RedButtonStubType) -> Unit) {
        RedButtonEventsManager.instance().setRbCallback(object : RedButtonEventCallback() {

            override fun onNeedClearCache(p0: Short) {
                callback(RedButtonStubMapper().apply(p0.toInt()))
            }
        })
    }
}