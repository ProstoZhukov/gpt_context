package ru.tensor.sbis.red_button.ui.settings_item

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Consumer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.red_button.R
import ru.tensor.sbis.red_button.data.RedButtonActions
import ru.tensor.sbis.red_button.data.RedButtonError
import ru.tensor.sbis.red_button.data.RedButtonOpenAction
import ru.tensor.sbis.red_button.data.RedButtonState
import ru.tensor.sbis.red_button.events.RedButtonStateRefresh
import ru.tensor.sbis.red_button.interactor.RedButtonStatesInteractor
import ru.tensor.sbis.settings_screen_common.content.ItemVMImpl
import ru.tensor.sbis.settings_screen_decl.vm.ItemVM
import javax.inject.Inject
import ru.tensor.sbis.common.R as RCommon

/**
 * @property rxBus Требуется чтобы поймать событие обновления состояния красной кнопки
 * @property stateInteractor Интерактор для обработки кликов по преференсу
 * @property resourceProvider Провайдер ресурсов для доступа к строкам
 *
 * @author ra.stepanov
 */
class RedButtonItemViewModel @Inject constructor(
    private val rxBus: RxBus,
    private val stateInteractor: RedButtonStatesInteractor,
    private val resourceProvider: ResourceProvider
) : ItemVM by ItemVMImpl() {

    private val disposable = CompositeDisposable()
    private val _errors = PublishSubject.create<String>()
    private val _openAction = PublishSubject.create<RedButtonOpenAction>()
    private val _title = BehaviorSubject.create<String>()
    private val _isVisible = BehaviorSubject.create<Boolean>()
    private val _visibility = MutableLiveData(View.GONE)
    private val _titleText = MutableLiveData("")

    /**
     * Геттеры событий для того, чтобы не протекала логика
     */
    val errors: Observable<String> = _errors
    val openAction: Observable<RedButtonOpenAction> = _openAction
    val title: Observable<String> = _title
    val isViewVisible: Observable<Boolean> = _isVisible
    val visibility: LiveData<Int> = _visibility
    val titleText: LiveData<String> = _titleText

    init {
        with(disposable) {
            add(rxBus.subscribe(RedButtonStateRefresh::class.java).subscribe { newState: RedButtonStateRefresh ->
                refreshByState(newState.redButtonState)
            })
            add(stateInteractor.getStateDirectly().subscribe({ refreshByState(it) }, { handleError(it) }))
            add(_isVisible.subscribe { if (it) show() else hide() })
            add(_title.subscribe { _titleText.value = it })
        }
        refreshByState(stateInteractor.getState())
    }

    /**
     * Очистка ресурсов
     */
    fun clear() {
        disposable.dispose()
    }

    /**
     * Обработчик клика по преференсу
     */
    fun onPreferenceClick() {
        var disposable = Disposables.disposed()
        disposable = stateInteractor.getStateDirectly()
            .doAfterSuccess { disposable.dispose() }
            .subscribe({ handleStateClick(it) }, { handleError(it) })
    }

    /**
     * Обновление видимости и текста преференса в зависимости от переданного состояния
     * @param state Состояние кнопки
     */
    private fun refreshByState(state: RedButtonState) {
        _isVisible.onNext(state != RedButtonState.ACCESS_LOCK && state != RedButtonState.ACCESS_DENIED)
        _title.onNext(getPreferenceText(state))
    }

    /**
     * Получение текста для пункта настроек в зависимости от состояния "Красной Кнопки"
     */
    private fun getPreferenceText(state: RedButtonState): String {
        val stringId = when (state) {
            RedButtonState.CLICK -> R.string.red_button_title_open
            RedButtonState.OPEN_IN_PROGRESS -> R.string.red_button_title_open_progress
            RedButtonState.CLOSE_IN_PROGRESS -> R.string.red_button_title_close_progress
            RedButtonState.NOT_CLICK -> R.string.red_button_title_close
            RedButtonState.ACCESS_LOCK -> RCommon.string.common_no_permission_error
            RedButtonState.ACCESS_DENIED -> RCommon.string.common_access_error
        }
        return resourceProvider.getString(stringId)
    }

    /**
     * Обработка ошибки запроса состояния контроллера
     * @param error объект ошибки
     */
    private fun handleError(error: Throwable) {
        if (error is RedButtonError.NoInternet) {
            _errors.onNext(resourceProvider.getString(R.string.red_button_network_error))
        }

        if (error is RedButtonError.General) {
            _errors.onNext(error.errorMessage)
            return
        }

        error.localizedMessage?.let { if (it.isNotEmpty()) _errors.onNext(it) }
    }

    /**
     * Обработка состояния кнопки при клике на неё. Отправляет делегату действие, которое требуется выполнить
     * или открывает диалоговое окно
     * @param state Состояние кнопки
     */
    private fun handleStateClick(state: RedButtonState) {
        if (state == RedButtonState.CLICK) {
            _openAction.onNext(RedButtonOpenAction.OPEN_FRAGMENT)
        } else if (state == RedButtonState.NOT_CLICK) {
            openDialog()
        }
    }

    /**
     * Получает данные с контроллера и отправляет событие открытия диалогового окна
     */
    private fun openDialog() {
        var disposable: Disposable? = null
        disposable = stateInteractor.getAction()
            .map { if (it == RedButtonActions.HIDE_MANAGEMENT) RedButtonOpenAction.OPEN_DIALOG_MANAGEMENT else RedButtonOpenAction.OPEN_DIALOG_EMPTY_CABINET }
            .subscribe(Consumer {
                _openAction.onNext(it)
                disposable?.dispose()
            })
    }
}