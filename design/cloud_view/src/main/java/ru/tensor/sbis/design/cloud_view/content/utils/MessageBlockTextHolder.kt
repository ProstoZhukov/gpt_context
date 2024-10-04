package ru.tensor.sbis.design.cloud_view.content.utils

import android.content.Context
import android.text.Spannable
import android.view.View
import android.widget.TextView
import ru.tensor.sbis.design.cloud_view.content.MessageBlockView
import ru.tensor.sbis.design.cloud_view.content.phone_number.PhoneNumberClickListener
import ru.tensor.sbis.design.cloud_view.model.QuoteCloudContent

/**
 * Интерфейс для поставки и настройки View для текста в [MessageBlockView]
 * Пример реализации можно увидеть в [DefaultMessageBlockTextHolder]
 *
 * @author da.zolotarev
 */
interface MessageBlockTextHolder {

    /**
     * Возвращает TextView, в котором отрисовывается текст для настройки стилей
     */
    fun getTextView(context: Context): TextView

    /**
     * Возвращает контейнер TextView для вставки в облако
     */
    fun getTextLayoutView(context: Context): View

    /**
     * Выставление текста в View
     */
    fun setText(message: Spannable?)

    /**
     * Установить спан цитаты.
     *
     * @param message текст сообщения, в который нужно установить span цитаты.
     * @param quoteContentList список контента с цитатами.
     */
    fun setQuoteClickSpan(message: Spannable, quoteContentList: List<QuoteCloudContent>)

    /**
     * Установить span номера телефона.
     *
     * @param message текст сообщения, в который нужно установить span номера телефона.
     */
    fun setPhoneClickSpan(message: Spannable, listener: PhoneNumberClickListener) = Unit
}