package ru.tensor.sbis.design.gallery.impl.store.primitives

/**
 * Реализация mvi-сущности Action
 */
internal sealed interface GalleryAction {

    /** Проверка разрешений */
    object CheckPermissions : GalleryAction
}