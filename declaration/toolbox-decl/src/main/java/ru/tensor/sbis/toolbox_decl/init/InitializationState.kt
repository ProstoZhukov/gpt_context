package ru.tensor.sbis.toolbox_decl.init

import java.io.Serializable

/**
 * Состояние инициализции приложения. Состоит из:
 * 1) вызова метода PlatformInitializer.init;
 * 2) вызова метод инициализации плагинной системы.
 *
 * @author du.bykov
 */

sealed interface InitializationState : Serializable

/**
 * Еще не вызывали метод инициализации.
 */
object NotInitialized : InitializationState

/**
 * Уже вызвали метод инициализации.
 */
object Initialized : InitializationState

/**
 * Вызвали метод инициалзиции, но он завершился с ошибкой с текстом [messageForUser] который нужно показать пользователю.
 */
class Error(val messageForUser: String) : InitializationState
