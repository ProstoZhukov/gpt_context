package ru.tensor.sbis.onboarding.di

import android.content.Context
import androidx.annotation.Nullable
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.permission.PermissionFeature
import ru.tensor.sbis.onboarding.contract.OnboardingDependency

/**
 * Класс для создания и инициализации [OnboardingSingletonComponent]
 *
 * @param permission для опциональной поддержки страниц заглушек об отсутствии прав на область
 * @param login для опциональной поддержки персонализации события отображения онбординга
 *
 * @author as.chadov
 */
class OnboardingSingletonComponentInitializer(
    private val context: Context,
    private val dependency: OnboardingDependency,
    @Nullable private val permission: PermissionFeature? = null,
    @Nullable private val login: LoginInterface? = null,
) {
    fun init() = createComponent()

    private fun createComponent(): OnboardingSingletonComponent =
        DaggerOnboardingSingletonComponent.factory().create(
            context = context,
            dependency = dependency,
            permissionFeature = permission,
            loginFeature = login
        )
}