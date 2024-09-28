package ru.tensor.sbis.share_menu.ui.store.domain.executor.use_case

import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.share_menu.R
import ru.tensor.sbis.share_menu.utils.base_mvi.executor.BaseExecutorUseCase
import ru.tensor.sbis.share_menu.ui.store.domain.Label
import ru.tensor.sbis.share_menu.ui.store.domain.Message
import ru.tensor.sbis.share_menu.ui.store.domain.State
import ru.tensor.sbis.verification_decl.login.LoginInterface
import javax.inject.Inject

/**
 * Use-case для обработки показа экрана логина для меню шаринга.
 *
 * @author vv.chekurda
 */
internal class ShowLoginUseCase @Inject constructor(
    private val loginInterface: LoginInterface
) : BaseExecutorUseCase<State, Message, Label>() {

    /**
     * Показать экран логина.
     * @return признак авторизованности пользователя.
     */
    fun showLoginScreen(): Boolean =
        if (!loginInterface.isAuthorized) {
            publish(
                Label.ShowErrorMessage(
                    message = PlatformSbisString.Res(R.string.share_menu_authorization_required_message),
                    withFinish = false
                )
            )
            loginInterface.startLoginActivity(true)
            publish(Label.NavigationLabel.FinishTask)
            false
        } else {
            true
        }
}