package ru.tensor.sbis.richtext.converter.sabydoc

import androidx.annotation.WorkerThread
import ru.tensor.sbis.richtext.converter.RichTextConverter

/**
 * Конвертер файлов с расширением .sabydoc в богатый текст.
 * Поддерживает конвертирование тела документа с набором тегов в виде строки формата json markup-model
 * с помощью вызова [convert].
 *
 * @author am.boldinov
 */
interface SabyDocConverter : RichTextConverter {

    /**
     * Конвертирует sabydoc файл с набором тегов формата json markup-model в модель,
     * содержащую стилизованную spannable строку и опциональное оглавление.
     * @param filePath локальный путь до файла на диске. Файл должен быть полностью загружен на устройство.
     */
    @WorkerThread
    fun convertFromFile(filePath: String): SabyDoc
}