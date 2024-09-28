package ru.tensor.sbis.onboarding_tour.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.arkivanov.mvikotlin.core.view.MviView
import com.arkivanov.mvikotlin.core.view.ViewEvents
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.PageCommand
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.RationaleCallback
import ru.tensor.sbis.onboarding_tour.ui.store.OnboardingTourStore
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.BannerCommand
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BannerButtonType

/**
 * Интерфейс вью через который поставляются новые намерения и обновляется состояние, описан
 * через комбинацию [ViewRenderer] и [ViewEvents].
 *
 * Используем отдельное состояние [Model] и события [Event], а не намерения и визуализацию
 * состояния [OnboardingTourStore] во избежание связи View и Store.
 */
internal interface TourView : MviView<TourView.Model, TourView.Event> {

    /**
     * Вью-модель потребляемая вью.
     *
     * @param requirePermissions требуется ли выполнение запроса разрешений перед уходом со страницы тура
     * @param isTransitionBlocked заблокирован ли переход на следующую страницу тура
     * @param isSwipeSupported поддерживается ли перелистывание свайпом
     * @param isSwipeClosable поддерживается ли закрытие свайпом
     * @param backgroundEffect [BackgroundEffect]
     */
    data class Model(
        val position: Int,
        val arePermissionsChecked: Boolean,
        val requirePermissions: Boolean,
        val isTransitionBlocked: Boolean,
        val isSwipeSupported: Boolean = false,
        val isSwipeClosable: Boolean,
        val backgroundEffect: BackgroundEffect,
        val passages: List<Passage>
    ) : Passage by passages.getOrNull(position) ?: Passage.empty {

        /**@SelfDocumented */
        val count get() = passages.size

        /**@SelfDocumented */
        val isEmpty get() = count == 0
    }

    /** Интерфейс описывающий переход вью. */
    interface Passage {
        val bannerLogo: SbisLogoType

        val bannerButton: BannerButtonType

        val bannerCommand: BannerCommand?

        @get:StringRes
        val terms: Int

        val termsLinks: List<String>

        @get:StringRes
        val buttonTitle: Int?

        val buttonIcon: SbisMobileIcon.Icon?

        val buttonStyle: SbisButtonStyle?

        val buttonTitlePosition: HorizontalPosition

        @get:DrawableRes
        val imageResId: Int?

        @get:StringRes
        val titleResId: Int?

        @get:StringRes
        val messageResId: Int?

        val permissions: List<String>

        val isMandatoryPermits: Boolean

        val rationaleCommand: RationaleCallback?

        val transitionCommand: PageCommand?

        companion object {
            val empty = OnboardingTourStore.StatePassage()
        }
    }

    /** Источник событий испускаемых вью. */
    sealed interface Event {

        /** Сообщить об изменении текущей страницы. */
        data class OnPageChanged(val position: Int) : Event

        /** Проверить требуемые разрешения [permissions]. */
        data class CheckPermissions(
            val position: Int,
            val permissions: List<String>
        ) : Event

        /** Запросить требуемые разрешения [permissions]. */
        data class RequestPermissions(
            val position: Int,
            val permissions: List<String>,
            val rationaleCommand: RationaleCallback?
        ) : Event

        /** Нажатие на ссылку из тура. */
        data class OnLinkClick(val url: String) : Event

        /** Нажатие на кнопку "Далее" для выполнения команды [command] экрана тура с позицией [position]. */
        data class OnCommandClick(
            val position: Int,
            val command: PageCommand,
            val isLastPage: Boolean
        ) : Event

        /** Нажатие на закрытие тура. */
        object OnCloseClick : Event
    }
}