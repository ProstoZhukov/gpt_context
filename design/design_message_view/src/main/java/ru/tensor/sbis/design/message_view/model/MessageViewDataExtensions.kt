package ru.tensor.sbis.design.message_view.model

import ru.tensor.sbis.communication_decl.communicator.media.data.MediaMessageData
import ru.tensor.sbis.design.cloud_view.thread.data.ThreadData
import ru.tensor.sbis.design.message_view.utils.castTo

/** Получить [ThreadData], если MessageViewData это ThreadViewData, иначе null. */
fun MessageViewData.getThreadData(): ThreadData? = this.castTo<ThreadViewData>()?.threadData

/** Получить [MediaMessageData], если MessageViewData это MessageCloudViewData или VideoCloudViewData, иначе null. */
fun MessageViewData.getMediaMessageData(): MediaMessageData? =
    this.castTo<MessageCloudViewData>()?.audioViewData ?: this.castTo<VideoCloudViewData>()?.videoViewData