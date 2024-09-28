package ru.tensor.sbis.design.cloud_view.content.certificate

/**
 * Реализация по умолчанию для [Signature]
 *
 * @author ma.kolpakov
 */
data class DefaultSignature(
    override val title: String,
    override val isMine: Boolean
) : Signature