package ru.tensor.sbis.version_checker.ui.mandatory

import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.mvp.presenter.BasePresenter
import ru.tensor.sbis.version_checker.data.UpdateCommand

/**
 * Интерфейс экрана принудительного обновления.
 *
 * @author as.chadov
 */
internal interface RequiredUpdateContract {

    /** Вью экрана принудительного обновления обновления */
    interface View {

        /**
         * Выполнить команду обновления
         */
        fun runCommand(command: UpdateCommand): String?
    }

    /** Презентер экрана принудительного обновления обновления. */
    interface Presenter : BasePresenter<View> {

        /** Вызывается при нажатии на кнопку согласия обновиться */
        fun onAcceptUpdate()

        /** Возвращает название приложения */
        fun getAppName(): String

        /** Стиль кнопки */
        fun getButtonStyle(): SbisButtonStyle

        /** Отправка аналитики о отображении экрана */
        fun sendAnalytics()
    }
}