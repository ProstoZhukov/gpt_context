package ru.tensor.sbis.design.cloud_view.content.certificate

/**
 * Бизнес модель подписи документа
 *
 * @author ma.kolpakov
 */
interface Signature {
    val title: String
    val isMine: Boolean
}