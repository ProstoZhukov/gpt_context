package ru.tensor.sbis.design_dialogs.dialogs.content.utils

import android.os.Bundle
import ru.tensor.sbis.design_dialogs.dialogs.content.Content

/**
 * @author sa.nikitin
 */

/**
 * Получить контейнер как [T]
 *
 * @throws IllegalStateException, если контейнер не является экземпляром [T]
 */
inline fun <reified T : Any> Content.requireContainerAs(): T =
    checkNotNull(containerAs()) { "Container must be an instance of ${T::class.java.canonicalName}" }

/**
 * Получить любой контейнер из иерархии как [T]
 * Пример: контейнер первого уровня - активность, второго - фрагмент.
 * Контенту может потребоваться изменить, например, заголовок тулбара, который определён в активности,
 * в таком случае идём по всем контейнерам вверх по иерархии, активность будет искомым контейнером
 *
 * @throws IllegalStateException, если контейнер не является экземпляром [T]
 */
inline fun <reified T : Any> Content.requireAnyContainerAs(): T =
    checkNotNull(anyContainerAs()) { "Any container must be an instance of ${T::class.java.canonicalName}" }

inline fun <reified T : Any> Content.containerAs(): T? = ContentFragmentUtils.containerAs(this, T::class.java)

inline fun <reified T : Any> Content.anyContainerAs(): T? = ContentFragmentUtils.anyContainerAs(this, T::class.java)

inline fun <reified T : Any> Content.containerIs(): Boolean = ContentFragmentUtils.containerIs(this, T::class.java)

fun Content.didAction(actionId: String, data: Bundle?) {
    ContentFragmentUtils.didAction(this, actionId, data)
}