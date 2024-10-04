package ru.tensor.sbis.design.sbis_text_view.contact

import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.sbis_text_view.SbisTextView.Extension

/**
 * API компонента [SbisTextView] по работе с расширениями [Extension].
 * @see [Extension] подробное описание механик работы расширений.
 *
 * @author vv.chekurda
 */
interface SbisTextViewExtensionApi {

    /**
     * Установить расширенеие.
     * @see Extension
     */
    fun setExtension(extension: Extension?)

    /**
     * Получить текущее расширение типа [EXTENSION].
     * Вернет null, если расширение типа [EXTENSION] не установлено.
     */
    fun <EXTENSION : Extension> getExtension(): EXTENSION?

    /**
     * Требовать расширение типа [EXTENSION].
     *
     * Вернет текущее расширение типа [EXTENSION],
     * если не установлено - создаст с помощью [creator] и установит в [SbisTextView.setExtension].
     */
    fun <EXTENSION : Extension> requireExtension(creator: () -> EXTENSION): EXTENSION
}