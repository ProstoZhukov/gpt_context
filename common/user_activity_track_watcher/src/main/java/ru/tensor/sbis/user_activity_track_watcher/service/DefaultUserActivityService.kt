package ru.tensor.sbis.user_activity_track_watcher.service

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.UiThread
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.user_activity_track.service.UserActivityService
import ru.tensor.sbis.user_activity_track_watcher.reporter.ActivityReporter
import ru.tensor.sbis.user_activity_track_watcher.reporter.default
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Класс, отвечающий за трекинг активности пользователя
 *
 * @param context
 * @param activityReporter посредник, который фиксирует активность пользователя
 *
 * @author kv.martyshenko
 */
class DefaultUserActivityService @JvmOverloads constructor(
    private val context: Context,
    private val activityReporter: ActivityReporter = ActivityReporter.default()
) : UserActivityService {
    private val userActivitySettings by lazy {
        UserActivitySettings(context.getSharedPreferences(SETTING_FILE_NAME, Context.MODE_PRIVATE))
    }

    private val subscribersNames: MutableSet<String> = Collections.synchronizedSet(HashSet())
    private var lastSuccessUpdateTime: Long = userActivitySettings.getLastSuccessUpdateTime()
    private var subscribers = 0
    private var userActivityDisposable: Disposable? = null

    @UiThread
    override fun startPeriodicallyRegisterActivity(screenName: String) {
        subscribersNames.add(screenName)
        if (noSubscribers()) {
            startSendingUserActivity()
        }
        subscribers++
    }

    @UiThread
    override fun stopPeriodicallyRegisterActivity(screenName: String) {
        subscribersNames.remove(screenName)
        subscribers--
        if (noSubscribers()) {
            stopSendingUserActivity()
        }
    }

    @SuppressLint("CheckResult")
    override fun registerOneTimeActivity(action: String) {
        Completable
            .fromAction {
                activityReporter.report(action)
            }
            .subscribeOn(Schedulers.io())
            .subscribe({}, Timber::d)
    }

    @UiThread
    override fun reset() {
        subscribersNames.clear()
        subscribers = 0
        stopSendingUserActivity()
        userActivitySettings.clear()
    }

    private fun startSendingUserActivity() {
        val currentTimeOffset = System.currentTimeMillis()
        val delta = currentTimeOffset - lastSuccessUpdateTime
        val initialDelay = if (isTimeToUpdate(delta)) 0 else TIME_TO_UPDATE_ACTIVITY_IN_MILLIS - delta

        userActivityDisposable = Observable
            .interval(
                initialDelay,
                TIME_TO_UPDATE_ACTIVITY_IN_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .observeOn(Schedulers.io())
            .map {
                activityReporter.report("Subscribers count = $subscribers SubscribersNames = $subscribersNames")
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Timber.d("Observable.interval")
                    if (result) {
                        Timber.d("Request result IS NOT BaseCommand.RESPONSE_FAILURE. Update SharedPreferences.")
                        lastSuccessUpdateTime = System.currentTimeMillis()
                        userActivitySettings.setLastSuccessUpdatingTime(lastSuccessUpdateTime)
                    } else {
                        Timber.d("Request result IS BaseCommand.RESPONSE_FAILURE. Don't update SharedPreferences.")
                    }
                }, Timber::d
            )
        Timber.d("UserActivityService.updateUserActivity() with initial delay equals %s seconds.", initialDelay / 1000)
    }

    private fun isTimeToUpdate(delta: Long): Boolean {
        return delta >= TIME_TO_UPDATE_ACTIVITY_IN_MILLIS
    }

    private fun noSubscribers(): Boolean {
        if (subscribers < 0) {
            Timber.wtf("Subscribers = %d", subscribers)
            subscribers = 0
        }
        return subscribers == 0
    }

    private fun stopSendingUserActivity() {
        if (userActivityDisposable != null) {
            Timber.d("UserActivityService.stop()")
            userActivityDisposable!!.dispose()
        }
    }

    companion object {
        private const val SETTING_FILE_NAME = "user_activity_settings"
        private val TIME_TO_UPDATE_ACTIVITY_IN_MILLIS = TimeUnit.SECONDS.toMillis(150) // 2.5 minutes
    }

}