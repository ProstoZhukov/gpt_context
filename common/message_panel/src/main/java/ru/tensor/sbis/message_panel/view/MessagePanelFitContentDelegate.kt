package ru.tensor.sbis.message_panel.view

import android.content.res.Resources
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.message_panel.helper.TextAreaHeightCalculator
import ru.tensor.sbis.message_panel.helper.TextAreaHeightFunction
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData

/**
 * Класс для определения наличия свободного места для отображения элементов панели ввода.
 * Правила определения доступности отображения определены в [SpaceForViewsAvailabilityMapper]
 */
internal class MessagePanelFitContentDelegate(
    private val spaceAvailabilityFunction: SpaceAvailabilityFunction,
    private val heightFunction: TextAreaHeightFunction
) {

    private val disposer = CompositeDisposable()
    private val panelMaxHeight = PublishSubject.create<Int>()

    constructor(resources: Resources, editTextLineHeight: Int) : this(
        SpaceForViewsAvailabilityMapper(resources, editTextLineHeight),
        TextAreaHeightCalculator(resources)
    )

    fun setPanelMaxHeight(maxHeight: Int) {
        panelMaxHeight.onNext(maxHeight)
    }

    fun bind(liveData: MessagePanelLiveData) {
        unbind()
        disposer += panelMaxHeight
            .distinctUntilChanged()
            .subscribe(liveData::setPanelMaxHeight)

        disposer += Observable.combineLatest(
            liveData.quotePanelVisible,
            liveData.recipientsVisibility,
            panelMaxHeight,
            liveData.newDialogModeEnabled,
            spaceAvailabilityFunction
        )
            .distinctUntilChanged()
            .subscribe {
                liveData.setHasSpaceForAttachments(it.hasSpaceForAttachments)
                liveData.setHasSpaceForRecipients(it.hasSpaceForRecipients)
            }

        disposer += Observable.combineLatest(
            liveData.attachmentsVisibility,
            liveData.quotePanelVisible,
            liveData.recipientsVisibility,
            panelMaxHeight,
            heightFunction
        )
            .distinctUntilChanged()
            .subscribe(liveData::setEditTextMaxHeight)
    }

    fun unbind() {
        disposer.clear()
    }
}

