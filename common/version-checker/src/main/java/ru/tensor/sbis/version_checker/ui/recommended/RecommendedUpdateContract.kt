package ru.tensor.sbis.version_checker.ui.recommended

import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.mvp.presenter.BasePresenter
import ru.tensor.sbis.version_checker.data.UpdateCommand

/**
 * @author as.chadov
 *
 * Интерфейс экрана предложения обновления
 */
internal interface RecommendedUpdateContract {

    /** Вью экрана предложения обновления. */
    interface View {

        /**
         * Выполнить команду обновления
         */
        fun runCommand(command: UpdateCommand): String?
    }

    /** Презентер экрана предложения обновления. */
    interface Presenter : BasePresenter<View> {

        /** Вызывается при нажатии на кнопку согласия обновиться. */
        fun onAcceptUpdate()

        /** Вызывается при нажатии на кнопку отложить. */
        fun onPostponeUpdate(postponedByButton: Boolean)

        /** Помечаем пропуск отображения последующих диалогов. */
        fun skipNextRecommendation()

        /** Возвращает название приложения. */
        fun getAppName(): String

        /** Стиль кнопки */
        fun getButtonStyle(): SbisButtonStyle
    }
}
