package ru.tensor.sbis.media.contract

import android.content.Context
import android.net.Uri
import androidx.fragment.app.FragmentManager
import androidx.media3.common.util.Util
import ru.tensor.sbis.attachments.loading.decl.model.AttachmentDownloadModel
import ru.tensor.sbis.attachments.loading.decl.request.DefaultDownloadRequest
import ru.tensor.sbis.attachments.loading.decl.request.DownloadByUrlRequest
import ru.tensor.sbis.attachments.loading.decl.request.DownloadRequest
import ru.tensor.sbis.attachments.models.id.AttachmentId
import ru.tensor.sbis.attachments.models.action.AttachmentLocalActionType
import ru.tensor.sbis.common.util.CommonUtils
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.media.MediaPlugin
import ru.tensor.sbis.media.video.SbisTubeActivity
import ru.tensor.sbis.storage_utils.openFromInternalStorage
import java.io.File
import java.util.*

private const val DOWNLOAD_FILE_DIALOG_TAG = "media_download_file_dialog_tag"

/**
 * @author sa.nikitin
 */
class MediaFeatureImpl : MediaFeature {

    private val anyExtensionRegex = Regex("\\.[a-zA-Z0-9]+$")

    private fun mediaComponent() = MediaPlugin.mediaComponent

    override fun playVideoByUri(context: Context, uri: String, name: String, replaceCloseByBackArrow: Boolean) {
        startPlayVideoActivity(context, uri, name, replaceCloseByBackArrow)
    }

    override fun playVideoByUrl(
        context: Context,
        fragmentManager: FragmentManager,
        nameWithExtension: String,
        url: String,
        replaceCloseByBackArrow: Boolean
    ) {
        if (isValidVideo(Uri.parse(url).path)) {
            startPlayVideoActivity(context, url, nameWithExtension, replaceCloseByBackArrow)
        } else {
            val downloadRequest = DownloadByUrlRequest(
                url = url,
                fileName = nameWithExtension,
                actionType = AttachmentLocalActionType.OPEN
            )
            showDownloadFileDialog(fragmentManager, downloadRequest)
        }
    }

    override fun playFileSDVideo(
        context: Context,
        fragmentManager: FragmentManager,
        fileSDUuid: String,
        nameWithExtension: String
    ) {
        if (isValidVideo(nameWithExtension)) {
            startPlayVideoActivity(
                context,
                CommonUtils.createLinkByUuid(UrlUtils.DISK_API_V1_SERVICE_POSTFIX, fileSDUuid),
                nameWithExtension
            )
        } else {
            val uuid = UUID.fromString(fileSDUuid)
            val downloadRequest = DefaultDownloadRequest(
                model = AttachmentDownloadModel(
                    attachmentId = AttachmentId(diskUuid = uuid),
                    downloadId = uuid,
                    title = nameWithExtension,
                    fileName = nameWithExtension
                ),
                blObjectName = UrlUtils.FILE_SD_OBJECT,
                relativeUrl = null,
                actionType = AttachmentLocalActionType.OPEN
            )
            showDownloadFileDialog(fragmentManager, downloadRequest)
        }
    }

    override fun playVideoByPath(context: Context, absolutePath: String) {
        if (isValidVideo(absolutePath)) {
            startPlayVideoActivity(
                context,
                mediaComponent().uriWrapper.getStringUriForFilePath(absolutePath),
                File(absolutePath).name
            )
        } else {
            openFromInternalStorage(absolutePath, context)
        }
    }

    override fun isSupportedFormat(extension: String): Boolean =
        SbisTubeActivity.invalidVideoExtensions.none { extension.equals(it, ignoreCase = true) }

    private fun isValidVideo(videoSourceWithExtension: String?): Boolean {
        if (videoSourceWithExtension == null) {
            return false
        }
        val extension = videoSourceWithExtension.substringAfterLast('.', "")
        return videoSourceWithExtension.contains(anyExtensionRegex)
                && isValidVideoType(extension)
                && isSupportedFormat(extension)
    }

    private fun isValidVideoType(extension: String): Boolean =
        SbisTubeActivity.supportedVideoTypes.contains(Util.inferContentTypeForExtension(extension))

    private fun startPlayVideoActivity(
        context: Context,
        videoUri: String,
        name: String?,
        replaceCloseByBackArrow: Boolean = false
    ) {
        context.startActivity(
            createPlayVideoActivityIntent(
                context,
                videoUri,
                name,
                replaceCloseByBackArrow
            )
        )
    }

    private fun createPlayVideoActivityIntent(
        context: Context,
        videoUri: String,
        name: String?,
        replaceCloseByBackArrow: Boolean
    ) =
        SbisTubeActivity.newIntent(context, Uri.parse(videoUri), name, replaceCloseByBackArrow)

    private fun showDownloadFileDialog(fragmentManager: FragmentManager, downloadRequest: DownloadRequest) {
        with(mediaComponent().dependency) {
            fragmentManager
                .beginTransaction()
                .add(createDownloadDialogFragment(downloadRequest), DOWNLOAD_FILE_DIALOG_TAG)
                .commitAllowingStateLoss()
        }
    }
}