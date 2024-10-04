package ru.tensor.sbis.review

import ru.tensor.sbis.verification_decl.login.CurrentAccount
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.review.decl.ReviewFeature
import ru.tensor.sbis.review.decl.ReviewFeatureImpl
import ru.tensor.sbis.review.triggers.AndTrigger
import ru.tensor.sbis.review.triggers.OrTrigger
import ru.tensor.sbis.review.triggers.Trigger

/**
 * Плагин для сервиса оценок приложения
 *
 * @author ma.kolpakov
 */
object ReviewPlugin : BasePlugin<ReviewCustomizationOptions>() {
    private const val noTriggerErrorMessage =
        "If you are using a ReviewService, define triggers in customizationOptions of ReviewPlugin"
    private var reviewLifecycleCallback = ReviewLifecycleCallback()

    private var currentAccountProvider: FeatureProvider<CurrentAccount>? = null

    private val reviewFeature by lazy {
        ReviewFeatureImpl(
            application,
            checkNotNull(customizationOptions.trigger) { noTriggerErrorMessage },
            reviewLifecycleCallback,
            customizationOptions.reviewType.action,
            currentAccountProvider?.get()
        )
    }
    override val api: Set<FeatureWrapper<out Feature>> =
        setOf(FeatureWrapper(ReviewFeature::class.java) { reviewFeature })

    override val dependency: Dependency = Dependency.Builder()
        .optional(CurrentAccount::class.java) { currentAccountProvider = it }
        .build()

    override val customizationOptions = ReviewCustomizationOptions()

    override fun doAfterInitialize() {
        application.registerActivityLifecycleCallbacks(reviewLifecycleCallback)
    }
}

/**
 * Опции компонента оценок
 */
class ReviewCustomizationOptions {
    /**
     * Триггер по которому будет показан inAppReview. Если необходимо использовать набор триггеров используйте в
     * качестве корневого триггера [OrTrigger] или [AndTrigger]
     */
    var trigger: Trigger = OrTrigger()

    /**
     * Тип оценочного сервиса который будет показан при наступлении положительного события
     */
    var reviewType = ReviewType.GOOGLE
}
