package ru.tensor.sbis.viewer.decl.slider

import io.reactivex.Observable

interface ThumbnailListPositionHolder {
    val thumbnailListPosition: Observable<ThumbnailListPositionArgs>
}

/**
 * Аргументы изменения позиции ThumbnailList
 *
 * @param thumbnailListHeight высота thumbnailLis
 * @param translationY текущее положение по Y
 */
data class ThumbnailListPositionArgs(
    val thumbnailListHeight: Float,
    val translationY: Float
)

/**
 * Просчитывает позицию thumbnailLis по Y в верхней точке
 */
fun ThumbnailListPositionArgs.calculateTopTranslationY(): Float = -(thumbnailListHeight - translationY)