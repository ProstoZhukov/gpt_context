package ru.tensor.sbis.design.message_panel.decl.record

import java.io.File

/**
 * Фабрика файлов для сохранения записанного медиа контента.
 *
 * @author vv.chekurda
 */
interface MediaFileFactory {

    /**
     * Создать файл.
     */
    fun createFile(): File
}
