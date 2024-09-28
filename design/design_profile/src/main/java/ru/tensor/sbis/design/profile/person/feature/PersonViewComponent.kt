package ru.tensor.sbis.design.profile.person.feature

import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Компонент, предоставляемый [PersonViewPlugin].
 *
 * @author us.bessonov
 */
interface PersonViewComponent : Feature

/**
 * Фиксирует в плагинной системе зависимость прикладного модуля, использующего компоненты фото сотрудника, от модуля
 * фото сотрудника.
 * Добавление такого прикладного модуля в приложение будет требовать регистрацию [PersonViewPlugin], необходимую для
 * корректной работы компонентов фото сотрудника, иначе при инициализации плагинной системы будет обнаружена ошибка.
 */
fun Dependency.Builder.requirePersonViewComponent() = require(PersonViewComponent::class.java) { }
