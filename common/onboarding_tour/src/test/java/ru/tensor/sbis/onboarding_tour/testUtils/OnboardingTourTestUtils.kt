package ru.tensor.sbis.onboarding_tour.testUtils

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.flow.flowOf
import org.mockito.kotlin.mock
import ru.tensor.sbis.onboarding_tour.contract.OnboardingTourDependency
import ru.tensor.sbis.onboarding_tour.domain.TourCreatorImpl
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.PageCommand
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.TourPriority
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BannerButtonType
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

internal fun Context.buildSimpleTourProvider(
    name: String = "name",
    priority: TourPriority = TourPriority.NORMAL,
    pageCount: Int = 1,
    hiddenPositions: List<Int> = emptyList(),
    buttonTitle: Int = ResourcesCompat.ID_NULL,
    buttonBannerType: BannerButtonType = BannerButtonType.NONE,
    @StringRes contentTitle: Int = ResourcesCompat.ID_NULL,
    @StringRes contentDescription: Int = ResourcesCompat.ID_NULL,
    @DrawableRes contentImage: Int = ResourcesCompat.ID_NULL,
    @StringRes termsCaption: Int = ResourcesCompat.ID_NULL,
    termsLinks: List<String> = emptyList(),
    dependency: OnboardingTourDependency = mock()
): OnboardingTourProvider =
    TourCreatorImpl(this, dependency)
        .createProvider(OnboardingTour.Name(name), priority) {
            defaultBanner {
                buttonType = buttonBannerType
            }
            rules { showOnlyOnce = true }
            repeat(pageCount) {
                page {
                    title = contentTitle
                    description = contentDescription
                    image = contentImage
                    button {
                        title = buttonTitle
                        onClickForward { _, _ ->
                            flowOf(PageCommand.ResultantAction.GO_AHEAD)
                        }
                    }
                    terms {
                        caption = termsCaption
                        links = termsLinks
                    }
                    checkIsRequired {
                        !hiddenPositions.contains(it)
                    }
                }
            }
        }

internal fun <T : Any> Any.getField(name: String): T {
    val f = javaClass.getDeclaredField(name)
    f.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    return f.get(this) as T
}

internal fun Any.setField(name: String, value: Any) {
    val f = javaClass.getDeclaredField(name)
    f.isAccessible = true
    f.set(this, value)
}

internal const val SOURCE_TERM_CAPTION = "By continuing, you agree to %%the terms of service%% and %%privacy policy%%"