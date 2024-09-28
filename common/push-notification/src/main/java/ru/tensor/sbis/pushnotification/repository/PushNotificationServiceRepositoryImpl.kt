package ru.tensor.sbis.pushnotification.repository

import android.annotation.SuppressLint
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.info_decl.model.NotificationType
import ru.tensor.sbis.push.generated.PushNotification
import ru.tensor.sbis.push.generated.PushService
import ru.tensor.sbis.push_cloud_messaging.PushCloudMessaging
import ru.tensor.sbis.pushnotification.BuildConfig
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage
import ru.tensor.sbis.pushnotification.repository.model.SupportTypesData
import ru.tensor.sbis.pushnotification.util.PushLogger
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.collections.HashSet

private const val STORE_APP_TYPE = "store"
private const val ENTERPRISE_APP_TYPE = "enterprise"

/**
 * Класс для взаимодействия с сервисом пуш-уведомлений,
 * реализующий прокси вызовы в сервис с дополнительным препроцессингом.
 *
 * @author am.boldinov
 */
internal class PushNotificationServiceRepositoryImpl(
    private val appName: String,
    private val pushService: DependencyProvider<PushService>,
    private val converter: PushNotificationMessageConverter
) : PushNotificationRepository {

    private val supportTypesSubject = PublishSubject.create<SupportTypesData>()

    init {
        subscribeToSupportTypeChanges()
    }

    override fun setToken(token: String) {
        Completable.fromAction {
            val appType = if (BuildConfig.DEBUG) ENTERPRISE_APP_TYPE else STORE_APP_TYPE
            val osName = PushCloudMessaging.getServiceName()
            pushService.get().setTokenAndOs(appName, token, BuildConfig.DEBUG, appType, osName)
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    override fun setSupportTypes(data: SupportTypesData) {
        supportTypesSubject.onNext(data)
    }

    @Synchronized
    override fun createNotification(payload: Map<String, String>): PushNotificationMessage? {
        val result = pushService.get().create(HashMap(payload))
        if (result != null) {
            val message = converter.convert(result)
            return if (message.type == PushType.UNDEFINED) { // если тип пуша неизвестен - удаляем сообщение
                PushLogger.warning("Unknown push notification type: ${result.type}")
                removeNotification(message)
                null
            } else {
                PushLogger.event("Push notification message was created, uuid: ${message.notificationUuid}, type: ${message.type}")
                message
            }
        } else {
            PushLogger.warning("PushService.create returned null value for payload: $payload")
        }
        return null
    }

    @Synchronized
    override fun removeNotification(message: PushNotificationMessage) {
        pushService.get().deleteByPush(converter.convert(message))
    }

    @Synchronized
    override fun clearAll() {
        pushService.get().deleteAll()
    }

    @Synchronized
    override fun clearAll(types: Set<PushType>) {
        pushService.get().deleteByTypes(types.toIntSet())
    }

    @Synchronized
    override fun clearAll(type: PushType) {
        pushService.get().deleteByType(type.intValue)
    }

    @Synchronized
    override fun getNotifications(type: PushType): List<PushNotificationMessage> {
        return getNotifications(hashSetOf(type))
    }

    @Synchronized
    override fun getNotifications(types: Set<PushType>): List<PushNotificationMessage> {
        return pushService.get().list(types.toIntSet()).map {
            converter.convert(it)
        }
    }

    @Synchronized
    override fun getPublishedNotifications(notifyTag: String): List<PushNotificationMessage> {
        return pushService.get().listByTag(notifyTag).map {
            converter.convert(it)
        }
    }

    @Synchronized
    override fun savePublishedNotifications(notifyTag: String, messages: List<PushNotificationMessage>) {
        pushService.get().apply {
            deleteByTag(notifyTag)
            val updated = ArrayList<PushNotification>(messages.size)
            messages.forEach {
                updated.add(converter.convert(it))
            }
            updateList(updated)
        }
    }

    override fun isGrouped(): Boolean {
        return pushService.get().isToGroup()
    }

    override fun confirmPushByLink(confirmLink: String) {
        Completable.fromAction {
            pushService.get().confirmPushByLink(confirmLink)
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    @SuppressLint("CheckResult")
    private fun subscribeToSupportTypeChanges() {
        supportTypesSubject // постим событие в главный поток для исключения доп. вызовов при его блокировке
            .throttleLastTimed(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .observeOn(Schedulers.single())
            .subscribe(
                { data ->
                    val supportTypes = HashSet<Int>(data.types.size)
                    supportTypes.addAll(convertToSupportTypes(data.types))
                    pushService.get().setSupportTypes(appName, supportTypes)
                }, {
                    PushLogger.error(it, "An error occurred while set supported push notification types")
                }
            )
    }

    private fun convertToSupportTypes(typeSet: Set<PushType>): HashSet<Int> {
        val result = HashSet<Int>(typeSet.size)
        for (pushType in typeSet) {
            val type = pushType.toNotificationType()
            if (type != NotificationType.UNKNOWN) {
                result.add(type.value)
            }
        }
        return result
    }

    private fun <T> Observable<T>.throttleLastTimed(
        duration: Long,
        unit: TimeUnit,
        scheduler: Scheduler
    ): Observable<T> {
        return RxJavaPlugins.onAssembly(ObservableThrottleLastTimed<T>(this, duration, unit, scheduler))
    }

    private fun Set<PushType>.toIntSet(): HashSet<Int> {
        val set = HashSet<Int>(size)
        forEach {
            set.add(it.intValue)
        }
        return set
    }
}
