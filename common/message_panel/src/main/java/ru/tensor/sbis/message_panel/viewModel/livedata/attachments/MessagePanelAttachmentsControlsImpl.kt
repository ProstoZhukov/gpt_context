package ru.tensor.sbis.message_panel.viewModel.livedata.attachments

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewVisibility
import ru.tensor.sbis.message_panel.helper.AttachmentsVisibilityMapper
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel

/**
 * Класс - реализация элементов управления вложениями сообщения
 * @author vv.chekurda
 */
internal class MessagePanelAttachmentsControlsImpl(
    viewModel: MessagePanelViewModel<*, *, *>
) : MessagePanelAttachmentsControls {

    /**
     * Находятся ли вложения в состоянии транзакции при редактировании
     */
    override val isAttachmentsOnEditTransaction: Boolean
        get() = attachmentsOnEditTransaction.value!!

    /**
     * Событие, которое сообщает о состоянии работы кнопки вложений - включена или нет
     */
    override val attachmentsButtonEnabled: Observable<Boolean> =
        viewModel.stateMachine.isEnabled.toObservable()

    /**
     * Признак доступности опции удаления на вложениях.
     * По умолчанию включена, в некоторых сценариях редактирование состава вложений не должно быть доступно.
     */
    override val attachmentsDeletable = BehaviorSubject.createDefault(true)

    /**
     * Признак доступности опции возобновления загрузки вложений.
     * По умолчанию включена, в некоторых сценариях возобновление недоступно.
     */
    override val attachmentsRestartable = BehaviorSubject.createDefault(true)

    /**
     * Признак доступности опции показа ошибки при загрузке вложений.
     * По умолчанию включена, в некоторых сценариях ошибку показывать не нужно.
     */
    override val attachmentsErrorVisible = BehaviorSubject.createDefault(true)

    private val canAddAttachments = BehaviorSubject.createDefault(true)
    private val forceHideAttachmentsButton = BehaviorSubject.createDefault(false)
    private val attachmentsOnEditTransaction = BehaviorSubject.createDefault(false)

    /**
     * Событие, которое сообщает о состоянии видимости кнопки вложений
     */
    override val attachmentsButtonVisible: Observable<Boolean> =
        Observable.combineLatest(
            canAddAttachments,
            forceHideAttachmentsButton
        ) { canAdd, forceHide -> canAdd && !forceHide }

    /**
     * Событие, которое сообщает о состоянии видимости панели вложений
     */
    override val attachmentsVisibility: Observable<AttachmentsViewVisibility> by lazy {
        val liveData = viewModel.liveData
        val keyboardState = Observable.just(false)
            .concatWith(liveData.hasFocus)

        // все компоненты должны предоставить значения по умолчанию
        Observable.combineLatest(
            keyboardState,
            liveData.hasAttachments,
            liveData.hasSpaceForAttachments,
            liveData.panelMaxHeight,
            liveData.isLandscape,
            AttachmentsVisibilityMapper(viewModel.resourceProvider)
        )
            .distinctUntilChanged()
    }

    /** @SelfDocumented */
    override fun showAttachmentsButton(show: Boolean) {
        canAddAttachments.onNext(show)
    }

    /** @SelfDocumented */
    override fun forceChangeAttachmentsButtonVisibility(isVisible: Boolean) {
        forceHideAttachmentsButton.onNext(!isVisible)
    }

    override fun setAttachmentsDeletable(isRemovable: Boolean) {
        attachmentsDeletable.onNext(isRemovable)
    }

    override fun setAttachmentsRestartable(isRestartable: Boolean) {
        attachmentsRestartable.onNext(isRestartable)
    }

    override fun setAttachmentsErrorVisible(isErrorVisible: Boolean) {
        attachmentsErrorVisible.onNext(isErrorVisible)
    }

    override fun setAttachmentsInEditTransaction(isEditTransaction: Boolean) {
        attachmentsOnEditTransaction.onNext(isEditTransaction)
    }
}