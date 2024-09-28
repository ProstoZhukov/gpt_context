package ru.tensor.sbis.communicator.sbis_conversation.utils

import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseLifecycleObserver

/**
 * Класс предназначенный для распознавания истинного события жизнненого цикла onResume
 * в рамках "великолепных" windowIsTranslucent стилей активностей для "неподражаемого" ios свайпбэка
 */
internal class ResumeEventHelper(private val isTablet: Boolean) : BaseLifecycleObserver {

    /**
     * Флаг констатирующий факт события onResume от появления активности
     * на переднем плане для взаимодействия с пользователем
     */
    var isResumeAfterPause: Boolean = false
    private var isInPause: Boolean = false

    /** @SelfDocumented */
    override fun viewIsStarted() {
        if (!isTablet) {
            isInPause = false
            isResumeAfterPause = false
        }
    }

    /** @SelfDocumented */
    override fun viewIsResumed() {
        if (isInPause) {
            isResumeAfterPause = true
        }
        isInPause = false
    }

    /** @SelfDocumented */
    override fun viewIsPaused() {
        isInPause = true
        isResumeAfterPause = false
    }

    /** @SelfDocumented */
    override fun viewIsStopped() {
        if (!isTablet) {
            isInPause = false
            isResumeAfterPause = false
        }
    }
}