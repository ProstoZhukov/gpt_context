package ru.tensor.sbis.edo_decl.passage.config

import android.os.Bundle
import android.os.Parcelable

/**
 * Фабрика класса контроллера.
 * Используется для передачи конкретной реализации между экранами внутри [Bundle].
 * Реализация не должна захватывать что-либо парселизуемое.
 *
 * @property T Тип класса контроллера
 *
 * @author sa.nikitin
 */
interface ControllerFactory<T : Any> : Parcelable {

    fun createController(): T
}