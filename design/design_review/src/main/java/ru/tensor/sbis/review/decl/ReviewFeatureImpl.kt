package ru.tensor.sbis.review.decl

import android.content.Context
import ru.tensor.sbis.verification_decl.login.CurrentAccount
import ru.tensor.sbis.review.ActivityProvider
import ru.tensor.sbis.review.ReviewEvent
import ru.tensor.sbis.review.ReviewService
import ru.tensor.sbis.review.action.GooglePlayReviewAction
import ru.tensor.sbis.review.action.ReviewAction
import ru.tensor.sbis.review.triggers.Trigger
import timber.log.Timber

/**
 * Контракт сервиса оценок в приложении
 *
 * @author ma.kolpakov
 */
class ReviewFeatureImpl internal constructor(
    appContext: Context,
    trigger: Trigger,
    activityProvider: ActivityProvider,
    reviewAction: ReviewAction = GooglePlayReviewAction(),
    accountProvider: CurrentAccount? = null
) : ReviewFeature {

    private var reviewService = ReviewService(appContext, trigger, activityProvider, reviewAction, accountProvider)

    override val reviewState = reviewAction.reviewState

    /**
     * Установить правила для предложения оценить приложение
     */
    @Deprecated(
        "Больше не используется " +
            "удалить после https://online.sbis.ru/opendoc.html?guid=774306ca-fe30-4ba8-927c-083c13fcea13",
        replaceWith = ReplaceWith("ReviewPlugin.customizationOptions.trigger")
    )
    override fun registerTrigger(trigger: Trigger) {
        Timber.w(
            "Триггер не был зарегистрирован, " +
                "теперь необходимо предавать триггер в опции плагина при его регистрации"
        )
    }

    /**
     * Установить правила для предложения оценить приложение
     */
    @Deprecated(
        "Больше не используется " +
            "удалить после https://online.sbis.ru/opendoc.html?guid=774306ca-fe30-4ba8-927c-083c13fcea13"
    )
    override fun unRegisterTrigger(trigger: Trigger) {
        Timber.w(
            "Больше нельзя удалять триггеры из системы " +
                "теперь триггеры настраиваются один раз при создании плагина"
        )
    }

    /**
     * Опубликовать событие [eventEnum] с задержкой [delay] в систему оценок
     */
    override fun <T> onEvent(eventEnum: T, delay: Long) where T : ReviewEvent, T : Enum<*> {
        reviewService.onEvent(eventEnum, delay)
    }

    /**
     * TODO: 06/18/2022 [Метод удалить после перехода на новую версию](https://online.sbis.ru/opendoc.html?guid=d8571409-ec5e-49f8-8c34-e3506a295daa)
     * Опубликовать событие [event] с задержкой [delay] в систему оценок
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("onEventReview")
    @Deprecated(
        "https://online.sbis.ru/opendoc.html?guid=d8571409-ec5e-49f8-8c34-e3506a295daa",
        ReplaceWith("Используй версию метода принимающую ReviewEvent")
    )
    override fun onEvent(event: Enum<*>, delay: Long) {
        reviewService.onEvent(event, delay)
    }
}
