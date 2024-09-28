package ru.tensor.sbis.design.view_ext.clickabletext

/**
 * Интерфейс слушателя кликов на кликабельный текст
 *
 * @author sa.nikitin
 */
interface IdentifiableTextClickListener {

    /**
     * Текст с идентификатором [textId] был кликнут
     *
     * @param textId Идентификатор кликнутого текста
     */
    fun onTextClick(textId: Int)
}