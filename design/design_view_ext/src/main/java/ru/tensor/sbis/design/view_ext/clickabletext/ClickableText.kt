package ru.tensor.sbis.design.view_ext.clickabletext

/**
 * Класс, определяющий кликабельный текст
 *
 * @property text           Текст
 * @property textId         Идентификатор текста
 * @property clickListener  Слушатель кликов на текст
 *
 * @author sa.nikitin
 */
class ClickableText(
    val text: CharSequence,
    val textId: Int,
    val clickListener: IdentifiableTextClickListener
)