package ru.tensor.sbis.toolbox_decl.share

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Данные функциональности "поделиться".
 *
 * @author vv.chekurda
 */
sealed interface ShareData : Parcelable {

    /**
     * Текст для "поделиться".
     */
    val text: CharSequence?

    /**
     * Файлы для "поделиться".
     */
    val files: List<String>

    /**
     * Текст без файлов.
     * Чаще всего текст, выделенный в браузерах, или ссылка.
     */
    @Parcelize
    class Text(override val text: CharSequence) : ShareData {
        override val files: List<String> get() = emptyList()
    }

    /**
     * Список из Uri в виде строк, указывающих на файлы, с доп. текстом описания к файлам.
     */
    @Parcelize
    class Files(
        override val files: List<String>,
        override val text: CharSequence? = null
    ) : ShareData

    /**
     * Контакты из телефонной книги.
     */
    @Parcelize
    class Contacts(
        override val files: List<String>,
        override val text: CharSequence? = null
    ) : ShareData

    /**
     * Ссылка, которая шарится offline из браузера.
     */
    @Parcelize
    class OfflineLink(
        override val files: List<String>,
        override val text: CharSequence? = null
    ) : ShareData
}
