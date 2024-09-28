package ru.tensor.sbis.message_panel.viewModel.livedata.hint

import androidx.annotation.StringRes
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

/**
 * Класс - реализация подсказки в поля ввода сообщения.
 * @author vv.chekurda
 */
internal class MessagePanelHintImpl : MessagePanelHint {

    @StringRes
    private var stateHint: Int

    private val hint: Subject<Int>

    /**@SelfDocumented*/
    override var hintConfig = MessagePanelHintConfig()

    /**@SelfDocumented*/
    override val messageHint: Observable<Int>

    init {
        stateHint = hintConfig.disabledStateHint
        hint = BehaviorSubject.createDefault(stateHint)
        messageHint = hint.distinctUntilChanged()
    }

    /**@SelfDocumented*/
    override fun setHint(hintRes: Int) {
        hint.onNext(hintRes)
    }

    /**@SelfDocumented*/
    override fun updateHintConfig(config: MessagePanelHintConfig) {
        val isPreviousEnabled = stateHint == hintConfig.enabledStateHint
        hintConfig = config
        stateHint = if (isPreviousEnabled) hintConfig.enabledStateHint else hintConfig.disabledStateHint
        hint.onNext(stateHint)
    }

    /**@SelfDocumented*/
    override fun applyDisabledHint() {
        stateHint = hintConfig.disabledStateHint
        hint.onNext(stateHint)
    }

    /**@SelfDocumented*/
    override fun applyEnabledHint() {
        stateHint = hintConfig.enabledStateHint
        hint.onNext(stateHint)
    }

    /**@SelfDocumented*/
    override fun resetHint() {
        hint.onNext(stateHint)
    }
}