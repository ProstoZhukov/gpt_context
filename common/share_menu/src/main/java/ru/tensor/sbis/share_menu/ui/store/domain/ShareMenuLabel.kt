package ru.tensor.sbis.share_menu.ui.store.domain

import android.content.Context
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.theme.res.SbisString
import ru.tensor.sbis.share_menu.utils.base_mvi.executor.LabelUseCase
import ru.tensor.sbis.share_menu.ui.view.ShareMenuContentDelegate
import ru.tensor.sbis.share_menu.ui.view.ShareMenuRouter
import ru.tensor.sbis.share_menu.ui.view.ShareMenuView
import ru.tensor.sbis.share_menu.utils.ShareAnalyticsHelper

/**
 * Широковещательное сообщение компонента меню для "поделиться".
 *
 * @author vv.chekurda
 */
internal sealed interface Label : LabelUseCase<Label.Environment> {

    /**
     * Среда для обработки [Label].
     */
    class Environment(
        val view: ShareMenuView,
        val router: ShareMenuRouter,
        val contentDelegate: ShareMenuContentDelegate,
        val analyticsHelper: ShareAnalyticsHelper
    )

    /** Показать контейнер меню. */
    object ShowMenuContainer : Label {
        override fun perform(env: Environment) {
            env.view.showMenuContainer()
        }
    }

    /** Показать сообщение об ошибке. */
    class ShowErrorMessage(
        private val message: SbisString,
        private val withFinish: Boolean
    ) : Label {
        override fun perform(env: Environment) {
            env.view.showErrorMessage(message = message)
            if (withFinish) env.router.finishTask()
        }
    }

    /** Обновить нижний отступ. */
    class UpdateBottomOffset(private val offset: Int) : Label {
        override fun perform(env: Environment) {
            env.contentDelegate.updateBottomOffset(offset = offset)
        }
    }

    /** Логировать событие для аналитики. */
    class LogAnalyticEvent(
        private val name: String,
        private val isQuickShare: Boolean
    ) : Label {
        override fun perform(env: Environment) {
            env.analyticsHelper.logEvent(name, isQuickShare)
        }
    }

    /** Широковещательные сообщения навигации. */
    sealed interface NavigationLabel : Label {

        /** Показать контенет [fragment]. */
        class ShowContent(private val fragment: Fragment) : NavigationLabel {
            override fun perform(env: Environment) {
                env.router.setContent(fragment = fragment)
            }
        }

        /** Открыть экран. */
        class OpenScreen(
            private val intentCreator: (Context) -> android.content.Intent
        ) : NavigationLabel {
            override fun perform(env: Environment) {
                env.router.openScreen(intentCreator = intentCreator)
            }
        }

        /** Обработать нажатие кнопки назад. */
        object HandleBackPressed : NavigationLabel {
            override fun perform(env: Environment) {
                env.router.onBackPressed()
            }
        }

        /** Завершить процесс, в котором отображается меню. */
        object FinishTask : NavigationLabel {
            override fun perform(env: Environment) {
                env.router.finishTask()
            }
        }
    }
}