package ru.tensor.sbis.toolbox_decl.linkopener

/**
 * Представляет информацию по ссылке из контроллера и дополнительную информацию.
 * @property model модель с данными.
 * @property isIntentSource true если источник ссылки интент [android.content.Intent].
 */
data class LinkPreviewData @JvmOverloads constructor(
    val model: LinkPreview,
    var isIntentSource: Boolean = false
)