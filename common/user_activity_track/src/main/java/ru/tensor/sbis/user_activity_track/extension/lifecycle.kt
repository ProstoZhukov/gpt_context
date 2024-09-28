@file:JvmName("UserActivityServiceExtension")
package ru.tensor.sbis.user_activity_track.extension

import androidx.annotation.UiThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.user_activity_track.service.UserActivityService

/**
 * Подписка на сервис активности пользователя, автоматом управляемая с помощью [Lifecycle]
 *
 * @param lifecycle
 * @param screenName название экрана
 *
 * @author kv.martyshenko
 */
@UiThread
fun UserActivityService.autoTrack(lifecycle: Lifecycle, screenName: String) {
    lifecycle.addObserver(object : DefaultLifecycleObserver {

        override fun onResume(owner: LifecycleOwner) {
            startPeriodicallyRegisterActivity(screenName)
        }

        override fun onPause(owner: LifecycleOwner) {
            stopPeriodicallyRegisterActivity(screenName)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
        }

    })
}