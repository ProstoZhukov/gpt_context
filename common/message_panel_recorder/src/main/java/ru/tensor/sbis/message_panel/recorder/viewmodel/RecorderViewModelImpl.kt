package ru.tensor.sbis.message_panel.recorder.viewmodel

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.internal.disposables.EmptyDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.message_panel.recorder.RECORDER_HINT_HIDE_DELAY
import ru.tensor.sbis.message_panel.recorder.util.DEFAULT_TIME
import ru.tensor.sbis.message_panel.recorder.util.toTimeString
import ru.tensor.sbis.message_panel.recorder.viewmodel.listener.RecordViewModelListener
import ru.tensor.sbis.recorder.decl.RecordPermissionMediator
import ru.tensor.sbis.recorder.decl.RecordRecipientMediator
import ru.tensor.sbis.recorder.decl.RecorderService
import java.util.concurrent.TimeUnit

/**
 * @author vv.chekurda
 * @since 7/25/2019
 */
internal class RecorderViewModelImpl(
    private val service: RecorderService,
    private val permissionMediator: RecordPermissionMediator,
    private val recipientMediator: RecordRecipientMediator,
    private val listener: RecordViewModelListener,
    mainThreadScheduler: Scheduler = AndroidSchedulers.mainThread(),
    private val computationScheduler: Scheduler = Schedulers.computation()
) : RecorderViewModel {

    private val hintSubject = BehaviorSubject.create<Boolean>()
    private val stateSubject = BehaviorSubject.createDefault(RecorderIconState.DEFAULT)
    private val timeSubject = BehaviorSubject.createDefault(DEFAULT_TIME)
    private val disposable: Disposable
    private val timeDisposable = SerialDisposable()

    init {
        listener.onShowHint(false)
        disposable = CompositeDisposable(
            hintSubject
                .throttleWithTimeout(RECORDER_HINT_HIDE_DELAY, TimeUnit.MILLISECONDS, computationScheduler)
                .observeOn(mainThreadScheduler)
                .subscribe(listener::onShowHint),
            stateSubject
                .skip(1)
                .subscribe(listener::onStateChanged),
            timeSubject
                .observeOn(mainThreadScheduler)
                .subscribe(listener::onTimeChanged),
            timeDisposable
        )
    }

    override fun onIconClick() {
        permissionMediator.withPermission {
            listener.onShowHint(true)
            hintSubject.onNext(false)
        }
    }

    override fun onIconLongClick(): Boolean {
        permissionMediator.withPermission {
            recipientMediator.withRecipient {
                stateSubject.onNext(RecorderIconState.RECORD)
                timeDisposable.set(
                    Observable
                        .interval(1L, TimeUnit.SECONDS, computationScheduler)
                        .map(Long::inc)
                        .map(Long::toTimeString)
                        .subscribe(timeSubject::onNext)
                )
                service.startRecord()
            }
        }
        return stateSubject.value == RecorderIconState.RECORD
    }

    override fun onIconReleased() {
        when (stateSubject.value) {
            RecorderIconState.DEFAULT -> throw IllegalStateException("Record is not started")
            RecorderIconState.RECORD  -> {
                service.stopRecord()
                resetState()
            }
            RecorderIconState.CANCEL  -> {
                service.cancelRecord()
                resetState()
            }
            else -> {}
        }
    }

    override fun onOutOfIcon(fingerOutOfIcon: Boolean) {
        when (stateSubject.value) {
            RecorderIconState.DEFAULT -> throw IllegalStateException("Record is not started")
            RecorderIconState.RECORD  -> {
                if (fingerOutOfIcon) {
                    stateSubject.onNext(RecorderIconState.CANCEL)
                }
            }
            RecorderIconState.CANCEL  -> {
                if (!fingerOutOfIcon) {
                    stateSubject.onNext(RecorderIconState.RECORD)
                }
            }
            else -> {}
        }
    }

    override fun isDisposed(): Boolean = disposable.isDisposed

    override fun dispose() = disposable.dispose()

    private fun resetState() {
        timeDisposable.set(EmptyDisposable.INSTANCE)
        timeSubject.onNext(DEFAULT_TIME)
        stateSubject.onNext(RecorderIconState.DEFAULT)
    }
}