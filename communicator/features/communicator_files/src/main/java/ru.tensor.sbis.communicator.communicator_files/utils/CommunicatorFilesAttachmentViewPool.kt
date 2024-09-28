package ru.tensor.sbis.communicator.communicator_files.utils

import android.content.Context
import android.os.Looper
import androidx.annotation.WorkerThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.Completable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.attachments.ui.view.collage.AttachmentCollageView
import ru.tensor.sbis.attachments.ui.view.collage.card.AttachmentCollageCardView
import ru.tensor.sbis.attachments.ui.view.collage.util.getCollageAttachmentPoolKey
import ru.tensor.sbis.attachments.ui.viewmodel.base.AttachmentVM
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communicator.communicator_files.R
import ru.tensor.sbis.communicator.communicator_files.ui.CommunicatorFilesItemView
import ru.tensor.sbis.design.message_view.utils.SimpleViewPool
import ru.tensor.sbis.design.utils.RecentlyUsedViewPool
import timber.log.Timber

/**
 * Класс для управления пулами представлений.
 *
 * @property context Контекст приложения, необходимый для создания представлений.
 *
 * @author da.zhukov.
 */
internal class CommunicatorFilesAttachmentViewPool(val context: Context) {

    private var isViewPoolsPrepared = false
    private val disposable = SerialDisposable()

    // Пул для представлений элементов списка файлов
    private val communicatorFileItemViewPool = SimpleViewPool {
        createCommunicatorFilesItemView()
    }

    // Пул для представлений вложений файлов
    private val attachmentViewPool = RecentlyUsedViewPool<AttachmentCollageView, Int>(viewFactory = ::createAttachmentView)

    // Пул для карточек вложений
    private val attachmentCollageCardViewPool = RecentlyUsedViewPool<AttachmentCollageCardView, Int> {
        AttachmentCollageCardView(context)
    }

    /**
     * Создает новое представление для отображения вложений файлов.
     *
     * @return Новое представление типа [AttachmentCollageView].
     */
    private fun createAttachmentView() = AttachmentCollageView(context).apply {
        id = R.id.communicator_files_attachment_preview_id
        viewPool = attachmentCollageCardViewPool
    }

    /**
     * Слушатель кликов на файл.
     */
    var communicatorFileClickListener: CommunicatorFileClickListener? = null

    /**
     * Создает новое представление для элементов списка файлов.
     *
     * @return Новое представление типа [CommunicatorFilesItemView].
     */
    private fun createCommunicatorFilesItemView() = CommunicatorFilesItemView(context).apply {
        this@CommunicatorFilesAttachmentViewPool.communicatorFileClickListener?.let { communicatorFileClickListener = it }
        setViewPool(this@CommunicatorFilesAttachmentViewPool)
    }

    /**
     * Извлекает представление для отображения списка вложений из пула, либо создаёт новое.
     *
     * @return Представление типа [CommunicatorFilesItemView].
     */
    internal fun getCommunicatorFilesItemView() = communicatorFileItemViewPool.getView()

    /**
     * Извлекает представление вложения из пула, либо создаёт новое.
     *
     * @param attachments Список вложений, для которых необходимо представление.
     * @return Представление типа [AttachmentCollageView].
     */
    internal fun getAttachmentView(attachments: AttachmentVM): AttachmentCollageView =
        attachmentViewPool.get(getAttachmentsKey(attachments))

    /**
     * Генерирует уникальный ключ для пула представлений вложений.
     *
     * @param attachments Список вложений.
     * @return Уникальный ключ в виде хеш-кода.
     */
    private fun getAttachmentsKey(attachments: AttachmentVM) =
       getCollageAttachmentPoolKey(attachments.model).hashCode()

    /**
     * Заполняет пулы представлений.
     *
     * @param communicatorFileItemViewsCapacity Вместимость пула представлений элементов списка файлов.
     * @param attachmentCollageViewsCapacity Вместимость пула представлений вложений файлов.
     * @param attachmentCardViewsCapacity Вместимость пула карточек вложений.
     */
    @WorkerThread
    fun prefetch(
        communicatorFileItemViewsCapacity: Int = COMMUNICATOR_FILES_ITEM_VIEW_POOL_CAPACITY,
        attachmentCollageViewsCapacity: Int = COMMUNICATOR_FILES_ATTACHMENT_COLLAGE_VIEW_POOL_CAPACITY,
        attachmentCardViewsCapacity: Int = COMMUNICATOR_FILES_ATTACHMENT_CARD_VIEW_POOL_CAPACITY
    ) {
        attachmentViewPool.inflate(attachmentCollageViewsCapacity)
        attachmentCollageCardViewPool.inflate(attachmentCardViewsCapacity)
        repeat(communicatorFileItemViewsCapacity) {
            communicatorFileItemViewPool.addView(createCommunicatorFilesItemView())
        }
    }

    /**
     * Подготавливает пулы представлений для использования.
     * Вызывается при возобновлении жизненного цикла фрагмента.
     */
    @Suppress("DEPRECATION")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun prepareViewPools() {
        if (isViewPoolsPrepared) return
        isViewPoolsPrepared = true
        Looper.myQueue().addIdleHandler {
            Completable.fromAction {
                try {
                    safeExecute { prefetch() }
                } catch (ex: Exception) {
                    Timber.w(ex, "Failed to async pumping up communicator view pools")
                }
            }
                .subscribeOn(Schedulers.computation())
                .subscribe()
                .storeIn(disposable)
            false
        }
    }

    /**
     * Безопасно выполняет указанное действие, если пул представлений не освобожден.
     *
     * @param action Действие, которое необходимо выполнить.
     */
    private fun safeExecute(action: () -> Unit) {
        if (!disposable.isDisposed) action()
    }

    /**
     * Освобождает ресурсы пулов представлений.
     * Вызывается при уничтожении жизненного цикла фрагмента.
     */
    @Suppress("unused", "DEPRECATION")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun flush() {
        disposable.dispose()
    }
}

/**
 * Константы для определения вместимости пулов представлений.
 */
private const val COMMUNICATOR_FILES_ITEM_VIEW_POOL_CAPACITY = 15
private const val COMMUNICATOR_FILES_ATTACHMENT_COLLAGE_VIEW_POOL_CAPACITY = 40
private const val COMMUNICATOR_FILES_ATTACHMENT_CARD_VIEW_POOL_CAPACITY = 40