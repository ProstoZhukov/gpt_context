package ru.tensor.sbis.red_button.interactor

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.red_button.data.RedButtonStubType
import ru.tensor.sbis.red_button.events.RedButtonNeedRefreshApp
import javax.inject.Inject

/**
 * Интерактор для работы заглушки [RedButtonStubActivity]
 * @property redButtonPreferencesInteractor интерактор для работы с [SharedPreferences]
 * @property rxBus требуется для подписки на событие необходимости перезагрузки приложения
 *
 * @author ra.stepanov
 */
@Suppress("KDocUnresolvedReference")
class RedButtonStubInteractor @Inject constructor(
    private val preferencesInteractor: RedButtonPreferencesInteractor,
    private val rxBus: RxBus
) : LifecycleObserver {

    private var disposable = CompositeDisposable()

    /**
     * Получение значения для заглушки и её открытие, если заглушка не требуется запускает делегат noNeedStub
     * @param needStub делегат вызываемый если требуется заглушка
     * @param noNeedStub делегат вызываемый если не требуется заглушка
     */
    fun openStubIfNeedOrRunCode(activity: ComponentActivity, needStub: NeedStub, noNeedStub: NoNeedStub) {
        activity.lifecycle.addObserver(this)
        disposable.add(
            preferencesInteractor.getStubPreference()
                .subscribe(Consumer {
                    if (it == RedButtonStubType.NO_STUB) {
                        noNeedStub()
                        subscribeOnRxBus(needStub)
                    } else {
                        needStub(it)
                    }
                })
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun clear() {
        disposable.dispose()
    }

    private fun subscribeOnRxBus(needStub: NeedStub) {
        disposable.add(rxBus.subscribe(RedButtonNeedRefreshApp::class.java).subscribe { needStub(it.stubType) })
    }
}

/** Алиас для лямбды вызываемой если требуется заглушка */
typealias NeedStub = (RedButtonStubType) -> Unit
/** Алиас для лямбды вызываемой если не требуется заглушка */
typealias NoNeedStub = () -> Unit