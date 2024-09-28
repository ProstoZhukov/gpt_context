package ru.tensor.sbis.onboarding_tour.ui.store

import androidx.core.content.res.ResourcesCompat.ID_NULL
import com.arkivanov.mvikotlin.core.store.Reducer
import ru.tensor.sbis.onboarding_tour.data.TourPage
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect

/**
 * Компонент с редуктор-функцией принимающей [Message] и текущее состояние [OnboardingTourStore.State] от
 * [TourExecutor] и возвращающей новое состояние [OnboardingTourStore.State].
 */
internal class TourReducer : Reducer<OnboardingTourStore.State, Message> {

    override fun OnboardingTourStore.State.reduce(msg: Message): OnboardingTourStore.State =
        when (msg) {
            is Message.UpdateTour -> {
                val pages = msg.pages.map { it.toStatePage() }
                copy(
                    pageCount = msg.pageCount,
                    pagePosition = msg.pagePosition,
                    pageId = pages[msg.pagePosition].id,
                    requirePermissions = pages[msg.pagePosition].isPermissionsRequired(),
                    requireCommand = pages[msg.pagePosition].isCommandRequired(),
                    hasLaunchedRestorableCommand = msg.hasLaunchedCommand,
                    isSwipeSupported = msg.isSwipeEnabled,
                    swipeCloseable = msg.swipeCloseable,
                    backgroundEffect = msg.backgroundEffect,
                    passages = pages,
                    dismissCommand = msg.dismissCommand
                )
            }

            is Message.MoveToPosition -> copy(
                pagePosition = msg.position,
                pageId = passages[msg.position].id,
                requirePermissions = passages[msg.position].isPermissionsRequired(),
                requireCommand = passages[msg.position].isCommandRequired(),
                hasLaunchedRestorableCommand = false,
                arePermissionsChecked = false
            )

            is Message.GrantedPermissions -> copy(
                arePermissionsChecked = true,
                requirePermissions = msg.haveNotGranted
            )

            is Message.ShowAnimation -> copy(
                backgroundEffect = BackgroundEffect.DYNAMIC
            )

            is Message.OnInitiatedRestorableCommand -> copy(
                hasLaunchedRestorableCommand = true
            )
        }

    private fun TourPage.toStatePage(): OnboardingTourStore.StatePassage =
        OnboardingTourStore.StatePassage(
            id = id,
            position = position,
            bannerLogo = banner.bannerLogoType,
            bannerButton = banner.bannerButtonType,
            bannerCommand = banner.bannerCommand,
            terms = terms?.termsCaption ?: ID_NULL,
            termsLinks = terms?.termsLinks.orEmpty(),
            buttonTitle = button.titleResId.takeIf { it != ID_NULL },
            buttonIcon = button.icon,
            buttonStyle = button.style,
            buttonTitlePosition = button.titlePosition,
            titleResId = titleResId.takeIf { it != ID_NULL },
            messageResId = descriptionResId.takeIf { it != ID_NULL },
            imageResId = imageResId.takeIf { it != ID_NULL },
            permissions = permissions?.permissions.orEmpty(),
            isMandatoryPermits = permissions?.isMandatory ?: false,
            transitionCommand = button.command,
            rationaleCommand = permissions?.rationaleCommand
        )

    private fun OnboardingTourStore.StatePassage?.isPermissionsRequired(): Boolean =
        if (this == null) false else permissions.isNotEmpty()

    private fun OnboardingTourStore.StatePassage?.isCommandRequired(): Boolean =
        if (this == null) false else transitionCommand != null
}