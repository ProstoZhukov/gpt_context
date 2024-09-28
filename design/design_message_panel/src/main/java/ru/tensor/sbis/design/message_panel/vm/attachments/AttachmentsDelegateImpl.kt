package ru.tensor.sbis.design.message_panel.vm.attachments

import android.content.Context
import android.view.View
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.tensor.sbis.attachments.decl.canonicalLocalUri
import ru.tensor.sbis.attachments.decl.mapper.AttachmentRegisterModelMapper
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerPresentationParams
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.design.message_panel.R
import ru.tensor.sbis.design.message_panel.decl.attachments.AttachmentsService
import ru.tensor.sbis.design.message_panel.vm.draft.DraftDelegate
import ru.tensor.sbis.design.message_panel.vm.notification.NotificationDelegate
import ru.tensor.sbis.design.utils.errorSafe
import ru.tensor.sbis.disk.decl.params.DiskDocumentParams
import ru.tensor.sbis.verification_decl.login.CurrentAccount
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import ru.tensor.sbis.attachments.decl.R as RAttachments

/**
 * @author ma.kolpakov
 */
internal class AttachmentsDelegateImpl internal constructor(
    private val draftDelegate: DraftDelegate,
    private val notificationDelegate: NotificationDelegate,
    private val attachmentsService: AttachmentsService,
    private val accountService: CurrentAccount,
    private val modelMapper: AttachmentRegisterModelMapper,
    appContext: Context,
    private val dispatcher: CoroutineContext
) : AttachmentsDelegate {

    private val fileUriUtil = FileUriUtil(appContext)

    private lateinit var coroutineScope: CoroutineScope
    private val attachmentChangeMutex = Mutex()

    override val attachments = MutableStateFlow(emptyList<FileInfo>())

    override lateinit var viewAttachments: StateFlow<List<AttachmentRegisterModel>>
        private set

    override val progressAttachments = attachmentsService.uploadingProgress

    override lateinit var attachmentButtonVisible: StateFlow<Boolean>
        private set

    override lateinit var attachmentsUuid: StateFlow<List<UUID>>
        private set

    override val attachmentsSelectionRequest = MutableSharedFlow<AttachmentsSelectionRequest>()

    @Inject
    constructor(
        draftDelegate: DraftDelegate,
        notificationDelegate: NotificationDelegate,
        attachmentsService: AttachmentsService,
        accountService: CurrentAccount,
        modelMapper: AttachmentRegisterModelMapper,
        appContext: Context
    ) : this(
        draftDelegate,
        notificationDelegate,
        attachmentsService,
        accountService,
        modelMapper,
        appContext,
        Dispatchers.IO
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun attachAttachmentsScope(scope: CoroutineScope) {
        coroutineScope = scope
        viewAttachments = attachments.map { attachments ->
            attachments.map { attachment -> modelMapper.map(attachment) }
        }
            .flowOn(dispatcher)
            .stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())
        attachmentButtonVisible = attachmentsSelectionRequest.subscriptionCount.map { it > 0 }
            .stateIn(coroutineScope, SharingStarted.Eagerly, true)
        attachmentsUuid = attachments.map { attachments ->
            attachments.map { it.attachId!! }
        }.stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())

        coroutineScope.launch {
            // при обновлении черновика загружаем вложения
            draftDelegate.draftUuid.filterNotNull()
                .flatMapLatest { draftUuid ->
                    attachmentsService.attachmentsFlow(draftUuid)
                }
                .flowOn(dispatcher)
                .collect { attachmentsList ->
                    attachmentChangeMutex.withLock {
                        attachments.value = attachmentsList
                    }
                }
        }
    }

    override fun addAttachments(attachments: List<SbisPickedItem>, isNeedCompressImages: Boolean) {
        if (attachments.isEmpty()) {
            // тут не с чем работать, уходим
            return
        }
        coroutineScope.launch(dispatcher) {
            if (accountService.getCurrentAccount()?.isDemo == true) {
                notificationDelegate.showToast(RAttachments.string.attach_decl_adding_denied_on_demo)
            } else {
                val grouped = async { groupAttachments(attachments) }
                val draftUuid = draftDelegate.draftUuid.filterNotNull().first()
                /*
                Критическую секцию разрывать нельзя.
                Гарантируется безопасность обновления [attachments]
                 */
                attachmentChangeMutex.withLock {
                    val (local, disk) = grouped.await()
                    val attachmentsList = this@AttachmentsDelegateImpl.attachments.value
                    val localFiltered = filterLocalAsync(local, attachmentsList)
                    val diskFiltered = filterDiskAsync(disk, attachmentsList)
                    /*
                    Тут только публикуется задание на сохранение вложений к сообщению.
                    Загрузка происходит асинхронно
                     */
                    attachmentsService.addAttachments(
                        draftUuid,
                        localFiltered.await(),
                        diskFiltered.await()
                    )
                }
            }
        }
    }

    override fun onDeleteAttachmentClick(position: Int) {
        coroutineScope.launch(dispatcher) {
            attachmentChangeMutex.withLock {
                val attachmentsList = attachments.value
                if (position in attachmentsList.indices) {
                    val attachment = attachmentsList[position]
                    attachmentsService.deleteAttachment(attachment)
                    attachments.value = attachmentsList.minusElement(attachment)
                }
            }
        }
    }

    override fun onAttachmentClick(position: Int) {
        // TODO: "Открывать через роутер"
    }

    override fun onMoreAttachmentsClick(position: Int) {
        errorSafe(
            "Unexpected call to onMoreAttachmentsClick method with list of attachments " +
                "$attachments and position $position"
        )
    }

    override fun onAttachmentsClearClicked() =
        clearAttachments()

    override fun onAttachButtonClicked(anchor: View) {
        val locator = SbisFilesPickerPresentationParams(
            horizontalLocator = AnchorHorizontalLocator(HorizontalAlignment.CENTER)
                .apply { anchorView = anchor },
            verticalLocator = AnchorVerticalLocator(VerticalAlignment.TOP)
                .apply { anchorView = anchor }
        )
        coroutineScope.launch {
            attachmentsSelectionRequest.emit(
                // TODO: реализовать исключение источников для выбора вложений
                AttachmentsSelectionRequest(presentationParams = locator)
            )
        }
    }

    override fun clearAttachments() {
        coroutineScope.launch {
            attachmentChangeMutex.withLock {
                attachments.value = emptyList()
            }
        }
    }

    private fun groupAttachments(
        attachments: List<SbisPickedItem>
    ): Pair<List<String>, List<DiskDocumentParams>> {
        val uriList = mutableListOf<String>()
        val diskFileList = mutableListOf<DiskDocumentParams>()
        attachments.forEach { file ->
            when (file) {
                is SbisPickedItem.DiskDocument -> diskFileList.add(file.params)
                is SbisPickedItem.LocalFile -> uriList.add(file.uri)
                else -> error("Unexpected file selection type ${file::class.java}")
            }
        }
        return uriList to diskFileList
    }

    /**
     * Фильтрует локальные файлы большого размера или те, которые уже добавлены
     */
    private fun CoroutineScope.filterLocalAsync(
        local: List<String>,
        attachments: List<FileInfo>
    ) = async {
        val added = filterLocalAddedAsync(local, attachments)
        val big = filterLocalBigAsync(local)
        @Suppress("ConvertArgumentToSet" /* допустимо добавление дубликатов */)
        local.asSequence().minus(added.await()).minus(big.await()).toList()
    }

    /**
     * Фильтрует локальные файлы, которые уже добавлены. Показывает сообщения по добавленым файлам
     */
    private fun CoroutineScope.filterLocalAddedAsync(
        local: List<String>,
        attachments: List<FileInfo>
    ) = async {
        val added = local.filter { uri ->
            attachments.any { attachment -> attachment.canonicalLocalUri == uri }
        }
        showNotificationForAddedFiles(added, local)
        added
    }

    /**
     * Фильтрует локальные файлы большого размера. Показывает сообщения при превышении размера
     */
    private fun CoroutineScope.filterLocalBigAsync(local: List<String>) = async {
        val big = local.filter { uri ->
            val size = fileUriUtil.getFileSize(uri).takeIf { it >= 0 } ?: 0
            size > FileUriUtil.convertMbToBytes(MAX_MESSAGE_ATTACHMENT_SIZE_MB)
        }
        showNotificationForBigFiles(big, local)
        big
    }

    private fun CoroutineScope.filterDiskAsync(
        diskParams: List<DiskDocumentParams>,
        attachments: List<FileInfo>
    ) = async {
        val added = diskParams.filter { param ->
            attachments.any { attachment -> UUIDUtils.equals(attachment.attachId, param.id) }
        }
        showNotificationForAddedFiles(added, diskParams)
        added
    }

    private fun showNotificationForAddedFiles(added: List<*>, all: List<*>) {
        with(notificationDelegate) {
            when (added.size) {
                0 -> Unit
                1 -> showToast(R.string.design_message_panel_file_already_attached)
                all.size -> showToast(R.string.design_message_panel_all_files_already_attached)
                else -> showToast(R.string.design_message_panel_some_files_already_attached)
            }
        }
    }

    private fun showNotificationForBigFiles(big: List<*>, all: List<*>) {
        with(notificationDelegate) {
            when (big.size) {
                0 -> Unit
                1 -> showToast(R.string.design_message_panel_attachment_files_size_limit)
                all.size -> showToast(R.string.design_message_panel_attachment_each_file_size_limit)
                else -> showToast(R.string.design_message_panel_attachment_some_files_size_limit)
            }
        }
    }

    companion object {
        internal const val MAX_MESSAGE_ATTACHMENT_SIZE_MB = 1024
    }
}
