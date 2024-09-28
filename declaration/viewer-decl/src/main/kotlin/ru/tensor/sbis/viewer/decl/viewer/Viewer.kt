package ru.tensor.sbis.viewer.decl.viewer

/**
 * Интерфейс просмотрщика
 *
 * @author sa.nikitin
 */
interface Viewer {
    /**
     * Аргументы просмотрщика. Требуется для идентификации просмотрщика в слайдере
     */
    val viewerArgs: ViewerArgs
}