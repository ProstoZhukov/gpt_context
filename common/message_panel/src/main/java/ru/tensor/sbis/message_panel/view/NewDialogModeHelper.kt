package ru.tensor.sbis.message_panel.view

import io.reactivex.internal.disposables.DisposableContainer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelDataControls

/**
 * Включение/отключение режима нового сообщения при записи/отмене аудио сообщения
 *
 * @author vv.chekurda
 */
internal class NewDialogModeHelper(
    liveData: MessagePanelDataControls,
    disposer: DisposableContainer
) {
    private var previousDialogModeEnabled = BehaviorSubject.create<Boolean>()
    private var recordState = PublishSubject.create<Boolean>()
    private var recordStarted = recordState.filter { it }
    private var recordCompleted = recordState.filter { !it }

    init {
        // захватываем актуальное значение режима "новый диалог" в момент начала записи
        val onRecordStarted = liveData.newDialogModeEnabled.sample(recordStarted)
        // публикуем его в свой поток
        onRecordStarted.subscribe(previousDialogModeEnabled)
        // отключаем режим нового диалога, если он был включен и началась запись
        disposer += onRecordStarted
            .filter { it }
            .map { it.not() }
            .subscribe(liveData::newDialogModeEnabled)
        // активируем режим нового диалога, если он был включен до начала записи
        disposer += previousDialogModeEnabled
            .sample(recordCompleted)
            .filter { it }
            .subscribe(liveData::newDialogModeEnabled)
    }

    fun onRecordStarted() {
        recordState.onNext(true)
    }

    fun onRecordCompleted() {
        recordState.onNext(false)
    }
}