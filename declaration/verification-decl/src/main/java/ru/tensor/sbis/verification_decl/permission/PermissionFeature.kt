package ru.tensor.sbis.verification_decl.permission

import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для модуля "Полномочия". Описывает предоставляемый модулем функционал
 *
 * @author ma.kolpakov
 * Создан 12/11/2018
 */
interface PermissionFeature : Feature {

    /**
     * Объект БЛ для проверки полномочий пользователя. Для управления активностью подписки на
     * изменение полномочий нужно привязать [LifecyclePermissionChecker] к жизненному циклу
     * [LifecycleOwner].
     */
    val permissionChecker: LifecyclePermissionChecker
}