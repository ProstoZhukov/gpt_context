package ru.tensor.sbis.webviewer.data

data class FileLoadedEvent(
    val isSuccess: Boolean,
    val filePath: String,
    val uuid: String?,
    val url: String,
    val action: FileAction
) {

    enum class FileAction constructor(val value: Int) {
        NONE(0),
        OPEN(1),
        SHARE(2);

        companion object {

            @JvmStatic
            fun fromInt(value: Int): FileAction = values().find { it.value == value } ?: NONE
        }
    }
}