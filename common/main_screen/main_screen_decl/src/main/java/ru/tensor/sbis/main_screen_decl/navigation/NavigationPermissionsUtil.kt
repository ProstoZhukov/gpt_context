package ru.tensor.sbis.main_screen_decl.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.verification_decl.permission.PermissionLevel
import ru.tensor.sbis.verification_decl.permission.PermissionScope

/**
 * Вспомогательный класс с утилитами для проверки прав в навигационных компонентах.
 *
 * @author kv.martyshenko
 */
class NavigationPermissionsUtil {

    companion object {

        /**
         * Метод для формирования функции проверки прав в [ConfigurableMainScreen].
         *
         * @param scope зона для проверки
         * @param default значение по умолчанию
         *
         * @return функции проверки прав в [ConfigurableMainScreen].
         */
        @JvmOverloads
        @JvmStatic
        fun createPermissionBasedVisibilitySourceProvider(
            scope: PermissionScope,
            default: Boolean = false
        ): (ConfigurableMainScreen) -> LiveData<Boolean> {
            return { mainScreen ->
                mainScreen.monitorPermissionScope(scope).map { permission ->
                    if (permission == null) {
                        default
                    } else {
                        permission >= PermissionLevel.READ
                    }
                }
            }
        }

        /**
         * Метод для формирования источника на основе комбинирования результата двух [LiveData]-источников.
         *
         * @param source1
         * @param source2
         * @param transformer функция для формирования итогового результат
         *
         * @return [LiveData]-источник.
         */
        @Suppress("unused")
        inline fun <T1, T2, R> combineLatest(
            source1: LiveData<T1>,
            source2: LiveData<T2>,
            crossinline transformer: (T1?, T2?) -> R?
        ): LiveData<R> {
            return MediatorLiveData<R>().apply {
                var lastA: T1? = null
                var lastB: T2? = null

                addSource(source1) {
                    lastA = it
                    value = transformer(lastA, lastB)
                }

                addSource(source2) {
                    lastB = it
                    value = transformer(lastA, lastB)
                }
            }
        }

    }
}