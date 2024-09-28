package ru.tensor.sbis.message_panel.attachments

import androidx.annotation.StringRes
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.attachments.decl.canonicalLocalUri
import ru.tensor.sbis.attachments.decl.mapper.AttachmentRegisterModelMapper
import ru.tensor.sbis.attachments.generated.AttachmentEvents
import ru.tensor.sbis.attachments.generated.DataRefreshedAttachmentCallback
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.attachments.ui.view.register.contract.AttachmentsActionListener
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common_attachments.AttachmentPresenterHelper
import ru.tensor.sbis.common_attachments.AttachmentsContainerAdapter
import ru.tensor.sbis.design.utils.DebounceActionHandler
import ru.tensor.sbis.design.utils.errorSafe
import ru.tensor.sbis.disk.decl.params.DiskDocumentParams
import ru.tensor.sbis.diskmobile.generated.FileLoaderApi
import ru.tensor.sbis.diskmobile.generated.LoaderEventCallback
import ru.tensor.sbis.diskmobile.generated.OperationEvents
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.message_panel.contract.attachments.AttachmentsRouter
import ru.tensor.sbis.message_panel.interactor.attachments.MessagePanelAttachmentsInteractor
import ru.tensor.sbis.message_panel.viewModel.MessageAttachError
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.ClosedByRequest
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventData
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.verification_decl.login.LoginInterface
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import ru.tensor.sbis.attachments.decl.R as RAttachments
import ru.tensor.sbis.design.design_dialogs.R as RDesignDialogs

internal const val MAX_MESSAGE_ATTACHMENT_SIZE_MB = 1024

/**
 * Используется для паузы/возобновления подписки на добавление вложения. При добавлении вложения контроллер вызывает
 * dataRefreshCallback, после чего панель ввода обновится и отобразит вложение внутри себя. Это не всегда нужно,
 * например при отправке аудиосообщения файл передается как вложение и должен быть сразу отправлен без отображения его
 * в панели ввода.
 */
private const val ATTACHMENT_REFRESH_CALLBACK_NAME = "attachment_refresh_callback_name"

/**
 * @author Subbotenko Dmitry
 */
interface MessagePanelAttachmentHelper : AttachmentsActionListener {

    val attachments: List<FileInfo>
    var router: AttachmentsRouter?
    val attachmentsObservable: Observable<out List<FileInfo>>

    fun clearAttachments()
    fun addAttachments(uriList: List<String>, compressImages: Boolean = false)
    fun addDiskAttachments(diskDocumentParamsList: List<DiskDocumentParams>)

    fun loadAttachments(messageUuid: UUID)
    fun loadAttachmentsFromDraft()
    fun restartUploadAttachment(id: Long)
}

/**
 * @author Subbotenko Dmitry
 */
class MessagePanelAttachmentPresenterImpl(
    private val viewModel: MessagePanelViewModel<*, *, *>,
    private val interactor: MessagePanelAttachmentsInteractor,
    private val modelMapper: AttachmentRegisterModelMapper,
    private val fileUriUtil: FileUriUtil,
    private val subscriptionManager: SubscriptionManager,
    private val observeScheduler: Scheduler,
    private val resourceProvider: ResourceProvider,
    private val loginInterface: LoginInterface
) : MessagePanelAttachmentHelper,
    AttachmentsContainerAdapter.OnAttachmentsActionsListener,
    Disposable {

    override val attachments = CopyOnWriteArrayList<FileInfo>()
    override var router: AttachmentsRouter? = null
    override val attachmentsObservable = BehaviorSubject.create<List<FileInfo>>()

    private val mapperDisposable = SerialDisposable()
    private val disposable = CompositeDisposable(mapperDisposable)
    private val refreshCallback: DataRefreshedAttachmentCallback =
        object : DataRefreshedAttachmentCallback() {
            override fun onEvent(param: HashMap<String, String>) {
                val catalogId: String? = param[AttachmentEvents.ATTACHMENT_REFRESH_CATALOG_ID]
                messageUuid?.let {
                    if (UUIDUtils.equals(catalogId, it)) {
                        loadAttachments(it)
                    }
                }
            }
        }

    private val localAttachmentsProgress = ConcurrentHashMap<UUID, Int>()
    private val messageUuid: UUID?
        get() = viewModel.liveData.getTargetMessageUuid()

    init {
        subscriptionManager.batch().apply {
            manage(
                name = ATTACHMENT_REFRESH_CALLBACK_NAME,
                subscription = interactor.setAttachmentListRefreshCallback(refreshCallback),
                permanent = false
            )
            val eventsHandler = AttachmentAddingEventsHandler(::onAddingProgress, ::onAddingError)
            manage(
                subscription = Observable.fromCallable {
                    FileLoaderApi.instance().loaderEvent().subscribe(
                        object : LoaderEventCallback() {
                            override fun onEvent(name: String, params: HashMap<String, String>) {
                                if (name == OperationEvents.ON_QUEUE_RESUMED ||
                                    name == OperationEvents.ON_QUEUE_HOLDED ||
                                    attachments.isEmpty()
                                ) {
                                    return
                                } else {
                                    eventsHandler.accept(EventData(name, params))
                                }
                            }
                        }
                    )
                }.subscribeOn(Schedulers.io()),
                permanent = false
            )
            subscribe()
        }
    }

    override fun addAttachments(uriList: List<String>, compressImages: Boolean) {
        if (loginInterface.getCurrentAccount()?.isDemo == true) {
            viewModel.liveData.showToast(
                resourceProvider.getString(RAttachments.string.attach_decl_adding_denied_on_demo)
            )
            return
        }
        disposable += viewModel.liveData.targetMessageUuid.firstOrError().flatMap { draftUuid ->
            viewModel.liveData.setSendControlClickable(isClickable = false)
            Single.fromCallable {
                val alreadyAdded: List<String> = uriList.filter { uri ->
                    attachments.any { attachment -> attachment.canonicalLocalUri == uri }
                }
                when (alreadyAdded.size) {
                    0 -> Unit
                    1 -> showToast(RDesignDialogs.string.design_dialogs_file_already_attached)
                    uriList.size -> showToast(R.string.message_panel_all_files_already_attached)
                    else -> showToast(R.string.message_panel_some_files_already_attached)
                }
                val bigSize: List<String> = uriList.filter { uri ->
                    fileUriUtil
                        .getFileInfo(uri, requestName = false, requestSize = true, requestMimeType = false)
                        ?.isTooBig() ?: false
                }
                when (bigSize.size) {
                    0 -> Unit
                    1 -> showToast(R.string.message_panel_attachment_files_size_limit)
                    uriList.size -> showToast(R.string.message_panel_attachment_each_file_size_limit)
                    else -> showToast(R.string.message_panel_attachment_some_files_size_limit)
                }
                val filteredList = uriList.minus(alreadyAdded.toSet()).minus(bigSize.toSet())
                if (filteredList.size + attachments.size > AttachmentPresenterHelper.MAX_ATTACHMENTS_COUNT) {
                    showToast(R.string.message_panel_to_many_attachments)
                    emptyList()
                } else {
                    filteredList
                }
            }.map { uriList ->
                Pair(draftUuid, uriList)
            }
        }
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { (messageUuid, uriList) ->
                interactor.addAttachments(messageUuid,
                    uriList = uriList,
                    compressImages = compressImages
                ).andThen(saveDraftSafeEdit(messageUuid))
            }
            .doOnTerminate {
                viewModel.liveData.setSendControlClickable(isClickable = true)
                onAttachmentsChanged()
            }
            .subscribe(Functions.EMPTY_ACTION, ::showAddAttachmentsError)
    }

    override fun addDiskAttachments(diskDocumentParamsList: List<DiskDocumentParams>) {
        if (loginInterface.getCurrentAccount()?.isDemo == true) {
            viewModel.liveData.showToast(resourceProvider.getString(RAttachments.string.attach_decl_adding_denied_on_demo))
            return
        }
        viewModel.liveData.setSendControlClickable(isClickable = false)
        disposable += viewModel.liveData.targetMessageUuid.firstOrError().flatMap { draftUuid ->
            Single.fromCallable {
                val alreadyAdded: List<DiskDocumentParams> = diskDocumentParamsList.filter { param ->
                    attachments.any { attachment -> attachment.attachId.toString() == param.id }
                }
                when (alreadyAdded.size) {
                    0 -> Unit
                    1 -> showToast(RDesignDialogs.string.design_dialogs_file_already_attached)
                    diskDocumentParamsList.size -> showToast(R.string.message_panel_all_files_already_attached)
                    else -> showToast(R.string.message_panel_some_files_already_attached)
                }
                diskDocumentParamsList.minus(alreadyAdded.toSet())
            }.map { documentParamsList ->
                Pair(draftUuid, documentParamsList)
            }
        }
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { (messageUuid, documentParamsList) ->
                interactor.addAttachments(messageUuid, diskDocumentParamsList = documentParamsList)
                    .andThen(saveDraftSafeEdit(messageUuid))
            }
            .doOnTerminate {
                viewModel.liveData.setSendControlClickable(isClickable = true)
                onAttachmentsChanged()
            }
            .subscribe(Functions.EMPTY_ACTION, ::showAddAttachmentsError)
    }

    override fun onDeleteAttachmentClick(position: Int) {
        if (position in attachments.indices) {
            val attachment = attachments.removeAt(position)
            attachmentsObservable.onNext(attachments)
            val deleteAttachment = if (viewModel.liveData.isAttachmentsOnEditTransaction) {
                interactor.deleteAttachmentByTransaction(attachment)
            } else {
                interactor.deleteAttachment(attachment)
            }
            disposable += deleteAttachment.subscribe()
            onAttachmentsChanged()
            updateAttachmentList()
        }
    }

    override fun onAttachmentClick(position: Int) = DebounceActionHandler.INSTANCE.handle {
        router?.let {
            val attachment: FileInfo? = attachments.getOrNull(position)
            if (attachment == null) {
                showNotFoundFileInfoError(position)
                return@handle
            }
            viewModel.liveData.postKeyboardEvent(ClosedByRequest)
            viewModel.onForceHideKeyboard()
            disposable += Completable.complete().delay(70, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.showViewerSlider(attachments, attachment)
                }
        }
    }

    override fun onMoreAttachmentsClick(position: Int) {
        errorSafe(
            "Unexpected call to onMoreAttachmentsClick method with list of attachments $attachments" +
                " and position $position"
        )
    }

    override fun onRestartAttachmentClick(position: Int) {
        val attachment: FileInfo? = attachments.getOrNull(position)
        if (attachment != null) {
            restartUploadAttachment(attachment)
        } else {
            showNotFoundFileInfoError(position)
        }
    }

    override fun onErrorAttachmentClick(position: Int) {
        val attachment: FileInfo? = attachments.getOrNull(position)
        if (attachment == null) {
            showNotFoundFileInfoError(position)
            return
        }
        val errorMessage: String? = attachment.contentOperation?.error?.errorMessage
        if (errorMessage == null) {
            Timber.e("Attachment upload is not error.")
            return
        }
        viewModel.onMessageAttachErrorClick(
            MessageAttachError(
                errorMessage = errorMessage,
                fileInfo = attachment
            )
        )
    }

    override fun clearAttachments() {
        attachments.clear()
        attachmentsObservable.onNext(attachments)
        localAttachmentsProgress.clear()
        viewModel.liveData.setAttachments(emptyList())
    }

    override fun loadAttachments(messageUuid: UUID) {
        val loadAttachments = if (viewModel.liveData.isAttachmentsOnEditTransaction) {
            interactor.loadAttachmentsByTransaction(messageUuid)
        } else {
            interactor.loadAttachments(messageUuid)
        }
        disposable += loadAttachments
            .map { Triple(messageUuid, it, it.toRegisterModels()) }
            .subscribeOn(Schedulers.io())
            .observeOn(observeScheduler)
            .subscribe(
                { (messageUuid, draftAttachments, registerModels) ->
                    // состояние может измениться и уже будет другое сообщение в фокусе
                    if (messageUuid != this.messageUuid) return@subscribe
                    attachments.clear()
                    attachments.addAll(draftAttachments)
                    attachmentsObservable.onNext(attachments)
                    viewModel.liveData.setAttachments(registerModels)
                    disposable += saveDraftSafeEdit(messageUuid).subscribe(
                        {},
                        { Timber.w(it, "Save draft failed after load attachments") }
                    )
                },
                { showUploadingError(it) }
            )
    }

    override fun loadAttachmentsFromDraft() {
        disposable += viewModel.liveData.draftUuidUpdater.firstOrError()
            .subscribeOn(Schedulers.io())
            .observeOn(observeScheduler)
            .subscribe(
                { loadAttachments(messageUuid = it) },
                Timber::e
            )
    }

    override fun restartUploadAttachment(id: Long) {
        val attachment = attachments.find { it.id == id }
        if (attachment != null) {
            restartUploadAttachment(attachment)
        } else {
            showNotFoundFileInfoError(id)
        }
    }

    private fun restartUploadAttachment(attachment: FileInfo) {
        disposable += interactor.restartUploadAttachment(attachment)
            .subscribe {
                messageUuid?.also(::loadAttachments)
            }
    }

    override fun isDisposed(): Boolean = disposable.isDisposed

    override fun dispose() {
        disposable.dispose()
        subscriptionManager.dispose()
    }

    private fun updateAttachmentList() {
        mapperDisposable.set(
            Observable.fromCallable { attachments.toRegisterModels() }
                .subscribeOn(Schedulers.io())
                .observeOn(observeScheduler)
                .subscribe(viewModel.liveData::setAttachments)
        )
    }

    private fun saveDraftSafeEdit(messageUuid: UUID): Completable =
        if (viewModel.liveData.isEditingState) {
            Completable.complete()
        } else {
            viewModel.saveDraft(messageUuid)
        }

    private fun onAttachmentsChanged() {
        if (viewModel.liveData.isEditingState) {
            viewModel.liveData.onAttachmentsEdited()
        }
    }

    private fun List<FileInfo>.toRegisterModels(): List<AttachmentRegisterModel> =
        map { attachment ->
            val progress: Int = attachment.attachId?.let { localAttachmentsProgress[it] } ?: 0
            modelMapper.map(attachment, progress)
        }

    private fun onAddingProgress(diskUuid: UUID, progress: Int) {
        localAttachmentsProgress[diskUuid] = progress
        viewModel.liveData.setAttachmentProgress(diskUuid to progress)
    }

    private fun onAddingError(diskUuid: UUID, errorType: String) {
        val isPanelAttachment = attachments.find { it.attachId == diskUuid } != null
        if (!isPanelAttachment) return
        messageUuid?.also(::loadAttachments)
        showUploadingError(
            IllegalStateException("Unable to load attachment with uuid $diskUuid (error: $errorType)")
        )
    }

    fun pauseAttachmentsUpdate() {
        subscriptionManager.pauseSubscription(ATTACHMENT_REFRESH_CALLBACK_NAME)
    }

    fun resumeAttachmentsUpdate() {
        subscriptionManager.resumeSubscription(ATTACHMENT_REFRESH_CALLBACK_NAME)
    }

    private fun showUploadingError(error: Throwable) {
        Timber.e(error)
    }

    private fun showAddAttachmentsError(error: Throwable) {
        Timber.e(error, "MessagePanelAttachmentsPresenter: addAttachmentsError")
        with(viewModel) {
            liveData.showToast(resourceProvider.getString(R.string.message_panel_attachment_upload_error))
        }
    }

    private fun showToast(@StringRes messageRes: Int) {
        with(viewModel) {
            liveData.showToast(resourceProvider.getString(messageRes))
        }
    }

    private fun showNotFoundFileInfoError(position: Int) {
        Timber.e("Not found FileInfo by position ($position).")
    }

    private fun showNotFoundFileInfoError(id: Long) {
        Timber.e("Not found FileInfo id ($id).")
    }
}

private fun FileUriUtil.FileInfo.isTooBig(): Boolean =
    size > FileUriUtil.convertMbToBytes(MAX_MESSAGE_ATTACHMENT_SIZE_MB)