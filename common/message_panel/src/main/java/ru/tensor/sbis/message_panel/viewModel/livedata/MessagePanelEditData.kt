package ru.tensor.sbis.message_panel.viewModel.livedata

import io.reactivex.Observable
import io.reactivex.internal.disposables.DisposableContainer
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel

/**
 * Модель данных редактирования сообщения
 *
 * @author vv.chekurda
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
interface MessagePanelEditData {

    /**
     * Состояние редактирования
     * @return true, если панель находится в состоянии редактирования сообщения
     */
    val isEditingState: Boolean

    /**
     * Для подписки на состояние редактирования
     * @return true, если панель находится в состоянии редактирования сообщения
     */
    val isEditing: Observable<Boolean>
}

/**
 * Реализация [MessagePanelEditData]
 */
internal class MessagePanelEditDataImpl(
    viewModel: MessagePanelViewModel<*, *, *>,
    disposer: DisposableContainer
) : MessagePanelEditData {

    private val isEditingSubject: BehaviorSubject<Boolean> =
        BehaviorSubject.createDefault(false).apply {
            disposer += viewModel.stateMachine.isEditing.subscribe { isEditing -> onNext(isEditing) }
        }

    override val isEditingState: Boolean
        get() = isEditingSubject.value!!

    override val isEditing = isEditingSubject
}