package ru.tensor.sbis.review

import android.content.Context
import android.content.SharedPreferences
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.design.utils.checkNotNullSafe
import ru.tensor.sbis.review.action.StatisticReviewAction
import ru.tensor.sbis.review.action.GooglePlayReviewAction
import ru.tensor.sbis.review.action.ReviewAction
import ru.tensor.sbis.review.triggers.EventSerializer
import ru.tensor.sbis.review.triggers.Trigger
import ru.tensor.sbis.review.triggers.TriggerParent
import ru.tensor.sbis.verification_decl.login.CurrentAccount
import java.util.concurrent.TimeUnit

/**
 * Сервис проверки событий по правилам для предложения пользователю оценить приложение
 * @property trigger набор триггеров для сервиса
 * @property activityProvider провайдер активити необходимо для работы ReviewManager
 * @property accountProvider опциональный провайдер аккаунта, нужен для приложений где есть авторизация
 * @author ma.kolpakov
 */
internal class ReviewService(
    appContext: Context,
    private val trigger: Trigger,
    private val activityProvider: ActivityProvider,
    reviewAction: ReviewAction = GooglePlayReviewAction(),
    private val accountProvider: CurrentAccount? = null,
) {
    companion object {
        private val twoMonthMillis = TimeUnit.DAYS.toMillis(60)
    }

    private val subject = BehaviorSubject.create<Pair<Enum<*>, Long>>()
    private val eventSerializers: MutableMap<String, MutableList<EventSerializer>> = mutableMapOf()
    private val reviewActions: List<ReviewAction> by lazy {
        listOf(reviewAction, StatisticReviewAction(Analytics(), { storage }))
    }

    init {
        initTriggers()
        // Удалять подписку нет необходимости компонент живет все время жизни приложения
        val disposable = subject.observeOn(Schedulers.single())
            .concatMap { (event, delay) -> Observable.just(event).delay(delay, TimeUnit.MILLISECONDS) }
            .map { event ->
                val eventKey = enumToString(event)
                eventSerializers[eventKey]?.forEach { it.serialize(eventKey, storage) }
                event
            }.filter {
                canShowReview() && trigger.checkEvent(storage)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event ->
                checkNotNullSafe(activityProvider.activity()) { ACTIVITY_EXCEPTION }?.let { activity ->
                    reviewActions.forEach { it.startReview(activity, event) }
                    updateReviewDate()
                }
            }
    }

    private val storage: SharedPreferences by lazy {
        appContext.getSharedPreferences(
            getSharedPreferencesFileName(),
            Context.MODE_PRIVATE
        )
    }

    private fun nextAllowedTime() = storage.getLong(NEXT_ALLOWED_TIME_KEY, 0)

    private fun getSharedPreferencesFileName() =
        REVIEW_PREFERENCE_NAME.format(accountProvider?.getCurrentAccount()?.userId ?: DEFAULT_USER)

    /**
     * Публикация события инициирует проверку правил.
     */
    fun <T> onEvent(eventEnum: T, delay: Long) where T : ReviewEvent, T : Enum<*> {
        subject.onNext(eventEnum to delay)
    }

    /**
     * TODO: 06/18/2022 [Метод удалить после перехода на новую версию](https://online.sbis.ru/opendoc.html?guid=d8571409-ec5e-49f8-8c34-e3506a295daa)
     * Публикация события инициирует проверку правил.
     */
    @JvmName("onEventReviewEvent")
    @Deprecated(
        "https://online.sbis.ru/opendoc.html?guid=d8571409-ec5e-49f8-8c34-e3506a295daa",
        ReplaceWith("Используй версию метода принимающую ReviewEvent")
    )
    fun onEvent(event: Enum<*>, delay: Long = 0) {
        subject.onNext(event to delay)
    }

    private fun updateReviewDate() {
        val newNextAllowedTime = System.currentTimeMillis() + twoMonthMillis
        storage.apply { edit().putLong(NEXT_ALLOWED_TIME_KEY, newNextAllowedTime).apply() }
    }

    private fun canShowReview() = System.currentTimeMillis() >= nextAllowedTime()

    private fun initTriggers() {
        eventSerializers.clear()
        depthCrawl(trigger) {
            val eventKey = it.eventKey

            if (!eventSerializers.containsKey(eventKey)) {
                eventSerializers[eventKey] = mutableListOf(it.serializer)
            } else {
                eventSerializers[eventKey]?.add(it.serializer)
            }
        }

    }

    /**
     * Обход дерева триггеров в глубину
     */
    private fun depthCrawl(trigger: Trigger, action: (Trigger) -> Unit) {
        if (trigger is TriggerParent) {
            trigger.children.forEach {
                depthCrawl(it, action)
            }
        }
        action.invoke(trigger)
    }

}

internal const val REVIEW_PREFERENCE_NAME = "Review_Feature_%s"
internal const val NEXT_ALLOWED_TIME_KEY = "NEXT_ALLOWED_TIME_KEY"
private const val DEFAULT_USER = "default_user"

// TODO: 13.08.2021 Избавиться от исключения в случае отсутствия активити https://online.sbis.ru/opendoc.html?guid=7155e686-f6d7-43e1-b144-68f8fa780140
private const val ACTIVITY_EXCEPTION = "activity not defined"
