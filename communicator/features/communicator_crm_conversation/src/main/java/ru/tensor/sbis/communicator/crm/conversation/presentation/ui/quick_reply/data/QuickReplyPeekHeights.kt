package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data

import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight

/**
 * Перечисление доступных высот шторки с быстрыми ответами.
 *
 * @property value значение высоты
 *
 * @author dv.baranov
 */
internal enum class QuickReplyPeekHeights(
    val value: MovablePanelPeekHeight.Percent,
) {
    /**
     * Начальное значения для шторки, открывающейся при вводе текста в панель сообщений.
     */
    INIT_ON_TEXT(MovablePanelPeekHeight.Percent(0.3F)),

    /**
     * Среднее значения для шторки, открывающейся при вводе текста в панель сообщений.
     */
    MIDDLE_ON_TEXT(MovablePanelPeekHeight.Percent(0.5F)),

    /**
     * Начальное значения для шторки, открывающейся по кнопке.
     */
    INIT_ON_BUTTON(MovablePanelPeekHeight.Percent(0.6F)),

    /**
     * Максимальная высота шторки.
     */
    MAX(MovablePanelPeekHeight.Percent(1F)),

    /**
     * Высота скрытия шторки.
     */
    HIDDEN(MovablePanelPeekHeight.Percent(0F)),
}
