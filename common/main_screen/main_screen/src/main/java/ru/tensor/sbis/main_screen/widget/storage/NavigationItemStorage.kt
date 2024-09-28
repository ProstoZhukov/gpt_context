package ru.tensor.sbis.main_screen.widget.storage

import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationServiceNode

/**
 * Локальное хранилище кэшированных данных навигации приложения от сервиса.
 * Помогает избежать промаргивания и некорректного состояния при смене конфигурации.
 * В дальнейшем будет удалено, либо заменено на сохранение состояния иным способом.
 *
 * @author us.bessonov
 */
internal object NavigationItemStorage {

    /** @SelfDocumented */
    var root: NavigationServiceNode? = null
        private set

    /** @SelfDocumented */
    fun update(root: NavigationServiceNode) {
        this.root = root
    }

    /** @SelfDocumented */
    fun clear() {
        root = null
    }
}