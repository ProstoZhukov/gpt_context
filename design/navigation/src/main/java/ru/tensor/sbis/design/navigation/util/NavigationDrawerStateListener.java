package ru.tensor.sbis.design.navigation.util;

/**
 * Слушатель изменения состояние открыт/закрыт боковой панели навигации.
 *
 * @author ma.kolpakov
 * @noinspection unused
 */
public interface NavigationDrawerStateListener {

    /**
     * @SelfDocumented
     */
    default void onNavigationDrawerStateChanged() {

    }

    /**
     * @SelfDocumented
     */
    default void onNavigationDrawerOpened() {
    }

    /**
     * @SelfDocumented
     */
    default void onNavigationDrawerClosed() {
    }
}
