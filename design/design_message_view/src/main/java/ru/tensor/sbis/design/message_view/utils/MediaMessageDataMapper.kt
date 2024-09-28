package ru.tensor.sbis.design.message_view.utils

import android.content.Context
import org.json.JSONObject
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaPlayerFileInfo
import ru.tensor.sbis.communication_decl.communicator.media.isAudioMessage
import ru.tensor.sbis.communication_decl.communicator.media.isVideoMessage
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.communicator.generated.MessageContentItemType
import ru.tensor.sbis.design.audio_player_view.view.message.contact.AudioMessageViewDataFactory
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageViewData
import ru.tensor.sbis.design.message_view.MessageViewPlugin.audioMessageViewDataFactoryProvider
import ru.tensor.sbis.design.message_view.MessageViewPlugin.complainServiceFeatureProvider
import ru.tensor.sbis.design.message_view.MessageViewPlugin.videoMessageViewDataFactoryProvider
import ru.tensor.sbis.design.message_view.model.CoreMessageData
import ru.tensor.sbis.design.message_view.utils.rich_text_converter.MessageRichTextConverterImpl
import ru.tensor.sbis.design.video_message_view.message.contract.VideoMessageViewDataFactory
import ru.tensor.sbis.design.video_message_view.message.data.VideoMessageViewData
import java.util.UUID

/**
 * Маппер модели [CoreMessageData] в модель [AudioMessageViewData] или [VideoMessageViewData].
 *
 * @author dv.baranov
 */
internal class MediaMessageDataMapper(
    context: Context,
    private val audioMessageViewDataFactory: AudioMessageViewDataFactory? = audioMessageViewDataFactoryProvider?.get(),
    private val videoMessageViewDataFactory: VideoMessageViewDataFactory? = videoMessageViewDataFactoryProvider?.get(),
    private val complainService: ComplainService? = complainServiceFeatureProvider?.get()?.getComplainService()
) {
    private val richTextConverter = MessageRichTextConverterImpl(context)

    /** @SelfDocumented */
    fun getAudioMessageViewData(
        data: CoreMessageData,
        service: JSONObject?
    ): AudioMessageViewData? {
        val audioMessageViewDataFactory = this.audioMessageViewDataFactory ?: return null
        val serviceObject = service ?: return null
        if (!canReturnMediaData(data, serviceObject, isVideoData = false)) return null
        val fileInfo = getMediaFileInfo(data) ?: return null
        return audioMessageViewDataFactory.createAudioMessageViewData(
            uuid = data.uuid,
            fileInfo = fileInfo,
            jsonObject = serviceObject,
            recognizedText = richTextConverter.convert(data.textModel)
        )
    }

    /** @SelfDocumented */
    fun getVideoMessageViewData(
        data: CoreMessageData,
        service: JSONObject?
    ): VideoMessageViewData? {
        val videoMessageViewDataFactory = this.videoMessageViewDataFactory ?: return null
        val serviceObject = service ?: return null
        if (!canReturnMediaData(data, serviceObject, isVideoData = true)) {
            return null
        }
        val fileInfo = getMediaFileInfo(data) ?: return null
        return videoMessageViewDataFactory.createVideoMessageViewData(
            uuid = data.uuid,
            fileInfo = fileInfo,
            jsonObject = serviceObject,
            recognizedText = richTextConverter.convert(data.textModel),
            isEdited = data.edited
        )
    }

    private fun canReturnMediaData(
        data: CoreMessageData,
        serviceObject: JSONObject,
        isVideoData: Boolean
    ): Boolean =
        when {
            isAuthorBlocked(data.senderPersonModel?.personData?.uuid) -> false
            isVideoData -> serviceObject.isVideoMessage()
            else -> serviceObject.isAudioMessage()
        }

    private fun getMediaFileInfo(data: CoreMessageData): MediaPlayerFileInfo? =
        data.messageContent.content.find {
            it.itemType == MessageContentItemType.ATTACHMENT &&
                it.attachment?.fileInfoViewModel != null
        }?.attachment?.fileInfoViewModel?.let {
            MediaPlayerFileInfo(
                attachId = it.attachId,
                localPath = it.localPath,
                previewParams = it.previewParams
            )
        }

    private fun isAuthorBlocked(senderUuid: UUID?): Boolean =
        if (senderUuid != null && complainService != null) {
            complainService.isPersonBlocked(senderUuid)
        } else {
            false
        }
}