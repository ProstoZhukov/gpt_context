package ru.tensor.sbis.design.video_message_view.player.utils

import android.content.Context
import android.net.Uri
import android.util.Size
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import ru.tensor.sbis.attachments.decl.canonicalUri
import ru.tensor.sbis.attachments.generated.AttachmentsUtils
import ru.tensor.sbis.attachments.generated.ImageParams
import ru.tensor.sbis.attachments.models.property.AttachmentPreviewMap
import ru.tensor.sbis.attachments.ui.mapper.AttachmentPreviewSourcesFactory
import ru.tensor.sbis.attachments.ui.viewmodel.base.preview.AttachmentPreviewVM
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaPlayerFileInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource.VideoSource
import ru.tensor.sbis.communication_decl.communicator.media.data.SourceData
import ru.tensor.sbis.design.video_message_view.R
import ru.tensor.sbis.design.video_message_view.VideoPlayerViewPlugin
import java.util.ArrayList
import java.util.UUID

/**
 * Утилиты видео плеера.
 *
 * @author vv.chekurda
 */
internal object VideoPlayerUtils {

    private val appContext: Context by lazy {
        VideoPlayerViewPlugin.themedAppContext
            ?: VideoPlayerViewPlugin.application.applicationContext
    }

    private val attachmentPreviewSourcesFactory by lazy {
        AttachmentPreviewSourcesFactory(
            appContext,
            AttachmentPreviewSourcesFactory.createDefaultSizes(appContext)
        )
    }

    private val previewStubBitmap by lazy {
        AppCompatResources.getDrawable(appContext, R.drawable.video_message_preview_stub)!!.toBitmap()
    }

    /**
     * Создать [VideoSource] по модели вложения [fileInfo].
     */
    fun createVideoSource(
        uuid: UUID,
        fileInfo: MediaPlayerFileInfo
    ): VideoSource =
        VideoSource(
            uuid = uuid,
            data = fileInfo.canonicalLocalUri()?.let { uri ->
                SourceData.UriData(uri.toUri())
            } ?: SourceData.DiskData(fileInfo.attachId)
        )

    /**
     * Создать превью видеосообщения [AttachmentPreviewVM] по модели вложения [fileInfo].
     */
    fun createVideoPlayerPreviewVM(
        fileInfo: MediaPlayerFileInfo
    ): AttachmentPreviewVM =
        AttachmentPreviewVM(
            fileInfo.attachId.toString(),
            AttachmentPreviewVM.Placeholder(
                R.drawable.video_message_preview_stub,
                previewStubBitmap.toDrawable(appContext.resources)
            ),
            attachmentPreviewSourcesFactory.createOptimized(
                fileInfo.getPreviewUrls(),
                immutablePreviewUris = arrayOf(fileInfo.canonicalLocalUri())
            ),
            false
        )

    /**
     * Создать превью видеосообщения [AttachmentPreviewVM] по модели источника [source].
     */
    fun createVideoPlayerPreviewVM(
        source: VideoSource
    ): AttachmentPreviewVM =
        AttachmentPreviewVM(
            source.uuid.toString(),
            AttachmentPreviewVM.Placeholder(
                R.drawable.video_message_preview_stub,
                previewStubBitmap.toDrawable(appContext.resources)
            ),
            source.data.let { data ->
                if (data is SourceData.UriData) {
                    attachmentPreviewSourcesFactory.createOptimized(
                        null,
                        immutablePreviewUris = arrayOf(Uri.decode(data.uri.toString()))
                    )
                } else {
                    emptyList()
                }
            },
            false
        )

    private fun MediaPlayerFileInfo.getPreviewUrls(): AttachmentPreviewMap {
        val defaultSizes = AttachmentPreviewSourcesFactory.createDefaultSizes(appContext)
        val previewHeights = defaultSizes.map(Size::getHeight)
        val previewSizes: ArrayList<ImageParams> =
            previewHeights.mapTo(arrayListOf()) { ImageParams(width = 0, height = it) }
        return previewParams?.let { previewParams ->
            AttachmentsUtils.getPreviewUrls(params = previewParams, extParams = null, imgParams = previewSizes)
        } ?: emptyMap()
    }

    private fun MediaPlayerFileInfo.canonicalLocalUri(): String? =
        canonicalUri(localPath)
}