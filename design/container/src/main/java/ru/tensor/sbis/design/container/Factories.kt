/**
 * Набор фабричных методов
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.container

import android.graphics.Rect
import android.os.Parcelable
import ru.tensor.sbis.design.utils.checkSafe

/**
 * Метод создания НЕ восстанавливающегося контейнера, с опциональной возможностью задать ограничение для выреза в затенении
 * @throws IllegalStateException - нельзя задавать ограничение для выреза в В восстанавливающемся контейнере
 */
fun createViewContainer(
    contentCreator: ContentCreator<ViewContent>,
    cutoutBounds: Rect? = null,
    tag: String = CONTAINER_DEFAULT_TAG
): SbisContainer {
    checkSafe(contentCreator !is Parcelable || cutoutBounds == null) {
        "CutoutBounds not support in Parcelable content creator"
    }
    return SbisContainerImpl.newInstance(contentCreator, cutoutBounds, tag)
}

/**
 * Метод создания  восстанавливающегося контейнера, с опциональной возможностью задать ограничение для выреза в затенении
 */
fun <T> createParcelableViewContainer(
    contentCreator: T,
    tag: String = CONTAINER_DEFAULT_TAG
): SbisContainer where T : ContentCreator<ViewContent>, T : Parcelable {
    return SbisContainerImpl.newInstance(contentCreator, tag = tag)
}

/**
 * Метод создания НЕ восстанавливающегося контейнера, с опциональной возможностью задать ограничение для выреза в затенении
 * @throws IllegalStateException - нельзя задавать ограничение для выреза в восстанавливающемся контейнере
 */
fun createFragmentContainer(
    contentCreator: ContentCreator<FragmentContent>,
    cutoutBounds: Rect? = null,
    tag: String = CONTAINER_DEFAULT_TAG
): SbisContainer {
    checkSafe(contentCreator !is Parcelable || cutoutBounds == null) {
        "CutoutBounds not support in Parcelable content creator"
    }
    return SbisContainerImpl.newInstance(contentCreator, cutoutBounds, tag)
}

/**
 * Метод создания  восстанавливающегося контейнера, с опциональной возможностью задать ограничение для выреза в затенении
 */
fun <T> createParcelableFragmentContainer(
    contentCreator: T,
    tag: String = CONTAINER_DEFAULT_TAG
): SbisContainer where T : ContentCreator<FragmentContent>, T : Parcelable {
    return SbisContainerImpl.newInstance(contentCreator, tag = tag)
}