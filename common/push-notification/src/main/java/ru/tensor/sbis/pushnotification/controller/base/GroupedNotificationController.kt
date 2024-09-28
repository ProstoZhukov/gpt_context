package ru.tensor.sbis.pushnotification.controller.base

import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import androidx.annotation.PluralsRes
import ru.tensor.sbis.common.util.collections.ListUtils
import ru.tensor.sbis.common.util.collections.Predicate
import ru.tensor.sbis.push.generated.PushNotifyMeta
import ru.tensor.sbis.pushnotification.PushContentCategory
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.controller.HandlingResult
import ru.tensor.sbis.pushnotification.controller.base.helper.MessageLineProvider
import ru.tensor.sbis.pushnotification.controller.base.helper.PushBuildingHelper
import ru.tensor.sbis.pushnotification.controller.base.strategy.DefaultPushCancelStrategy
import ru.tensor.sbis.pushnotification.controller.base.strategy.PushCancelStrategy
import ru.tensor.sbis.pushnotification.controller.base.strategy.PushUpdateStrategy
import ru.tensor.sbis.pushnotification.model.PushData
import ru.tensor.sbis.pushnotification.model.factory.PushDataFactory
import ru.tensor.sbis.pushnotification.notification.PushNotification
import ru.tensor.sbis.pushnotification.notification.decorator.impl.ChannelDecorator
import ru.tensor.sbis.pushnotification.notification.decorator.impl.QuietDecorator
import ru.tensor.sbis.pushnotification.repository.model.PushCloudAction
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage
import ru.tensor.sbis.pushnotification.util.PushLogger
import ru.tensor.sbis.pushnotification.util.SwipeOutHelper
import kotlin.math.max

// идентификатор для публикации группового уведомления
private const val GROUPED_NOTIFY_ID = 0

/**
 * Базовый класс обработчика пуш-уведомлений, который включает в себя опциональную группировку
 * нескольких уведомлений в один пуш, автоматическую обработку пушей на удаление и обновление и их публикацию,
 * а так же реагирует на события удаления пуш-уведомлений, которые были инициированы приложением посредством
 * вызова [cancel], [cancelAll].
 *
 * Обработчик достает все опубликованные уведомления по [getNotifyTag], фильтрует новые уведомления на группы -
 * для публикации, для обновления и для удаления, мержит все эти списки
 * и уведомляет об этом через [notificationManager].
 * Обработчик так же учитывает, что пуши на удаление могут прийти раньше, чем новые пуши о публикации
 * (порядок поступления не гарантируется), соответственно эти пуши показаны не будут.
 *
 * @author am.boldinov
 */
abstract class GroupedNotificationController<DATA : PushData> constructor(
    context: Context,
    private val pushDataFactory: PushDataFactory<DATA>,
    private val cancelStrategy: PushCancelStrategy<in DATA>,
    private val updateStrategy: PushUpdateStrategy<in DATA>?
) : AbstractNotificationController(context) {

    constructor(
        context: Context,
        pushDataFactory: PushDataFactory<DATA>,
    ) : this(context, pushDataFactory, DefaultPushCancelStrategy(), null)

    constructor(
        context: Context,
        pushDataFactory: PushDataFactory<DATA>,
        cancelStrategy: PushCancelStrategy<in DATA>
    ) : this(context, pushDataFactory, cancelStrategy, null)

    constructor(
        context: Context,
        pushDataFactory: PushDataFactory<DATA>,
        updateStrategy: PushUpdateStrategy<in DATA>
    ) : this(context, pushDataFactory, DefaultPushCancelStrategy(), updateStrategy)

    final override fun getNotifyTag(): String {
        return "TAG_" + javaClass.canonicalName
    }

    @Synchronized
    final override fun handle(messages: List<PushNotificationMessage>): HandlingResult {

        // вытаскиваем из списка новые уведомления
        val notifyData = ListUtils.takeWithMutate(messages, CloudActionPredicate(PushCloudAction.NOTIFY)).toFilteredDataModel()
        val notifyDataList = notifyData.displayedDataList

        // вытаскиваем из списка уведомления об удалении
        val cancelMessages = ListUtils.takeWithMutate(messages, CloudActionPredicate(PushCloudAction.CANCEL))

        // оставляем в списке только уведомления об обновлении
        val updateData = ListUtils.filter(messages, CloudActionPredicate(PushCloudAction.UPDATE)).toFilteredDataModel()
        val updateDataList = updateData.displayedDataList

        onBeforeProcessing(notifyDataList, cancelMessages)

        // пуш сообщения, удаленные из из списка новых сообщений
        // (пуш об удалении пришел одновременно с новым, либо раньше)
        val removedFromNotify = arrayListOf<PushNotificationMessage>()
        // список новых пуш сообщений, обработанных контроллером
        var notifyForHandlingResult = emptyList<PushNotificationMessage>()

        if (notifyDataList.isNotEmpty()) {
            // вычищаем прочитанные из новых уведомлений
            processOuterCancelMessages(notifyDataList, cancelMessages)?.let { cancelled ->
                removedFromNotify.addAll(cancelled)
            }
            // достаем все закешированные пуши по типам, которые являются кандидатами к показу
            val cachedMessages = repository.getNotifications(notifyDataList.toPushTypeSet())
            // отбираем из всех закешированных только те, которые являются прочитанными уведомлениями
            val cachedCancelMessages =
                ListUtils.takeWithMutate(cachedMessages, CloudActionPredicate(PushCloudAction.CANCEL))
            // вычищаем прочитанные (которые пришли раньше) из новых уведомлений
            processOuterCancelMessages(notifyDataList, cachedCancelMessages)?.let { cancelled ->
                removedFromNotify.addAll(cancelled)
            }
            // Сохраняем в результат до применения обновлений
            notifyForHandlingResult = notifyDataList.map { it.message }
            // применяем обновления к новым уведомлениям
            processUpdatingNotifications(notifyDataList, updateDataList)
        }

        val handleResult = HandlingResult(
            notifyDisplayed = notifyForHandlingResult,
            notifyHidden = notifyData.hiddenMessageList.apply { addAll(removedFromNotify) },
            updateDisplayed = updateDataList.map { it.message },
            updateHidden = updateData.hiddenMessageList,
            cancelled = cancelMessages
        )

        val publishedDataList = repository.getPublishedNotifications(getNotifyTag()).toFilteredDataModel().displayedDataList

        if (publishedDataList.isNotEmpty()) {
            // вычищаем прочитанные уведомления из кеша
            processOuterCancelMessages(publishedDataList, cancelMessages)
            // применяем обновления к уведомлениям из кеша
            processUpdatingNotifications(publishedDataList, updateDataList)
        }

        if (notifyDataList.isNotEmpty()) {
            if (needToGroup()) {
                // публикуем все как одно группированное
                val groupedDataList = publishedDataList.filterOnlyGrouped().apply {
                    addAll(notifyDataList) // добавляем новые к уже сгруппированным
                }
                val notifyMeta = showNotificationAsGrouped(groupedDataList, false)
                if (notifyMeta != null) {
                    notifyDataList.forEach { data ->
                        data.message.notifyMeta = notifyMeta
                        publishedDataList.add(data) // добавляем опубликованную запись в кеш
                    }
                }
            } else {
                // публикуем каждое по отдельности
                notifyDataList.forEach { data ->
                    val notifyMeta = showNotificationAsSingle(
                        data, publishedDataList.generateNextNotifyId(),
                        isGrouped = false,
                        update = false
                    )
                    if (notifyMeta != null) {
                        data.message.notifyMeta = notifyMeta
                        publishedDataList.add(data) // добавляем опубликованную запись в кеш
                    }
                }
            }
        }

        // обновляем кеш на диске
        repository.savePublishedNotifications(getNotifyTag(), publishedDataList.toListMessage())

        onAfterProcessing()

        return handleResult
    }

    @Synchronized
    final override fun cancel(type: PushType, params: Bundle) {
        val matcher = cancelStrategy.getInnerCancelMatcher(params)
        if (matcher != null) {
            val publishedDataList = repository.getPublishedNotifications(getNotifyTag()).toListData()
            cancelNotifications(
                matcher,
                publishedDataList
            )
            repository.savePublishedNotifications(getNotifyTag(), publishedDataList.toListMessage())
        }
    }

    @Synchronized
    final override fun cancelAll(type: PushType) {
        repository.getNotifications(type).forEach { message ->
            val notifyMeta = message.notifyMeta
            if (notifyMeta != null) {
                notificationManager.cancel(notifyMeta.tag, notifyMeta.notifyId)
            }
        }
    }

    /**
     * Возвращает ссылку на ресурс для отображения в заголовке группового уведомления.
     */
    @PluralsRes
    protected abstract fun getPluralsTitleRes(): Int

    /**
     * Формирует интент для открытия при нажатии на одиночный пуш.
     */
    protected abstract fun createIntentForSingle(data: DATA, requestCode: Int): PendingIntent?

    /**
     * Возвращает необходимость показа пуш-уведомления пользователю на основе валидности данных в пуше.
     */
    protected open fun needToShow(data: DATA): Boolean {
        return true
    }

    /**
     * Возвращает признак группировки нескольких уведомлений в один пуш.
     * Если вернуть false то каждое новое событие будет опубликовано отдельным пуш-уведомлением в шторке.
     * По умолчанию берет значение из хранилища настроек уведомлений.
     * Переключатель группировки доступен на экране настроек уведомлений в вашем приложении.
     */
    protected open fun needToGroup(): Boolean {
        return repository.isGrouped()
    }

    /**
     * Определяет действие с пуш-уведомлением при их обработке.
     * По умолчанию действие приходит вместе с пуш-сообщением, но для сложных уведомлений
     * оно может быть переопределено.
     */
    protected open fun determineCloudAction(message: PushNotificationMessage): PushCloudAction {
        return message.cloudAction
    }

    /**
     * Событие о начале обработки уведомлений.
     *
     * @param notifyDataList список новых уведомлений для публикации
     * @param cancelMessages список пуш-сообщений для удаления
     */
    protected open fun onBeforeProcessing(notifyDataList: List<DATA>, cancelMessages: List<PushNotificationMessage>) {

    }

    /**
     * Событие о завершении обработки всех уведомлений контроллером.
     */
    protected open fun onAfterProcessing() {

    }

    /**
     * Формирует одиночное уведомление для отображения в шторке.
     */
    protected open fun createSingleNotification(data: DATA, requestCode: Int): PushNotification? {
        val notification = pushBuildingHelper.createSbisNotification(data)
        notification.builder.setContentIntent(createIntentForSingle(data, requestCode))
        return notification
    }

    /**
     * Формирует групповое уведомление для отображения в шторке.
     */
    protected open fun createGroupedNotification(dataList: List<DATA>, requestCode: Int): PushNotification? {
        if (dataList.isNotEmpty()) {
            val notification = pushBuildingHelper.createSbisNotification(dataList.last())
            pushBuildingHelper.customizeAsGrouped(
                notification,
                getPluralsTitleRes(),
                dataList,
                createInboxLineProvider(),
                true
            )
            notification.builder.setContentIntent(createIntentForGrouped(dataList, requestCode))
            return notification
        }
        return null
    }

    /**
     * Формирует интент для открытия при нажатии на групповой пуш.
     */
    protected open fun createIntentForGrouped(dataList: List<DATA>, requestCode: Int): PendingIntent {
        return pushIntentHelper.createMainActivityPendingIntent(getContentCategory(), requestCode)
    }

    /**
     * Формируем категорию контента для открытия пуша
     */
    protected abstract fun getContentCategory(): PushContentCategory

    /**
     * Создает провайдер для отображения текста в групповом уведомлении.
     */
    protected open fun createInboxLineProvider(): PushBuildingHelper.LineProvider<in DATA> {
        return MessageLineProvider()
    }

    /**
     * Публикует новое уведомление.
     *
     * @param notifyId идентификатор уведомления для публикации
     * @param isGrouped является ли уведомление группированным
     * @param notification уведомление для отображения
     */
    protected fun showNotification(notifyId: Int, isGrouped: Boolean, notification: PushNotification): PushNotifyMeta {
        notification.builder.setDeleteIntent(SwipeOutHelper.createSwipeOutIntent(context, getNotifyTag(), notifyId))
        notificationManager.notify(getNotifyTag(), notifyId, notification)
        return PushNotifyMeta(getNotifyTag(), notifyId, isGrouped)
    }

    private fun showNotificationAsSingle(
        data: DATA,
        notifyId: Int,
        isGrouped: Boolean,
        update: Boolean
    ): PushNotifyMeta? {
        val notification = createSingleNotification(data, generateRequestCode(notifyId)) ?: return null
        notification.decorate(ChannelDecorator(update))
        if (update) {
            notification.decorate(QuietDecorator(false))
        }
        return showNotification(notifyId, isGrouped, notification)
    }

    private fun showNotificationAsGrouped(notifyDataList: List<DATA>, update: Boolean): PushNotifyMeta? {
        if (notifyDataList.isNotEmpty()) {
            if (notifyDataList.size == 1) {
                // если в списке содержится только одно уведомление - публикуем как одиночное
                return showNotificationAsSingle(notifyDataList[0], GROUPED_NOTIFY_ID, true, update)
            }
            val notification = createGroupedNotification(
                notifyDataList,
                generateRequestCode(GROUPED_NOTIFY_ID)
            ) ?: return null
            notification.decorate(ChannelDecorator(update))
            if (update) {
                notification.decorate(QuietDecorator(false))
            }
            return showNotification(GROUPED_NOTIFY_ID, true, notification)
        }
        return null
    }

    private fun processUpdatingNotifications(notifyDataList: MutableList<DATA>, updateDataList: List<DATA>) {
        if (updateStrategy != null && notifyDataList.isNotEmpty() && updateDataList.isNotEmpty()) {
            val updated = mutableListOf<DATA>()
            // проходим по всем обновлениям
            updateDataList.forEach { updateData ->
                notifyDataList.forEachIndexed { index, notifyData ->
                    // если обновление относится к данному уведомлению - заменяем уведомление
                    if (updateStrategy.getUpdateMatcher(updateData)?.apply(notifyData) == true) {
                        // копируем мета-информацию
                        updateData.message.notifyMeta = notifyData.message.notifyMeta
                        notifyDataList[index] = updateData
                        updated.add(updateData)
                    }
                }
            }
            if (updated.isNotEmpty()) {
                var needToUpdateGrouped = false
                updated.forEach {
                    val notifyMeta = it.message.notifyMeta
                    if (notifyMeta != null) {
                        if (notifyMeta.isGrouped) {
                            // необходимо обновить группированное уведомление
                            needToUpdateGrouped = true
                        } else {
                            showNotificationAsSingle(it, notifyMeta.notifyId, isGrouped = false, update = true)
                        }
                    }
                }
                if (needToUpdateGrouped) {
                    // обновляем группированное уведомление
                    val result = showNotificationAsGrouped(notifyDataList.filterOnlyGrouped(), true)
                    if (result == null) {
                        // если восстановить групповое не удалось, к примеру список остался пустым, то можно удалить пуш
                        notificationManager.cancel(getNotifyTag(), GROUPED_NOTIFY_ID)
                    }
                }
            }
        }
    }

    private fun processOuterCancelMessages(
        notifyDataList: MutableList<DATA>,
        cancelMessages: List<PushNotificationMessage>
    ): List<PushNotificationMessage>? {
        if (notifyDataList.isNotEmpty()) {
            val removedResult = ArrayList<PushNotificationMessage>()
            cancelMessages.forEach {
                cancelStrategy.getOuterCancelMatcher(it)?.apply {
                    cancelNotifications(this, notifyDataList).let { removed ->
                        removedResult.addAll(removed)
                    }
                }
            }
            return removedResult
        }
        return null
    }

    private fun cancelNotifications(matcher: Predicate<in DATA>, notifyDataList: MutableList<DATA>): List<PushNotificationMessage> {
        // вычищаем уведомления, которые нужно удалить
        val removeDataList = ListUtils.takeWithMutate(notifyDataList, matcher)
        if (removeDataList.isNotEmpty()) {
            var needToRemoveGrouped = false
            removeDataList.forEach {
                val notifyMeta = it.message.notifyMeta
                if (notifyMeta != null) {
                    if (notifyMeta.isGrouped) {
                        // необходимо удалить группированное уведомление
                        needToRemoveGrouped = true
                    } else {
                        // необходимо удалить одиночное уведомление
                        notificationManager.cancel(notifyMeta.tag, notifyMeta.notifyId)
                    }
                }
            }
            if (needToRemoveGrouped) {
                // восстанавливаем группированное уведомление, если часть его была удалена
                val result = showNotificationAsGrouped(notifyDataList.filterOnlyGrouped(), true)
                if (result == null) {
                    // если восстановить групповое не удалось, к примеру список остался пустым, то можно удалить пуш
                    notificationManager.cancel(getNotifyTag(), GROUPED_NOTIFY_ID)
                }
            }
        }
        return removeDataList.toListMessage()
    }

    private fun List<DATA>.generateNextNotifyId(): Int {
        var max = -1
        forEach {
            val notifyMeta = it.message.notifyMeta
            if (notifyMeta != null && notifyMeta.notifyId > max) {
                max = notifyMeta.notifyId
            }
        }
        return max(max, GROUPED_NOTIFY_ID) + 1
    }

    private fun List<PushNotificationMessage>.toFilteredDataModel(): FilteredDataModel<DATA> {
        val hidden = ArrayList<PushNotificationMessage>(size)
        val displayedDataList = ArrayList<DATA>(size)
        forEach {
            val data = pushDataFactory.create(it)
            if (needToShow(data)) {
                displayedDataList.add(data)
            } else {
                hidden.add(it)
                PushLogger.event("Push notification showing was skipped, needToShow: false, uuid: ${data.message.notificationUuid}, type: ${data.message.type}")
            }
        }
        return FilteredDataModel(displayedDataList, hidden)
    }

    private fun List<PushNotificationMessage>.toListData(): MutableList<DATA> {
        return mapTo(ArrayList(size), pushDataFactory::create)
    }

    private fun List<DATA>.toListMessage(): MutableList<PushNotificationMessage> {
        val result = ArrayList<PushNotificationMessage>(size)
        forEach {
            result.add(it.message)
        }
        return result
    }


    private fun List<DATA>.toPushTypeSet(): Set<PushType> {
        val result = hashSetOf<PushType>()
        forEach {
            result.add(it.message.type)
        }
        return result
    }

    private fun List<DATA>.filterOnlyGrouped(): MutableList<DATA> {
        return ListUtils.takeWithoutMutate(this, object : Predicate<DATA> {
            override fun apply(t: DATA): Boolean {
                val notifyMeta = t.message.notifyMeta
                return notifyMeta != null && notifyMeta.isGrouped
            }
        })
    }

    private fun generateRequestCode(notifyId: Int): Int {
        return getNotifyTag().hashCode() + notifyId
    }

    /**
     * Модель данных с результатом фильтрации списка пуш уведомлений
     * (разделение по принципу будет показан/не будет показан)
     *
     * @property displayedDataList список уведомлений к показу в виде списка моделей
     * @property hiddenMessageList список уведомлений, которые пользователь не увидит
     */
    private data class FilteredDataModel<DATA : PushData>(
        val displayedDataList: MutableList<DATA>,
        val hiddenMessageList: MutableList<PushNotificationMessage>
    )

    private inner class CloudActionPredicate(
        private val target: PushCloudAction
    ) : Predicate<PushNotificationMessage> {
        override fun apply(t: PushNotificationMessage): Boolean {
            return determineCloudAction(t) == target
        }
    }
}