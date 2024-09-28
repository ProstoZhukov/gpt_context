package ru.tensor.sbis.design.message_panel.decl.quote

import android.content.Context
import android.view.View

/**
 * Панель цитирования/редактирования.
 *
 * @author vv.chekurda
 */
abstract class QuoteView(context: Context) : View(context) {

    /**
     * Установить/получить данные панели цитирования/редактирования.
     */
    abstract var data: MessagePanelQuote?

    /**
     * Установить слушателя на закрытие цитирования/редакции.
     */
    abstract fun setCloseListener(listener: (() -> Unit)?)
}