package ru.tensor.sbis.onboarding_tour.ui.store

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.PageCommand
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.RationaleCallback
import ru.tensor.sbis.onboarding_tour.ui.TourView
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.BannerCommand
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.DismissCommand
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BannerButtonType
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BannerButtonType.*
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * Хранилище бизнес-логики Приветственного экрана настроект приложения.
 * Устойчивые данные об UI.
 */
internal interface OnboardingTourStore :
    Store<OnboardingTourStore.Intent, OnboardingTourStore.State, OnboardingTourStore.Label> {

    /** Намерения получаемые извне и обрабатываемые [TourExecutor]. */
    sealed interface Intent {
        /** Запрос на отображение тура. */
        object ShowTour : Intent

        /** Запрос на отображение тура с учетом восстановленного состояния [state]. */
        class ReshowTour(val state: State) : Intent

        class UpdateForthcomingPages(val position: Int) : Intent

        /** Запрос на обработку результата выполнения тура [OnCloseTour]. */
        object OnCloseTour : Intent

        /** Запрос на отображение анимации тура [BackgroundEffect.DYNAMIC]. */
        object ShowBackgroundEffect : Intent

        /** Запрос на обработку изменения текущей отображаемой страницы. */
        class OnPageChanged(val position: Int) : Intent

        /** Запрос на обработку результата о предоставленных разрешениях для страницы с позицией [position]. */
        class GrantedPermissions(
            val position: Int,
            val notGranted: List<String>,
            val onRequest: Boolean
        ) : Intent

        /** Инициировать выполнение команды [command] на экране с позицией [position]. */
        class InitDeferredCommand(
            val position: Int,
            val command: PageCommand,
            val isLastPage: Boolean
        ) : Intent

        /** Запрос на мониторинг результата [flow] выполнения команды [PageCommand] экрана на позиции [position]. */
        class ObserveDeferredCommand(
            val position: Int,
            val flow: Flow<PageCommand.ResultantAction>,
            val isLastPage: Boolean
        ) : Intent

        /** Запрос на выполнение результирующего действия [action] после выполнения команды [PageCommand]. */
        class OnCommandPerformed(
            val position: Int,
            val action: PageCommand.ResultantAction
        ) : Intent
    }

    /** Широковещательное сообщение. */
    sealed interface Label {

        /**
         * Сообщение об запуске [PageCommand] на странице с позицией [position] инициированное пользователем если [byUser] истинно.
         */
        data class InitiateCommand(
            val position: Int,
            val command: PageCommand,
            val isLastPage: Boolean,
            val byUser: Boolean
        ) : Label

        /** Сообщение с результирующим действием [action] после выполнения [PageCommand] на странице с позицией [position]. */
        data class PageCommandResult(
            val position: Int,
            val action: PageCommand.ResultantAction,
            val isLastPage: Boolean
        ) : Label
    }

    /**
     * Состояние тура [OnboardingTourStore].
     *
     * @param pageCount кол-во страниц в туре
     * @param pagePosition позиция текущей страницы в туре
     * @param pageId id текущей страницы в туре
     * @param requirePermissions требуется ли выполнение запроса разрешений перед уходом со страницы тура
     * @param requireCommand требуется ли выполнение команды перед уходом со страницы тура
     * @param hasLaunchedRestorableCommand true если онбординг находится в ожидании обработки рестартуемого [StatePassage.transitionCommand]
     * @param arePermissionsChecked имела ли место уже проверка актуальности требуемых разрешений для страницы тура
     * @param isSwipeSupported поддержка закрытия смахиванием последней страницы
     * @param swipeCloseable поддерживается ли перелистывание свайпом
     * @param backgroundEffect [BackgroundEffect]
     */
    @Parcelize
    data class State(
        val name: OnboardingTour.Name? = null,
        val pageCount: Int = 0,
        val pagePosition: Int = 0,
        val pageId: Int = 0,
        val requirePermissions: Boolean = false,
        val requireCommand: Boolean = false,
        val hasLaunchedRestorableCommand: Boolean = false,
        val arePermissionsChecked: Boolean = false,
        val isSwipeSupported: Boolean = false,
        val swipeCloseable: Boolean = false,
        val backgroundEffect: BackgroundEffect = BackgroundEffect.NONE,
        @IgnoredOnParcel val passages: List<StatePassage> = emptyList(),
        @IgnoredOnParcel val dismissCommand: DismissCommand? = null
    ) : Parcelable {

        /**@SelfDocumented */
        val isInLastPassage: Boolean get() = pagePosition + 1 == pageCount
    }

    /**
     * Реализация подсостояния перехода по туру [OnboardingTourStore].
     */
    data class StatePassage(
        val id: Int = 0,
        val position: Int = 0,
        override val bannerLogo: SbisLogoType = SbisLogoType.Empty,
        override val bannerButton: BannerButtonType = NONE,
        override val bannerCommand: BannerCommand? = null,
        override val terms: Int = ID_NULL,
        override val termsLinks: List<String> = emptyList(),
        @StringRes override val buttonTitle: Int? = null,
        override val buttonIcon: SbisMobileIcon.Icon? = null,
        override val buttonStyle: SbisButtonStyle? = null,
        override val buttonTitlePosition: HorizontalPosition = HorizontalPosition.RIGHT,
        @StringRes override val titleResId: Int? = null,
        @StringRes override val messageResId: Int? = null,
        @DrawableRes override val imageResId: Int? = null,
        override val permissions: List<String> = emptyList(),
        override val isMandatoryPermits: Boolean = false,
        override val rationaleCommand: RationaleCallback? = null,
        override val transitionCommand: PageCommand? = null
    ) : TourView.Passage

    companion object {
        /**@SelfDocumented */
        const val NAME = "OnboardingTourStore"
    }
}