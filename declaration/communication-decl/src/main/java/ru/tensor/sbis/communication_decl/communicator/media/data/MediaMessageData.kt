package ru.tensor.sbis.communication_decl.communicator.media.data

/** @SelfDocumented **/
interface MediaMessageData {
    val duration: Int
    val recognizedText: CharSequence?
    val recognized: Boolean?
    val type: MediaType
}

enum class MediaType {
    AUDIO,
    VIDEO
}