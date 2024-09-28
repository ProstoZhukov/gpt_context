/**
 * Набор вспомогательных инструментов для работы с TaskModel
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.appdesign.kdoc

/**
 * Возвращает копию задачи с изменениями [title] и [description]
 */
internal fun TaskModel.applyChanges(title: String?, description: String): TaskModel = copy(
    title = title?.trim(),
    description = description.trim()
)

