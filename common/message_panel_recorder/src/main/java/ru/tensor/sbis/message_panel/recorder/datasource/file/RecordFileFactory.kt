package ru.tensor.sbis.message_panel.recorder.datasource.file

import android.net.Uri
import java.io.File

/**
 * Поставщик временных файлов для сохранения аудиозаписи
 *
 * @author vv.chekurda
 * Создан 8/8/2019
 */
internal interface RecordFileFactory {

    /**
     * Запрос на создание временного файла
     */
    fun createFile(): File

    /**
     * Метод преобразования [File] в [Uri]
     */
    fun fileToUri(file: File): Uri
}