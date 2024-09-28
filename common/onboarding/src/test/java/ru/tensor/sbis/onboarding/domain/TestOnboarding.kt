package ru.tensor.sbis.onboarding.domain

import org.mockito.kotlin.spy
import io.reactivex.Observable
import ru.tensor.sbis.verification_decl.permission.PermissionScope
import ru.tensor.sbis.onboarding.contract.providers.content.BasePage
import ru.tensor.sbis.onboarding.contract.providers.content.FeaturePage
import ru.tensor.sbis.onboarding.contract.providers.content.NoPermissionPage
import ru.tensor.sbis.onboarding.contract.providers.content.Onboarding
import ru.tensor.sbis.onboarding.domain.TestPermissionScope.TEST_1
import ru.tensor.sbis.onboarding.domain.TestPermissionScope.TEST_2

internal object TestOnboarding {

    const val headerTextId = 1
    const val headerLogoId = 2

    const val firstPageDescId = 1_1
    const val firstPageImageId = 1_2

    const val noPermissionPageDescId = 4_1
    const val noPermissionPageImageId = 4_2

    val customAction = spy<((Boolean) -> Unit) -> Unit> {}

    const val systemPermissionsName1 = "System permission 1"
    const val systemPermissionsName2 = "System permission 2"

    val customButtonAction = spy<() -> Unit> {}

    val onboarding: Onboarding by lazy {
        Onboarding {
            backPressedSwipe = true
            header {
                textResId = headerTextId
                imageResId = headerLogoId
            }
            page {
                descriptionResId = firstPageDescId
                imageResId = firstPageImageId
            }
            page {
                descriptionResId = 2_1
                imageResId = 2_3
                button {
                    action = customButtonAction
                    textResId = 2_4
                    defaultAction = false
                }
            }
            page {
                descriptionResId = 3_1
                imageResId = 3_2
                permission(systemPermissionsName1)
                permission(systemPermissionsName2)
                action {
                    byLeave = true
                    execute = customAction
                }
                defaultButton = true
            }
            noPermissionPage {
                permissionScopes(TEST_1, TEST_2)
                descriptionResId = noPermissionPageDescId
                imageResId = noPermissionPageImageId
                inclusiveStrategy = false
            }
        }
    }

    fun getOnboardingObservable() = Observable.just(onboarding)

    fun getSimplePage() = onboarding.pages[0] as BasePage

    fun getCustomButtonPage() = onboarding.pages[1] as BasePage

    fun getFinalPage() = onboarding.pages
            .filterIsInstance(FeaturePage::class.java).last()

    fun getStubPage() = onboarding.pages
            .filterIsInstance(NoPermissionPage::class.java).first()

    fun getFeatureCount() = onboarding.pages.filterIsInstance<FeaturePage>().size
}

internal enum class TestPermissionScope(
        override val id: String
) : PermissionScope {
    TEST_1("test_scope_1"),
    TEST_2("test_scope_2")
}