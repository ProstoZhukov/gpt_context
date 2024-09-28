package ru.tensor.sbis.viewer.decl.viewer

import android.os.Parcelable

/**
 * Аргументы просмотрщика
 *
 * @author sa.nikitin
 */
interface ViewerArgs : Parcelable {
    /**
     * Идентификатор просмотрщика
     * ВНИМАНИЕ! Не должен быть статичным, должен зависеть от контента, отображаемого в просмотрщике,
     * например, идентификатор вложения для просмотрщика вложения
     */
    val id: String

    /**
     * Заголовок просмотрщика
     * ВНИМАНИЕ! Не должен быть статичным, должен зависеть от контента, отображаемого в просмотрщике,
     * например, название вложения для просмотрщика вложения
     */
    var title: String?
}

/** @SelfDocumented */
fun ViewerArgs.equalsById(otherViewerArgs: ViewerArgs): Boolean = equalsById(otherViewerArgs.id)

/** @SelfDocumented */
fun ViewerArgs.equalsById(otherViewerId: String): Boolean = id == otherViewerId